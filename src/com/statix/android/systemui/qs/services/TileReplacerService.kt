package com.statix.android.systemui.qs.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

class TileReplacerService : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val curQsTiles = Settings.Secure.getString(context.contentResolver, QS_TILE_SETTING).split(",").toMutableList()
        curQsTiles.map { tileSpec ->
            if (QS_TILES_TO_REPLACE.containsKey(tileSpec)) {
                QS_TILES_TO_REPLACE[tileSpec]
            } else {
                tileSpec
            }
        }
        Settings.Secure.putString(context.contentResolver, QS_TILE_SETTING, curQsTiles.joinToString(separator = ","))
    }

    companion object {
        private val QS_TILE_SETTING = Settings.Secure.QS_TILES
        private val QS_TILES_TO_REPLACE = mapOf(
                "flashlight" to "flashlightstrength",
            )
    }

}
