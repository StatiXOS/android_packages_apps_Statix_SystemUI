/*
 * Copyright (C) 2025 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.screenrecord;

import android.annotation.Nullable;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.MediaRecorder;
import android.media.projection.StopReason;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.dagger.qualifiers.LongRunning;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.mediaprojection.MediaProjectionCaptureTarget;
import com.android.systemui.recordissue.ScreenRecordingStartTimeStore;
import com.android.systemui.screenrecord.Events;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.screenrecord.RecordingServiceStrings;
import com.android.systemui.screenrecord.ScreenMediaRecorder;
import com.android.systemui.screenrecord.ScreenMediaRecorder.SavedRecording;
import com.android.systemui.screenrecord.ScreenRecordingAudioSource;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;

import com.statix.android.systemui.res.R;

import java.io.IOException;
import java.util.concurrent.Executor;

import javax.inject.Inject;

/**
 * A service which records the device screen and optionally microphone input.
 */
public class StatixRecordingService extends RecordingService {
    private static final int USER_ID_NOT_SPECIFIED = -1;
    private static final String ACTION_DELETE = "com.android.systemui.screenrecord.DELETE";
    private static final String TAG = "StatixRecordingService";
    private static final String CHANNEL_ID = "screen_record";
    private static final String GROUP_KEY_ERROR_STARTING = "screen_record_error_starting";
    private static final String GROUP_KEY_SAVED = "screen_record_saved";
    private static final String EXTRA_RESULT_CODE = "extra_resultCode";
    private static final String EXTRA_AUDIO_SOURCE = "extra_useAudio";
    private static final String EXTRA_SHOW_TAPS = "extra_showTaps";
    private static final String EXTRA_CAPTURE_TARGET = "extra_captureTarget";
    private static final String EXTRA_DISPLAY_ID = "extra_displayId";
    private static final String EXTRA_STOP_REASON = "extra_stopReason";
    private static final String PERMISSION_SELF = "com.android.systemui.permission.SELF";

    private final NotificationManager mNotificationManager;
    private final KeyguardDismissUtil mKeyguardDismissUtil;
    private final UserContextProvider mUserContextTracker;
    private final RecordingController mController;
    private final Handler mMainHandler;
    private ScreenRecordingAudioSource mAudioSource = ScreenRecordingAudioSource.NONE;
    private boolean mShowTaps;
    private boolean mOriginalShowTaps;
    private ScreenMediaRecorder mRecorder;
    private final ScreenRecordingStartTimeStore mScreenRecordingStartTimeStore;
    private final Executor mLongExecutor;
    private final UiEventLogger mUiEventLogger;
    private RecordingServiceStrings mStrings;

