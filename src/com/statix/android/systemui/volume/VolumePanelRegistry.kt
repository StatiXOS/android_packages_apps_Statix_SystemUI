package com.statix.android.systemui.volume

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.UserHandle
import android.provider.Settings
import android.util.Log
import com.android.systemui.CoreStartable
import com.android.systemui.dagger.SysUISingleton
import com.android.systemui.dagger.qualifiers.Application
import com.android.systemui.dagger.qualifiers.Background
import com.android.systemui.plugins.PluginManager
import com.android.systemui.plugins.VolumeDialog
import com.android.systemui.shared.plugins.PluginEnabler
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private val KNOWN_PLUGINS =
  listOf(
    "co.potatoproject.plugin.volume.compact",
    "co.potatoproject.plugin.volume.oreo",
    "co.potatoproject.plugin.volume.tiled",
  )

private const val SETTING = "systemui_volume_plugin"

/** Singleton plugin manager to manage instances of [VolumeDialog]. */
@SysUISingleton
class VolumePanelRegistry
@Inject
constructor(
  private val context: Context,
  handler: Handler,
  private val pluginEnabler: PluginEnabler,
  @Application private val scope: CoroutineScope,
  @Background private val bgDispatcher: CoroutineDispatcher,
) : CoreStartable {

  // A flow emitting a map of package name -> component name for any registered volume dialog
  // plugins.
  private val registeredPluginsFlow = callbackFlow {
    fun queryPlugins(): Map<String, ComponentName> {
      val resolveInfos =
        context.packageManager.queryIntentServices(
          Intent(VolumeDialog.ACTION),
          PackageManager.MATCH_DISABLED_COMPONENTS,
        )
      val result = mutableMapOf<String, ComponentName>()
      Log.d(TAG, "Found ${resolveInfos.size} plugins")
      for (resolvedService in resolveInfos) {
        result[resolvedService.serviceInfo.packageName] =
          ComponentName(resolvedService.serviceInfo.packageName, resolvedService.serviceInfo.name)
      }
      return result
    }

    val receiver =
      object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          trySend(queryPlugins())
        }
      }
    val filter =
      IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_CHANGED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addDataScheme("package")
      }
    context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
    trySend(queryPlugins())

    awaitClose { context.unregisterReceiver(receiver) }
  }

  private val pluginComponentFlow = callbackFlow {
    class VolumePluginSettingObserver(handler: Handler) : ContentObserver(handler) {
      override fun onChange(selfChange: Boolean, uri: Uri?) {
        trySend(Settings.System.getString(context.contentResolver, SETTING))
      }
    }
    val observer = VolumePluginSettingObserver(handler)
    context.contentResolver.registerContentObserver(
      Settings.System.getUriFor(SETTING),
      false,
      observer,
      UserHandle.USER_ALL,
    )

    trySend(Settings.System.getString(context.contentResolver, SETTING))

    awaitClose { context.contentResolver.unregisterContentObserver(observer) }
  }

  override fun start() {
    scope.launch(bgDispatcher) {
      combine(registeredPluginsFlow, pluginComponentFlow.distinctUntilChanged()) {
          registeredPlugins,
          pluginComponent ->
          registeredPlugins to pluginComponent
        }
        .collect { (registeredPlugins, pluginPackage) ->
          registeredPlugins.forEach { packageName, componentName ->
            if (packageName == pluginPackage) {
              Log.d(TAG, "Enabling plugin $pluginPackage")
              pluginEnabler.setEnabled(componentName)
            } else {
              Log.d(TAG, "Disabling plugin $pluginPackage")
              pluginEnabler.setDisabled(componentName, PluginEnabler.DISABLED_MANUALLY)
            }
            val intent =
              Intent(PluginManager.PLUGIN_CHANGED, Uri.fromParts("package", packageName, null))
            context.sendBroadcast(intent)
          }
        }
    }
  }

  companion object {
    private const val TAG = "VolumePluginRegistry"
  }
}
