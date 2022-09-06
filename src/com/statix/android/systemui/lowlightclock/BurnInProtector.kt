package com.statix.android.systemui.lowlightclock

import android.view.View

import javax.inject.Inject

class BurnInProtector @Inject constructor(private val maxBurnInOffset: Long, private val timeUntilFullJitterMs: Long) {

    var view: View? = null
    var jitterStartTimeMillis: Long = 0

}
