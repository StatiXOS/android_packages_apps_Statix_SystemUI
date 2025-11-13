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

import com.android.systemui.qs.QsEventLogger
import com.android.systemui.qs.pipeline.shared.TileSpec
import com.android.systemui.qs.shared.model.TileCategory
import com.android.systemui.qs.tileimpl.QSTileImpl
import com.android.systemui.qs.tiles.base.shared.model.QSTileConfig
import com.android.systemui.qs.tiles.base.shared.model.QSTileUIConfig
import com.statix.android.systemui.qs.tiles.CaffeineTile
import com.statix.android.systemui.qs.tiles.DataSwitchTile
import com.statix.android.systemui.qs.tiles.GloveModeTile
import com.statix.android.systemui.qs.tiles.PowerShareTile
import com.statix.android.systemui.qs.tiles.SmartPixelsTile
import com.statix.android.systemui.res.R
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
interface StatixQSModule {

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

  companion object {

    const val CAFFEINE_TILE_SPEC = "caffeine"
    const val DATA_SWITCH_TILE_SPEC = "dataswitch"
    const val GLOVE_MODE_TILE_SPEC = "glovemode"
    const val NFC_TILE_SPEC = "nfc"
    const val POWERSHARE_TILE_SPEC = "powershare"
    const val SMART_PIXELS_TILE_SPEC = "smartpixels"

    @Provides
    @IntoMap
    @StringKey(CAFFEINE_TILE_SPEC)
    fun provideCaffeineTileConfig(uiEventLogger: QsEventLogger): QSTileConfig =
      QSTileConfig(
        tileSpec = TileSpec.create(CAFFEINE_TILE_SPEC),
        uiConfig =
          QSTileUIConfig.Resource(
            iconRes = R.drawable.ic_qs_caffeine,
            labelRes = R.string.quick_settings_caffeine_label,
          ),
        instanceId = uiEventLogger.getNewInstanceId(),
        category = TileCategory.DISPLAY,
      )

    @Provides
    @IntoMap
    @StringKey(DATA_SWITCH_TILE_SPEC)
    fun provideDataSwitchTileConfig(uiEventLogger: QsEventLogger): QSTileConfig =
      QSTileConfig(
        tileSpec = TileSpec.create(DATA_SWITCH_TILE_SPEC),
        uiConfig =
          QSTileUIConfig.Resource(
            iconRes = R.drawable.ic_qs_data_switch_1,
            labelRes = R.string.qs_data_switch_label,
          ),
        instanceId = uiEventLogger.getNewInstanceId(),
        category = TileCategory.CONNECTIVITY,
      )

    @Provides
    @IntoMap
    @StringKey(GLOVE_MODE_TILE_SPEC)
    fun provideGloveModeTileConfig(uiEventLogger: QsEventLogger): QSTileConfig =
      QSTileConfig(
        tileSpec = TileSpec.create(GLOVE_MODE_TILE_SPEC),
        uiConfig =
          QSTileUIConfig.Resource(
            iconRes = R.drawable.ic_qs_glove_mode,
            labelRes = R.string.quick_settings_glove_mode_label,
          ),
        instanceId = uiEventLogger.getNewInstanceId(),
        category = TileCategory.UTILITIES,
      )

    @Provides
    @IntoMap
    @StringKey(NFC_TILE_SPEC)
    fun provideNfcTileConfig(uiEventLogger: QsEventLogger): QSTileConfig =
      QSTileConfig(
        tileSpec = TileSpec.create(NFC_TILE_SPEC),
        uiConfig =
          QSTileUIConfig.Resource(
            iconRes = R.drawable.ic_qs_nfc,
            labelRes = com.android.systemui.res.R.string.quick_settings_nfc_label,
          ),
        instanceId = uiEventLogger.getNewInstanceId(),
        category = TileCategory.CONNECTIVITY,
      )

    @Provides
    @IntoMap
    @StringKey(POWERSHARE_TILE_SPEC)
    fun providePowerShareTileConfig(uiEventLogger: QsEventLogger): QSTileConfig =
      QSTileConfig(
        tileSpec = TileSpec.create(POWERSHARE_TILE_SPEC),
        uiConfig =
          QSTileUIConfig.Resource(
            iconRes = R.drawable.ic_qs_powershare,
            labelRes = R.string.quick_settings_powershare_label,
          ),
        instanceId = uiEventLogger.getNewInstanceId(),
        category = TileCategory.UTILITIES,
      )

    @Provides
    @IntoMap
    @StringKey(SMART_PIXELS_TILE_SPEC)
    fun provideSmartPixelsTileConfig(uiEventLogger: QsEventLogger): QSTileConfig =
      QSTileConfig(
        tileSpec = TileSpec.create(SMART_PIXELS_TILE_SPEC),
        uiConfig =
          QSTileUIConfig.Resource(
            iconRes = R.drawable.ic_qs_smart_pixels,
            labelRes = R.string.quick_settings_smart_pixels,
          ),
        instanceId = uiEventLogger.getNewInstanceId(),
        category = TileCategory.DISPLAY,
      )
  }
}
