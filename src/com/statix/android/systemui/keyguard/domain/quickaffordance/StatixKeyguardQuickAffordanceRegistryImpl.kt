package com.statix.android.systemui.keyguard.domain.quickaffordance

import com.android.systemui.keyguard.domain.model.KeyguardQuickAffordancePosition
import com.android.systemui.keyguard.domain.quickaffordance.HomeControlsKeyguardQuickAffordanceConfig
import com.android.systemui.keyguard.domain.quickaffordance.KeyguardQuickAffordanceConfig
import com.android.systemui.keyguard.domain.quickaffordance.KeyguardQuickAffordanceRegistry
import com.android.systemui.keyguard.domain.quickaffordance.KeyguardQuickAffordanceRegistryImpl
import com.android.systemui.keyguard.domain.quickaffordance.QrCodeScannerKeyguardQuickAffordanceConfig
import com.android.systemui.keyguard.domain.quickaffordance.QuickAccessWalletKeyguardQuickAffordanceConfig

import javax.inject.Inject
import kotlin.reflect.KClass

class StatixKeyguardQuickAffordanceRegistryImpl
@Inject
constructor(
    homeControls: HomeControlsKeyguardQuickAffordanceConfig,
    quickAccessWallet: QuickAccessWalletKeyguardQuickAffordanceConfig,
    qrCodeScanner: QrCodeScannerKeyguardQuickAffordanceConfig,
    camera: CameraKeyguardQuickAffordanceConfig,
    flashlight: FlashlightKeyguardQuickAffordanceConfig,
) : KeyguardQuickAffordanceRegistry<KeyguardQuickAffordanceConfig> {
    private val configsByPosition =
        mapOf(
            KeyguardQuickAffordancePosition.BOTTOM_START to
                listOf(
                    homeControls,
                    flashlight
                ),
            KeyguardQuickAffordancePosition.BOTTOM_END to
                listOf(
                    quickAccessWallet,
                    qrCodeScanner,
                    camera
                ),
        )
    private val configByClass =
        configsByPosition.values.flatten().associateBy { config -> config::class }

    override fun getAll(
        position: KeyguardQuickAffordancePosition,
    ): List<KeyguardQuickAffordanceConfig> {
        return configsByPosition.getValue(position)
    }

    override fun get(
        configClass: KClass<out KeyguardQuickAffordanceConfig>
    ): KeyguardQuickAffordanceConfig {
        return configByClass.getValue(configClass)
    }
}
