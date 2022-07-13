package com.google.android.systemui.smartspace

import android.app.ActivityManager
import android.app.AlarmManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.text.format.DateFormat
import android.view.View
import com.android.internal.annotations.VisibleForTesting
import com.android.systemui.R
import com.android.systemui.plugins.BcSmartspaceDataPlugin
import com.android.systemui.statusbar.policy.NextAlarmController
import com.android.systemui.statusbar.policy.ZenModeController
import java.util.LinkedHashSet
import java.util.concurrent.TimeUnit

class KeyguardZenAlarmViewController constructor(
    val context: Context,
    val plugin: BcSmartspaceDataPlugin,
    val zenModeController: ZenModeController,
    val alarmManager: AlarmManager,
    val nextAlarmController: NextAlarmController,
    val handler: Handler) {

    val alarmImage = context.resources.getDrawable(R.drawable.ic_access_alarms_big, null)
    var smartspaceViews = LinkedHashSet<BcSmartspaceDataPlugin.SmartspaceView>()
    val zenModeCallback = object : ZenModeController.Callback {
        override fun onZenChanged(i: Int) = updateDnd()
    }
    val nextAlarmCallback = NextAlarmController.NextAlarmChangeCallback { updateNextAlarm() }
    val dndImage = loadDndImage()

    fun init() {
        plugin.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                smartspaceViews.add(v as BcSmartspaceDataPlugin.SmartspaceView)
                if (smartspaceViews.size == 1) {
                    zenModeController.addCallback(zenModeCallback)
                    nextAlarmController.addCallback(nextAlarmCallback)
                }
                refresh()
            }

            override fun onViewDetachedFromWindow(v: View) {
                smartspaceViews.remove(v as BcSmartspaceDataPlugin.SmartspaceView)
                if (smartspaceViews.isEmpty()) {
                    zenModeController.removeCallback(zenModeCallback)
                    nextAlarmController.removeCallback(nextAlarmCallback)
                }
                refresh()
            }
        })
        updateNextAlarm()
    }

    fun refresh() {
        updateDnd()
        updateNextAlarm()
    }

    private fun loadDndImage() : Drawable {
        var drawable = context.resources.getDrawable(R.drawable.stat_sys_dnd, null)
        var drawable2 = (drawable!! as InsetDrawable).getDrawable()!!
        return drawable2
    }

    fun updateDnd() {
        if (zenModeController.getZen() != 0) {
            var string = context.resources.getString(R.string.accessibility_quick_settings_dnd)
            for (smartspaceView in smartspaceViews) {
                smartspaceView.setDnd(dndImage, string)
            }
            return
        }
        for (smartspaceView in smartspaceViews) {
            smartspaceView.setDnd(null, null)
        }
    }

    fun updateNextAlarm() {
        alarmManager.cancel(object : AlarmManager.OnAlarmListener {
            override fun onAlarm() = showAlarm()
        })
        val nextAlarm = zenModeController.getNextAlarm()
        if (nextAlarm > 0) {
            val millis = nextAlarm - TimeUnit.HOURS.toMillis(12L)
            if (millis > 0) {
                alarmManager.setExact(1, millis, "lock_screen_next_alarm", object : AlarmManager.OnAlarmListener {
                    override fun onAlarm() = showAlarm()
                }, handler)
            }
        }
        showAlarm()
    }

    fun showAlarm() {
        val nextAlarm = zenModeController.getNextAlarm()
        if (nextAlarm > 0 && withinNHours(nextAlarm, 12L)) {
            var nextAlarmStr = DateFormat.format(if (DateFormat.is24HourFormat(context, ActivityManager.getCurrentUser())) { "HH:mm" } else { "h:mm" }, nextAlarm).toString()
            for (smartspaceView in smartspaceViews) {
                smartspaceView.setNextAlarm(alarmImage, nextAlarmStr)
            }
            return
        }
        for (smartspaceView in smartspaceViews) {
            smartspaceView.setNextAlarm(null, null)
        }
    }

    companion object {
        private fun withinNHours(j: Long, j2: Long): Boolean {
            return j <= System.currentTimeMillis() + TimeUnit.HOURS.toMillis(j2)
        }
    }
}
