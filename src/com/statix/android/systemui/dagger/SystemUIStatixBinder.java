/*
 * Copyright (C) 2021 The Pixel Experience Project
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.dagger;

import android.app.Activity;
import android.app.Service;
import com.android.systemui.LatencyTester;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.SliceBroadcastRelayHandler;
import com.android.systemui.SystemUI;
import com.android.systemui.accessibility.SystemActions;
import com.android.systemui.accessibility.WindowMagnification;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.globalactions.GlobalActionsComponent;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.dagger.KeyguardModule;
import com.android.systemui.media.systemsounds.HomeSoundEffectController;
import com.android.systemui.power.PowerUI;
import com.android.systemui.privacy.television.TvOngoingPrivacyChip;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsModule;
import com.android.systemui.shortcut.ShortcutKeyDispatcher;
import com.android.systemui.statusbar.notification.InstantAppNotifier;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.tv.TvStatusBar;
import com.android.systemui.statusbar.tv.notifications.TvNotificationPanel;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.toast.ToastUI;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.volume.VolumeUI;
import com.android.systemui.wmshell.WMShell;
import com.android.systemui.dagger.SysUISingleton;

import com.google.android.systemui.columbus.ColumbusTargetRequestService;

import com.statix.android.systemui.StatixServices;
import com.statix.android.systemui.statusbar.dagger.StatixStatusBarModule;
import com.statix.android.systemui.theme.ThemeOverlayControllerStatix;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(includes = {RecentsModule.class, StatixStatusBarModule.class, KeyguardModule.class})
public abstract class SystemUIStatixBinder {
    /**
     * Inject into AuthController.
     */
    @Binds
    @IntoMap
    @ClassKey(AuthController.class)
    public abstract SystemUI bindAuthController(AuthController service);

    /**
     * Inject into GarbageMonitor.Service.
     */
    @Binds
    @IntoMap
    @ClassKey(GarbageMonitor.Service.class)
    public abstract SystemUI bindGarbageMonitorService(GarbageMonitor.Service sysui);

    /**
     * Inject into GlobalActionsComponent.
     */
    @Binds
    @IntoMap
    @ClassKey(GlobalActionsComponent.class)
    public abstract SystemUI bindGlobalActionsComponent(GlobalActionsComponent sysui);

    /**
     * Inject into InstantAppNotifier.
     */
    @Binds
    @IntoMap
    @ClassKey(InstantAppNotifier.class)
    public abstract SystemUI bindInstantAppNotifier(InstantAppNotifier sysui);

    /**
     * Inject into KeyguardViewMediator.
     */
    @Binds
    @IntoMap
    @ClassKey(KeyguardViewMediator.class)
    public abstract SystemUI bindKeyguardViewMediator(KeyguardViewMediator sysui);

    /**
     * Inject into LatencyTests.
     */
    @Binds
    @IntoMap
    @ClassKey(LatencyTester.class)
    public abstract SystemUI bindLatencyTester(LatencyTester sysui);

    /**
     * Inject into PowerUI.
     */
    @Binds
    @IntoMap
    @ClassKey(PowerUI.class)
    public abstract SystemUI bindPowerUI(PowerUI sysui);

    /**
     * Inject into Recents.
     */
    @Binds
    @IntoMap
    @ClassKey(Recents.class)
    public abstract SystemUI bindRecents(Recents sysui);

    /**
     * Inject into ScreenDecorations.
     */
    @Binds
    @IntoMap
    @ClassKey(ScreenDecorations.class)
    public abstract SystemUI bindScreenDecorations(ScreenDecorations sysui);

    /**
     * Inject into ShortcutKeyDispatcher.
     */
    @Binds
    @IntoMap
    @ClassKey(ShortcutKeyDispatcher.class)
    public abstract SystemUI bindsShortcutKeyDispatcher(ShortcutKeyDispatcher sysui);

    /**
     * Inject into SliceBroadcastRelayHandler.
     */
    @Binds
    @IntoMap
    @ClassKey(SliceBroadcastRelayHandler.class)
    public abstract SystemUI bindSliceBroadcastRelayHandler(SliceBroadcastRelayHandler sysui);

    /**
     * Inject into StatusBar.
     */
    @Binds
    @IntoMap
    @ClassKey(StatusBar.class)
    public abstract SystemUI bindsStatusBar(StatusBar sysui);

    /**
     * Inject into SystemActions.
     */
    @Binds
    @IntoMap
    @ClassKey(SystemActions.class)
    public abstract SystemUI bindSystemActions(SystemActions sysui);

    /**
     * Inject into ThemeOverlayController.
     */
    @Binds
    @IntoMap
    @ClassKey(ThemeOverlayController.class)
    public abstract SystemUI bindThemeOverlayController(ThemeOverlayControllerStatix sysui);

    /**
     * Inject into ToastUI.
     */
    @Binds
    @IntoMap
    @ClassKey(ToastUI.class)
    public abstract SystemUI bindToastUI(ToastUI service);

    /**
     * Inject into TvStatusBar.
     */
    @Binds
    @IntoMap
    @ClassKey(TvStatusBar.class)
    public abstract SystemUI bindsTvStatusBar(TvStatusBar sysui);

    /**
     * Inject into TvNotificationPanel.
     */
    @Binds
    @IntoMap
    @ClassKey(TvNotificationPanel.class)
    public abstract SystemUI bindsTvNotificationPanel(TvNotificationPanel sysui);

    /**
     * Inject into TvOngoingPrivacyChip.
     */
    @Binds
    @IntoMap
    @ClassKey(TvOngoingPrivacyChip.class)
    public abstract SystemUI bindsTvOngoingPrivacyChip(TvOngoingPrivacyChip sysui);

    /**
     * Inject into VolumeUI.
     */
    @Binds
    @IntoMap
    @ClassKey(VolumeUI.class)
    public abstract SystemUI bindVolumeUI(VolumeUI sysui);

    /**
     * Inject into WindowMagnification.
     */
    @Binds
    @IntoMap
    @ClassKey(WindowMagnification.class)
    public abstract SystemUI bindWindowMagnification(WindowMagnification sysui);

    /**
     * Inject into WMShell.
     */
    @Binds
    @IntoMap
    @ClassKey(WMShell.class)
    public abstract SystemUI bindWMShell(WMShell sysui);

    /**
     * Inject into HomeSoundEffectController.
     */
    @Binds
    @IntoMap
    @ClassKey(HomeSoundEffectController.class)
    public abstract SystemUI bindHomeSoundEffectController(HomeSoundEffectController sysui);

    /**
     * Inject into StatixServices.
     */
    @Binds
    @IntoMap
    @ClassKey(StatixServices.class)
    public abstract SystemUI bindStatixServices(StatixServices sysui);

    /**
     * Inject into ColumbusTargetRequestService.
     */
    @Binds
    @IntoMap
    @ClassKey(ColumbusTargetRequestService.class)
    public abstract Service bindColumbusTargetRequestService(ColumbusTargetRequestService activity);
}
