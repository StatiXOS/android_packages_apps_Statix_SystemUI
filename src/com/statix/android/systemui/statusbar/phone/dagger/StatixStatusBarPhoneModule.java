/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.statix.android.systemui.statusbar.phone.dagger;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.statusbar.phone.CentralSurfaces;

import com.statix.android.systemui.statusbar.phone.StatixCentralSurfacesImpl;

import dagger.Binds;
import dagger.Module;

/**
 * Dagger Module providing {@link StatixCentralSurfacesImpl}.
 */
@Module
public interface StatixStatusBarPhoneModule {
    /**
     * Provides our instance of CentralSurfaces which is considered optional.
     */
    @Binds
    @SysUISingleton
    CentralSurfaces bindsCentralSurfaces(StatixCentralSurfacesImpl impl);
}
