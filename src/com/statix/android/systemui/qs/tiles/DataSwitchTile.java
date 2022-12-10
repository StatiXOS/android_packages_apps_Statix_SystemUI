/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.qs.tiles;

import static android.telephony.TelephonyManager.DATA_ENABLED_REASON_USER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.TelephonyIntents;

import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SysUIToast;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.telephony.TelephonyListenerManager;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class DataSwitchTile extends QSTileImpl<BooleanState> {

    private final SubscriptionManager mSubscriptionManager;
    private final TelephonyManager mTelephonyManager;
    private final TelephonyListenerManager mTelephonyListenerManager;
    private final Executor mExecutor;

    private boolean mCanSwitch = true;
    private boolean mRegistered = false;
    private int mSimCount = 0;

    BroadcastReceiver mSimReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "mSimReceiver:onReceive");
            refreshState();
        }
    };

    private final TelephonyCallback.CallStateListener mPhoneStateListener = state -> {
        final boolean canSwitch = state == TelephonyManager.CALL_STATE_IDLE;
        if (mCanSwitch != canSwitch) {
            mCanSwitch = canSwitch;
            refreshState();
        }
    };

    @Inject
    public DataSwitchTile(
            QSHost host,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger,
            SubscriptionManager subscriptionManager,
            TelephonyManager telephonyManager,
            TelephonyListenerManager telephonyListenerManager,
            @Background Executor executor
    ) {
        super(host, backgroundLooper, mainHandler, falsingManager, metricsLogger,
                statusBarStateController, activityStarter, qsLogger);
        mSubscriptionManager = subscriptionManager;
        mTelephonyManager = telephonyManager;
        mTelephonyListenerManager = telephonyListenerManager;
        mExecutor = executor;
    }

    @Override
    public boolean isAvailable() {
        int count = mTelephonyManager.getActiveModemCount();
        if (DEBUG) Log.d(TAG, "phoneCount: " + count);
        return count >= 2;
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    public void handleSetListening(boolean listening) {
        super.handleSetListening(listening);

        if (listening) {
            if (!mRegistered) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(TelephonyIntents.ACTION_SIM_STATE_CHANGED);
                mContext.registerReceiver(mSimReceiver, filter);
                mTelephonyListenerManager.addCallStateListener(mPhoneStateListener);
                mRegistered = true;
            }
            refreshState();
        } else if (mRegistered) {
            mContext.unregisterReceiver(mSimReceiver);
            mTelephonyListenerManager.removeCallStateListener(mPhoneStateListener);
            mRegistered = false;
        }
    }

    private void updateSimCount() {
        String simState = SystemProperties.get("gsm.sim.state");
        if (DEBUG) Log.d(TAG, "DataSwitchTile:updateSimCount:simState=" + simState);
        mSimCount = 0;
        try {
            String[] sims = TextUtils.split(simState, ",");
            for (int i = 0; i < sims.length; i++) {
                if (!sims[i].isEmpty()
                        && !sims[i].equalsIgnoreCase(IccCardConstants.INTENT_VALUE_ICC_ABSENT)
                        && !sims[i].equalsIgnoreCase(IccCardConstants.INTENT_VALUE_ICC_NOT_READY)) {
                    mSimCount++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error to parse sim state");
        }
        if (DEBUG) Log.d(TAG, "DataSwitchTile:updateSimCount:mSimCount=" + mSimCount);
    }

    @Override
    public void handleClick(@Nullable View view) {
        if (mCanSwitch) {
            if (mSimCount == 0) {
                if (DEBUG) Log.d(TAG, "handleClick:no sim card");
                SysUIToast.makeText(mContext, mContext.getString(R.string.qs_data_switch_toast_0),
                        Toast.LENGTH_LONG).show();
            } else if (mSimCount == 1) {
                if (DEBUG) Log.d(TAG, "handleClick:only one sim card");
                SysUIToast.makeText(mContext, mContext.getString(R.string.qs_data_switch_toast_1),
                        Toast.LENGTH_LONG).show();
            } else {
                mExecutor.execute(() -> {
                    toggleMobileDataEnabled();
                    refreshState();
                });
            }
        }
    }

    @Override
    public Intent getLongClickIntent() {
        return new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.qs_data_switch_label);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        boolean activeSIMZero;
        if (arg == null) {
            int defaultPhoneId = mSubscriptionManager.getDefaultDataPhoneId();
            if (DEBUG) Log.d(TAG, "default data phone id=" + defaultPhoneId);
            activeSIMZero = defaultPhoneId == 0;
        } else {
            activeSIMZero = (Boolean) arg;
        }
        updateSimCount();
        switch (mSimCount) {
            case 1:
                state.icon = ResourceIcon.get(activeSIMZero
                        ? R.drawable.ic_qs_data_switch_1
                        : R.drawable.ic_qs_data_switch_2);
                state.value = false;
                break;
            case 2:
                state.icon = ResourceIcon.get(activeSIMZero
                        ? R.drawable.ic_qs_data_switch_1
                        : R.drawable.ic_qs_data_switch_2);
                state.value = true;
                break;
            default:
                state.icon = ResourceIcon.get(R.drawable.ic_qs_data_switch_1);
                state.value = false;
                break;
        }
        if (mSimCount < 2) {
            state.state = 0;
        } else if (!mCanSwitch) {
            state.state = 0;
            if (DEBUG) Log.d(TAG, "call state isn't idle, set to unavailable.");
        } else {
            state.state = state.value ? 2 : 1;
        }

        state.contentDescription =
                mContext.getString(activeSIMZero
                        ? R.string.qs_data_switch_changed_1
                        : R.string.qs_data_switch_changed_2);
        state.label = mContext.getString(R.string.qs_data_switch_label);
    }

    /**
     * Set whether to enable data for {@code subId}, also whether to disable data for other
     * subscription
     */
    private void toggleMobileDataEnabled() {
        // Get opposite slot 2 ^ 3 = 1, 1 ^ 3 = 2
        int subId = mSubscriptionManager.getDefaultDataSubscriptionId() ^ 3;
        final TelephonyManager telephonyManager =
                mTelephonyManager.createForSubscriptionId(subId);
        telephonyManager.setDataEnabledForReason(DATA_ENABLED_REASON_USER, true);
        mSubscriptionManager.setDefaultDataSubId(subId);
        if (DEBUG) Log.d(TAG, "Enabled subID: " + subId);

        List<SubscriptionInfo> subInfoList =
                mSubscriptionManager.getActiveSubscriptionInfoList(true);
        if (subInfoList != null) {
            for (SubscriptionInfo subInfo : subInfoList) {
                // We never disable mobile data for opportunistic subscriptions.
                if (subInfo.getSubscriptionId() != subId && !subInfo.isOpportunistic()) {
                    mTelephonyManager.createForSubscriptionId(subInfo.getSubscriptionId())
                            .setDataEnabledForReason(DATA_ENABLED_REASON_USER, false);
                    if (DEBUG) Log.d(TAG, "Disabled subID: " + subInfo.getSubscriptionId());
                }
            }
        }
    }
}
