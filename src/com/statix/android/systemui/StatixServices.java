/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui;

import android.app.AlarmManager;
import android.content.Context;

import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.VendorServices;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.shade.NotificationShadeWindowView;
import com.android.systemui.shade.ShadeViewController;
import com.android.systemui.statusbar.phone.CentralSurfaces;
import com.android.systemui.statusbar.policy.FlashlightController;

import com.statix.android.systemui.ambient.AmbientIndicationContainer;
import com.statix.android.systemui.ambient.AmbientIndicationService;
import com.statix.android.systemui.elmyra.ElmyraService;
import com.statix.android.systemui.smartpixels.SmartPixelsReceiver;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.inject.Inject;

@SysUISingleton
public class StatixServices extends VendorServices {

    private final ArrayList<Object> mServices = new ArrayList<>();
    private final AlarmManager mAlarmManager;
    private final AssistManager mAssistManager;
    private final ShadeViewController mShadeViewController;
    private final NotificationShadeWindowView mNotificationShadeWindowView;
    private final Context mContext;
    private final FlashlightController mFlashlightController;

    @Inject
    public StatixServices(
            Context context,
            AlarmManager alarmManager,
            AssistManager assistManager,
            FlashlightController flashlightController,
            ShadeViewController shadeViewController,
            NotificationShadeWindowView notificationShadeWindowView) {
        super();
        mAlarmManager = alarmManager;
        mAssistManager = assistManager;
        mContext = context;
        mFlashlightController = flashlightController;
        mShadeViewController = shadeViewController;
        mNotificationShadeWindowView = notificationShadeWindowView;
    }

    @Override
    public void start() {
        addService(new SmartPixelsReceiver(mContext));
        if (mContext.getPackageManager().hasSystemFeature("android.hardware.context_hub")
                && mContext.getPackageManager()
                .hasSystemFeature("android.hardware.sensor.assist")) {
            addService(new ElmyraService(mContext, mAssistManager, mFlashlightController));
        }
        AmbientIndicationContainer ambientIndicationContainer =
                (AmbientIndicationContainer)
                        mNotificationShadeWindowView
                                .findViewById(R.id.ambient_indication_container);
        ambientIndicationContainer.initializeView(mShadeViewController);
        addService(
                new AmbientIndicationService(mContext, ambientIndicationContainer, mAlarmManager));
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
