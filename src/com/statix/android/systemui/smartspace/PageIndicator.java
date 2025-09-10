package com.google.android.systemui.smartspace;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.statix.android.systemui.res.R;

public class PageIndicator extends LinearLayout {
    public int mCurrentPageIndex = -1;
    public int mNumPages = -1;
    public int mPrimaryColor;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(new int[] {android.R.attr.textColorPrimary});
        mPrimaryColor = a.getColor(0, 0);
        a.recycle();
    }

    public void setNumPages(int numPages, boolean fromEnd) {
        if (numPages <= 0) {
            Log.w(
                    "PageIndicator",
                    "Total number of pages invalid: " + numPages + ". Assuming 1 page.");
            numPages = 1;
        }

        if (numPages < 2) {
            BcSmartspaceTemplateDataUtils.updateVisibility(this, View.GONE);
            return;
        }

        if (getVisibility() != View.INVISIBLE) {
            BcSmartspaceTemplateDataUtils.updateVisibility(this, View.VISIBLE);
        }

        if (numPages != mNumPages) {
            mNumPages = numPages;
            if (mCurrentPageIndex < 0) {
                mCurrentPageIndex = fromEnd ? numPages - 1 : 0;
            } else if (mCurrentPageIndex >= numPages) {
                mCurrentPageIndex = fromEnd ? 0 : numPages - 1;
            }

            int currentChildCount = getChildCount();
            int delta = currentChildCount - numPages;
            for (int i = 0; i < delta; i++) {
                removeViewAt(0);
            }

            int dotMargin = getResources().getDimensionPixelSize(R.dimen.page_indicator_dot_margin);
            for (int i = 0; i < numPages; i++) {
                ImageView dot;
                LayoutParams params;
                if (i < getChildCount()) {
                    dot = (ImageView) getChildAt(i);
                    params = (LayoutParams) dot.getLayoutParams();
                } else {
                    dot = new ImageView(getContext());
                    params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                }

                params.setMarginStart(i == 0 ? 0 : dotMargin);
                params.setMarginEnd(i == numPages - 1 ? 0 : dotMargin);

                if (i >= getChildCount()) {
                    dot.setImageDrawable(
                            getContext().getDrawable(R.drawable.page_indicator_dot).mutate());
                    dot.getDrawable().setTint(mPrimaryColor);
                    addView(dot, params);
                } else {
                    dot.setLayoutParams(params);
                }

                dot.setAlpha(i == mCurrentPageIndex ? 1.0f : 0.4f);
            }

            setContentDescription(
                    getContext().getString(R.string.accessibility_smartspace_page, 1, numPages));
        }
    }

    public void setPageOffset(float offset, int position) {
        if (offset == 0.0f && position == mCurrentPageIndex) {
            return;
        }

        if (position < 0 || position >= getChildCount()) {
            return;
        }

        ImageView currentDot = (ImageView) getChildAt(position);
        ImageView nextDot = (ImageView) getChildAt(position + 1);

        if (offset == 0.0f || offset >= 0.99f) {
            if (mCurrentPageIndex >= 0 && mCurrentPageIndex < getChildCount()) {
                ((ImageView) getChildAt(mCurrentPageIndex)).setAlpha(0.4f);
            }
            mCurrentPageIndex = offset == 0.0f ? position : position + 1;
        }

        float alphaCurrent = (1.0f - offset) * 0.6f + 0.4f;
        currentDot.setAlpha(alphaCurrent);

        if (nextDot != null) {
            float alphaNext = offset * 0.6f + 0.4f;
            nextDot.setAlpha(alphaNext);
        }

        int displayPosition = offset < 0.5 ? position + 1 : position + 2;
        setContentDescription(
                getContext()
                        .getString(
                                R.string.accessibility_smartspace_page,
                                displayPosition,
                                mNumPages));
    }
}
