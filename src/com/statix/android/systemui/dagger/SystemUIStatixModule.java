/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.dagger;

import static com.android.systemui.Dependency.ALLOW_NOTIFICATION_LONG_PRESS_NAME;
import static com.android.systemui.Dependency.LEAK_REPORT_EMAIL_NAME;
import static com.android.systemui.smartspace.dagger.SmartspaceModule.GLANCEABLE_HUB_SMARTSPACE_DATA_PLUGIN;
import static com.android.systemui.smartspace.dagger.SmartspaceModule.DATE_SMARTSPACE_DATA_PLUGIN;
import static com.android.systemui.smartspace.dagger.SmartspaceModule.WEATHER_SMARTSPACE_DATA_PLUGIN;
import static com.android.systemui.smartspace.dagger.SmartspaceModule.DREAM_SMARTSPACE_DATA_PLUGIN;
import static com.android.systemui.smartspace.dagger.SmartspaceModule.DREAM_WEATHER_SMARTSPACE_DATA_PLUGIN;
import static com.android.systemui.smartspace.dagger.SmartspaceModule.LOCKSCREEN_SMARTSPACE_TARGET_FILTER;

import android.app.AlarmManager;
import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.os.Handler;

import com.android.keyguard.KeyguardViewController;
import com.android.systemui.ScreenDecorationsModule;
import com.android.systemui.accessibility.AccessibilityModule;
import com.android.systemui.accessibility.SystemActionsModule;
import com.android.systemui.accessibility.data.repository.AccessibilityRepositoryModule;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.battery.BatterySaverModule;
import com.android.systemui.biometrics.FingerprintInteractiveToAuthProvider;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.broadcast.BroadcastSender;
import com.android.systemui.clipboardoverlay.dagger.ClipboardOverlayOverrideModule;
import com.android.systemui.communal.posturing.dagger.NoopPosturingModule;
import com.android.systemui.controls.controller.ControlsTileResourceConfiguration;
import com.android.systemui.CoreStartable;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.display.ui.viewmodel.ConnectingDisplayViewModel;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManagerImpl;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.education.dagger.ContextualEducationModule;
import com.android.systemui.topwindoweffects.dagger.SqueezeEffectRepositoryModule;
import com.android.systemui.topwindoweffects.dagger.TopLevelWindowEffectsModule;
import com.android.systemui.emergency.EmergencyGestureModule;
import com.android.systemui.globalactions.GlobalActionsModule;
import com.android.systemui.inputdevice.tutorial.KeyboardTouchpadTutorialModule;
import com.android.systemui.keyboard.shortcut.ShortcutHelperModule;
import com.android.systemui.keyguard.dagger.KeyguardModule;
import com.android.systemui.keyguard.ui.composable.blueprint.DefaultBlueprintModule;
import com.android.systemui.keyguard.ui.view.layout.blueprints.KeyguardBlueprintModule;
import com.android.systemui.keyguard.ui.view.layout.sections.KeyguardSectionsModule;
import com.android.systemui.log.LogBufferFactory;
import com.android.systemui.media.NotificationMediaManager;
import com.android.systemui.media.dagger.MediaModule;
import com.android.systemui.media.muteawait.MediaMuteAwaitConnectionCli;
import com.android.systemui.media.nearby.NearbyMediaDevicesManager;
import com.android.systemui.navigationbar.NavigationBarControllerModule;
import com.android.systemui.navigationbar.gestural.GestureModule;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.plugins.qs.QSFactory;
import com.android.systemui.qs.dagger.QSModule;
import com.android.systemui.reardisplay.RearDisplayModule;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.recents.RecentsModule;
import com.android.systemui.rotationlock.DeviceStateAutoRotateModule;
import com.android.systemui.rotationlock.RotationLockModule;
import com.android.systemui.rotationlock.RotationLockNewModule;
import com.android.systemui.scene.SceneContainerFrameworkModule;
import com.android.systemui.screenshot.ReferenceScreenshotModule;
import com.android.systemui.settings.MultiUserUtilsModule;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.settings.brightness.dagger.BrightnessSliderModule;
import com.android.systemui.shade.NotificationShadeWindowControllerImpl;
import com.android.systemui.shade.ShadeModule;
import com.android.systemui.smartspace.dagger.SmartspaceModule;
import com.android.systemui.startable.Dependencies;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.KeyboardShortcutsModule;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.dagger.StartCentralSurfacesModule;
import com.android.systemui.statusbar.notification.dagger.ReferenceNotificationsModule;
import com.android.systemui.statusbar.notification.headsup.HeadsUpModule;
import com.android.systemui.statusbar.phone.CentralSurfaces;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.dagger.StatusBarPhoneModule;
import com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragmentStartableModule;
import com.android.systemui.statusbar.policy.AospPolicyModule;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.domain.interactor.ZenModeInteractor;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.toast.ToastModule;
import com.android.systemui.touchpad.tutorial.TouchpadTutorialModule;
import com.android.systemui.unfold.SysUIUnfoldStartableModule;
import com.android.systemui.unfold.UnfoldTransitionModule;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.kotlin.SysUICoroutinesModule;
import com.android.systemui.volume.dagger.VolumeModule;
import com.android.systemui.wallpapers.dagger.WallpaperModule;

