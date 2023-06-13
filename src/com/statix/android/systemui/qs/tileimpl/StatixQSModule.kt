/*
 * Copyright (C) 2023 StatiXOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package com.statix.android.systemui.qs.tileimpl

import com.android.systemui.qs.tileimpl.QSTileImpl

import com.statix.android.systemui.qs.tiles.BluetoothDialogTile
import com.statix.android.systemui.qs.tiles.CaffeineTile
import com.statix.android.systemui.qs.tiles.DataSwitchTile
import com.statix.android.systemui.qs.tiles.FlashlightStrengthTile
import com.statix.android.systemui.qs.tiles.GloveModeTile
import com.statix.android.systemui.qs.tiles.PowerShareTile
import com.statix.android.systemui.qs.tiles.SmartPixelsTile
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
interface StatixQSModule {

    /** Inject BluetoothDialogTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(BluetoothDialogTile.TILE_SPEC)
    fun bindBluetoothDialogTile(bluetoothDialogTile: BluetoothDialogTile): QSTileImpl<*>

    /** Inject CaffeineTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(CaffeineTile.TILE_SPEC)
    fun bindCaffeineTile(caffeineTile: CaffeineTile): QSTileImpl<*>

    /** Inject DataSwitchTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(DataSwitchTile.TILE_SPEC)
    fun bindDataSwitchTile(dataSwitchTile: DataSwitchTile): QSTileImpl<*>

    /** Inject FlashlightStrengthTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(FlashlightStrengthTile.TILE_SPEC)
    fun bindFlashlightStrengthTile(flashlightStrengthTile: FlashlightStrengthTile): QSTileImpl<*>

    /** Inject GloveModeTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(GloveModeTile.TILE_SPEC)
    fun bindGloveModeTile(gloveModeTile: GloveModeTile): QSTileImpl<*>


    /** Inject PowerShareTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(PowerShareTile.TILE_SPEC)
    fun bindPowerShareTile(powerShareTile: PowerShareTile): QSTileImpl<*>

    /** Inject SmartPixelsTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(SmartPixelsTile.TILE_SPEC)
    fun bindSmartPixelsTile(smartPixelsTile: SmartPixelsTile): QSTileImpl<*>
}