    @Inject
    public StatixRecordingService(RecordingController controller, @LongRunning Executor executor,
            @Main Handler handler, UiEventLogger uiEventLogger,
            NotificationManager notificationManager,
            UserContextProvider userContextTracker, KeyguardDismissUtil keyguardDismissUtil,
            ScreenRecordingStartTimeStore screenRecordingStartTimeStore) {
        super(
            controller,
            executor,
            handler,
            uiEventLogger,
            notificationManager,
            userContextTracker,
            keyguardDismissUtil,
            screenRecordingStartTimeStore);
        mController = controller;
        mLongExecutor = executor;
        mMainHandler = handler;
        mUiEventLogger = uiEventLogger;
        mNotificationManager = notificationManager;
        mUserContextTracker = userContextTracker;
        mKeyguardDismissUtil = keyguardDismissUtil;
        mScreenRecordingStartTimeStore = screenRecordingStartTimeStore;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_NOT_STICKY;
        }
        String action = intent.getAction();
        Log.d(getTag(), "onStartCommand " + action);
        NotificationChannel channel = new NotificationChannel(
                getChannelId(),
                getString(com.android.systemui.res.R.string.screenrecord_title),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(getString(com.android.systemui.res.R.string.screenrecord_channel_description));
        channel.enableVibration(true);
        mNotificationManager.createNotificationChannel(channel);

        int currentUid = Process.myUid();
        int currentUserId = mUserContextTracker.getUserContext().getUserId();
        UserHandle currentUser = new UserHandle(currentUserId);
        switch (action) {
            case ACTION_START:
                // Get a unique ID for this recording's notifications
                mNotificationId = NOTIF_BASE_ID + (int) SystemClock.uptimeMillis();
                mAudioSource = ScreenRecordingAudioSource
                        .values()[intent.getIntExtra(EXTRA_AUDIO_SOURCE, 0)];
                Log.d(getTag(), "recording with audio source " + mAudioSource);
                mShowTaps = intent.getBooleanExtra(EXTRA_SHOW_TAPS, false);
                MediaProjectionCaptureTarget captureTarget =
                        intent.getParcelableExtra(EXTRA_CAPTURE_TARGET,
                                MediaProjectionCaptureTarget.class);

                mOriginalShowTaps = Settings.System.getInt(
                        getApplicationContext().getContentResolver(),
                        Settings.System.SHOW_TOUCHES, 0) != 0;
                int displayId = intent.getIntExtra(EXTRA_DISPLAY_ID, Display.DEFAULT_DISPLAY);

                setTapsVisible(mShowTaps);

                mRecorder = new ScreenMediaRecorder(
                        mUserContextTracker.getUserContext(),
                        mMainHandler,
                        currentUid,
                        mAudioSource,
                        captureTarget,
                        displayId,
                        this,
                        mScreenRecordingStartTimeStore
                );

                if (startRecording()) {
                    updateState(true);
                    createRecordingNotification();
                    mUiEventLogger.log(Events.ScreenRecordEvent.SCREEN_RECORD_START);
                } else {
                    updateState(false);
                    createErrorStartingNotification(currentUser);
                    stopForeground(STOP_FOREGROUND_DETACH);
                    stopSelf();
                    return Service.START_NOT_STICKY;
                }
                break;
            case ACTION_SHOW_START_NOTIF:
                createRecordingNotification();
                mUiEventLogger.log(Events.ScreenRecordEvent.SCREEN_RECORD_START);
                break;
            case ACTION_STOP_NOTIF:
            case ACTION_STOP:
                // only difference for actions is the log event
                if (ACTION_STOP_NOTIF.equals(action)) {
                    mUiEventLogger.log(Events.ScreenRecordEvent.SCREEN_RECORD_END_NOTIFICATION);
                } else {
                    mUiEventLogger.log(Events.ScreenRecordEvent.SCREEN_RECORD_END_QS_TILE);
                }
                // Check user ID - we may be getting a stop intent after user switch, in which case
                // we want to post the notifications for that user, which is NOT current user
                int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, USER_ID_NOT_SPECIFIED);
                int stopReason = intent.getIntExtra(EXTRA_STOP_REASON, mController.getStopReason());
                stopService(userId, stopReason);
                break;

            case ACTION_SHARE:
                Uri shareUri = intent.getParcelableExtra(EXTRA_PATH, Uri.class);

                Intent shareIntent = new Intent(Intent.ACTION_SEND)
                        .setType("video/mp4")
                        .putExtra(Intent.EXTRA_STREAM, shareUri);
                mKeyguardDismissUtil.executeWhenUnlocked(() -> {
                    String shareLabel = strings().getShareLabel();
                    startActivity(Intent.createChooser(shareIntent, shareLabel)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    // Remove notification
                    mNotificationManager.cancelAsUser(null, mNotificationId, currentUser);
                    return false;
                }, false, false);

                // Close quick shade
                closeSystemDialogs();
                break;

            case ACTION_DELETE:
                ContentResolver resolver = getContentResolver();
                Uri uri = Uri.parse(intent.getStringExtra(EXTRA_PATH));
                resolver.delete(uri, null, null);

                Toast.makeText(this, R.string.screenrecord_delete_description, Toast.LENGTH_LONG)
                        .show();

                // Remove notification
                mNotificationManager.cancelAsUser(null, mNotificationId, currentUser);
                Log.d(TAG, "Deleted recording " + uri);
                stopSelf();
                stopForeground(STOP_FOREGROUND_DETACH);

                // Close quick shade
                closeSystemDialogs();
                break;
        }
        return Service.START_STICKY;
    }

