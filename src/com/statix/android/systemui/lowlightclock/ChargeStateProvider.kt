package com.statix.android.systemui.lowlightclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager

import com.android.keyguard.KeyguardUpdateMonitor
import com.android.keyguard.KeyguardUpdateMonitorCallback

import com.android.settingslib.fuelgauge.BatteryStatus

import javax.inject.Inject

import kotlin.collections.ArrayList

class ChargeStateProvider @Inject constructor(
    keyguardUpdateMonitor: KeyguardUpdateMonitor,
) {

    interface ChargeStateCallback {
        abstract fun onChargeLevelChanged(level: Int)
        abstract fun onPluggedChange(isCharging: Boolean, isWireless: Boolean)
    }

    private val listeners = ArrayList<ChargeStateCallback>()
    private var currentChargeLevel = 0
    private var isCharging = false
    private var isWirelessCharge = false

    private val chargeUpdateCallback = object : KeyguardUpdateMonitorCallback() {
        override fun onRefreshBatteryInfo(status: BatteryStatus) {
            if (currentChargeLevel != status.level) {
                currentChargeLevel = status.level
                fireChargeLevelChange()
            }
            if (status.isPluggedIn() != isCharging) {
                isCharging = status.isPluggedIn()
                isWirelessCharge = status.isPluggedInWireless()
                firePluggedInChange()
            }
        }
    }

    init {
        keyguardUpdateMonitor.registerCallback(chargeUpdateCallback)
    }

    public fun addCallback(cb: ChargeStateCallback) {
        listeners.add(cb)
        // update new callback with added data
        cb.onChargeLevelChanged(currentChargeLevel)
        cb.onPluggedChange(isCharging, isWirelessCharge)
    }

    public fun removeCallback(cb: ChargeStateCallback) {
        listeners.remove(cb)
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
}
