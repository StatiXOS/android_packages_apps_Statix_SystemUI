/*
 * Copyright (C) 2020 The LineageOS Project
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.qs.tiles;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.quicksettings.Tile;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.statix.android.systemui.res.R;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.QsEventLogger;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.shade.NotificationShadeWindowView;
import com.android.systemui.statusbar.policy.BatteryController;

import com.statix.android.systemui.ambient.AmbientIndicationContainer;

import vendor.lineage.powershare.IPowerShare;

import java.util.NoSuchElementException;

import javax.inject.Inject;

public class PowerShareTile extends QSTileImpl<BooleanState>
        implements BatteryController.BatteryStateChangeCallback {

    public static final String TILE_SPEC = "powershare";

    private final IPowerShare mPowerShare;
    private NotificationShadeWindowView mNotificationShadeWindowView;
    private AmbientIndicationContainer mAmbientContainer;
    private BatteryController mBatteryController;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private static final String CHANNEL_ID = "powershare";
    private static final int NOTIFICATION_ID = 273298;

    private static final String POWERSHARE_SERVICE_NAME =
            "vendor.lineage.powershare.IPowerShare/default";

    @Inject
    public PowerShareTile(
            QSHost host,
            QsEventLogger qsEventLogger,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger,
            BatteryController batteryController,
            NotificationShadeWindowView notificationShadeWindowView) {
        super(
                host,
                qsEventLogger,
                backgroundLooper,
                mainHandler,
                falsingManager,
                metricsLogger,
                statusBarStateController,
                activityStarter,
                qsLogger);
        mPowerShare = getPowerShare();
        if (mPowerShare == null) {
            return;
        }
        mNotificationShadeWindowView = notificationShadeWindowView;

        mBatteryController = batteryController;
        mNotificationManager = mContext.getSystemService(NotificationManager.class);

        NotificationChannel notificationChannel =
                new NotificationChannel(
                        CHANNEL_ID,
                        mContext.getString(R.string.quick_settings_powershare_label),
                        NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(notificationChannel);

        Notification.Builder builder = new Notification.Builder(mContext, CHANNEL_ID);
        builder.setContentTitle(
                mContext.getString(R.string.quick_settings_powershare_enabled_label));
        builder.setSmallIcon(R.drawable.ic_qs_powershare);
        builder.setOnlyAlertOnce(true);
        mNotification = builder.build();
        mNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        mNotification.visibility = Notification.VISIBILITY_PUBLIC;

        batteryController.addCallback(this);
    }

    public void initialize() {
        if (isAvailable()) {
            mAmbientContainer =
                    (AmbientIndicationContainer)
                            mNotificationShadeWindowView.findViewById(
                                    R.id.ambient_indication_container);
        }
    }

    @Override
    public void onPowerSaveChanged(boolean isPowerSave) {
        refreshState();
    }

    @Override
    public void refreshState() {
        updatePowerShareState();

        super.refreshState();
    }

    private void updatePowerShareState() {
        if (!isAvailable()) {
            return;
        }

        if (mBatteryController.isPowerSave()) {
            try {
                mPowerShare.setEnabled(false);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }

        try {
            if (mPowerShare.isEnabled()) {
                mNotificationManager.notify(NOTIFICATION_ID, mNotification);
                if (mAmbientContainer != null) {
                    mAmbientContainer.setReverseChargingMessage("Sharing battery");
                }
            } else {
                mNotificationManager.cancel(NOTIFICATION_ID);
                if (mAmbientContainer != null) {
                    mAmbientContainer.setReverseChargingMessage("");
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isAvailable() {
        return mPowerShare != null;
    }

    @Override
    public BooleanState newTileState() {
        BooleanState state = new BooleanState();
        state.handlesLongClick = false;
        return state;
    }

    @Override
    public void handleClick(@Nullable View view) {
        try {
            boolean powerShareEnabled = mPowerShare.isEnabled();

            if (mPowerShare.setEnabled(!powerShareEnabled) != powerShareEnabled) {
                refreshState();
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    @Override
    public CharSequence getTileLabel() {
        if (mBatteryController.isPowerSave()) {
            return mContext.getString(R.string.quick_settings_powershare_off_powersave_label);
        } else {
            if (getBatteryLevel() < getMinBatteryLevel()) {
                return mContext.getString(R.string.quick_settings_powershare_off_low_battery_label);
            }
        }

        return mContext.getString(R.string.quick_settings_powershare_label);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        if (!isAvailable()) {
            return;
        }

        state.icon = ResourceIcon.get(R.drawable.ic_qs_powershare);
        try {
            state.value = mPowerShare.isEnabled();
        } catch (RemoteException ex) {
            state.value = false;
            ex.printStackTrace();
        }
        state.label = mContext.getString(R.string.quick_settings_powershare_label);

        if (mBatteryController.isPowerSave() || getBatteryLevel() < getMinBatteryLevel()) {
            state.state = Tile.STATE_UNAVAILABLE;
        } else if (!state.value) {
            state.state = Tile.STATE_INACTIVE;
        } else {
            state.state = Tile.STATE_ACTIVE;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.QS_CUSTOM;
    }

    @Override
    public void handleSetListening(boolean listening) {}

    private synchronized IPowerShare getPowerShare() {
        try {
            return IPowerShare.Stub.asInterface(ServiceManager.getService(POWERSHARE_SERVICE_NAME));
        } catch (NoSuchElementException ex) {
            // service not available
        }

        return null;
    }

    private int getMinBatteryLevel() {
        try {
            return mPowerShare.getMinBattery();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    private int getBatteryLevel() {
        BatteryManager bm = mContext.getSystemService(BatteryManager.class);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }
}
