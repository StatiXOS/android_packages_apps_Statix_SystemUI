/*
 * Copyright (C) 2020 The Android Open Source Project
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.statusbar.dagger;

import com.android.systemui.statusbar.notification.dagger.NotificationsModule;
import com.android.systemui.statusbar.notification.row.NotificationRowModule;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule;
import com.statix.android.systemui.statusbar.phone.dagger.StatixStatusBarPhoneModule;

import dagger.Module;

/** */
@Module(includes = {StatixStatusBarPhoneModule.class, StatusBarDependenciesModule.class,
        NotificationsModule.class, NotificationRowModule.class})
public interface StatixStatusBarModule {
}
