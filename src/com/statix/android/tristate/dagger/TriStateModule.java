/*
 * Copyright (C) 2022 Paranoid Android
 * Copyright (C) 2023 StatiXOS
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
package com.statix.android.systemui.tristate.dagger;

import com.statix.android.systemui.tristate.TriStateUiController;
import com.statix.android.systemui.tristate.TriStateUiControllerImpl;
import dagger.Binds;
import dagger.Module;

/**
 * Dagger Module for code in the tristate package.
 */
@Module
public interface TriStateModule {
    /** */
    @Binds
    TriStateUiController providesTriStateUiController(TriStateUiControllerImpl controller);
}
