/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.dagger;

import com.android.systemui.dagger.GlobalModule;
import com.android.systemui.dagger.GlobalRootComponent;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {GlobalModule.class})
public interface GlobalRootComponentStatix extends GlobalRootComponent {

    @Component.Builder
    interface Builder extends GlobalRootComponent.Builder {
        GlobalRootComponentStatix build();
    }

    @Override
    SysUIComponentStatix.Builder getSysUIComponent();
}
