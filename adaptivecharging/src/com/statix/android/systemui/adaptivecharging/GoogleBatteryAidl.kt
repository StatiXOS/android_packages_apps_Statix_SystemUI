package com.statix.android.systemui.adaptivecharging

import android.os.ServiceManager

import vendor.google.google_battery.IGoogleBattery

const val GOOGLE_BATTERY_AIDL_DESCRIPTOR = "vendor.google.google_battery.IGoogleBattery/default"

fun getAidlImplementation(): IGoogleBattery? {
    return IGoogleBattery.Stub.asInterface(ServiceManager.getService(GOOGLE_BATTERY_AIDL_DESCRIPTOR))
}
