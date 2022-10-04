/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.qs.tileimpl;

import android.content.Context;

import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTileView;

// keep in sync with frameworks/base/packages/SystemUI/src/com/android/systemui/qs/tileimpl/QSFactoryImpl.java
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.qs.tileimpl.QSTileViewImpl;
import com.android.systemui.qs.tiles.AirplaneModeTile;
import com.android.systemui.qs.tiles.AlarmTile;
import com.android.systemui.qs.tiles.BatterySaverTile;
import com.android.systemui.qs.tiles.BluetoothTile;
import com.android.systemui.qs.tiles.CameraToggleTile;
import com.android.systemui.qs.tiles.CastTile;
import com.android.systemui.qs.tiles.CellularTile;
import com.android.systemui.qs.tiles.ColorCorrectionTile;
import com.android.systemui.qs.tiles.ColorInversionTile;
import com.android.systemui.qs.tiles.DataSaverTile;
import com.android.systemui.qs.tiles.DeviceControlsTile;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.FlashlightTile;
import com.android.systemui.qs.tiles.HotspotTile;
import com.android.systemui.qs.tiles.InternetTile;
import com.android.systemui.qs.tiles.LocationTile;
import com.android.systemui.qs.tiles.MicrophoneToggleTile;
import com.android.systemui.qs.tiles.NfcTile;
import com.android.systemui.qs.tiles.NightDisplayTile;
import com.android.systemui.qs.tiles.OneHandedModeTile;
import com.android.systemui.qs.tiles.QRCodeScannerTile;
import com.android.systemui.qs.tiles.QuickAccessWalletTile;
import com.android.systemui.qs.tiles.ReduceBrightColorsTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.qs.tiles.ScreenRecordTile;
import com.android.systemui.qs.tiles.UiModeNightTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.util.leak.GarbageMonitor;

// Custom tiles
import com.statix.android.systemui.qs.tiles.CaffeineTile;
import com.statix.android.systemui.qs.tiles.DataSwitchTile;
import com.statix.android.systemui.qs.tiles.FlashlightStrengthTile;
import com.statix.android.systemui.qs.tiles.GloveModeTile;
import com.statix.android.systemui.qs.tiles.PowerShareTile;
import com.statix.android.systemui.qs.tiles.SmartPixelsTile;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;

@SysUISingleton
public class QSFactoryImplStatix extends QSFactoryImpl {

    private static final String[] SLIDER_TILES = { "flashlightstrength" };
    private final Provider<CaffeineTile> mCaffeineTileProvider;
    private final Provider<DataSwitchTile> mDataSwitchTileProvider;
    private final Provider<FlashlightStrengthTile> mFlashlightStrengthTileProvider;
    private final Provider<GloveModeTile> mGloveModeTileProvider;
    private final Provider<PowerShareTile> mPowerShareTileProvider;
    private final Provider<SmartPixelsTile> mSmartPixelsTileProvider;

