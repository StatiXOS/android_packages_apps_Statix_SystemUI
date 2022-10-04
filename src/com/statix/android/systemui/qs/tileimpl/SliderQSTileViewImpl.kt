/*
 * Copyright (C) 2022 StatiXOS
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
 * limitations under the License.
 */

package com.statix.android.systemui.qs.tileimpl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.view.View

import com.android.systemui.R
import com.android.systemui.plugins.qs.QSIconView
import com.android.systemui.plugins.qs.QSTile
import com.android.systemui.qs.tileimpl.QSTileViewImpl

import kotlin.math.roundToInt

class SliderQSTileViewImpl constructor(context: Context,
    _icon: QSIconView,
    collapsed: Boolean = false,
    touchListener: View.OnTouchListener
) : QSTileViewImpl(context, _icon, collapsed), TouchableQSTileImpl.Callback {

    private var currentPercent = 0f
    private var percentageDrawable: PercentageDrawable

    init {
        percentageDrawable = PercentageDrawable()
        percentageDrawable.setAlpha(64)
        setOnTouchListener(touchListener)
    }

    override fun onStateChanged(state: QSTile.State) {
        post {
            handleStateChanged(state)
            updatePercentBackground(state.state == STATE_ACTIVE)
        }
    }

    override fun onPercentUpdated(newPercent: Float) = updatePercent(newPercent)

    fun updatePercentBackground(active: Boolean) {
        percentageDrawable.setTint(if (active) { Color.WHITE } else { Color.BLACK })
        if (background is LayerDrawable) {
            val layerDrawable = LayerDrawable(arrayOf(colorBackgroundDrawable, percentageDrawable)) 
            background = layerDrawable
        }
    }

    private fun updatePercent(newPercent: Float) {
        android.util.Log.d("SliderQSTileViewImpl", "updatePercent called with newPercent: ${newPercent}")
        currentPercent = newPercent
        updatePercentBackground(true)
    }

    private inner class PercentageDrawable : Drawable() {
        private var shape: Drawable

        init {
            shape = mContext.getDrawable(R.drawable.qs_tile_background_shape)
        }

        override fun draw(canvas: Canvas) {
            val width = (shape.getBounds().width().toFloat() * currentPercent).roundToInt()
            android.util.Log.d("SliderQSTileViewImpl", width.toString());
            android.util.Log.d("SliderQSTileViewImpl", "width: ${shape.getBounds().width().toFloat()}");
            android.util.Log.d("SliderQSTileViewImpl", "currentPercent: ${currentPercent}");
            if (width == 0 || shape.getBounds().height() == 0) {
                return
            }
            val bmp = Bitmap.createBitmap(width, shape.getBounds().height(), Bitmap.Config.ARGB_8888)
            val tmpCanvas = Canvas(bmp)
            shape.draw(tmpCanvas)
            canvas.drawBitmap(bmp, 0.0f, 0.0f, Paint())
        }

        override fun setBounds(bounds: Rect) = shape.setBounds(bounds)
        override fun setBounds(a: Int, b: Int, c: Int, d: Int) = shape.setBounds(a, b, c, d)
        override fun setAlpha(alpha: Int) = shape.setAlpha(alpha)
        override fun setColorFilter(colorFilter: ColorFilter?) = shape.setColorFilter(colorFilter)
        override fun getOpacity(): Int = PixelFormat.UNKNOWN
        override fun setTint(tint: Int) = shape.setTint(tint)
    }
}
