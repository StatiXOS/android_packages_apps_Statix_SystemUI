package com.google.android.systemui.smartspace;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.content.res.AppCompatResources;
import com.android.wm.shell.R;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class PageIndicator extends LinearLayout {
    public int mCurrentPageIndex;
    public int mNumPages;
    public int mPrimaryColor;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public final void setNumPages(int i, boolean z) {
        if (i <= 0) {
            Log.w("PageIndicator", "Total number of pages invalid: " + i + ". Assuming 1 page.");
            i = 1;
        }
        if (i < 2) {
            BcSmartspaceTemplateDataUtils.updateVisibility(this, 8);
            return;
        }
        BcSmartspaceTemplateDataUtils.updateVisibility(this, 0);
        if (i != this.mNumPages) {
            this.mNumPages = i;
            int i2 = this.mCurrentPageIndex;
            if (i2 < 0) {
                this.mCurrentPageIndex = z ? i - 1 : 0;
            } else if (i2 >= i) {
                this.mCurrentPageIndex = z ? 0 : i - 1;
            }
            int childCount = getChildCount() - this.mNumPages;
            for (int i3 = 0; i3 < childCount; i3++) {
                removeViewAt(0);
            }
            int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.page_indicator_dot_margin);
            int i4 = 0;
            while (i4 < this.mNumPages) {
                ImageView imageView = i4 < getChildCount() ? (ImageView) getChildAt(i4) : new ImageView(getContext());
                LinearLayout.LayoutParams layoutParams = i4 < getChildCount() ? (LinearLayout.LayoutParams) imageView.getLayoutParams() : new LinearLayout.LayoutParams(-2, -2);
                if (i4 == 0) {
                    layoutParams.setMarginStart(0);
                } else {
                    layoutParams.setMarginStart(dimensionPixelSize);
                }
                if (i4 == this.mNumPages - 1) {
                    layoutParams.setMarginEnd(0);
                } else {
                    layoutParams.setMarginEnd(dimensionPixelSize);
                }
                if (i4 < getChildCount()) {
                    imageView.setLayoutParams(layoutParams);
                } else {
                    Drawable drawable = AppCompatResources.getDrawable(R.drawable.page_indicator_dot, getContext());
                    drawable.setTint(this.mPrimaryColor);
                    imageView.setImageDrawable(drawable);
                    addView(imageView, layoutParams);
                }
                imageView.setAlpha(i4 == this.mCurrentPageIndex ? 1.0f : 0.4f);
                i4++;
            }
            setContentDescription(getContext().getString(R.string.accessibility_smartspace_page, 1, Integer.valueOf(this.mNumPages)));
        }
    }

    public final void setPageOffset(int i, float f) {
        if (!(f == 0.0f && i == this.mCurrentPageIndex) && i >= 0 && i < getChildCount()) {
            ImageView imageView = (ImageView) getChildAt(i);
            int i2 = i + 1;
            ImageView imageView2 = (ImageView) getChildAt(i2);
            if (f == 0.0f || f >= 0.99f) {
                int i3 = this.mCurrentPageIndex;
                if (i3 >= 0 && i3 < getChildCount()) {
                    getChildAt(this.mCurrentPageIndex).setAlpha(0.4f);
                }
                this.mCurrentPageIndex = f == 0.0f ? i : i2;
            }
            imageView.setAlpha(((1.0f - f) * 0.6f) + 0.4f);
            if (imageView2 != null) {
                imageView2.setAlpha((0.6f * f) + 0.4f);
            }
            Context context = getContext();
            if (f >= 0.5d) {
                i2 = i + 2;
            }
            setContentDescription(context.getString(R.string.accessibility_smartspace_page, Integer.valueOf(i2), Integer.valueOf(this.mNumPages)));
        }
    }

    public PageIndicator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PageIndicator(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public PageIndicator(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
        int color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        this.mPrimaryColor = color;
        this.mCurrentPageIndex = -1;
        this.mNumPages = -1;
    }
}
