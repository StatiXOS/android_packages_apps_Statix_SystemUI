/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.dagger;

import com.android.systemui.dagger.DefaultComponentBinder;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.SysUIComponent;

import dagger.Subcomponent;

@SysUISingleton
@Subcomponent(modules = {
        DefaultComponentBinder.class,
        DependencyProvider.class,
        SystemUIStatixBinder.class,
        SystemUIStatixModule.class,
        SystemUIStatixCoreStartableModule.class,
        StatixSystemUIModule.class})

public interface SysUIComponentStatix extends SysUIComponent {
    @SysUISingleton
    @Subcomponent.Builder
    interface Builder extends SysUIComponent.Builder {
        SysUIComponentStatix build();
    }
}
