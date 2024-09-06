/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.dagger;

import static com.android.systemui.Dependency.ALLOW_NOTIFICATION_LONG_PRESS_NAME;
import static com.android.systemui.Dependency.LEAK_REPORT_EMAIL_NAME;

import android.content.Context;
import android.hardware.SensorPrivacyManager;

import com.android.keyguard.KeyguardViewController;
import com.android.systemui.ScreenDecorationsModule;
import com.android.systemui.accessibility.SystemActionsModule;
import com.android.systemui.battery.BatterySaverModule;
import com.android.systemui.biometrics.FingerprintInteractiveToAuthProvider;
import com.android.systemui.controls.controller.ControlsTileResourceConfiguration;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.display.ui.viewmodel.ConnectingDisplayViewModel;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManagerImpl;
import com.android.systemui.doze.DozeHost;
//import com.android.systemui.globalactions.GlobalActionsModule;
import com.android.systemui.keyguard.ui.view.layout.blueprints.KeyguardBlueprintModule;
import com.android.systemui.keyguard.ui.view.layout.sections.KeyguardSectionsModule;
import com.android.systemui.media.dagger.MediaModule;
import com.android.systemui.media.muteawait.MediaMuteAwaitConnectionCli;
import com.android.systemui.media.nearby.NearbyMediaDevicesManager;
import com.android.systemui.navigationbar.NavigationBarControllerModule;
import com.android.systemui.navigationbar.gestural.GestureModule;
import com.android.systemui.plugins.qs.QSFactory;
import com.android.systemui.qs.dagger.QSModule;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.reardisplay.RearDisplayModule;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.rotationlock.RotationLockModule;
import com.android.systemui.rotationlock.RotationLockNewModule;
import com.android.systemui.scene.SceneContainerFrameworkModule;
import com.android.systemui.screenshot.ReferenceScreenshotModule;
import com.android.systemui.settings.MultiUserUtilsModule;
import com.android.systemui.shade.NotificationShadeWindowControllerImpl;
import com.android.systemui.shade.ShadeModule;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.KeyboardShortcutsModule;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.HeadsUpModule;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragmentStartableModule;
import com.android.systemui.statusbar.policy.AospPolicyModule;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl;
import com.android.systemui.toast.ToastModule;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.unfold.SysUIUnfoldStartableModule;
import com.android.systemui.unfold.UnfoldTransitionModule;
import com.android.systemui.volume.dagger.VolumeModule;
import com.android.systemui.wallpapers.dagger.WallpaperModule;

import com.statix.android.systemui.biometrics.FingerprintExtProvider;
import com.statix.android.systemui.biometrics.FingerprintInteractiveToAuthProviderImpl;
import com.statix.android.systemui.controls.StatixControlsTileResourceConfigurationImpl;
import com.statix.android.systemui.power.dagger.StatixPowerModule;
import com.statix.android.systemui.qs.tileimpl.QSFactoryImplStatix;
import com.statix.android.systemui.qs.tileimpl.StatixQSModule;
import com.statix.android.systemui.statusbar.KeyguardIndicationControllerStatix;
import com.statix.android.systemui.statusbar.dagger.StatixStartCentralSurfacesModule;
import com.statix.android.systemui.theme.ThemeOverlayControllerStatix;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

/**
 * A dagger module for injecting default implementations of components of System UI.
 *
 * Variants of SystemUI should make a copy of this, include it in their component, and customize it
 * as needed.
 *
 * This module might alternatively be named `AospSystemUIModule`, `PhoneSystemUIModule`,
 * or `BasicSystemUIModule`.
 *
 * Nothing in the module should be strictly required. Each piece should either be swappable with
 * a different implementation or entirely removable.
 *
 * This is different from {@link SystemUIModule} which should be used for pieces of required
 * SystemUI code that variants of SystemUI _must_ include to function correctly.
 */
