package com.google.android.systemui.smartspace;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class CheckLongPressHelper {
    private boolean mHasPerformedLongPress;
    private Runnable mPendingCheckForLongPress;
    private final float mSlop;
    private final View mView;

    public CheckLongPressHelper(View view) {
        mView = view;
        mSlop = (float) ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
    }

    private void clearCallbacks() {
        Runnable runnable = mPendingCheckForLongPress;
        if (runnable != null) {
            mView.removeCallbacks(runnable);
            mPendingCheckForLongPress = null;
        }
    }

    private static boolean isStylusButtonPressed(MotionEvent motionEvent) {
        return motionEvent.getToolType(0) == 2 && motionEvent.isButtonPressed(2);
    }

    private static boolean pointInView(View view, float f, float f2, float f3) {
        float f4 = -f3;
        return f >= f4 && f2 >= f4 && f < ((float) view.getWidth()) + f3 && f2 < ((float) view.getHeight()) + f3;
    }

    private void postCheckForLongPress() {
        mHasPerformedLongPress = false;
        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = new Runnable() {
                @Override
                public final void run() {
                    triggerLongPress();
		}
            };
        }
        mView.postDelayed(mPendingCheckForLongPress, (long) ViewConfiguration.getLongPressTimeout());
    }

    private void triggerLongPress() {
        if (mView.getParent() != null && mView.hasWindowFocus() && !mView.isPressed() && !mHasPerformedLongPress) {
            if (mView.performLongClick()) {
                mView.setPressed(false);
                mHasPerformedLongPress = true;
            }
            clearCallbacks();
        }
    }

    public void cancelLongPress() {
        mHasPerformedLongPress = false;
        clearCallbacks();
    }

    public boolean hasPerformedLongPress() {
        return mHasPerformedLongPress;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action != 2) {
                    if (action != 3) {
                        return;
                    }
                } else if (!pointInView(mView, motionEvent.getX(), motionEvent.getY(), mSlop)) {
                    cancelLongPress();
                    return;
                } else if (mPendingCheckForLongPress != null && isStylusButtonPressed(motionEvent)) {
                    triggerLongPress();
                    return;
                } else {
                    return;
                }
            }
            cancelLongPress();
            return;
        }
        cancelLongPress();
        postCheckForLongPress();
        if (isStylusButtonPressed(motionEvent)) {
            triggerLongPress();
        }
    }
}
