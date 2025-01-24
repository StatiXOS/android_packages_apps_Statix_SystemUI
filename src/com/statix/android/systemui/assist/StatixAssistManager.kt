package com.statix.android.systemui.assist

import android.app.ActivityManager
import android.app.StatusBarManager
import android.app.contextualsearch.ContextualSearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.provider.Settings.SettingNotFoundException
import android.view.WindowManager
import com.android.app.viewcapture.ViewCaptureAwareWindowManager
import com.android.internal.app.AssistUtils
import com.android.internal.util.ScreenshotHelper
import com.android.systemui.assist.AssistLogger
import com.android.systemui.assist.AssistManager
import com.android.systemui.assist.PhoneStateMonitor
import com.android.systemui.assist.domain.interactor.AssistInteractor
import com.android.systemui.assist.ui.DefaultUiController
import com.android.systemui.camera.CameraGestureHelper
import com.android.systemui.dagger.SysUISingleton
import com.android.systemui.dagger.qualifiers.Background
import com.android.systemui.dagger.qualifiers.Main
import com.android.systemui.model.SysUiState
import com.android.systemui.recents.OverviewProxyService
import com.android.systemui.settings.DisplayTracker
import com.android.systemui.settings.UserTracker
import com.android.systemui.statusbar.CommandQueue
import com.android.systemui.statusbar.policy.DeviceProvisionedController
import com.android.systemui.user.domain.interactor.SelectedUserInteractor
import com.android.systemui.util.settings.SecureSettings
import com.android.systemui.util.settings.SettingsProxyExt.observerFlow
import dagger.Lazy
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@SysUISingleton
class StatixAssistManager
@Inject
constructor(
  controller: DeviceProvisionedController,
  context: Context,
  assistUtils: AssistUtils,
  commandQueue: CommandQueue,
  phoneStateMonitor: PhoneStateMonitor,
  overviewProxyService: OverviewProxyService,
  sysUiState: Lazy<SysUiState>,
  defaultUiController: DefaultUiController,
  assistLogger: AssistLogger,
  @Main private val uiHandler: Handler,
  userTracker: UserTracker,
  displayTracker: DisplayTracker,
  private val cameraGestureHelper: Lazy<CameraGestureHelper>,
  private val contextualSearchManager: ContextualSearchManager,
  private val secureSettings: SecureSettings,
  selectedUserInteractor: SelectedUserInteractor,
  activityManager: ActivityManager,
  interactor: AssistInteractor,
  viewCaptureAwareWindowManager: ViewCaptureAwareWindowManager,
  @Background backgroundScope: CoroutineScope,
) :
  AssistManager(
    controller,
    context,
    assistUtils,
    commandQueue,
    phoneStateMonitor,
    overviewProxyService,
    sysUiState,
    defaultUiController,
    assistLogger,
    uiHandler,
    userTracker,
    displayTracker,
    secureSettings,
    selectedUserInteractor,
    activityManager,
    interactor,
    viewCaptureAwareWindowManager,
  ) {

  private val assistActionFlow =
    secureSettings
      .observerFlow(ASSIST_ACTION)
      .map { getAssistInt() }
      .stateIn(backgroundScope, started = SharingStarted.Eagerly, initialValue = getAssistInt())

  private val screenshotHelper = ScreenshotHelper(context)

  private fun getAssistInt(): Int {
    return try {
      secureSettings.getInt(ASSIST_ACTION)
    } catch (e: SettingNotFoundException) {
      -1
    }
  }

  override fun startAssist(args: Bundle) {
    val invocationType = args.getInt(AssistManager.INVOCATION_TYPE_KEY, 0)
    if (invocationType == AssistUtils.INVOCATION_TYPE_GESTURE) {
      // Check what action we should be doing.
      when (assistActionFlow.value) {
        0 -> {
          // Take screenshot
          screenshotHelper.takeScreenshot(
            WindowManager.ScreenshotSource.SCREENSHOT_VENDOR_GESTURE,
            uiHandler,
            null,
          )
          return
        }
        1 -> {
          // Open camera
          cameraGestureHelper
            .get()
            .launchCamera(StatusBarManager.CAMERA_LAUNCH_SOURCE_POWER_DOUBLE_TAP)
          return
        }
        2 -> {
          // Invoke Contextual Search
          contextualSearchManager.startContextualSearch(
            ContextualSearchManager.ENTRYPOINT_LONG_PRESS_NAV_HANDLE
          )
        }
        else -> return super.startAssist(args)
      }
    }
    return super.startAssist(args)
  }

  companion object {
    private val ASSIST_ACTION = "assist_action"
  }
}
