package com.statix.android.systemui.adaptivecharging

import android.os.RemoteException
import android.os.ServiceManager
import android.util.Log

import vendor.google.google_battery.ChargingStage
import vendor.google.google_battery.V1_0.IGoogleBattery.getChargingStageAndDeadlineCallback
import vendor.google.google_battery.V1_0.Result

const val TAG = "GoogleBatteryManager"

@Throws(RemoteException::class)
fun setChargingDeadline(secondsFromNow: Int) {
    // Check for AIDL existence first and use that if possible.
    getAidlImplementation()?.let {
        it.setChargingDeadline(secondsFromNow)
        return
    }
    getHidlImplementation()?.let {
        it.setChargingDeadline(secondsFromNow)
        return
    }
}

fun getChargingStageAndDeadline(adaptiveReceiver: AdaptiveChargingManager.AdaptiveChargingStatusReceiver): Boolean {
    var result: Boolean = false
    getAidlImplementation()?.let {
        try {
            val stage = it.getChargingStageAndDeadline()
            adaptiveReceiver.onReceiveStatus(stage.deadlineSecs, stage.stage)
            adaptiveReceiver.onDestroyInterface()
            result = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get Adaptive Charging status (AIDL): ", e)
        }
        return result
    }
    getHidlImplementation()?.let {
        try {
            it.getChargingStageAndDeadline(object: getChargingStageAndDeadlineCallback {
                override fun onValues(hidlResult: Byte, stage: String, seconds: Int) {
                    if (hidlResult == Result.OK) {
                        adaptiveReceiver.onReceiveStatus(seconds, stage)
                    }
                    adaptiveReceiver.onDestroyInterface()
                    result = true
                }
            })
        } catch (e: RemoteException) {
            Log.e(TAG, "Failed to get Adaptive Charging status (HIDL): ", e)
            adaptiveReceiver.onDestroyInterface()
        }
        return result
    }
    return result
}
