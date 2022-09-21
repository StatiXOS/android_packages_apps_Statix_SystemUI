package com.statix.android.systemui.statusbar.phone;

import android.content.Context;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl;
import com.android.systemui.statusbar.phone.StatusBarIconHolder;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;

import com.statix.android.systemui.statusbar.phone.StatixPhoneStatusBarPolicy.BluetoothIconState;

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

    @Override
    public void setBluetoothIcon(String slot, BluetoothIconState state) {
        int index = getSlotIndex(slot);

        if (state == null) {
            removeIcon(index, 0);
            return;
        }

        StatusBarIconHolder holder = getIcon(index, 0);
        if (holder == null) {
            holder = StatixStatusBarIconHolder.fromBluetoothIconState(state);
            setIcon(index, holder);
        } else {
            ((StatixStatusBarIconHolder) holder).setBluetoothState(state);
            handleSet(index, holder);
        }
    }
}
