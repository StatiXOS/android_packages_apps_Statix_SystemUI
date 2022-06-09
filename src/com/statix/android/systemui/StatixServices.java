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

import com.google.android.systemui.ambientmusic.AmbientIndicationContainer;
import com.google.android.systemui.ambientmusic.AmbientIndicationService;
import com.google.android.systemui.columbus.ColumbusContext;
import com.google.android.systemui.columbus.ColumbusServiceWrapper;
import com.google.android.systemui.elmyra.ElmyraContext;
import com.google.android.systemui.elmyra.ElmyraService;
import com.google.android.systemui.elmyra.ServiceConfigurationGoogle;

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
    private final Lazy<ColumbusServiceWrapper> mColumbusServiceLazy;
    private final Lazy<ServiceConfigurationGoogle> mServiceConfigurationGoogle;
    private final ArrayList<Object> mServices = new ArrayList<>();

    @Inject
    public StatixServices(Context context, UiEventLogger uiEventLogger, Lazy<ServiceConfigurationGoogle> serviceConfigurationGoogle, Lazy<ColumbusServiceWrapper> columbusService, AlarmManager alarmManager, StatusBar statusBar) {
        super(context);
        mUiEventLogger = uiEventLogger;
        mServiceConfigurationGoogle = serviceConfigurationGoogle;
        mColumbusServiceLazy = columbusService;
        mAlarmManager = alarmManager;
        mStatusBar = statusBar;
    }

    @Override
    public void start() {
        AmbientIndicationContainer ambientIndicationContainer = mStatusBar.getNotificationShadeWindowView().findViewById(R.id.ambient_indication_container);
        ambientIndicationContainer.initializeView(mStatusBar);
        addService(new AmbientIndicationService(mContext, ambientIndicationContainer, mAlarmManager));
        if (mContext.getPackageManager().hasSystemFeature("android.hardware.context_hub") && new ElmyraContext(mContext).isAvailable()) {
            addService(new ElmyraService(mContext, mServiceConfigurationGoogle.get(), mUiEventLogger));
        }
        if (new ColumbusContext(mContext).isAvailable()) {
            addService(mColumbusServiceLazy.get());
        }
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
