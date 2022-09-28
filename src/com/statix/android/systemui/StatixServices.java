/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui;

import android.content.Context;

import com.android.systemui.Dumpable;
import com.android.systemui.VendorServices;
import com.android.systemui.dagger.SysUISingleton;

import com.statix.android.systemui.face.FaceNotificationService;
import com.statix.android.systemui.smartpixels.SmartPixelsReceiver;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.inject.Inject;

@SysUISingleton
public class StatixServices extends VendorServices {

    private final ArrayList<Object> mServices = new ArrayList<>();

    @Inject
    public StatixServices(Context context) {
        super(context);
    }

    @Override
    public void start() {
        addService(new SmartPixelsReceiver(mContext));
        if (mContext.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            addService(new FaceNotificationService(mContext));
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
