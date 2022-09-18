package com.statix.android.systemui.statusbar.dagger;

import com.statix.android.systemui.statusbar.phone.StatixStatusBarIconController;
import com.statix.android.systemui.statusbar.phone.StatixStatusBarIconControllerImpl;

import dagger.Binds;
import dagger.Module;

@Module
public interface StatixCentralSurfacesDependenciesModule {

    @Binds
    StatixStatusBarIconController bindStatusBarIconController(StatixStatusBarIconControllerImpl impl);
}
