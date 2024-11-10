/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.statix.android.systemui.power.dagger;

import android.content.Context;

import com.android.internal.logging.UiEventLogger;
import com.android.systemui.animation.DialogTransitionAnimator;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.broadcast.BroadcastSender;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.power.PowerUI;
import com.android.systemui.power.data.repository.PowerRepositoryModule;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.BatteryController;

import com.statix.android.systemui.power.EnhancedEstimatesStatixImpl;
import com.statix.android.systemui.power.PowerNotificationWarningsStatixImpl;

import dagger.Binds;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

/** Dagger Module for code in the power package. */
@Module(
        includes = {
            PowerRepositoryModule.class,
        })
public interface StatixPowerModule {
    @Provides
    @SysUISingleton
    static PowerNotificationWarningsStatixImpl providePowerNotificationWarningsStatixImpl(
            Context context,
            ActivityStarter activityStarter,
            BroadcastSender broadcastSender,
            Lazy<BatteryController> batteryControllerLazy,
            DialogTransitionAnimator dialogTransitionAnimator,
            UiEventLogger uiEventLogger,
            UserTracker userTracker,
            SystemUIDialog.Factory systemUIDialogFactory,
            BroadcastDispatcher broadcastDispatcher) {
        return new PowerNotificationWarningsStatixImpl(
                context,
                activityStarter,
                broadcastSender,
                batteryControllerLazy,
                dialogTransitionAnimator,
                uiEventLogger,
                userTracker,
                systemUIDialogFactory,
                broadcastDispatcher);
    }

    /** */
    @Binds
    EnhancedEstimates bindEnhancedEstimates(EnhancedEstimatesStatixImpl enhancedEstimates);

    /** */
    @Binds
    PowerUI.WarningsUI provideWarningsUi(PowerNotificationWarningsStatixImpl controllerImpl);
}
