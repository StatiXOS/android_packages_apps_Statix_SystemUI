/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.statix.android.systemui.dagger

import com.google.android.systemui.smartspace.KeyguardSmartspaceStartable

import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

/** Collection of {@link CoreStartable}s that should be run on AOSP. */
@Module
abstract class SystemUIStatixCoreStartableModule {
    /** Inject into KeyguardSmartspaceStartable. */
    @Binds
    @IntoMap
    @ClassKey(KeyguardSmartspaceStartable::class)
    abstract fun bindKeyguardSmartspaceStartable(sysui: KeyguardSmartspaceStartable): CoreStartable
}
