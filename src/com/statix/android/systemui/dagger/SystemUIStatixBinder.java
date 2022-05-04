/*
 * Copyright (C) 2021 The Pixel Experience Project
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.dagger;

import com.android.systemui.keyguard.dagger.KeyguardModule;
import com.android.systemui.recents.RecentsModule;
//import com.android.systemui.statusbar.dagger.CentralSurfacesModule;

import com.statix.android.systemui.statusbar.dagger.StatixCentralSurfacesModule;

import dagger.Module;

@Module(includes = {RecentsModule.class, StatixCentralSurfacesModule.class, KeyguardModule.class})
public abstract class SystemUIStatixBinder {
}