    private void updateState(boolean state) {
        int userId = mUserContextTracker.getUserContext().getUserId();
        if (userId == UserHandle.USER_SYSTEM) {
            // Main user has a reference to the correct controller, so no need to use a broadcast
            mController.updateState(state);
        } else {
            Intent intent = new Intent(RecordingController.INTENT_UPDATE_STATE);
            intent.putExtra(RecordingController.EXTRA_STATE, state);
            intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            sendBroadcast(intent, PERMISSION_SELF);
        }
    }

    /**
     * Begin the recording session
     * @return true if successful, false if something went wrong
     */
    private boolean startRecording() {
        try {
            getRecorder().start();
            return true;
        } catch (IOException | RemoteException | RuntimeException e) {
            showErrorToast(com.android.systemui.res.R.string.screenrecord_start_error);
            e.printStackTrace();
        }
        return false;
    }

    private void createErrorNotification(
            UserHandle currentUser,
            String notificationContentTitle,
            String groupKey,
            int notificationIdForGroup) {
        // Make sure error notifications get their own group.
        postGroupSummaryNotification(
                currentUser, notificationContentTitle, groupKey, notificationIdForGroup);

        Bundle extras = new Bundle();
        extras.putString(Notification.EXTRA_SUBSTITUTE_APP_NAME, strings().getTitle());

        Notification.Builder builder = new Notification.Builder(this, getChannelId())
                .setSmallIcon(com.android.systemui.res.R.drawable.ic_screenrecord)
                .setContentTitle(notificationContentTitle)
                .setGroup(groupKey)
                .addExtras(extras);
        startForeground(mNotificationId, builder.build());
    }

