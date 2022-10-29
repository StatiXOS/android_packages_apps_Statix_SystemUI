/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui;

import android.content.Context;

import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.VendorServices;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.flags.FeatureFlags;
import com.android.systemui.statusbar.policy.FlashlightController;

import com.statix.android.systemui.elmyra.ElmyraService;
import com.statix.android.systemui.smartpixels.SmartPixelsReceiver;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.Lazy;

@SysUISingleton
public class StatixServices extends VendorServices {

    private final ArrayList<Object> mServices = new ArrayList<>();
    private AssistManager mAssistManager;
    private FlashlightController mFlashlightController;

    @Inject
    public StatixServices(Context context, AssistManager assistManager, FlashlightController flashlightController) {
        super(context);
        mAssistManager = assistManager;
        mFlashlightController = flashlightController;
    }

    @Override
    public void start() {
        addService(new SmartPixelsReceiver(mContext));
        if (mContext.getPackageManager().hasSystemFeature("android.hardware.context_hub") && mContext.getPackageManager().hasSystemFeature("android.hardware.sensor.assist")) {
            addService(new ElmyraService(mContext, mAssistManager, mFlashlightController));
        }
    }

    @Override
    public void dump(PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < mServices.size(); i++) {
            if (mServices.get(i) instanceof Dumpable) {
                ((Dumpable) mServices.get(i)).dump(printWriter, strArr);
            }
        }
    }

    private void addService(Object obj) {
        if (obj != null) {
            mServices.add(obj);
        }
    }

}
