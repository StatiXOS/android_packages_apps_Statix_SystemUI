/*
 * Copyright (C) 2020 The Proton AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.statix.android.systemui.elmyra

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.IntentFilter
import android.hardware.location.ContextHubClient
import android.hardware.location.ContextHubClientCallback
import android.hardware.location.ContextHubManager
import android.hardware.location.NanoAppMessage
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.preference.PreferenceManager

import com.google.protobuf.nano.MessageNano

import com.android.systemui.R
import com.android.systemui.assist.AssistManager

import com.statix.android.systemui.elmyra.actions.*
import com.statix.android.systemui.elmyra.proto.nano.ContextHubMessages
import com.statix.android.systemui.elmyra.TAG
import com.statix.android.systemui.elmyra.getDePrefs
import com.statix.android.systemui.elmyra.getAction
import com.statix.android.systemui.elmyra.getSensitivity
import com.statix.android.systemui.elmyra.getEnabled
import com.statix.android.systemui.elmyra.getAllowScreenOff

private const val NANOAPP_ID = 0x476f6f676c00100eL
private const val REJECT_COOLDOWN_TIME = 1000 // ms

class ElmyraService constructor(
    private val context: Context,
    private val assistManager: AssistManager)
: SharedPreferences.OnSharedPreferenceChangeListener {

    // Services
    private lateinit var vibrator: Vibrator
    private lateinit var prefs: SharedPreferences
    private lateinit var action: Action
    private lateinit var client: ContextHubClient

    // State
    private var inGesture = false
    private var screenRegistered = false
    private var lastRejectTime = 0L - REJECT_COOLDOWN_TIME

    // Settings
    private var enabled = true
    private var sensitivity = 0.5f
        set(value) {
            field = value
            if (enabled) {
                sendNewSensitivity()
            }
        }

    init {
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        prefs = context.getDePrefs()

        Log.i(TAG, "Initializing CHRE gesture")

        val manager = context.getSystemService("contexthub") as ContextHubManager
        client = manager.createClient(manager.contextHubs[0], chreCallback)

        updateAction()
        updateSensitivity()
        updateEnabled()
        updateScreenCallback()

        // Only register for changes after initial pref updates
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    private fun createAction(key: String): Action {
        return when (key) {
            "screenshot" -> ScreenshotAction(context)
            "assistant" -> AssistantAction(context, assistManager)
            "camera" -> CameraAction(context)
            "power_menu" -> PowerMenuAction(context)
            "mute" -> MuteAction(context)
            "flashlight" -> FlashlightAction(context)
            "screen" -> ScreenAction(context)

            else -> DummyAction(context)
        }
    }

    private fun updateSensitivity() {
        sensitivity = prefs.getSensitivity(context) / 10f
        Log.i(TAG, "Setting sensitivity to $sensitivity")
    }

    private fun updateAction() {
        val key = prefs.getAction(context)
        Log.i(TAG, "Setting action to $key")
        action = createAction(key)

        // For settings
        prefs.edit().putBoolean(context.getString(R.string.pref_key_allow_screen_off_action_forced),
                !action.canRunWhenScreenOff()).commit()
    }

    private fun updateEnabled() {
        enabled = prefs.getEnabled(context)
        if (enabled) {
            Log.i(TAG, "Enabling gesture by pref")
            enableGesture()
        } else {
            Log.i(TAG, "Disabling gesture by pref")
            disableGesture()
        }
    }

    private fun updateScreenCallback() {
        val allowScreenOff = prefs.getAllowScreenOff(context)

        // Listen if either condition *can't* run when screen is off
        if (!allowScreenOff || !action.canRunWhenScreenOff()) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }

            if (!screenRegistered) {
                Log.i(TAG, "Listening to screen on/off events")
                context.registerReceiver(screenCallback, filter)
                screenRegistered = true
            }
        } else if (screenRegistered) {
            Log.i(TAG, "Stopped listening to screen on/off events")
            context.unregisterReceiver(screenCallback)
            screenRegistered = false
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        when (key) {
            context.getString(R.string.pref_key_enabled) -> updateEnabled()
            context.getString(R.string.pref_key_sensitivity) -> updateSensitivity()
            // Action might change screen callback behavior
            context.getString(R.string.pref_key_action) -> {
                updateAction()
                updateScreenCallback()
            }
            context.getString(R.string.pref_key_allow_screen_off) -> updateScreenCallback()
        }
    }

    private fun enableGesture() {
        val msg = ContextHubMessages.RecognizerStart()
        // Only report events to AP if gesture is halfway done
        msg.progressReportThreshold = 0.5f
        msg.sensitivity = sensitivity

        sendNanoappMsg(MessageType.RECOGNIZER_START.id, MessageNano.toByteArray(msg))
    }

    private fun disableGesture() {
        sendNanoappMsg(MessageType.RECOGNIZER_STOP.id, ByteArray(0))
    }

    private fun sendNewSensitivity() {
        val msg = ContextHubMessages.SensitivityUpdate()
        msg.sensitivity = sensitivity
        sendNanoappMsg(MessageType.SENSITIVITY_UPDATE.id, MessageNano.toByteArray(msg))
    }

    private fun sendNanoappMsg(msgType: Int, bytes: ByteArray) {
        val message = NanoAppMessage.createMessageToNanoApp(NANOAPP_ID, msgType, bytes)
        val ret = client.sendMessageToNanoApp(message)
        if (ret != 0) {
            Log.e(TAG, "Failed to send message of type $msgType to nanoapp: $ret")
        }
    }

    private fun onGestureDetected(msg: ContextHubMessages.GestureDetected) {
        Log.i(TAG, "Gesture detected hostSuspended=${msg.hostSuspended} hapticConsumed=${msg.hapticConsumed}")

        if (action.canRun()) {
            if (!msg.hapticConsumed) {
                vibrator.vibrate(vibEdgeRelease)
            }

            action.run()
            inGesture = false
        }
    }

    private fun onGestureProgress(msg: ContextHubMessages.GestureProgress) {
        // Ignore beginning and end points
        if (msg.progress < 0.49f || msg.progress > 0.99f) {
            inGesture = false
        } else if (!inGesture) {
            if (action.canRun()) {
                // Enter gesture and vibrate to indicate that
                inGesture = true
                vibrator.vibrate(vibEdgeSqueeze)
            } else {
                val now = SystemClock.elapsedRealtime()
                if (now - lastRejectTime >= REJECT_COOLDOWN_TIME) {
                    vibrator.vibrate(vibReject)
                    lastRejectTime = now
                }
            }
        }
    }

    private val chreCallback = object : ContextHubClientCallback() {
        override fun onMessageFromNanoApp(client: ContextHubClient, msg: NanoAppMessage) {
            // Ignore other nanoapps
            if (msg.nanoAppId != NANOAPP_ID) {
                return
            }

            when (msg.messageType) {
                MessageType.GESTURE_DETECTED.id -> {
                    val detectedMsg = ContextHubMessages.GestureDetected.parseFrom(msg.messageBody)
                    onGestureDetected(detectedMsg)
                }
                MessageType.GESTURE_PROGRESS.id -> {
                    val progressMsg = ContextHubMessages.GestureProgress.parseFrom(msg.messageBody)
                    onGestureProgress(progressMsg)
                }

                // Fallback for other unexpected messages
                else -> Log.w(TAG, "Received unknown message of type ${msg.messageType}: $msg")
            }
        }

        override fun onNanoAppAborted(client: ContextHubClient, nanoappId: Long, error: Int) {
            if (nanoappId == NANOAPP_ID) {
                Log.e(TAG, "Elmyra CHRE nanoapp aborted: $error")
            }
        }
    }

    private val screenCallback = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (enabled) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        Log.i(TAG, "Enabling gesture due to screen on")
                        enableGesture()
                    }
                    // Disable gesture entirely to save power
                    Intent.ACTION_SCREEN_OFF -> {
                        Log.i(TAG, "Disabling gesture due to screen off")
                        disableGesture()
                    }
                }
            }
        }
    }

    companion object {
        // Vibration effects from HapticFeedbackConstants
        // Duplicated because we can't use performHapticFeedback in a background service
        private val vibEdgeRelease = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
        private val vibEdgeSqueeze = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
        private val vibReject = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
    }
}
