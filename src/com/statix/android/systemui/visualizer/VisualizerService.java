package com.statix.android.systemui.visualizer;

import static android.media.session.PlaybackState.STATE_PLAYING;

import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.PlaybackState;

import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;

public class VisualizerService {

    private KeyguardStateController mKeyguardStateController;
    private NotificationMediaManager mMediaManager;
    private VisualizerView mVisualizerView;

    private NotificationMediaManager.MediaListener mMediaListener = new NotificationMediaManager.MediaListener() {
        @Override
        public void onPrimaryMetadataOrStateChanged(MediaMetadata metadata,
                @PlaybackState.State int state) {
            // Only used for palette extraction so no need to have a high-res image.
            if (metadata != null) {
                Bitmap albumArt = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
                mVisualizerView.setBitmap(albumArt);
            }
            android.util.Log.d("VisualizerService", "new state: " + state);
            mVisualizerView.setPlaying(state == STATE_PLAYING);
        }
    };

    private final KeyguardStateController.Callback mKeyguardStateControllerCallback =
            new KeyguardStateController.Callback() {
                @Override
                public void onKeyguardShowingChanged() {
                    boolean occluded = mKeyguardStateController.isOccluded();
                    mVisualizerView.setOccluded(occluded);
                }
            };

    public VisualizerService(NotificationMediaManager manager, KeyguardStateController keyguardStateController, VisualizerView view) {
        mMediaManager = manager;
        mKeyguardStateController = keyguardStateController;
        mVisualizerView = view;
    }

    public void start() {
        mMediaManager.addCallback(mMediaListener);
        mKeyguardStateController.addCallback(mKeyguardStateControllerCallback);
    }

}
