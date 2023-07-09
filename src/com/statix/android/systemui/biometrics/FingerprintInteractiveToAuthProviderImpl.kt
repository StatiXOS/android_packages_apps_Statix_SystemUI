/*
 * Copyright (C) 2023 ArrowOS
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
 * limitations under the License.
 */

package com.statix.android.systemui.biometrics

import android.content.Context
import android.provider.Settings

import com.android.systemui.biometrics.FingerprintInteractiveToAuthProvider

import javax.inject.Inject

class FingerprintInteractiveToAuthProviderImpl @Inject constructor(private val context: Context) : FingerprintInteractiveToAuthProvider {

    private val defaultValue = if (context.getResources().getBoolean(
                com.android.internal.R.bool.config_performantAuthDefault)) 1 else 0

    override fun isEnabled(userId: Int): Boolean {
        var value = Settings.Secure.getIntForUser(context.contentResolver,
                Settings.Secure.SFPS_PERFORMANT_AUTH_ENABLED, -1, userId)
        if (value == -1) {
            value = defaultValue
            Settings.Secure.putIntForUser(context.contentResolver,
                    Settings.Secure.SFPS_PERFORMANT_AUTH_ENABLED, value, userId)
        }
        return value == 0
    }
}
