/*
 * Copyright (C) 2020 The Proton AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package com.statix.android.systemui.elmyra.actions

import android.content.Context
import android.telecom.TelecomManager
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager.CALL_STATE_RINGING

import com.android.systemui.telephony.TelephonyListenerManager

class SilenceCallAction(
    context: Context,
    private val telephonyListenerManager: TelephonyListenerManager)
: Action(context) {

    private var isRinging: Boolean
    private val telecomManager = context.getSystemService(TelecomManager::class.java)
    
    init {
        isRinging = telecomManager.callState == CALL_STATE_RINGING
        telephonyListenerManager.addCallStateListener(object : TelephonyCallback.CallStateListener {
            override fun onCallStateChanged(state: Int) {
                val ringing = state == CALL_STATE_RINGING
                if (ringing != isRinging) {
                    isRinging = ringing
                }
            }
         })
    }

    override fun canRun() = isRinging

    override fun run() = telecomManager.silenceRinger()

}
