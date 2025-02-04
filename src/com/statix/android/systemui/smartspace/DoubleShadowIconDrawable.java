package com.google.android.systemui.smartspace;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import com.android.internal.graphics.ColorUtils;
import com.android.wm.shell.R;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class DoubleShadowIconDrawable extends Drawable {
    public final int mAmbientShadowRadius;
    public final int mCanvasSize;
    public RenderNode mDoubleShadowNode;
    public InsetDrawable mIconDrawable;
    public final int mIconInsetSize;
    public final int mKeyShadowOffsetX;
    public final int mKeyShadowOffsetY;
    public final int mKeyShadowRadius;
    public boolean mShowShadow;

    public DoubleShadowIconDrawable(Context context) {
        this(context.getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_icon_size), context.getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_icon_inset), context);
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        RenderNode renderNode;
        if (canvas.isHardwareAccelerated() && (renderNode = this.mDoubleShadowNode) != null && this.mShowShadow) {
            if (!renderNode.hasDisplayList()) {
                this.mIconDrawable.draw(this.mDoubleShadowNode.beginRecording());
                this.mDoubleShadowNode.endRecording();
            }
            canvas.drawRenderNode(this.mDoubleShadowNode);
        }
        InsetDrawable insetDrawable = this.mIconDrawable;
        if (insetDrawable != null) {
            insetDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        return this.mCanvasSize;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        return this.mCanvasSize;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i) {
        this.mIconDrawable.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        this.mIconDrawable.setColorFilter(colorFilter);
    }

    public final void setIcon(Drawable drawable) {
        RenderNode renderNode = null;
        if (drawable == null) {
            this.mIconDrawable = null;
            return;
        }
        InsetDrawable insetDrawable = new InsetDrawable(drawable, this.mIconInsetSize);
        this.mIconDrawable = insetDrawable;
        int i = this.mCanvasSize;
        insetDrawable.setBounds(0, 0, i, i);
        if (this.mIconDrawable != null) {
            RenderNode renderNode2 = new RenderNode("DoubleShadowNode");
            int i2 = this.mCanvasSize;
            renderNode2.setPosition(0, 0, i2, i2);
            int i3 = this.mAmbientShadowRadius;
            int argb = Color.argb(48, 0, 0, 0);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(argb, mode);
            float f = 0;
            float f2 = i3;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            RenderEffect createColorFilterEffect = RenderEffect.createColorFilterEffect(porterDuffColorFilter, RenderEffect.createOffsetEffect(f, f, RenderEffect.createBlurEffect(f2, f2, tileMode)));
            float f3 = this.mKeyShadowRadius;
            RenderEffect createColorFilterEffect2 = RenderEffect.createColorFilterEffect(new PorterDuffColorFilter(Color.argb(72, 0, 0, 0), mode), RenderEffect.createOffsetEffect(this.mKeyShadowOffsetX, this.mKeyShadowOffsetY, RenderEffect.createBlurEffect(f3, f3, tileMode)));
            if (createColorFilterEffect != null && createColorFilterEffect2 != null) {
                renderNode2.setRenderEffect(RenderEffect.createBlendModeEffect(createColorFilterEffect, createColorFilterEffect2, BlendMode.DARKEN));
                renderNode = renderNode2;
            }
        }
        this.mDoubleShadowNode = renderNode;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTint(int i) {
        InsetDrawable insetDrawable = this.mIconDrawable;
        if (insetDrawable != null) {
            insetDrawable.setTint(i);
        }
        this.mShowShadow = ColorUtils.calculateLuminance(i) > 0.5d;
    }

    public DoubleShadowIconDrawable(int i, int i2, Context context) {
        this.mShowShadow = true;
        this.mIconInsetSize = i2;
        int i3 = (i2 * 2) + i;
        this.mCanvasSize = i3;
        this.mAmbientShadowRadius = context.getResources().getDimensionPixelSize(R.dimen.ambient_text_shadow_radius);
        this.mKeyShadowRadius = context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_radius);
        this.mKeyShadowOffsetX = context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_dx);
        this.mKeyShadowOffsetY = context.getResources().getDimensionPixelSize(R.dimen.key_text_shadow_dy);
        setBounds(0, 0, i3, i3);
    }
}
