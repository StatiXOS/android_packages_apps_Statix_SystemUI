/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui;

import android.content.Context;

import com.statix.android.systemui.dagger.DaggerGlobalRootComponentStatix;

import com.android.systemui.SystemUIInitializer;
import com.android.systemui.dagger.GlobalRootComponent;

public class SystemUIStatixInitializer extends SystemUIInitializer {

    public SystemUIStatixInitializer(Context context) {
        super(context);
    }

    @Override
    protected GlobalRootComponent.Builder getGlobalRootComponentBuilder() {
        return DaggerGlobalRootComponentStatix.builder();
    }
}