@Module(includes = {
        AospPolicyModule.class,
        BatterySaverModule.class,
        CollapsedStatusBarFragmentStartableModule.class,
        ConnectingDisplayViewModel.StartableModule.class,
        GestureModule.class,
        HeadsUpModule.class,
        KeyboardShortcutsModule.class,
        KeyguardBlueprintModule.class,
        KeyguardSectionsModule.class,
        MediaModule.class,
        MediaMuteAwaitConnectionCli.StartableModule.class,
        MultiUserUtilsModule.class,
        NavigationBarControllerModule.class,
        NearbyMediaDevicesManager.StartableModule.class,
        QSModule.class,
        RearDisplayModule.class,
        ReferenceScreenshotModule.class,
        RotationLockModule.class,
        RotationLockNewModule.class,
        SceneContainerFrameworkModule.class,
        ScreenDecorationsModule.class,
        ShadeModule.class,
        StatixStartCentralSurfacesModule.class,
        StatixPowerModule.class,
        StatixQSModule.class,
        SystemActionsModule.class,
        SysUIUnfoldStartableModule.class,
        ToastModule.class,
        UnfoldTransitionModule.Startables.class,
        VolumeModule.class,
        WallpaperModule.class
})
public abstract class SystemUIStatixModule {

    @SysUISingleton
    @Provides
    @Named(LEAK_REPORT_EMAIL_NAME)
    static String provideLeakReportEmail() {
        return "";
    }

    @Binds
    abstract NotificationLockscreenUserManager bindNotificationLockscreenUserManager(
            NotificationLockscreenUserManagerImpl notificationLockscreenUserManager);

    @Provides
    @SysUISingleton
    static SensorPrivacyController provideSensorPrivacyController(
            SensorPrivacyManager sensorPrivacyManager) {
        SensorPrivacyController spC = new SensorPrivacyControllerImpl(sensorPrivacyManager);
        spC.init();
        return spC;
    }

    @Provides
    @SysUISingleton
    static IndividualSensorPrivacyController provideIndividualSensorPrivacyController(
            SensorPrivacyManager sensorPrivacyManager) {
        IndividualSensorPrivacyController spC = new IndividualSensorPrivacyControllerImpl(
                sensorPrivacyManager);
        spC.init();
        return spC;
    }

    @Binds
    abstract DockManager bindDockManager(DockManagerImpl dockManager);

    @SysUISingleton
    @Provides
    @Named(ALLOW_NOTIFICATION_LONG_PRESS_NAME)
    static boolean provideAllowNotificationLongPress() {
        return true;
    }

    @Provides
    @SysUISingleton
    static Recents provideRecents(Context context, RecentsImplementation recentsImplementation,
            CommandQueue commandQueue) {
        return new Recents(context, recentsImplementation, commandQueue);
    }

    @SysUISingleton
    @Provides
    static DeviceProvisionedController bindDeviceProvisionedController(
            DeviceProvisionedControllerImpl deviceProvisionedController) {
        deviceProvisionedController.init();
        return deviceProvisionedController;
    }

    @Binds
    abstract KeyguardViewController bindKeyguardViewController(
            StatusBarKeyguardViewManager statusBarKeyguardViewManager);

    @Binds
    abstract NotificationShadeWindowController bindNotificationShadeController(
            NotificationShadeWindowControllerImpl notificationShadeWindowController);

    @Binds
    abstract DozeHost provideDozeHost(DozeServiceHost dozeServiceHost);

    /** */
    @Binds
    @SysUISingleton
    public abstract QSFactory bindQSFactory(QSFactoryImplStatix qsFactoryImpl);

    @Binds
    abstract ControlsTileResourceConfiguration bindControlsTileResourceConfiguration(
            StatixControlsTileResourceConfigurationImpl configuration);

    @Binds
    abstract ThemeOverlayController provideThemeOverlayController(
            ThemeOverlayControllerStatix themeOverlayController);

    @Binds
    abstract KeyguardIndicationController bindKeyguardIndicationController(
            KeyguardIndicationControllerStatix impl);

    @Binds
    abstract FingerprintInteractiveToAuthProvider bindFingerprintInteractiveToAuthProviderImpl(
            FingerprintInteractiveToAuthProviderImpl impl);
}
