/*
 * Copyright (C) 2024 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.screenrecord

import android.content.res.Resources
import com.android.systemui.res.R

open class StatixRecordingServiceStrings(private val res: Resources) {
    open val title
        get() = res.getString(R.string.screenrecord_title)
    open val saveTitle
        get() = res.getString(R.string.screenrecord_save_title)

    val saveText
        get() = res.getString(R.string.screenrecord_save_text)
    val shareLabel
        get() = res.getString(R.string.screenrecord_share_label)
    val deleteLabel
        get() = res.getString(R.string.screenrecord_delete_label)
}
