package com.google.android.systemui.smartspace;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

import com.statix.android.systemui.res.R;

public class DoubleShadowTextView extends TextView {
    public final float mAmbientShadowBlur;
    public final int mAmbientShadowColor;
    public boolean mDrawShadow;
    public final float mKeyShadowBlur;
    public final int mKeyShadowColor;
    public final float mKeyShadowOffsetX;
    public final float mKeyShadowOffsetY;

    public DoubleShadowTextView(Context context) {
        this(context, null);
    }

    public DoubleShadowTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DoubleShadowTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDrawShadow = ColorUtils.calculateLuminance(getCurrentTextColor()) > 0.5d;
        this.mKeyShadowBlur =
                context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_radius);
        this.mKeyShadowOffsetX =
                context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_dx);
        this.mKeyShadowOffsetY =
                context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_dy);
        this.mKeyShadowColor = context.getResources().getColor(R.color.key_text_shadow_color);
        this.mAmbientShadowBlur =
                context.getResources().getDimensionPixelSize(R.dimen.ambient_text_shadow_radius);
        this.mAmbientShadowColor =
                context.getResources().getColor(R.color.ambient_text_shadow_color);
    }

    @Override
    public final void onDraw(Canvas canvas) {
        if (!this.mDrawShadow) {
            getPaint().clearShadowLayer();
            super.onDraw(canvas);
            return;
        }
        getPaint().setShadowLayer(this.mAmbientShadowBlur, 0.0f, 0.0f, this.mAmbientShadowColor);
        super.onDraw(canvas);
        canvas.save();
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int extendedPaddingTop = getExtendedPaddingTop();
        int scrollX2 = getScrollX();
        canvas.clipRect(
                scrollX,
                extendedPaddingTop + scrollY,
                getWidth() + scrollX2,
                getHeight() + getScrollY());
        getPaint()
                .setShadowLayer(
                        this.mKeyShadowBlur,
                        this.mKeyShadowOffsetX,
                        this.mKeyShadowOffsetY,
                        this.mKeyShadowColor);
        super.onDraw(canvas);
        canvas.restore();
    }

    @Override
    public final void setTextColor(int i) {
        super.setTextColor(i);
        this.mDrawShadow = ColorUtils.calculateLuminance(i) > 0.5d;
    }
}
