package com.google.android.systemui.smartspace

import android.app.smartspace.SmartspaceAction
import android.app.smartspace.SmartspaceTarget
import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.os.UserHandle
import android.text.TextUtils
import com.android.systemui.media.NotificationMediaManager
import com.android.systemui.plugins.BcSmartspaceDataPlugin
import com.android.systemui.res.R
import com.android.systemui.settings.UserTracker
import com.android.systemui.util.concurrency.DelayableExecutor
import javax.inject.Inject

class KeyguardMediaViewController
@Inject
constructor(
    val context: Context,
    val userTracker: UserTracker,
    val plugin: BcSmartspaceDataPlugin,
    val uiExecutor: DelayableExecutor,
    val mediaManager: NotificationMediaManager,
) {
    var smartspaceView: BcSmartspaceDataPlugin.SmartspaceView? = null
    var title: CharSequence? = null
    var artist: CharSequence? = null
    val mediaComponent = ComponentName(context, KeyguardMediaViewController::class.java)
    val mediaListener =
        object : NotificationMediaManager.MediaListener {
            override fun onPrimaryMetadataOrStateChanged(metadata: MediaMetadata?, state: Int) {
                uiExecutor.execute { updateMediaInfo(metadata, state) }
            }
        }

    fun updateMediaInfo(metadata: MediaMetadata?, state: Int) {
        if (!NotificationMediaManager.isPlayingState(state)) {
            title = null
            artist = null
            smartspaceView?.setMediaTarget(null)
            return
        }
        val newTitle =
            metadata?.let {
                val displayTitle = it.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                if (TextUtils.isEmpty(displayTitle)) {
                    it.getText(MediaMetadata.METADATA_KEY_TITLE)
                } else {
                    displayTitle
                } ?: context.resources.getString(R.string.music_controls_no_title)
            }
        val newArtist = metadata?.getText(MediaMetadata.METADATA_KEY_ARTIST)
        if (TextUtils.equals(title, newTitle) && TextUtils.equals(artist, newArtist)) {
            return
        }
        title = newTitle
        artist = newArtist
        if (newTitle != null) {
            val action =
                SmartspaceAction.Builder("deviceMediaTitle", newTitle.toString())
                    .setSubtitle(newArtist)
                    .setIcon(mediaManager.getMediaIcon())
                    .build()
            val target =
                SmartspaceTarget.Builder(
                        "deviceMedia",
                        mediaComponent,
                        UserHandle.of(userTracker.userId),
                    )
                    .setFeatureType(SmartspaceTarget.FEATURE_MEDIA)
                    .setHeaderAction(action)
                    .build()
            smartspaceView?.setMediaTarget(target)
        } else {
            title = null
            artist = null
            smartspaceView?.setMediaTarget(null)
        }
    }

    fun onViewAttachedToWindow(v: android.view.View) {
        smartspaceView = v as BcSmartspaceDataPlugin.SmartspaceView
        mediaManager.addCallback(mediaListener)
        mediaManager.findAndUpdateMediaNotifications()
    }

    fun onViewDetachedFromWindow(v: android.view.View) {
        smartspaceView = null
        mediaManager.removeCallback(mediaListener)
    }
}
