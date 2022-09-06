package com.statix.android.systemui.lowlightclock

// static
import android.view.View.INVISIBLE
import android.view.View.VISIBLE

import android.annotation.ColorInt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.PowerManager
import android.os.SystemClock
import android.provider.DeviceConfig
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.android.keyguard.AnimatableClockView
import com.android.systemui.dagger.qualifiers.Main
import com.android.systemui.R
import com.android.systemui.lowlightclock.LowLightClockController
import com.android.systemui.util.DeviceConfigProxy

import java.text.NumberFormat
import java.util.Locale
import java.util.Objects
import java.util.TimeZone
import java.util.concurrent.Executor

import javax.inject.Inject

import kotlin.collections.Map

class LowLightClockControllerImpl @Inject constructor(
    private val context: Context,
    @Main private val resources: Resources,
    private val layoutInflater: LayoutInflater,
    private val powerManager: PowerManager,
    @Main private val mainExecutor: Executor,
    private val deviceConfig: DeviceConfigProxy,
    private val chargeStateProvider: ChargeStateProvider,
    private val burnInProtector: BurnInProtector,
) : LowLightClockController {

    private var clockParent: ViewGroup? = null
    private var lowLightView: View? = null
    private var clockView: AnimatableClockView? = null
    private lateinit var chargingTextView: TextView
    private val viewsToHide = mutableMapOf<View, Float>()
    private var isShowing = false
    private var locale: Locale? = null
    private val burmeseNumerals = NumberFormat.getInstance(Locale.forLanguageTag("my"))
    private val burmeseLineSpacing = resources.getFloat(R.dimen.keyguard_clock_line_spacing_scale_burmese)
    private val defaultLineSpacing = resources.getFloat(R.dimen.keyguard_clock_line_spacing_scale)
    private val timeZoneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateTimeZone(intent.getStringExtra(Intent.EXTRA_TIMEZONE))
        }
    }

    private var isAvailable = false
    private val deviceConfigChangedListener = object : DeviceConfig.OnPropertiesChangedListener {
        override fun onPropertiesChanged(properties: DeviceConfig.Properties) {
            if (properties.getNamespace() == DeviceConfig.NAMESPACE_SYSTEMUI) {
                isAvailable = isAvailable or properties.getBoolean(KEY_LOW_LIGHT_ENABLED, false)
            }
        }
    }

    private var batteryLevel = 0
    private var charging = false
    private var wireless = false
    private val chargeStatusCallback = object : ChargeStateProvider.ChargeStateCallback {
        override fun onChargeLevelChanged(level: Int) {
            batteryLevel = level
            updateChargingText()
        }
        override fun onPluggedChange(isCharging: Boolean, isWireless: Boolean) {
            charging = isCharging
            wireless = isWireless
            updateChargingText()
        }
    }

    init {
        isAvailable = resources.getBoolean(com.android.internal.R.bool.config_dozeAlwaysOnEnabled)
                || DeviceConfig.getBoolean(DeviceConfig.NAMESPACE_SYSTEMUI, KEY_LOW_LIGHT_ENABLED, false)
        deviceConfig.addOnPropertiesChangedListener(DeviceConfig.NAMESPACE_SYSTEMUI, mainExecutor, deviceConfigChangedListener)
    }

    override fun isLowLightClockEnabled(): Boolean {
        return isAvailable
    }

    override fun attachLowLightClockView(viewGroup: ViewGroup) {
        if (clockParent != null || !isLowLightClockEnabled()) {
            return
        }
        clockParent = viewGroup
        lowLightView = layoutInflater.inflate(R.layout.low_light_clock, null)
        clockView = lowLightView!!.findViewById<AnimatableClockView>(R.id.clock_view)
        chargingTextView = lowLightView!!.findViewById<TextView>(R.id.charging_status_text_view)
        lowLightView!!.visibility = INVISIBLE
        lowLightView!!.setOnClickListener {
            _ -> powerManager.wakeUp(SystemClock.uptimeMillis(), PowerManager.WAKE_REASON_GESTURE, "com.android.systemui:LOW_LIGHT_GESTURE")
        }
        val notificationPanel: View = viewGroup.findViewById<View>(R.id.notification_panel)
        viewsToHide.put(notificationPanel, 1.0f)
        val keyguardMessageArea: View = viewGroup.findViewById<View?>(R.id.keyguard_message_area)
        if (keyguardMessageArea != null) {
            viewsToHide.put(keyguardMessageArea, 1.0f)
        }
        clockParent!!.addView(lowLightView, clockParent!!.indexOfChild(notificationPanel) + 1)
        clockView!!.animateDoze(isShowing, false)
        @ColorInt val currAccent = resources.getInteger(android.R.color.system_accent1_50)
        clockView!!.setColors(currAccent, currAccent)
        clockView!!.refreshTime()
        updateLocale()
    }

    override fun dozeTimeTick() {
        if (lowLightView == null) {
            return
        }
        clockView!!.refreshTime()
    }

    override fun showLowLightClock(show: Boolean): Boolean {
        if (isShowing == show) {
            return show
        }
        isShowing = show
        if (isShowing) {
            updateClockColor()
            burnInProtector.startProtection(clockView!!)
            chargeStateProvider.addCallback(chargeStatusCallback)
            context.registerReceiver(
                timeZoneReceiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))
            viewsToHide.entries.forEach { hideView(it) }
            updateLocale()
            clockView!!.refreshTime()
            lowLightView!!.visibility = VISIBLE
        } else {
            burnInProtector.stopProtection()
            for (entry in viewsToHide.entries.iterator()) {
                entry.key.alpha = entry.value
            }
            lowLightView!!.visibility = INVISIBLE
            context.unregisterReceiver(timeZoneReceiver)
            chargeStateProvider.removeCallback(chargeStatusCallback)
        }
        return isShowing
    }

    private fun updateChargingText() {
        var chargingText: String = "${batteryLevel}%"
        if (charging) {
            chargingText += " \u2022 Charging"
            if (wireless) {
                chargingText += " wirelessly"
            }
        }
        chargingTextView.text = chargingText
    }

    private fun hideView(entry: Map.Entry<View, Float>) {
        val view = entry.key
        viewsToHide.put(view, view.alpha)
        view.alpha = 0.0f
    }

    private fun updateLocale() {
        val locale = Locale.getDefault()
        if (!Objects.equals(this.locale, locale)) {
            this.locale = locale
            val burmese = NumberFormat.getInstance(locale).format(1234567890L).equals(burmeseNumerals)
            if (burmese) {
                clockView!!.setLineSpacing(0.0f, burmeseLineSpacing)
            } else {
                clockView!!.setLineSpacing(0.0f, defaultLineSpacing)
            }
            clockView!!.refreshFormat()
        }
    }

    private fun updateClockColor() {
        @ColorInt val currAccent = resources.getInteger(android.R.color.system_accent1_50)
        mainExecutor.execute({ -> Unit
            clockView!!.setColors(currAccent, currAccent)
            clockView!!.setTextColor(currAccent)
        })
    }

    private fun updateTimeZone(timezone: String) {
        mainExecutor.execute({ -> Unit
            clockView!!.onTimeZoneChanged(TimeZone.getTimeZone(timezone))
        })
    }

    companion object {
        private val KEY_LOW_LIGHT_ENABLED = "low_light_clock_enabled"
    }
}
