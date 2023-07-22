/*
 * Copyright (C) 2022 StatiXOS
 * SPDX-License-Identifer: Apache-2.0
 */

package com.statix.android.systemui.screenrecord;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R;
import com.android.systemui.dagger.qualifiers.LongRunning;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.screenrecord.ScreenMediaRecorder;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class StatixRecordingService extends RecordingService {

    private static final int NOTIFICATION_VIEW_ID = 4273;

    private static final String ACTION_DELETE = "com.android.systemui.screenrecord.DELETE";
    private static final String ACTION_SHARE = "com.android.systemui.screenrecord.SHARE";
    private static final String CHANNEL_ID = "screen_record";
    private static final String EXTRA_PATH = "extra_path";
    private static final String TAG = "StatixRecordingService";

    private final NotificationManager mNotificationManager;
    private final UserContextProvider mUserContextTracker;

    @Inject
    public StatixRecordingService(
            RecordingController controller,
            @LongRunning Executor executor,
            @Main Handler handler,
            UiEventLogger uiEventLogger,
            NotificationManager notificationManager,
            UserContextProvider userContextTracker,
            KeyguardDismissUtil keyguardDismissUtil) {
        super(
                controller,
                executor,
                handler,
                uiEventLogger,
                notificationManager,
                userContextTracker,
                keyguardDismissUtil);
        mNotificationManager = notificationManager;
        mUserContextTracker = userContextTracker;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int userId) {
        if (intent == null) {
            return Service.START_NOT_STICKY;
        }
        String action = intent.getAction();
        Log.d(TAG, "onStartCommand " + action);

        int currentUserId = mUserContextTracker.getUserContext().getUserId();
        UserHandle currentUser = new UserHandle(currentUserId);
        switch (action) {
            case ACTION_DELETE:
                // Close quick shade
                sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

                ContentResolver resolver = getContentResolver();
                Uri uri = Uri.parse(intent.getStringExtra(EXTRA_PATH));
                resolver.delete(uri, null, null);

                Toast.makeText(this, R.string.screenrecord_delete_description, Toast.LENGTH_LONG)
                        .show();

                // Remove notification
                mNotificationManager.cancelAsUser(null, NOTIFICATION_VIEW_ID, currentUser);
                Log.d(TAG, "Deleted recording " + uri);
                return Service.START_STICKY;
            default:
                return super.onStartCommand(intent, flags, userId);
        }
    }

    @Override
    protected Notification createSaveNotification(ScreenMediaRecorder.SavedRecording recording) {
        // Keep in sync with AOSP.
        Uri uri = recording.getUri();
        Intent viewIntent =
                new Intent(Intent.ACTION_VIEW)
                        .setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        .setDataAndType(uri, "video/mp4");

        Notification.Action shareAction =
                new Notification.Action.Builder(
                                Icon.createWithResource(this, R.drawable.ic_screenrecord),
                                getResources().getString(R.string.screenrecord_share_label),
                                PendingIntent.getService(
                                        this,
                                        REQUEST_CODE,
                                        getShareIntent(this, uri.toString()),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                                | PendingIntent.FLAG_IMMUTABLE))
                        .build();

        Notification.Action deleteAction =
                new Notification.Action.Builder(
                                Icon.createWithResource(this, R.drawable.ic_screenrecord),
                                getResources().getString(R.string.screenrecord_delete_label),
                                PendingIntent.getService(
                                        this,
                                        REQUEST_CODE,
                                        getDeleteIntent(this, uri.toString()),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                                | PendingIntent.FLAG_IMMUTABLE))
                        .build();

        Bundle extras = new Bundle();
        extras.putString(
                Notification.EXTRA_SUBSTITUTE_APP_NAME,
                getResources().getString(R.string.screenrecord_name));

        Notification.Builder builder =
                new Notification.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_screenrecord)
                        .setContentTitle(getResources().getString(R.string.screenrecord_save_title))
                        .setContentText(getResources().getString(R.string.screenrecord_save_text))
                        .setContentIntent(
                                PendingIntent.getActivity(
                                        this,
                                        REQUEST_CODE,
                                        viewIntent,
                                        PendingIntent.FLAG_IMMUTABLE))
                        .addAction(shareAction)
                        .addAction(deleteAction)
                        .setAutoCancel(true)
                        .addExtras(extras);

        // Add thumbnail if available
        Bitmap thumbnailBitmap = recording.getThumbnail();
        if (thumbnailBitmap != null) {
            Notification.BigPictureStyle pictureStyle =
                    new Notification.BigPictureStyle()
                            .bigPicture(thumbnailBitmap)
                            .showBigPictureWhenCollapsed(true);
            builder.setStyle(pictureStyle);
        }
        return builder.build();
    }

    private static Intent getDeleteIntent(Context context, String path) {
        return new Intent(context, RecordingService.class)
                .setAction(ACTION_DELETE)
                .putExtra(EXTRA_PATH, path);
    }

    private static Intent getShareIntent(Context context, String path) {
        return new Intent(context, RecordingService.class)
                .setAction(ACTION_SHARE)
                .putExtra(EXTRA_PATH, path);
    }
}
