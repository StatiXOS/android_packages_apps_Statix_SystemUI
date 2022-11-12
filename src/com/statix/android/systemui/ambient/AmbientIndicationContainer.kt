package com.statix.android.systemui.ambient

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

import com.android.systemui.AutoReinflateContainer
import com.android.systemui.R

class AmbientIndicationContainer(context: Context, attrs: AttributeSet) : AutoReinflateContainer(context, attrs) {

    public fun setReverseChargingMessage(str: String, visible: Boolean) {
        if (TextUtils.isEmpty(str)) {
            return
        }
        findViewById<TextView>(R.id.ambient_indication_text).let {
            it.text = str
            it.visibility = if (visible) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

}
