package com.statix.android.systemui.statusbar;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.widget.LockPatternUtils;

import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

import com.android.settingslib.fuelgauge.BatteryStatus;

import com.android.systemui.R;
import com.android.systemui.biometrics.FaceHelpMessageDeferral;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.dock.DockManager;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.wakelock.WakeLock;

import com.statix.android.systemui.adaptivecharging.AdaptiveChargingManager;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@SysUISingleton
public class KeyguardIndicationControllerStatix extends KeyguardIndicationController {

    private boolean mAdaptiveChargingActive;
    private boolean mAdaptiveChargingEnabledInSettings;
    @VisibleForTesting
    private AdaptiveChargingManager mAdaptiveChargingManager;
    @VisibleForTesting
    private AdaptiveChargingManager.AdaptiveChargingStatusReceiver mAdaptiveChargingStatusReceiver =
        new AdaptiveChargingManager.AdaptiveChargingStatusReceiver() {
            @Override
            public void onDestroyInterface() {}

            @Override
            public void onReceiveStatus(int seconds, String stage) {
                boolean wasActive = mAdaptiveChargingActive;
                mAdaptiveChargingActive = AdaptiveChargingManager.isActive(stage, seconds);
                long currentEstimation = mEstimatedChargeCompletion;
                long currentTimeMillis = System.currentTimeMillis();
                mEstimatedChargeCompletion = TimeUnit.SECONDS.toMillis(seconds + 29) + currentTimeMillis;
                long abs = Math.abs(mEstimatedChargeCompletion - currentEstimation);
                if (mAdaptiveChargingActive != wasActive || (mAdaptiveChargingActive && abs > TimeUnit.SECONDS.toMillis(30L))) {
                    updateDeviceEntryIndication(true);
                }
            }
        };
    private int mBatteryLevel;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public final void onReceive(Context context, Intent intent) {
                if ("com.google.android.systemui.adaptivecharging.ADAPTIVE_CHARGING_DEADLINE_SET".equals(intent.getAction())) {
                    triggerAdaptiveChargingStatusUpdate();
                }
            }
        };
    private final Context mContext;
    private final DeviceConfigProxy mDeviceConfig;
    private long mEstimatedChargeCompletion;
    private boolean mInited;
    private boolean mIsCharging;
    private StatixKeyguardCallback mUpdateMonitorCallback;
    @Background
    private Handler mBgHandler;

    private class StatixKeyguardCallback extends KeyguardIndicationController.BaseKeyguardCallback {
        private StatixKeyguardCallback() {
            super();
        }

        @Override
        public final void onRefreshBatteryInfo(BatteryStatus batteryStatus) {
            super.onRefreshBatteryInfo(batteryStatus);
            mIsCharging = batteryStatus.status == BatteryManager.BATTERY_STATUS_CHARGING;
            mBatteryLevel = batteryStatus.level;
            if (mIsCharging) {
                triggerAdaptiveChargingStatusUpdate();
            } else {
                mAdaptiveChargingActive = false;
            }
        }
    }

    @Inject
    public KeyguardIndicationControllerStatix(
            Context context,
            @Main Looper mainLooper,
            WakeLock.Builder wakeLockBuilder,
            KeyguardStateController keyguardStateController,
            StatusBarStateController statusBarStateController,
            KeyguardUpdateMonitor keyguardUpdateMonitor,
            DockManager dockManager,
            BroadcastDispatcher broadcastDispatcher,
            DevicePolicyManager devicePolicyManager,
            IBatteryStats iBatteryStats,
            UserManager userManager,
            @Main DelayableExecutor executor,
            @Background DelayableExecutor bgExecutor,
            FalsingManager falsingManager,
            LockPatternUtils lockPatternUtils,
            ScreenLifecycle screenLifecycle,
            KeyguardBypassController keyguardBypassController,
            AccessibilityManager accessibilityManager,
            FaceHelpMessageDeferral faceHelpMessageDeferral,
            DeviceConfigProxy deviceConfigProxy,
            @Background Handler handler) {
        super(context, mainLooper, wakeLockBuilder, keyguardStateController, statusBarStateController, keyguardUpdateMonitor, dockManager, broadcastDispatcher,
              devicePolicyManager, iBatteryStats, userManager, executor, bgExecutor, falsingManager, lockPatternUtils, screenLifecycle,
              keyguardBypassController, accessibilityManager, faceHelpMessageDeferral);
        mContext = context;
        mBroadcastDispatcher = broadcastDispatcher;
        mDeviceConfig = deviceConfigProxy;
        mAdaptiveChargingManager = new AdaptiveChargingManager(context);
        mBgHandler = handler;
    }

    @Override
    public String computePowerIndication() {
        if (mIsCharging && mAdaptiveChargingEnabledInSettings && mAdaptiveChargingActive) {
            String formatTimeToFull = mAdaptiveChargingManager.formatTimeToFull(mEstimatedChargeCompletion);
            return mContext.getResources().getString(R.string.adaptive_charging_time_estimate, NumberFormat.getPercentInstance().format(mBatteryLevel / 100.0f), formatTimeToFull);
        }
        return super.computePowerIndication();
    }

    @Override
    public final KeyguardUpdateMonitorCallback getKeyguardCallback() {
        if (mUpdateMonitorCallback == null) {
            mUpdateMonitorCallback = new StatixKeyguardCallback();
        }
        return mUpdateMonitorCallback;
    }

    @Override
    public void setIndicationArea(ViewGroup viewGroup) {
        super.setIndicationArea(viewGroup);
        viewGroup.setOnClickListener(new ViewGroup.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdaptiveChargingManager.setEnabled(!mAdaptiveChargingManager.getEnabled());
                boolean newEnabled = mAdaptiveChargingManager.getEnabled();
                mBgHandler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        mAdaptiveChargingManager.setEnabled(!newEnabled);
                    }
                }, System.currentTimeMillis() + 43200000 /* 12 hours */);
            }
        });
    }

    private void refreshAdaptiveChargingEnabled() {
        boolean supported = mAdaptiveChargingManager.isAvailable();
        if (supported) {
            mAdaptiveChargingEnabledInSettings = mAdaptiveChargingManager.getEnabled();
        } else {
            mAdaptiveChargingEnabledInSettings = false;
        }
    }

    @Override
    public final void init() {
        super.init();
        if (mInited) {
            return;
        }
        mInited = true;
        DelayableExecutor delayableExecutor = mExecutor;
        mDeviceConfig.addOnPropertiesChangedListener("adaptive_charging", delayableExecutor,
            (properties) -> {
                if (properties.getKeyset().contains("adaptive_charging_enabled")) {
                    triggerAdaptiveChargingStatusUpdate();
                }
        });
        triggerAdaptiveChargingStatusUpdate();
        mBroadcastDispatcher.registerReceiver(mBroadcastReceiver, new IntentFilter("com.google.android.systemui.adaptivecharging.ADAPTIVE_CHARGING_DEADLINE_SET"), null, UserHandle.ALL);
    }

    public void triggerAdaptiveChargingStatusUpdate() {
        refreshAdaptiveChargingEnabled();
        if (mAdaptiveChargingEnabledInSettings) {
            mAdaptiveChargingManager.queryStatus(mAdaptiveChargingStatusReceiver);
        } else {
            mAdaptiveChargingActive = false;
        }
    }

}