    @Override
    @VisibleForTesting
    protected Notification createSaveNotification(@Nullable SavedRecording recording) {
        Uri uri = recording != null ? recording.getUri() : null;
        Intent viewIntent = new Intent(Intent.ACTION_VIEW)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(uri, "video/mp4");

        Notification.Action shareAction = new Notification.Action.Builder(
                Icon.createWithResource(this, com.android.systemui.res.R.drawable.ic_screenrecord),
                strings().getShareLabel(),
                PendingIntent.getService(
                        this,
                        REQUEST_CODE,
                        getShareIntent(this, uri),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .build();

        Notification.Action deleteAction = new Notification.Action.Builder(
                Icon.createWithResource(this, com.android.systemui.res.R.drawable.ic_screenrecord),
                getResources().getString(R.string.screenrecord_delete_label),
                PendingIntent.getService(
                        this,
                        REQUEST_CODE,
                        getDeleteIntent(this, recording.getUri().toString()),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .build();

        Bundle extras = new Bundle();
        extras.putString(Notification.EXTRA_SUBSTITUTE_APP_NAME, strings().getTitle());

        Notification.Builder builder = new Notification.Builder(this, getChannelId())
                .setSmallIcon(com.android.systemui.res.R.drawable.ic_screenrecord)
                .setContentTitle(strings().getSaveTitle())
                .setContentText(strings().getSaveText())
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        REQUEST_CODE,
                        viewIntent,
                        PendingIntent.FLAG_IMMUTABLE))
                .setActions(shareAction, deleteAction)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_SAVED)
                .addExtras(extras);

        // Add thumbnail if available
        Icon thumbnail = recording != null ? recording.getThumbnail() : null;
        if (thumbnail != null) {
            Notification.BigPictureStyle pictureStyle = new Notification.BigPictureStyle()
                    .bigPicture(thumbnail)
                    .showBigPictureWhenCollapsed(true);
            builder.setStyle(pictureStyle);
        }
        return builder.build();
    }

    /**
     * Posts a group summary notification for the given group.
     *
     * Notifications that should be grouped:
     *  - Save notifications
     *  - Error saving notifications
     *  - Error starting notifications
     *
     * The foreground service recording notification should never be grouped.
     */
    private void postGroupSummaryNotification(
            UserHandle currentUser,
            String notificationContentTitle,
            String groupKey,
            int notificationIdForGroup) {
        Bundle extras = new Bundle();
        extras.putString(Notification.EXTRA_SUBSTITUTE_APP_NAME,
                strings().getTitle());
        Notification groupNotif = new Notification.Builder(this, getChannelId())
                .setSmallIcon(com.android.systemui.res.R.drawable.ic_screenrecord)
                .setContentTitle(notificationContentTitle)
                .setGroup(groupKey)
                .setGroupSummary(true)
                .setExtras(extras)
                .build();
        mNotificationManager.notifyAsUser(
                getTag(), notificationIdForGroup, groupNotif, currentUser);
    }

    private void stopService(@StopReason int stopReason) {
        stopService(USER_ID_NOT_SPECIFIED, stopReason);
    }

    private void stopService(int userId, @StopReason int stopReason) {
        if (userId == USER_ID_NOT_SPECIFIED) {
            userId = mUserContextTracker.getUserContext().getUserId();
        }
        UserHandle currentUser = new UserHandle(userId);
        Log.d(getTag(), "notifying for user " + userId);
        setTapsVisible(mOriginalShowTaps);
        try {
            if (getRecorder() != null) {
                getRecorder().end(stopReason);
            }
            saveRecording(userId);
        } catch (RuntimeException exception) {
            if (getRecorder() != null) {
                // RuntimeException could happen if the recording stopped immediately after starting
                // let's release the recorder and delete all temporary files in this case
                getRecorder().release();
            }
            showErrorToast(com.android.systemui.res.R.string.screenrecord_save_error);
            Log.e(getTag(), "stopRecording called, but there was an error when ending"
                    + "recording");
            exception.printStackTrace();
            createErrorSavingNotification(currentUser);
        } catch (Throwable throwable) {
            if (getRecorder() != null) {
                // Something unexpected happen, SystemUI will crash but let's delete
                // the temporary files anyway
                getRecorder().release();
            }
            throw new RuntimeException(throwable);
        }
        updateState(false);
        stopForeground(STOP_FOREGROUND_DETACH);
        stopSelf();
    }

    private void saveRecording(int userId) {
        UserHandle currentUser = new UserHandle(userId);
        mNotificationManager.notifyAsUser(null, mNotificationId,
                createProcessingNotification(), currentUser);

        mLongExecutor.execute(() -> {
            try {
                Log.d(getTag(), "saving recording");
                SavedRecording savedRecording = getRecorder() != null ? getRecorder().save() : null;
                postGroupSummaryNotification(
                        currentUser,
                        strings().getSaveTitle(),
                        GROUP_KEY_SAVED,
                        NOTIF_GROUP_ID_SAVED);
                onRecordingSaved(savedRecording, currentUser);
            } catch (IOException | IllegalStateException e) {
                Log.e(getTag(), "Error saving screen recording: " + e.getMessage());
                e.printStackTrace();
                showErrorToast(com.android.systemui.res.R.string.screenrecord_save_error);
                mNotificationManager.cancelAsUser(null, mNotificationId, currentUser);
            }
        });
    }

    private void setTapsVisible(boolean turnOn) {
        int value = turnOn ? 1 : 0;
        Settings.System.putInt(getContentResolver(), Settings.System.SHOW_TOUCHES, value);
    }

    private RecordingServiceStrings strings() {
        if (mStrings == null) {
            mStrings = provideRecordingServiceStrings();
        }
        return mStrings;
    }

    private Intent getShareIntent(Context context, Uri path) {
        return new Intent(context, this.getClass()).setAction(ACTION_SHARE)
                .putExtra(EXTRA_PATH, path)
                .putExtra(EXTRA_NOTIFICATION_ID, mNotificationId);
    }

    private Intent getDeleteIntent(Context context, String path) {
        return new Intent(context, RecordingService.class)
                .setAction(ACTION_DELETE)
                .putExtra(EXTRA_PATH, path);
    }
}
