package com.statix.android.systemui.statusbar.phone;

import android.annotation.Nullable;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.UserManager;
import android.telecom.TelecomManager;

import com.android.systemui.R;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.qualifiers.DisplayId;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.dagger.qualifiers.UiBackground;
import com.android.systemui.privacy.PrivacyItemController;
import com.android.systemui.privacy.logging.PrivacyLogger;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.PhoneStatusBarPolicy;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.RingerModeTracker;
import com.android.systemui.util.time.DateFormatUtil;

import com.statix.android.systemui.statusbar.policy.StatixBluetoothController;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class StatixPhoneStatusBarPolicy extends PhoneStatusBarPolicy {

    private Resources mResources;
    private StatixBluetoothController mBluetooth;
    private StatixStatusBarIconController mIconController;
    private final String mSlotBluetooth;

    @Inject
    public StatixPhoneStatusBarPolicy(StatixStatusBarIconController iconController,
            CommandQueue commandQueue, BroadcastDispatcher broadcastDispatcher,
            @UiBackground Executor uiBgExecutor, @Main Resources resources,
            CastController castController, HotspotController hotspotController,
            StatixBluetoothController bluetoothController, NextAlarmController nextAlarmController,
            UserInfoController userInfoController, RotationLockController rotationLockController,
            DataSaverController dataSaverController, ZenModeController zenModeController,
            DeviceProvisionedController deviceProvisionedController,
            KeyguardStateController keyguardStateController,
            LocationController locationController,
            SensorPrivacyController sensorPrivacyController, IActivityManager iActivityManager,
            AlarmManager alarmManager, UserManager userManager,
            DevicePolicyManager devicePolicyManager, RecordingController recordingController,
            @Nullable TelecomManager telecomManager, @DisplayId int displayId,
            @Main SharedPreferences sharedPreferences, DateFormatUtil dateFormatUtil,
            RingerModeTracker ringerModeTracker,
            PrivacyItemController privacyItemController,
            PrivacyLogger privacyLogger) {
        super(iconController, commandQueue, broadcastDispatcher, uiBgExecutor, resources, castController, hotspotController,
            bluetoothController, nextAlarmController, userInfoController, rotationLockController, dataSaverController,
            zenModeController, deviceProvisionedController, keyguardStateController, locationController, sensorPrivacyController,
            iActivityManager, alarmManager, userManager, devicePolicyManager, recordingController, telecomManager, displayId,
            sharedPreferences, dateFormatUtil, ringerModeTracker, privacyItemController, privacyLogger);
        mIconController = iconController;
        mBluetooth = bluetoothController;
        mSlotBluetooth = resources.getString(com.android.internal.R.string.status_bar_bluetooth);
        mResources = resources;
    }

    @Override
    public void onBluetoothDevicesChanged() {
        updateBluetooth();
    }

    @Override
    public void onBluetoothStateChange(boolean enabled) {
        updateBluetooth();
    }

    private final void updateBluetooth() {
        int iconId = R.drawable.stat_sys_data_bluetooth_connected;
        String contentDescription =
                mResources.getString(R.string.accessibility_quick_settings_bluetooth_on);
        boolean bluetoothVisible = false;
        int batteryLevel = -1;
        if (mBluetooth != null) {
            if (mBluetooth.isBluetoothConnected()
                    && (mBluetooth.isBluetoothAudioActive()
                    || !mBluetooth.isBluetoothAudioProfileOnly())) {
                contentDescription = mResources.getString(
                        R.string.accessibility_bluetooth_connected);
                bluetoothVisible = mBluetooth.isBluetoothEnabled();
                batteryLevel = mBluetooth.getBatteryLevel();
            }
        }

        mIconController.setBluetoothIcon(mSlotBluetooth,
                new BluetoothIconState(bluetoothVisible, batteryLevel, contentDescription));
    }

    public static class BluetoothIconState {
        public boolean visible;
        public int batteryLevel;
        public String contentDescription;

        public BluetoothIconState(boolean visible, int batteryLevel, String contentDescription) {
            this.visible = visible;
            this.batteryLevel = batteryLevel;
            this.contentDescription = contentDescription;
        }

        @Override
        public String toString() {
            return "BluetoothIconState(visible=" + visible + " batteryLevel=" + batteryLevel + ")";
        }
    }
}
