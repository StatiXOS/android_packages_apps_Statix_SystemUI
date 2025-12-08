package com.statix.android.systemui.statusbar;

import android.app.AlarmManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.os.BatteryManager;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.Formatter;
import android.view.accessibility.AccessibilityManager;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.logging.KeyguardLogger;
import com.android.settingslib.fuelgauge.BatteryStatus;
import com.android.settingslib.fuelgauge.BatteryUtils;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.biometrics.FaceHelpMessageDeferralFactory;
import com.android.systemui.bouncer.domain.interactor.AlternateBouncerInteractor;
import com.android.systemui.bouncer.domain.interactor.BouncerMessageInteractor;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.deviceentry.domain.interactor.BiometricMessageInteractor;
import com.android.systemui.deviceentry.domain.interactor.DeviceEntryBiometricSettingsInteractor;
import com.android.systemui.deviceentry.domain.interactor.DeviceEntryFaceAuthInteractor;
import com.android.systemui.deviceentry.domain.interactor.DeviceEntryFingerprintAuthInteractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.domain.interactor.KeyguardInteractor;
import com.android.systemui.keyguard.util.IndicationHelper;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.securelockdevice.domain.interactor.SecureLockDeviceInteractor;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.user.domain.interactor.UserLogoutInteractor;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.time.SystemClock;
import com.android.systemui.util.time.impl.SystemClockImpl;
import com.android.systemui.util.wakelock.WakeLock;

import com.statix.android.systemui.adaptivecharging.AdaptiveChargingManager;
import com.statix.android.systemui.res.R;

import dagger.Lazy;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@SysUISingleton
public class KeyguardIndicationControllerStatix extends KeyguardIndicationController {

    private boolean mAdaptiveChargingActive;
    private boolean mAdaptiveChargingEnabledInSettings;
    @VisibleForTesting private AdaptiveChargingManager mAdaptiveChargingManager;

    @VisibleForTesting
    private AdaptiveChargingManager.AdaptiveChargingStatusReceiver mAdaptiveChargingStatusReceiver;

