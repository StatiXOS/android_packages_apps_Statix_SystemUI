package com.statix.android.systemui.adaptivecharging

import android.util.Log

import vendor.google.google_battery.V1_2.IGoogleBattery

fun getHidlImplementation(): IGoogleBattery? {
    try {
        val service = IGoogleBattery.getService()
        return service
    } catch (e: Exception) {
        Log.e("AdaptiveChargingManager", "failed to get Google Battery HIDL: ", e)
        return null
    }
}
