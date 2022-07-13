package com.google.android.systemui.smartspace;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.viewpager.widget.ViewPager;
/* loaded from: classes2.dex */
public class InterceptingViewPager extends ViewPager {
    private boolean mHasPerformedLongPress;
    private boolean mHasPostedLongPress;
    private final EventProxy mSuperOnTouch = new EventProxy() { // from class: com.google.android.systemui.smartspace.InterceptingViewPager..ExternalSyntheticLambda1
        @Override // com.google.android.systemui.smartspace.InterceptingViewPager.EventProxy
        public final boolean delegateEvent(MotionEvent motionEvent) {
            return InterceptingViewPager.super.onTouchEvent(motionEvent);
        }
    };
    private final EventProxy mSuperOnIntercept = new EventProxy() { // from class: com.google.android.systemui.smartspace.InterceptingViewPager..ExternalSyntheticLambda0
        @Override // com.google.android.systemui.smartspace.InterceptingViewPager.EventProxy
        public final boolean delegateEvent(MotionEvent motionEvent) {
            return InterceptingViewPager.super.onInterceptTouchEvent(motionEvent);
        }
    };
    private final Runnable mLongPressCallback = new Runnable() { // from class: com.google.android.systemui.smartspace.InterceptingViewPager..ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            triggerLongPress();
        }
    };

    /* loaded from: classes2.dex */
    public interface EventProxy {
        boolean delegateEvent(MotionEvent motionEvent);
    }

    public InterceptingViewPager(Context context) {
        super(context);
    }

    public InterceptingViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return handleTouchOverride(motionEvent, mSuperOnIntercept);
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return handleTouchOverride(motionEvent, mSuperOnTouch);
    }

    private boolean handleTouchOverride(MotionEvent motionEvent, EventProxy eventProxy) {
        int action = motionEvent.getAction();
        if (action == 0) {
            mHasPerformedLongPress = false;
            if (isLongClickable()) {
                cancelScheduledLongPress();
                mHasPostedLongPress = true;
                postDelayed(mLongPressCallback, ViewConfiguration.getLongPressTimeout());
            }
        } else if (action == 1 || action == 3) {
            cancelScheduledLongPress();
        }
        if (mHasPerformedLongPress) {
            cancelScheduledLongPress();
            return true;
        } else if (!eventProxy.delegateEvent(motionEvent)) {
            return false;
        } else {
            cancelScheduledLongPress();
            return true;
        }
    }

    private void cancelScheduledLongPress() {
        if (mHasPostedLongPress) {
            mHasPostedLongPress = false;
            removeCallbacks(mLongPressCallback);
        }
    }

    public void triggerLongPress() {
        mHasPerformedLongPress = true;
        if (performLongClick()) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }
}
