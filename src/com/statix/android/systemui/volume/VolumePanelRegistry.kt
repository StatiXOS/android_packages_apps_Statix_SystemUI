package com.statix.android.systemui.volume

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.UserHandle
import android.provider.Settings
import com.android.systemui.CoreStartable
import com.android.systemui.dagger.SysUISingleton
import com.android.systemui.dagger.qualifiers.Background
import com.android.systemui.plugins.PluginLifecycleManager
import com.android.systemui.plugins.PluginListener
import com.android.systemui.plugins.PluginManager
import com.android.systemui.plugins.VolumeDialog
import com.android.systemui.shared.plugins.PluginEnabler
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
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
  private val pluginManager: PluginManager,
  private val pluginEnabler: PluginEnabler,
  private val scope: CoroutineScope,
  @Background private val bgDispatcher: CoroutineDispatcher,
) : CoreStartable {

  // A mapping of package name -> component name for any recognized and registered volume dialog
  // plugins.
  private val registeredPlugins = mutableMapOf<String, ComponentName>()

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

    awaitClose { context.contentResolver.unregisterContentObserver(observer) }
  }

  private val volumeDialogListener =
    object : PluginListener<VolumeDialog> {
      override fun onPluginAttached(manager: PluginLifecycleManager<VolumeDialog>): Boolean {
        if (manager.getPackage() in KNOWN_PLUGINS) {
          registeredPlugins[manager.getPackage()] = manager.getComponentName()
          return true
        }
        return false
      }

      override fun onPluginDetached(lifecycleManager: PluginLifecycleManager<VolumeDialog>) {
        if (
          lifecycleManager.getPackage() ==
            Settings.System.getString(context.contentResolver, SETTING)
        ) {
          // Reset to default.
          Settings.System.putString(context.contentResolver, SETTING, "")
        }
        registeredPlugins.remove(lifecycleManager.getPackage())
      }
    }

  override fun start() {
    pluginManager.addPluginListener(
      volumeDialogListener,
      VolumeDialog::class.java,
      /*allowMultiple=*/ true,
    )
    scope.launch {
      pluginComponentFlow.distinctUntilChanged().collect { plugin ->
        scope.launch(bgDispatcher) {
          registeredPlugins.forEach { packageName, componentName ->
            if (packageName == plugin) {
              pluginEnabler.setEnabled(componentName)
            } else {
              pluginEnabler.setDisabled(componentName, PluginEnabler.DISABLED_MANUALLY)
            }
            val intent =
              Intent(PluginManager.PLUGIN_CHANGED, Uri.fromParts("package", packageName, null))
            context.sendBroadcast(intent)
          }
        }
      }
    }
  }
}
