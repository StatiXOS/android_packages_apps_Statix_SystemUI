package com.google.android.systemui.smartspace

import android.app.smartspace.SmartspaceAction
import android.app.smartspace.SmartspaceTarget
import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.os.UserHandle
import android.text.TextUtils
import android.view.View
import com.android.internal.annotations.VisibleForTesting
import com.android.systemui.R
import com.android.systemui.broadcast.BroadcastDispatcher
import com.android.systemui.plugins.BcSmartspaceDataPlugin
import com.android.systemui.settings.CurrentUserTracker
import com.android.systemui.statusbar.NotificationMediaManager
import com.android.systemui.util.concurrency.DelayableExecutor

class KeyguardMediaViewController constructor(val context: Context, val plugin: BcSmartspaceDataPlugin,
        val uiExecutor: DelayableExecutor, val mediaManager: NotificationMediaManager,
        val broadcastDispatcher: BroadcastDispatcher) {

        val mediaComponent: ComponentName = ComponentName(context, KeyguardMediaViewController::class.java)
        var artist: CharSequence? = null
        var smartspaceView: BcSmartspaceDataPlugin.SmartspaceView? = null
        var title: CharSequence? = null
        lateinit var userTracker: CurrentUserTracker

        val mediaListener = object : NotificationMediaManager.MediaListener {
            override fun onPrimaryMetadataOrStateChanged(mediaMetadata: MediaMetadata, i: Int) {
                uiExecutor.execute(object : Runnable {
                    override fun run() {
                        updateMediaInfo(mediaMetadata, i)
                    }
                })
            }
        }

        fun init() {
            plugin.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    smartspaceView = (v as BcSmartspaceDataPlugin.SmartspaceView)
                    mediaManager.addCallback(mediaListener)
                }
                override fun onViewDetachedFromWindow(v: View) {
                    smartspaceView = null
                    mediaManager.removeCallback(mediaListener)
                }
            })
            userTracker = object : CurrentUserTracker(broadcastDispatcher) {
                override fun onUserSwitched(i: Int) = reset()
            }

        }

        fun updateMediaInfo(mediaMetadata: MediaMetadata?, i: Int) {
            if (!NotificationMediaManager.isPlayingState(i)) {
                reset()
                return
            }
            var text: CharSequence?
            if (mediaMetadata == null) {
                text = null
            } else {
                text = mediaMetadata.getText("android.media.metadata.TITLE")
                if (TextUtils.isEmpty(text)) {
                    text = context.resources.getString(R.string.music_controls_no_title)
                }
            }
            var text2 = if (mediaMetadata == null) { null } else { mediaMetadata.getText("android.media.metadata.ARTIST") }
            if (TextUtils.equals(title, text) && TextUtils.equals(artist, text2)) {
                return
            }
            title = text
            artist = text2
            if (text != null) {
                val action = SmartspaceAction.Builder("deviceMediaTitle", text.toString()).setSubtitle(artist).setIcon(mediaManager.getMediaIcon()).build()
                val action2 = SmartspaceTarget.Builder("deviceMedia", mediaComponent, UserHandle.of(userTracker.getCurrentUserId())).setFeatureType(41).setHeaderAction(action).build()
                if (smartspaceView != null) {
                    smartspaceView!!.setMediaTarget(action2)
                    return
                }
            }
            reset()
        }

        fun reset() {
            artist = null
            title = null
            if (smartspaceView == null) {
                return
            }
            smartspaceView!!.setMediaTarget(null)
        }
}