    @Inject
    public QSFactoryImplStatix(
            Lazy<QSHost> qsHostLazy,
            Provider<CustomTile.Builder> customTileBuilderProvider,
            Provider<WifiTile> wifiTileProvider,
            Provider<InternetTile> internetTileProvider,
            Provider<BluetoothTile> bluetoothTileProvider,
            Provider<CellularTile> cellularTileProvider,
            Provider<DndTile> dndTileProvider,
            Provider<ColorInversionTile> colorInversionTileProvider,
            Provider<AirplaneModeTile> airplaneModeTileProvider,
            Provider<WorkModeTile> workModeTileProvider,
            Provider<RotationLockTile> rotationLockTileProvider,
            Provider<FlashlightTile> flashlightTileProvider,
            Provider<LocationTile> locationTileProvider,
            Provider<CastTile> castTileProvider,
            Provider<HotspotTile> hotspotTileProvider,
            Provider<BatterySaverTile> batterySaverTileProvider,
            Provider<DataSaverTile> dataSaverTileProvider,
            Provider<NightDisplayTile> nightDisplayTileProvider,
            Provider<NfcTile> nfcTileProvider,
            Provider<GarbageMonitor.MemoryTile> memoryTileProvider,
            Provider<UiModeNightTile> uiModeNightTileProvider,
            Provider<ScreenRecordTile> screenRecordTileProvider,
            Provider<ReduceBrightColorsTile> reduceBrightColorsTileProvider,
            Provider<CameraToggleTile> cameraToggleTileProvider,
            Provider<MicrophoneToggleTile> microphoneToggleTileProvider,
            Provider<DeviceControlsTile> deviceControlsTileProvider,
            Provider<AlarmTile> alarmTileProvider,
            Provider<QuickAccessWalletTile> quickAccessWalletTileProvider,
            Provider<QRCodeScannerTile> qrCodeScannerTileProvider,
            Provider<OneHandedModeTile> oneHandedModeTileProvider,
            Provider<ColorCorrectionTile> colorCorrectionTileProvider,
            Provider<CaffeineTile> caffeineTileProvider,
            Provider<PowerShareTile> powerShareTileProvider,
            Provider<GloveModeTile> gloveModeTileProvider,
            Provider<SmartPixelsTile> smartPixelsTileProvider,
            Provider<DataSwitchTile> dataSwitchTileProvider,
            Provider<FlashlightStrengthTile> flashlightStrengthTileProvider) {
        super(qsHostLazy, customTileBuilderProvider, wifiTileProvider, internetTileProvider, bluetoothTileProvider, cellularTileProvider, dndTileProvider, colorInversionTileProvider,
            airplaneModeTileProvider, workModeTileProvider, rotationLockTileProvider, flashlightTileProvider, locationTileProvider, castTileProvider, hotspotTileProvider, batterySaverTileProvider,
            dataSaverTileProvider, nightDisplayTileProvider, nfcTileProvider, memoryTileProvider, uiModeNightTileProvider, screenRecordTileProvider, reduceBrightColorsTileProvider,
            cameraToggleTileProvider, microphoneToggleTileProvider, deviceControlsTileProvider, alarmTileProvider, quickAccessWalletTileProvider, qrCodeScannerTileProvider, oneHandedModeTileProvider,
            colorCorrectionTileProvider);
        // custom tile
        mCaffeineTileProvider = caffeineTileProvider;
        mDataSwitchTileProvider = dataSwitchTileProvider;
        mGloveModeTileProvider = gloveModeTileProvider;
        mPowerShareTileProvider = powerShareTileProvider;
        mSmartPixelsTileProvider = smartPixelsTileProvider;
        mFlashlightStrengthTileProvider = flashlightStrengthTileProvider;
    }

    private QSTileImpl createTileStatix(String tileSpec) {
        switch(tileSpec) {
            case "caffeine":
                return mCaffeineTileProvider.get();
            case "dataswitch":
                return mDataSwitchTileProvider.get();
            case "glovemode":
                return mGloveModeTileProvider.get();
            case "powershare":
                return mPowerShareTileProvider.get();
            case "smartpixels":
                return mSmartPixelsTileProvider.get();
            case "flashlightstrength":
                return mFlashlightStrengthTileProvider.get();
            default:
                return null;
        }
    }

    @Override
    protected QSTileImpl createTileInternal(String tileSpec) {
        QSTileImpl tile = createTileStatix(tileSpec);
        return tile != null ? tile : super.createTileInternal(tileSpec);
    }

    @Override
    public QSTileView createTileView(Context context, QSTile tile, boolean collapsedView) {
        QSIconView icon = tile.createTileView(context);
        if (Arrays.asList(SLIDER_TILES).contains(tile.getTileSpec())) {
            TouchableQSTileImpl touchableTile = (TouchableQSTileImpl) tile;
            return new SliderQSTileViewImpl(context, icon, collapsedView, touchableTile.getTouchListener(), touchableTile.getSettingsSystemKey());
        }
        return new QSTileViewImpl(context, icon, collapsedView);
    }
}
