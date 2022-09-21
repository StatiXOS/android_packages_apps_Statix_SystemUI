package com.statix.android.systemui.statusbar.policy;

import android.annotation.Nullable;
import android.content.Context;
import android.os.Looper;

import com.android.settingslib.bluetooth.LocalBluetoothManager;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl;
import com.android.systemui.dump.DumpManager;

import javax.inject.Inject;

@SysUISingleton
public class StatixBluetoothControllerImpl extends BluetoothControllerImpl implements StatixBluetoothController {

    private int mBatteryLevel;

    @Inject
    public StatixBluetoothControllerImpl(
            Context context,
            DumpManager dumpManager,
            @Background Looper bgLooper,
            @Main Looper mainLooper,
            @Nullable LocalBluetoothManager localBluetoothManager) {
        super(context, dumpManager, bgLooper, mainLooper, localBluetoothManager);
    }

    @Override
    public int getBatteryLevel() {
        if (!mConnectedDevices.isEmpty()) {
            return mConnectedDevices.get(0).getBatteryLevel();
        }
        return -1;
    }

    @Override
    protected void updateConnected() {
        super.updateConnected();
        updateBattery();
    }

    private void updateBattery() {
        int batteryLevel = getBatteryLevel();
        if (batteryLevel != mBatteryLevel) {
            mBatteryLevel = batteryLevel;
            mHandler.sendEmptyMessage(H.MSG_STATE_CHANGED);
        }
    }

}
