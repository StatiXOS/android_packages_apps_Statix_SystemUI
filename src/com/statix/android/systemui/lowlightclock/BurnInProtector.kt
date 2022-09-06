package com.statix.android.systemui.lowlightclock

import android.view.View
import android.util.MathUtils

import com.android.systemui.doze.util.getBurnInOffset

import com.statix.android.systemui.lowlightclock.dagger.LowLightModule.LOW_LIGHT_MAX_BURN_IN_OFFSET
import com.statix.android.systemui.lowlightclock.dagger.LowLightModule.LOW_LIGHT_TIME_UNTIL_FULL_JITTER_MILLIS

import javax.inject.Inject
import javax.inject.Named

class BurnInProtector @Inject constructor(
    @Named(LOW_LIGHT_MAX_BURN_IN_OFFSET) private val maxBurnInOffset: Int,
    @Named(LOW_LIGHT_TIME_UNTIL_FULL_JITTER_MILLIS) private val timeUntilFullJitterMillis: Long,
) {

    private var view: View? = null
    private var jitterStartTimeMillis: Long = 0

    fun startProtection(view: View) {
        this.view = view
        this.jitterStartTimeMillis = System.currentTimeMillis()
        updateViewProtection()
    }

    fun stopProtection() {
        view = null
    }

    fun updateViewProtection() {
        if (view != null) {
            val currentTimeMillis = System.currentTimeMillis() - jitterStartTimeMillis;
            val burnInOffsetSubtract = 2 * if (currentTimeMillis < timeUntilFullJitterMillis) {
                Math.round(MathUtils.lerp(0.0f, (maxBurnInOffset.toFloat()), ((currentTimeMillis/timeUntilFullJitterMillis).toFloat())));
            } else {
                maxBurnInOffset;
            }
            val burnInOffsetX = getBurnInOffset(burnInOffsetSubtract, true) - burnInOffsetSubtract;
            val burnInOffsetY = getBurnInOffset(burnInOffsetSubtract, false) - burnInOffsetSubtract;
            view!!.setTranslationX(burnInOffsetX.toFloat());
            view!!.setTranslationY(burnInOffsetY.toFloat());
        }
    }

}
