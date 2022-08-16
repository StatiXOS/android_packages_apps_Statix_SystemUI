/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui;

import android.app.AlarmManager;
import android.content.Context;

import com.android.internal.logging.UiEventLogger;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.VendorServices;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.flags.FeatureFlags;
import com.android.systemui.statusbar.phone.StatusBar;

import com.statix.android.systemui.smartpixels.SmartPixelsReceiver;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.Lazy;

@SysUISingleton
public class StatixServices extends VendorServices {

    private final AlarmManager mAlarmManager;
    private final StatusBar mStatusBar;
    private final UiEventLogger mUiEventLogger;
    private final ArrayList<Object> mServices = new ArrayList<>();

    @Inject
    public StatixServices(Context context, UiEventLogger uiEventLogger, AlarmManager alarmManager, StatusBar statusBar) {
        super(context);
        mUiEventLogger = uiEventLogger;
        mAlarmManager = alarmManager;
        mStatusBar = statusBar;
    }

    @Override
    public void start() {
        addService(new SmartPixelsReceiver(mContext));
    }

    @Override
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < mServices.size(); i++) {
            if (mServices.get(i) instanceof Dumpable) {
                ((Dumpable) mServices.get(i)).dump(fileDescriptor, printWriter, strArr);
            }
        }
    }

    private void addService(Object obj) {
        if (obj != null) {
            mServices.add(obj);
        }
    }

}
