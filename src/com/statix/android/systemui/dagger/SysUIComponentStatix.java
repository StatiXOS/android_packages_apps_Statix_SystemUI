/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.dagger;

import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.SystemUICoreStartableModule;
import com.android.systemui.dagger.SystemUIModule;
import com.android.systemui.keyguard.CustomizationProvider;
import com.android.systemui.statusbar.NotificationInsetsModule;
import com.android.systemui.statusbar.QsFrameTranslateModule;
import com.statix.android.systemui.dagger.SystemUIStatixCoreStartableModule;

import dagger.Subcomponent;

/**
 * Dagger Subcomponent for Core SysUI used in AOSP.
 */
@SysUISingleton
@Subcomponent(modules = {
        DependencyProvider.class,
        NotificationInsetsModule.class,
        QsFrameTranslateModule.class,
        StatixComponentBinder.class,
        SystemUIModule.class,
        SystemUIStatixBinder.class,
        SystemUIStatixModule.class,
        SystemUICoreStartableModule.class,
        SystemUIStatixCoreStartableModule.class})
public interface SysUIComponentStatix extends SysUIComponent {

    /**
     * Builder for a SysUIComponentStatix.
     */
    @SysUISingleton
    @Subcomponent.Builder
    interface Builder extends SysUIComponent.Builder {
        SysUIComponentStatix build();
    }

    /**
     * Member injection into the supplied argument.
     */
    void inject(CustomizationProvider customizationProvider);
}
