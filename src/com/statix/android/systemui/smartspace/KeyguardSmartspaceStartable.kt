/*
 * Copyright (C) 2025 auroraOSP
 * SPDX-License-Identifier: Apache-2.0
 */
package com.google.android.systemui.smartspace

import com.android.systemui.CoreStartable
import com.android.systemui.util.InitializationChecker
import javax.inject.Inject
import kotlinx.coroutines.launch

class KeyguardSmartspaceStartable
@Inject
constructor(
    val zenController: KeyguardZenAlarmViewController,
    val mediaController: KeyguardMediaViewController,
    val initializationChecker: InitializationChecker,
) : CoreStartable {

    override fun start() {
        if (!initializationChecker.initializeComponents()) {
            return
        }

        zenController.datePlugin.addOnAttachStateChangeListener(
            object : android.view.View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: android.view.View) {
                    zenController.onViewAttachedToWindow(v)
                }

                override fun onViewDetachedFromWindow(v: android.view.View) {
                    zenController.onViewDetachedFromWindow(v)
                }
            }
        )

        with(zenController.nextClockAlarmController) {
            if (isUserUnlocked()) {
                updateSession(userTracker.userContext)
            }
            dumpManager.registerNormalDumpable(TAG, this)
            userTracker.addCallback(userChangedCallback, mainExecutor)
            applicationScope.launch { zenController.updateNextAlarm() }
        }

        mediaController.plugin.addOnAttachStateChangeListener(
            object : android.view.View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: android.view.View) {
                    mediaController.onViewAttachedToWindow(v)
                }

                override fun onViewDetachedFromWindow(v: android.view.View) {
                    mediaController.onViewDetachedFromWindow(v)
                }
            }
        )
    }

    companion object {
        const val TAG = "NextClockAlarmCtlr"
    }
}
