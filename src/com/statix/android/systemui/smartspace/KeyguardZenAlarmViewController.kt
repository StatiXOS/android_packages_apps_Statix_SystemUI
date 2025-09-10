/*
 * Copyright (C) 2025 auroraOSP
 * SPDX-License-Identifier: Apache-2.0
 */
package com.google.android.systemui.smartspace

import android.app.AlarmManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.View
import com.android.systemui.lifecycle.repeatWhenAttached
import com.android.systemui.plugins.BcSmartspaceDataPlugin
import com.android.systemui.res.R
import com.android.systemui.statusbar.policy.ZenModeController
import com.android.systemui.statusbar.policy.domain.interactor.ZenModeInteractor
import com.android.systemui.statusbar.policy.domain.model.ZenModeInfo
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class KeyguardZenAlarmViewController
@Inject
constructor(
    val context: Context,
    val datePlugin: BcSmartspaceDataPlugin,
    val zenModeController: ZenModeController,
    val zenModeInteractor: ZenModeInteractor,
    val alarmManager: AlarmManager,
    val nextClockAlarmController: NextClockAlarmController,
    val handler: Handler,
    val applicationScope: CoroutineScope,
) {
    companion object {
        const val TAG = "KeyguardZenAlarmViewController"
    }

    val smartspaceViews = mutableSetOf<BcSmartspaceDataPlugin.SmartspaceView>()
    val alarmImage: Drawable
    val showNextAlarm =
        object : AlarmManager.OnAlarmListener {
            override fun onAlarm() {
                showAlarm()
            }
        }

    val nextAlarmCallback =
        object : NextAlarmCallback {
            override fun onNextAlarmChanged(nextAlarm: Long, alarmInfo: String) {
                applicationScope.launch { updateNextAlarm() }
            }
        }

    init {
        val dndDrawable =
            context.resources.getDrawable(R.drawable.stat_sys_dnd, null) as? Drawable
                ?: throw IllegalStateException("Required value was null.")
        alarmImage = context.resources.getDrawable(R.drawable.ic_access_alarms_big, null)
        nextClockAlarmController.addCallback(nextAlarmCallback)
    }

    fun showAlarm(): Job =
        applicationScope.launch {
            val nextAlarmTime = nextClockAlarmController.nextAlarm
            if (nextAlarmTime > 0) {
                val currentTime = System.currentTimeMillis()
                val twelveHoursLater = currentTime + TimeUnit.HOURS.toMillis(12L)
                if (nextAlarmTime <= twelveHoursLater) {
                    val is24Hour =
                        android.text.format.DateFormat.is24HourFormat(
                            context,
                            android.app.ActivityManager.getCurrentUser(),
                        )
                    val format = if (is24Hour) "HH:mm" else "h:mm"
                    val formattedTime =
                        android.text.format.DateFormat.format(format, nextAlarmTime).toString()
                    smartspaceViews.forEach { view -> view.setNextAlarm(alarmImage, formattedTime) }
                } else {
                    smartspaceViews.forEach { view -> view.setNextAlarm(null, null) }
                }
            } else {
                smartspaceViews.forEach { view -> view.setNextAlarm(null, null) }
            }
        }

    fun updateDnd() {
        Log.wtf(TAG, "updateDnd should not be called when modes_ui_icons flag is enabled")
    }

    fun updateModeIcon(view: BcSmartspaceDataPlugin.SmartspaceView, zenModeInfo: ZenModeInfo?) {
        applicationScope.launch {
            if (zenModeInfo != null) {
                val drawable =
                    zenModeInfo.icon.drawable.let { d ->
                        d.constantState?.newDrawable(context.resources) ?: d.mutate()
                    }
                val description =
                    context.getString(R.string.active_mode_content_description, zenModeInfo.name)
                view.setDnd(drawable, description)
            } else {
                view.setDnd(null, null)
            }
        }
    }

    fun addSmartspaceView(v: BcSmartspaceDataPlugin.SmartspaceView) {
        if (smartspaceViews.add(v)) {
            v as View
            v.repeatWhenAttached {
                zenModeInteractor.mainActiveMode.collect { zenModeInfo ->
                    updateModeIcon(v, zenModeInfo)
                }
            }
        }
        if (smartspaceViews.size == 1) {
            applicationScope.launch { updateNextAlarm() }
        }
        applicationScope.launch { updateNextAlarm() }
    }

    fun onViewAttachedToWindow(v: View) {
        addSmartspaceView(v as BcSmartspaceDataPlugin.SmartspaceView)
    }

    fun onViewDetachedFromWindow(v: View) {
        smartspaceViews.remove(v as BcSmartspaceDataPlugin.SmartspaceView)
        if (smartspaceViews.isEmpty()) {
            nextClockAlarmController.removeCallback(nextAlarmCallback)
        }
    }

    suspend fun updateNextAlarm() {
        alarmManager.cancel(showNextAlarm)
        val nextAlarmTime = nextClockAlarmController.nextAlarm
        if (nextAlarmTime > 0) {
            val triggerTime = nextAlarmTime - TimeUnit.HOURS.toMillis(12)
            if (triggerTime > 0) {
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME,
                    triggerTime,
                    "lock_screen_next_alarm",
                    showNextAlarm,
                    handler,
                )
            }
        }
        showAlarm()
    }
}
