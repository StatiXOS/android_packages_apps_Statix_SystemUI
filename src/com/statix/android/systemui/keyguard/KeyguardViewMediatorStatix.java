/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */
package com.statix.android.systemui.keyguard;

import android.content.Context;
import android.os.PowerManager;
import android.view.GestureDetector;
import android.view.View.OnTouchListener;
import com.android.systemui.keyguard.KeyguardViewMediator;

public class KeyguardViewMediatorStatix extends KeyguardViewMediator 
        implements OnTouchListener {
    
    private final PowerManager mPm;

    public KeyguardSliceProviderStatix() {
        super();
        mPm = (PowerManager) workspace.getContext().getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        mPm.goToSleep(event.getEventTime());
        return true;
    }
}