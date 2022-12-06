/*
 * Copyright (C) 2021 The ProtonAOSP Project
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.biometrics

import com.android.systemui.biometrics.AuthController
import com.android.systemui.biometrics.UdfpsDisplayModeProvider
import com.android.systemui.dagger.SysUISingleton
import com.android.systemui.dagger.qualifiers.DisplayId
import javax.inject.Inject

@SysUISingleton
class StatixUdfpsDisplayModeProvider @Inject constructor(
    @DisplayId private val displayId: Int,
    private val authController: AuthController,
) : UdfpsDisplayModeProvider {

    override fun enable(onEnabled: Runnable?) {
        // Request AuthController to lock the refresh rate. On the Pixel 6 Pro (raven), LHBM only
        // works at peak refresh rate.
        authController.udfpsHbmListener?.onHbmEnabled(displayId)
        onEnabled?.run()
    }

    override fun disable(onDisabled: Runnable?) {
        // Unlock refresh rate
        authController.udfpsHbmListener?.onHbmDisabled(displayId)
        onDisabled?.run()
    }
}
