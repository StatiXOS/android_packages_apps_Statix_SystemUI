/*
 * Copyright (C) 2022 StatiXOS
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
 *
 * Changes from Qualcomm Innovation Center are provided under the following license:
 *
 * Copyright (c) 2022 Qualcomm Innovation Center, Inc. All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause-Clear
 */

package com.statix.android.systemui.qs.tiles;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.internal.logging.MetricsLogger;

import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.tiles.BluetoothTile;
import com.android.systemui.statusbar.policy.BluetoothController;

import com.statix.android.systemui.qs.tiles.dialog.BluetoothDialogFactory;

import javax.inject.Inject;

public class BluetoothDialogTile extends BluetoothTile {

    private final Handler mHandler;
    private final BluetoothDialogFactory mBluetoothDialogFactory;

    @Inject
    public BluetoothDialogTile(
            QSHost host,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger,
            BluetoothController bluetoothController,
            BluetoothDialogFactory bluetoothDialogFactory
    ) {
        super(host, backgroundLooper, mainHandler, falsingManager, metricsLogger,
                statusBarStateController, activityStarter, qsLogger, bluetoothController);
        mHandler = mainHandler;
        mBluetoothDialogFactory = bluetoothDialogFactory;
    }

    @Override
    public BooleanState newTileState() {
        BooleanState s = new BooleanState();
        s.forceExpandIcon = true;
        return s;
    }

    @Override
    protected void handleClick(@Nullable View view) {
        mHandler.post(() -> mBluetoothDialogFactory.create(true, view));
    }
}
