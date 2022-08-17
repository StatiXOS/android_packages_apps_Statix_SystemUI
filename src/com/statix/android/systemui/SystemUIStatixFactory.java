/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui;

import android.content.Context;

import com.statix.android.systemui.dagger.DaggerGlobalRootComponentStatix;
import com.statix.android.systemui.dagger.GlobalRootComponentStatix;
import com.statix.android.systemui.dagger.SysUIComponentStatix;

import com.android.systemui.SystemUIFactory;
import com.android.systemui.dagger.GlobalRootComponent;
import com.android.systemui.navigationbar.gestural.BackGestureTfClassifierProvider;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class SystemUIStatixFactory extends SystemUIFactory {
    @Override
    protected GlobalRootComponent buildGlobalRootComponent(Context context) {
        return DaggerGlobalRootComponentStatix.builder()
                .context(context)
                .build();
    }
}
