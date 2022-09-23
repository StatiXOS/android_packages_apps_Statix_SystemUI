/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.dagger;

import com.android.systemui.dagger.SystemUICoreStartableModule;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.ReferenceSystemUIModule;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.dagger.SystemUIModule;

import dagger.Subcomponent;

@SysUISingleton
@Subcomponent(modules = {
        StatixComponentBinder.class,
        DependencyProvider.class,
        SystemUICoreStartableModule.class,
        SystemUIModule.class,
        SystemUIStatixBinder.class,
        SystemUIStatixModule.class})

public interface SysUIComponentStatix extends SysUIComponent {
    @SysUISingleton
    @Subcomponent.Builder
    interface Builder extends SysUIComponent.Builder {
        SysUIComponentStatix build();
    }
}