    private int mBatteryLevel = -1;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final BroadcastReceiver mBroadcastReceiver;
    private final Context mContext;
    private final DeviceConfigProxy mDeviceConfig;
    private final SystemClock mSystemClock;
    private long mEstimatedChargeCompletion;
    private boolean mInited;
    private boolean mIsCharging;
    private StatixKeyguardCallback mUpdateMonitorCallback;

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
            AuthController authController,
            LockPatternUtils lockPatternUtils,
            ScreenLifecycle screenLifecycle,
            KeyguardBypassController keyguardBypassController,
            AccessibilityManager accessibilityManager,
            FaceHelpMessageDeferralFactory faceHelpMessageDeferralFactory,
            DeviceConfigProxy deviceConfigProxy,
            KeyguardLogger keyguardLogger,
            AlternateBouncerInteractor alternateBouncerInteractor,
            AlarmManager alarmManager,
            UserTracker userTracker,
            BouncerMessageInteractor bouncerMessageInteractor,
            IndicationHelper indicationHelper,
            SystemClock systemClock,
            DeviceEntryBiometricSettingsInteractor deviceEntryBiometricSettingsInteractor,
            KeyguardInteractor keyguardInteractor,
            BiometricMessageInteractor biometricMessageInteractor,
            DeviceEntryFingerprintAuthInteractor deviceEntryFingerprintAuthInteractor,
            DeviceEntryFaceAuthInteractor deviceEntryFaceAuthInteractor,
            UserLogoutInteractor userLogoutInteractor,
            Lazy<SecureLockDeviceInteractor> secureLockDeviceInteractor) {
        super(
                context,
                mainLooper,
                wakeLockBuilder,
                keyguardStateController,
                statusBarStateController,
                keyguardUpdateMonitor,
                dockManager,
                broadcastDispatcher,
                devicePolicyManager,
                iBatteryStats,
                userManager,
                executor,
                bgExecutor,
                falsingManager,
                authController,
                lockPatternUtils,
                screenLifecycle,
                keyguardBypassController,
                accessibilityManager,
                faceHelpMessageDeferralFactory,
                keyguardLogger,
                alternateBouncerInteractor,
                alarmManager,
                userTracker,
                bouncerMessageInteractor,
                indicationHelper,
                deviceEntryBiometricSettingsInteractor,
                keyguardInteractor,
                biometricMessageInteractor,
                deviceEntryFingerprintAuthInteractor,
                deviceEntryFaceAuthInteractor,
                userLogoutInteractor,
                secureLockDeviceInteractor);
        mBroadcastReceiver =
                new BroadcastReceiver() {
                    @Override
                    public final void onReceive(Context context, Intent intent) {
                        if ("com.google.android.systemui.adaptivecharging.ADAPTIVE_CHARGING_DEADLINE_SET"
                                .equals(intent.getAction())) {
                            triggerAdaptiveChargingStatusUpdate();
                        }
                    }
                };
        mAdaptiveChargingStatusReceiver =
                new AdaptiveChargingManager.AdaptiveChargingStatusReceiver() {
                    @Override
                    public void onDestroyInterface() {}

                    @Override
                    public void onReceiveStatus(int seconds, String stage) {
                        boolean wasActive = mAdaptiveChargingActive;
                        mAdaptiveChargingActive = AdaptiveChargingManager.isActive(stage, seconds);
                        long currentEstimation = mEstimatedChargeCompletion;
                        long currentTimeMillis = System.currentTimeMillis();
                        mEstimatedChargeCompletion =
                                TimeUnit.SECONDS.toMillis(seconds + 29) + currentTimeMillis;
                        long abs = Math.abs(mEstimatedChargeCompletion - currentEstimation);
                        if (mAdaptiveChargingActive != wasActive
                                || (mAdaptiveChargingActive
                                        && abs > TimeUnit.SECONDS.toMillis(30L))) {
                            updateDeviceEntryIndication(true);
                        }
                    }
                };
        mContext = context;
        mBroadcastDispatcher = broadcastDispatcher;
        mDeviceConfig = deviceConfigProxy;
        mAdaptiveChargingManager = new AdaptiveChargingManager(context);
        mSystemClock = systemClock;
    }

    @Override
    public String computePowerIndication() {
        if (mIsCharging && mAdaptiveChargingEnabledInSettings && mAdaptiveChargingActive) {
            String formatTimeToFull =
                    mAdaptiveChargingManager.formatTimeToFull(mEstimatedChargeCompletion);
            return mContext.getResources()
                    .getString(
                            R.string.adaptive_charging_time_estimate,
                            NumberFormat.getPercentInstance().format(mBatteryLevel / 100.0f),
                            formatTimeToFull);
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
        mDeviceConfig.addOnPropertiesChangedListener(
                "adaptive_charging",
                delayableExecutor,
                (properties) -> {
                    if (properties.getKeyset().contains("adaptive_charging_enabled")) {
                        triggerAdaptiveChargingStatusUpdate();
                    }
                });
        triggerAdaptiveChargingStatusUpdate();
        mBroadcastDispatcher.registerReceiver(
                mBroadcastReceiver,
                new IntentFilter(
                        "com.google.android.systemui.adaptivecharging.ADAPTIVE_CHARGING_DEADLINE_SET"),
                null,
                UserHandle.ALL);
    }

    public void triggerAdaptiveChargingStatusUpdate() {
        refreshAdaptiveChargingEnabled();
        if (mAdaptiveChargingEnabledInSettings) {
            mAdaptiveChargingManager.queryStatus(mAdaptiveChargingStatusReceiver);
        } else {
            mAdaptiveChargingActive = false;
        }
    }

    @Override
    protected String computePowerChargingStringIndication() {
        if (mPowerCharged) {
            return mContext.getResources()
                    .getString(com.android.systemui.res.R.string.keyguard_charged);
        }

        String percentage = NumberFormat.getPercentInstance().format(mBatteryLevel / 100f);
        if (mBatteryDead) {
            return mContext.getResources()
                    .getString(
                            BatteryUtils.isChargingStringV2Enabled()
                                    ? R.string.keyguard_plugged_in_v2
                                    : com.android.systemui.res.R.string.keyguard_plugged_in,
                            percentage);
        }

        final boolean hasChargingTime = mChargingTimeRemaining > 0;
        int chargingId;
        if (mPowerPluggedInWired) {
            switch (mChargingSpeed) {
                case BatteryStatus.CHARGING_FAST:
                    chargingId =
                            hasChargingTime
                                    ? (BatteryUtils.isChargingStringV2Enabled()
                                            ? R.string.keyguard_indication_charging_time_fast_v2
                                            : com.android.systemui.res.R.string
                                                    .keyguard_indication_charging_time_fast)
                                    : (BatteryUtils.isChargingStringV2Enabled()
                                            ? R.string.keyguard_plugged_in_charging_fast_v2
                                            : com.android.systemui.res.R.string
                                                    .keyguard_plugged_in_charging_fast);
                    break;
                case BatteryStatus.CHARGING_SLOWLY:
                    chargingId =
                            hasChargingTime
                                    ? (BatteryUtils.isChargingStringV2Enabled()
                                            ? R.string.keyguard_indication_charging_time_slowly_v2
                                            : com.android.systemui.res.R.string
                                                    .keyguard_indication_charging_time_slowly)
                                    : (BatteryUtils.isChargingStringV2Enabled()
                                            ? R.string.keyguard_plugged_in_charging_slowly_v2
                                            : com.android.systemui.res.R.string
                                                    .keyguard_plugged_in_charging_slowly);
                    break;
                default:
                    chargingId =
                            hasChargingTime
                                    ? (BatteryUtils.isChargingStringV2Enabled()
                                            ? R.string.keyguard_indication_charging_time_v2
                                            : com.android.systemui.res.R.string
                                                    .keyguard_indication_charging_time)
                                    : (BatteryUtils.isChargingStringV2Enabled()
                                            ? R.string.keyguard_plugged_in_v2
                                            : com.android.systemui.res.R.string
                                                    .keyguard_plugged_in);
                    break;
            }
        } else if (mPowerPluggedInWireless) {
            chargingId =
                    hasChargingTime
                            ? (BatteryUtils.isChargingStringV2Enabled()
                                    ? R.string.keyguard_indication_charging_time_wireless_v2
                                    : com.android.systemui.res.R.string
                                            .keyguard_indication_charging_time_wireless)
                            : (BatteryUtils.isChargingStringV2Enabled()
                                    ? R.string.keyguard_plugged_in_wireless_v2
                                    : com.android.systemui.res.R.string
                                            .keyguard_plugged_in_wireless);
        } else if (mPowerPluggedInDock) {
            chargingId =
                    hasChargingTime
                            ? (BatteryUtils.isChargingStringV2Enabled()
                                    ? R.string.keyguard_indication_charging_time_dock_v2
                                    : com.android.systemui.res.R.string
                                            .keyguard_indication_charging_time_dock)
                            : (BatteryUtils.isChargingStringV2Enabled()
                                    ? R.string.keyguard_plugged_in_dock_v2
                                    : com.android.systemui.res.R.string.keyguard_plugged_in_dock);
        } else {
            chargingId =
                    hasChargingTime
                            ? (BatteryUtils.isChargingStringV2Enabled()
                                    ? R.string.keyguard_indication_charging_time_v2
                                    : com.android.systemui.res.R.string
                                            .keyguard_indication_charging_time)
                            : (BatteryUtils.isChargingStringV2Enabled()
                                    ? R.string.keyguard_plugged_in_v2
                                    : com.android.systemui.res.R.string.keyguard_plugged_in);
        }

        if (hasChargingTime) {
            String chargingTimeFormatted =
                    getChargingTimeFormatted(mContext, mChargingTimeRemaining);
            return mContext.getResources().getString(chargingId, chargingTimeFormatted, percentage);
        } else {
            return mContext.getResources().getString(chargingId, percentage);
        }
    }

    public final String getChargingTimeFormatted(Context context, long millis) {
        if (!BatteryUtils.isChargingStringV2Enabled()) {
            return Formatter.formatShortElapsedTimeRoundingUpToMinutes(context, millis);
        }
        ((SystemClockImpl) mSystemClock).getClass();
        long currentTimeMillis = System.currentTimeMillis() + millis;
        long fifteenMinutesMillis = PowerUtil.FIFTEEN_MINUTES_MILLIS;
        if (millis >= fifteenMinutesMillis) {
            long absCurrentTimeMillis = Math.abs(currentTimeMillis);
            long absFifteenMinutesMillis = Math.abs(fifteenMinutesMillis);
            currentTimeMillis =
                    absFifteenMinutesMillis
                            * (((absCurrentTimeMillis + absFifteenMinutesMillis) - 1)
                                    / absFifteenMinutesMillis);
        }
        return DateFormat.getInstanceForSkeleton(
                        android.text.format.DateFormat.getTimeFormatString(context))
                .format(Date.from(Instant.ofEpochMilli(currentTimeMillis)));
    }
}