import com.google.android.systemui.smartspace.AlarmAppSearchController;
import com.google.android.systemui.smartspace.BcSmartspaceDataProvider;
import com.google.android.systemui.smartspace.dagger.SmartspaceStartableModule;
import com.google.android.systemui.smartspace.DateSmartspaceDataProvider;
import com.google.android.systemui.smartspace.KeyguardMediaViewController;
import com.google.android.systemui.smartspace.KeyguardZenAlarmViewController;
import com.google.android.systemui.smartspace.log.NextClockAlarmControllerLogger;
import com.google.android.systemui.smartspace.NextClockAlarmController;
import com.google.android.systemui.smartspace.WeatherSmartspaceDataProvider;

import com.statix.android.systemui.assist.StatixAssistManager;
import com.statix.android.systemui.biometrics.FingerprintInteractiveToAuthProviderImpl;
import com.statix.android.systemui.controls.controller.StatixControlsTileResourceConfigurationImpl;
import com.statix.android.systemui.power.dagger.StatixPowerModule;
import com.statix.android.systemui.qs.tileimpl.QSFactoryImplStatix;
import com.statix.android.systemui.qs.tileimpl.StatixQSModule;
import com.statix.android.systemui.statusbar.KeyguardIndicationControllerStatix;
import com.statix.android.systemui.statusbar.dagger.StatixCentralSurfacesModule;
import com.statix.android.systemui.statusbar.dagger.StatixStartCentralSurfacesModule;
import com.statix.android.systemui.theme.ThemeOverlayControllerStatix;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.inject.Named;
import javax.inject.Provider;

