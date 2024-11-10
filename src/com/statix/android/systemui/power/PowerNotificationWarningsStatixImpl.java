/*
 * Copyright (C) 2022 The PixelExperience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.statix.android.systemui.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.android.internal.logging.UiEventLogger;
import com.android.systemui.animation.DialogTransitionAnimator;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.broadcast.BroadcastSender;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.power.PowerNotificationWarnings;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.BatteryController;

import dagger.Lazy;

import javax.inject.Inject;

public final class PowerNotificationWarningsStatixImpl extends PowerNotificationWarnings {
    @VisibleForTesting final BroadcastReceiver mBroadcastReceiver;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Handler mHandler;
    private AdaptiveChargingNotification mAdaptiveChargingNotification;
    private BatteryDefenderNotification mBatteryDefenderNotification;
    private BatteryInfoBroadcast mBatteryInfoBroadcast;

    @Inject
    public PowerNotificationWarningsStatixImpl(
            Context context,
            ActivityStarter activityStarter,
            BroadcastSender broadcastSender,
            Lazy<BatteryController> batteryControllerLazy,
            DialogTransitionAnimator dialogTransitionAnimator,
            UiEventLogger uiEventLogger,
            UserTracker userTracker,
            SystemUIDialog.Factory systemUIDialogFactory,
            BroadcastDispatcher broadcastDispatcher) {
        super(
                context,
                activityStarter,
                broadcastSender,
                batteryControllerLazy,
                dialogTransitionAnimator,
                uiEventLogger,
                userTracker,
                systemUIDialogFactory);
        Handler handler = new Handler(Looper.getMainLooper());
        mHandler = handler;
        mBroadcastReceiver =
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context2, Intent intent) {
                        if (intent == null || intent.getAction() == null) {
                            return;
                        }
                        Log.d(
                                "PowerNotificationWarningsStatixImpl",
                                "onReceive: " + intent.getAction());
                        mBatteryInfoBroadcast.notifyBatteryStatusChanged(intent);
                        mBatteryDefenderNotification.dispatchIntent(intent);
                        mAdaptiveChargingNotification.dispatchIntent(intent);
                    }
                };
        mBroadcastDispatcher = broadcastDispatcher;
        handler.post(
                () -> {
                    long currentTimeMillis = System.currentTimeMillis();
                    mBatteryDefenderNotification =
                            new BatteryDefenderNotification(context, uiEventLogger);
                    mAdaptiveChargingNotification = new AdaptiveChargingNotification(context);
                    mBatteryInfoBroadcast = new BatteryInfoBroadcast(context, broadcastSender);
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
                    intentFilter.addAction("PNW.defenderResumeCharging");
                    intentFilter.addAction("PNW.defenderResumeCharging.settings");
                    intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
                    intentFilter.addAction(
                            "com.statix.android.systemui.adaptivecharging.ADAPTIVE_CHARGING_DEADLINE_SET");
                    intentFilter.addAction("PNW.acChargeNormally");
                    intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
                    intentFilter.addAction(
                            "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
                    intentFilter.addAction("android.bluetooth.device.action.ALIAS_CHANGED");
                    intentFilter.addAction("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED");
                    intentFilter.addAction(
                            "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
                    intentFilter.addAction(
                            "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                    intentFilter.addAction(
                            "android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED");
                    mBroadcastDispatcher.registerReceiverWithHandler(
                            mBroadcastReceiver, intentFilter, mHandler);
                    Intent registerReceiver =
                            context.registerReceiver(
                                    null,
                                    new IntentFilter("android.intent.action.BATTERY_CHANGED"));
                    if (registerReceiver != null) {
                        mBroadcastReceiver.onReceive(context, registerReceiver);
                    }
                    Log.d(
                            "PowerNotificationWarningsStatixImpl",
                            String.format(
                                    "Finish initialize in %d/ms",
                                    System.currentTimeMillis() - currentTimeMillis));
                });
    }
}
