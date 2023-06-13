/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.qs.tileimpl;

import android.content.Context;

import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTileView;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.qs.tileimpl.QSTileViewImpl;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;

/**
 * A factory that creates Quick Settings tiles based on a tileSpec
 *
 * To create a new tile within SystemUI, the tile class should extend {@link QSTileImpl} and have
 * a public static final TILE_SPEC field which serves as a unique key for this tile. (e.g. {@link
 * com.android.systemui.qs.tiles.DreamTile#TILE_SPEC})
 *
 * After, create or find an existing Module class to house the tile's binding method (e.g. {@link
 * com.android.systemui.accessibility.AccessibilityModule}). If creating a new module, add your
 * module to the SystemUI dagger graph by including it in an appropriate module.
 */
@SysUISingleton
public class QSFactoryImplStatix extends QSFactoryImpl {

    private static final String[] SLIDER_TILES = { "flashlight" };

    @Inject
    public QSFactoryImplStatix(
            Lazy<QSHost> qsHostLazy,
            Provider<CustomTile.Builder> customTileBuilderProvider,
            Map<String, Provider<QSTileImpl<?>>> tileMap) {
        super(qsHostLazy, customTileBuilderProvider, tileMap);
    }

    @Override
    public QSTileView createTileView(Context context, QSTile tile, boolean collapsedView) {
        QSIconView icon = tile.createTileView(context);
        if (Arrays.asList(SLIDER_TILES).contains(tile.getTileSpec())) {
            TouchableQSTile touchableTile = (TouchableQSTile) tile;
            return new SliderQSTileViewImpl(context, icon, collapsedView, touchableTile.getTouchListener(), touchableTile.getSettingsSystemKey());
        }
        return new QSTileViewImpl(context, icon, collapsedView);
    }
}
