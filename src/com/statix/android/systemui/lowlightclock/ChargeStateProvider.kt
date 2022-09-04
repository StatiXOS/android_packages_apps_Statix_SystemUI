package com.statix.android.systemui.lowlightclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager

import javax.inject.Inject

import kotlin.collections.ArrayList

class ChargeStateProvider @Inject constructor(
    context: Context,
    powerManager: PowerManager,
) {

    interface ChargeStateCallback {
        abstract fun onChargeLevelChanged(level: Int)
        abstract fun onPluggedChange(isCharging: Boolean, isWireless: Boolean)
        abstract fun onPowerSaveChanged(powerSave: Boolean)
    }

    private val listeners = ArrayList<ChargeStateCallback>()
    private var isCharging = false
    private var isWirelessCharge = false
    private var currentChargeLevel = 0
    private var isPowerSave = false
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                updateBatteryInfo()
            } else if (intent.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                if (isPowerSave == powerManager.isPowerSaveMode()) return
                isPowerSave = powerManager.isPowerSaveMode()
                firePowerSaveChanged()
            }
        }
    }

    public fun addCallback(cb: ChargeStateCallback) {
        listeners.add(cb)
        // update new callback with added data
        cb.onChargeLevelChanged(currentChargeLevel)
        cb.onPluggedChange(isCharging, isWirelessCharge)
        cb.onPowerSaveChanged(isPowerSave)
    }

    public fun removeCallback(cb: ChargeStateCallback) {
        listeners.remove(cb)
    }

    private val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(batteryReceiver, ifilter)
        }

    private val powerSaver: Intent? = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED).let {
        ifilter -> context.registerReceiver(batteryReceiver, ifilter)
    }

    private fun updateBatteryInfo() {
        // Get charge status
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        // How are we charging?
        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val wirelessCharge = !BatteryManager.isPlugWired(chargePlug)
        if (isCharging != this.isCharging) {
            this.isCharging = isCharging
            isWirelessCharge = wirelessCharge
            firePluggedInChange()
        }

        // Battery level
        val batteryPct: Int? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale
        }
        if (batteryPct != currentChargeLevel) {
            currentChargeLevel = (batteryPct as Int)
            fireChargeLevelChange()
        }
    }

    @Synchronized
    private fun fireChargeLevelChange() {
        listeners.forEach {
            it.onChargeLevelChanged(currentChargeLevel)
        }
    }

    @Synchronized
    private fun firePluggedInChange() {
        listeners.forEach {
            it.onPluggedChange(isCharging, isWirelessCharge)
        }
    }

    @Synchronized
    private fun firePowerSaveChanged() {
        listeners.forEach {
            it.onPowerSaveChanged(isPowerSave)
        }
    }
}
