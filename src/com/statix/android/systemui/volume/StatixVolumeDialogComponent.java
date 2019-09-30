/*
 * Copyright (C) 2022 Paranoid Android
 * Copyright (C) 2023 StatiXOS
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

package com.statix.android.systemui.volume;

import android.content.Context;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.VolumeDialog;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.volume.VolumeDialogComponent;
import com.android.systemui.volume.VolumeDialogControllerImpl;

import javax.inject.Inject;

import com.statix.android.systemui.tristate.TriStateUiController;
import dagger.Lazy;

@SysUISingleton
public class StatixVolumeDialogComponent extends VolumeDialogComponent
        implements TriStateUiController.UserActivityListener {

    @Nullable
    private TriStateUiController mTriStateUiController;

    @Inject
    public StatixVolumeDialogComponent(Context context,
            KeyguardViewMediator keyguardViewMediator,
            ActivityStarter activityStarter,
            VolumeDialogControllerImpl volumeDialogController,
            DemoModeController demoModeController,
            PluginDependencyProvider pluginDependencyProvider,
            ExtensionController extensionController,
            TunerService tunerService,
            VolumeDialog volumeDialog,
            Lazy<TriStateUiController> triStateUiController) {
        super(context, keyguardViewMediator, activityStarter, volumeDialogController,
                demoModeController, pluginDependencyProvider, extensionController, tunerService,
                volumeDialog);

        final boolean hasAlertSlider = context.getResources()
                .getBoolean(com.android.internal.R.bool.config_hasAlertSlider);
        if (hasAlertSlider) {
            extensionController.newExtension(TriStateUiController.class)
                    .withPlugin(TriStateUiController.class)
                    .withDefault(triStateUiController::get)
                    .withCallback(controller -> {
                        if (mTriStateUiController != null) {
                            mTriStateUiController.destroy();
                        }
                        mTriStateUiController = controller;
                        controller.init(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY, this);
                    }).build();
        }
    }

    @Override
    public void onTriStateUserActivity() {
        onUserActivity();
    }
}
