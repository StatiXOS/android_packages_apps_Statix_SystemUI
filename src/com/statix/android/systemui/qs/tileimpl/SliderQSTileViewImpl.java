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

package com.statix.android.systemui.qs.tileimpl;

import static android.service.quicksettings.Tile.STATE_ACTIVE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileViewImpl;

public class SliderQSTileViewImpl extends QSTileViewImpl implements TouchableQSTileImpl.Callback {

    private PercentageDrawable percentageDrawable;

    public SliderQSTileViewImpl(Context context, QSIconView icon, boolean collapsed, View.OnTouchListener touchListener) {
        super(context, icon, collapsed);
        percentageDrawable = new PercentageDrawable();
        percentageDrawable.setAlpha(64);
        updatePercentBackground(false /* default */);
        setOnTouchListener(touchListener);
    }
    
    @Override
    public void handleStateChanged(QSTile.State state) {
        super.handleStateChanged(state);
        updatePercentBackground(state.state == STATE_ACTIVE);
    }

    @Override
    public void onPercentUpdated(float newPercent) {
        percentageDrawable.updatePercent(newPercent);
        updatePercentBackground(true);
    }

    private void updatePercentBackground(boolean active) {
        percentageDrawable.setTint(active ? Color.WHITE : Color.BLACK);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorBackgroundDrawable, percentageDrawable});
        setBackground(layerDrawable);
    }

    private class PercentageDrawable extends Drawable {
        private Drawable shape;
        private float mCurrentPercent;

        private PercentageDrawable() {
            shape = mContext.getDrawable(R.drawable.qs_tile_background_shape);
        }
        
        void updatePercent(float newPercent) {
            mCurrentPercent = newPercent;
        }

        @Override
        public void setBounds(Rect bounds) {
			shape.setBounds(bounds);
        }

        @Override
        public void setBounds(int a, int b, int c, int d) {
            shape.setBounds(a, b, c, d);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            android.util.Log.d("PercentageDrawable", "mCurrentPercent: " + mCurrentPercent);
            Bitmap bitmap = Bitmap.createBitmap(Math.round(shape.getBounds().width() * mCurrentPercent), shape.getBounds().height(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(bitmap);
            shape.draw(tempCanvas);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
        }

        @Override
        public void setAlpha(int i) {
            shape.setAlpha(i);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            shape.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }

        @Override
        public void setTint(int t) {
            shape.setTint(t);
        }
    }
}