import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;

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
@Module(
        includes = {
            AccessibilityModule.class,
            AccessibilityRepositoryModule.class,
            AospPolicyModule.class,
            BatterySaverModule.class,
            BrightnessSliderModule.class,
            ClipboardOverlayOverrideModule.class,
            CollapsedStatusBarFragmentStartableModule.class,
            ConnectingDisplayViewModel.StartableModule.class,
            ContextualEducationModule.class,
            DefaultBlueprintModule.class,
            DeviceStateAutoRotateModule.class,
            EmergencyGestureModule.class,
            GestureModule.class,
            GlobalActionsModule.class,
            HeadsUpModule.class,
            KeyguardModule.class,
            KeyboardShortcutsModule.class,
            KeyguardBlueprintModule.class,
            KeyguardSectionsModule.class,
            KeyboardTouchpadTutorialModule.class,
            MediaModule.class,
            MediaMuteAwaitConnectionCli.StartableModule.class,
            MultiUserUtilsModule.class,
            NavigationBarControllerModule.class,
            NearbyMediaDevicesManager.StartableModule.class,
            QSModule.class,
            RearDisplayModule.class,
            RecentsModule.class,
            ReferenceNotificationsModule.class,
            NoopPosturingModule.class,
            ReferenceScreenshotModule.class,
            RotationLockModule.class,
            RotationLockNewModule.class,
            SceneContainerFrameworkModule.class,
            ScreenDecorationsModule.class,
            ShadeModule.class,
            SmartspaceStartableModule.class,
            SqueezeEffectRepositoryModule.class,
            ShortcutHelperModule.class,
            StatixCentralSurfacesModule.class,
            StatixStartCentralSurfacesModule.class,
            StatixPowerModule.class,
            StatixQSModule.class,
            StatusBarPhoneModule.class,
            SystemActionsModule.class,
            SysUICoroutinesModule.class,
            SysUIUnfoldStartableModule.class,
            ToastModule.class,
            TopLevelWindowEffectsModule.class,
            TouchpadTutorialModule.class,
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

    @BindsOptionalOf
    @Named(SmartspaceModule.GLANCEABLE_HUB_SMARTSPACE_DATA_PLUGIN)
    abstract BcSmartspaceDataPlugin optionalGlanceableHubSmartspaceDataPlugin();

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
            SensorPrivacyManager sensorPrivacyManager, UserTracker userTracker) {
        IndividualSensorPrivacyController spC =
                new IndividualSensorPrivacyControllerImpl(sensorPrivacyManager, userTracker);
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
    static Recents provideRecents(
            Context context,
            RecentsImplementation recentsImplementation,
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

    @Binds
    abstract AssistManager bindAssistManager(StatixAssistManager assistManager);

    /** */
    @Provides
    @IntoMap
    @Dependencies
    @ClassKey(SysuiStatusBarStateController.class)
    static Set<Class<? extends CoreStartable>> providesStatusBarStateControllerDeps() {
        return Set.of(CentralSurfaces.class);
    }

    @Provides
    @SysUISingleton
    static KeyguardZenAlarmViewController provideKeyguardZenAlarmViewController(
            Context context,
            @Named(SmartspaceModule.DATE_SMARTSPACE_DATA_PLUGIN) BcSmartspaceDataPlugin datePlugin,
            ZenModeController zenModeController,
            ZenModeInteractor zenModeInteractor,
            AlarmManager alarmManager,
            NextClockAlarmController nextClockAlarmController,
            @Main Handler handler,
            @Background CoroutineScope applicationScope) {
        return new KeyguardZenAlarmViewController(
                context,
                datePlugin,
                zenModeController,
                zenModeInteractor,
                alarmManager,
                nextClockAlarmController,
                handler,
                applicationScope);
    }

    @Provides
    @SysUISingleton
    static KeyguardMediaViewController provideKeyguardMediaViewController(
            Context context,
            UserTracker userTracker,
            @Named(SmartspaceModule.GLANCEABLE_HUB_SMARTSPACE_DATA_PLUGIN) BcSmartspaceDataPlugin plugin,
            @Main DelayableExecutor uiExecutor,
            NotificationMediaManager mediaManager) {
        return new KeyguardMediaViewController(context, userTracker, plugin, uiExecutor, mediaManager);
    }

    @Provides
    @SysUISingleton
    static BcSmartspaceDataPlugin provideBcSmartspaceDataPlugin() {
        return new BcSmartspaceDataProvider();
    }

    @Provides
    @SysUISingleton
    @Named(SmartspaceModule.DATE_SMARTSPACE_DATA_PLUGIN)
    static BcSmartspaceDataPlugin provideDateSmartspaceDataPlugin() {
        return new DateSmartspaceDataProvider();
    }

    @Provides
    @SysUISingleton
    @Named(SmartspaceModule.GLANCEABLE_HUB_SMARTSPACE_DATA_PLUGIN)
    static BcSmartspaceDataPlugin provideGlanceableHubSmartspaceDataPlugin() {
        return new BcSmartspaceDataProvider();
    }

    @Provides
    @SysUISingleton
    @Named(SmartspaceModule.WEATHER_SMARTSPACE_DATA_PLUGIN)
    static BcSmartspaceDataPlugin provideWeatherSmartspaceDataPlugin() {
        return new WeatherSmartspaceDataProvider();
    }

    @Provides
    @SysUISingleton
    static DateSmartspaceDataProvider provideDateSmartspaceDataProvider() {
        return new DateSmartspaceDataProvider();
    }

    @Provides
    @SysUISingleton
    static WeatherSmartspaceDataProvider provideWeatherSmartspaceDataProvider() {
        return new WeatherSmartspaceDataProvider();
    }

    @Provides
    @SysUISingleton
    static AlarmAppSearchController provideAlarmAppSearchController(
            @Main Executor mainExecutor,
            @Background CoroutineDispatcher bgDispatcher) {
        return new AlarmAppSearchController(mainExecutor, bgDispatcher);
    }

    @Provides
    @SysUISingleton
    static NextClockAlarmController provideNextClockAlarmController(
            UserTracker userTracker,
            BroadcastDispatcher broadcastDispatcher,
            DumpManager dumpManager,
            AlarmAppSearchController alarmAppSearchController,
            @Main Executor mainExecutor,
            @Application CoroutineScope applicationScope,
            @Background CoroutineScope backgroundScope) {
        return new NextClockAlarmController(
                userTracker,
                broadcastDispatcher,
                dumpManager,
                alarmAppSearchController,
                mainExecutor,
                applicationScope,
                backgroundScope);
    }

    @Provides
    @SysUISingleton
    static NextClockAlarmControllerLogger provideNextClockAlarmControllerLogger(
            LogBufferFactory factory) {
        return new NextClockAlarmControllerLogger(factory.create("NextClockAlarmControllerLog", 100));
    }
}
