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

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings

// Refer to elmyra/res/values/strings.xml for the source of the string values
val uriForEnabled = Settings.System.getUriFor("elmyra_enabled")
val uriForAction = Settings.System.getUriFor("elmyra_action")
val uriForScreenOff = Settings.System.getUriFor("elmyra_allow_screen_off")
val uriForSensitivity = Settings.System.getUriFor("elmyra_sensitivity")

fun Boolean.toInt() = if (this) 1 else 0

fun getEnabled(context: Context): Boolean {
    return getBoolean(context.contentResolver, context.getString(R.string.pref_key_enabled),
            context.resources.getBoolean(R.bool.default_enabled))
}

fun getAction(context: Context): String {
    val curAction = Settings.System.getString(context.contentResolver, context.getString(R.string.pref_key_action))
    return if (curAction == null) { context.getString(R.string.default_action) } else { curAction }
}

fun getAllowScreenOff(context: Context): Boolean {
    return getBoolean(context.contentResolver, context.getString(R.string.pref_key_allow_screen_off),
            context.resources.getBoolean(R.bool.default_allow_screen_off))
}

fun getSensitivity(context: Context): Int {
    return Settings.System.getInt(context.contentResolver, context.getString(R.string.pref_key_sensitivity),
            context.resources.getInteger(R.integer.default_sensitivity))
}

fun getActionName(context: Context): String {
    val actionNames = context.resources.getStringArray(R.array.action_names)
    val actionValues = context.resources.getStringArray(R.array.action_values)
    return actionNames[actionValues.indexOf(getAction(context))]
}

fun putBoolean(contentResolver: ContentResolver, key: String, value: Boolean) {
    Settings.System.putInt(contentResolver, key, value.toInt())
}

fun getBoolean(contentResolver: ContentResolver, key: String, def_value: Boolean): Boolean {
    return if (Settings.System.getInt(contentResolver, key, def_value.toInt()) == 1) { true } else { false }
}
