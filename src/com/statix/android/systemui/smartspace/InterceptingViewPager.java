package com.google.android.systemui.smartspace;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.viewpager.widget.ViewPager;
import com.android.wm.shell.R;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class InterceptingViewPager extends ViewPager {
    public boolean mHasPerformedLongPress;
    public boolean mHasPostedLongPress;
    public final Runnable mLongPressCallback;
    public final InterceptingViewPager$$ExternalSyntheticLambda0 mSuperOnIntercept;
    public final InterceptingViewPager$$ExternalSyntheticLambda0 mSuperOnTouch;

    public InterceptingViewPager(Context context) {
        super(context);
        this.mSuperOnTouch = new InterceptingViewPager$$ExternalSyntheticLambda0(this, 0);
        this.mSuperOnIntercept = new InterceptingViewPager$$ExternalSyntheticLambda0(this, 1);
        this.mLongPressCallback = new Runnable() { // from class: com.google.android.systemui.smartspace.InterceptingViewPager$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                InterceptingViewPager interceptingViewPager = InterceptingViewPager.this;
                interceptingViewPager.mHasPerformedLongPress = true;
                if (interceptingViewPager.performLongClick()) {
                    interceptingViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        };
    }

    public final void cancelScheduledLongPress() {
        if (this.mHasPostedLongPress) {
            this.mHasPostedLongPress = false;
            removeCallbacks(this.mLongPressCallback);
        }
    }

    @Override // android.view.View
    public final AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityNodeInfo createAccessibilityNodeInfo = super.createAccessibilityNodeInfo();
        createAccessibilityNodeInfo.getExtras().putCharSequence("AccessibilityNodeInfo.roleDescription", getContext().getString(R.string.smartspace_role_desc));
        return createAccessibilityNodeInfo;
    }

    public final boolean handleTouchOverride(MotionEvent motionEvent, InterceptingViewPager$$ExternalSyntheticLambda0 interceptingViewPager$$ExternalSyntheticLambda0) {
        boolean onTouchEvent;
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mHasPerformedLongPress = false;
            if (isLongClickable()) {
                cancelScheduledLongPress();
                this.mHasPostedLongPress = true;
                postDelayed(this.mLongPressCallback, ViewConfiguration.getLongPressTimeout());
            }
        } else if (action == 1 || action == 3) {
            cancelScheduledLongPress();
        }
        if (this.mHasPerformedLongPress) {
            cancelScheduledLongPress();
            return true;
        }
        int i = interceptingViewPager$$ExternalSyntheticLambda0.$r8$classId;
        InterceptingViewPager interceptingViewPager = interceptingViewPager$$ExternalSyntheticLambda0.f$0;
        switch (i) {
            case 0:
                onTouchEvent = super.onTouchEvent(motionEvent);
                break;
            default:
                onTouchEvent = super.onInterceptTouchEvent(motionEvent);
                break;
        }
        if (!onTouchEvent) {
            return false;
        }
        cancelScheduledLongPress();
        return true;
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return handleTouchOverride(motionEvent, this.mSuperOnIntercept);
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        return handleTouchOverride(motionEvent, this.mSuperOnTouch);
    }

    public InterceptingViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSuperOnTouch = new InterceptingViewPager$$ExternalSyntheticLambda0(this, 0);
        this.mSuperOnIntercept = new InterceptingViewPager$$ExternalSyntheticLambda0(this, 1);
        this.mLongPressCallback = new Runnable() { // from class: com.google.android.systemui.smartspace.InterceptingViewPager$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                InterceptingViewPager interceptingViewPager = InterceptingViewPager.this;
                interceptingViewPager.mHasPerformedLongPress = true;
                if (interceptingViewPager.performLongClick()) {
                    interceptingViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        };
    }
}
