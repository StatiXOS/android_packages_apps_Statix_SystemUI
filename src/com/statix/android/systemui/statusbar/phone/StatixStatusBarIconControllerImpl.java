package com.statix.android.systemui.statusbar.phone;

import android.content.Context;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;

import javax.inject.Inject;

@SysUISingleton
public class StatixStatusBarIconControllerImpl extends StatusBarIconControllerImpl implements StatixStatusBarIconController {

    @Inject
    public StatixStatusBarIconControllerImpl(
            Context context,
            CommandQueue commandQueue,
            DemoModeController demoModeController,
            ConfigurationController configurationController,
            TunerService tunerService,
            DumpManager dumpManager) {
        super(context, commandQueue, demoModeController, configurationController, tunerService, dumpManager);
    }
}

