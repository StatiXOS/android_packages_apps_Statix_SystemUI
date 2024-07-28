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
import com.android.systemui.dagger.qualifiers.LongRunning;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.res.R;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.screenrecord.ScreenMediaRecorder;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class StatixRecordingService extends RecordingService {

    private static final String ACTION_DELETE = "com.android.systemui.screenrecord.DELETE";
    private static final String ACTION_STOP_NOTIF =
            "com.android.systemui.screenrecord.STOP_FROM_NOTIF";
    private static final String ACTION_SHARE = "com.android.systemui.screenrecord.SHARE";
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
            case ACTION_STOP_NOTIF:
            case ACTION_STOP:
                super.onStartCommand(intent, flags, userId);
                stopForeground(STOP_FOREGROUND_DETACH);
                return Service.START_NOT_STICKY;
            case ACTION_DELETE:
                // Close quick shade
                closeSystemDialogs();

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
                return Service.START_NOT_STICKY;
            default:
                return super.onStartCommand(intent, flags, userId);
        }
    }

    @Override
    protected Notification createSaveNotification(ScreenMediaRecorder.SavedRecording recording) {
        Notification originalNotification = super.createSaveNotification(recording);
        Notification.Builder originalBuilder = Notification.Builder.recoverBuilder(this, originalNotification);

        // BEGIN: Keep in sync with AOSP
        Notification.Action shareAction = new Notification.Action.Builder(
                Icon.createWithResource(this, R.drawable.ic_screenrecord),
                provideRecordingServiceStrings().getShareLabel(),
                PendingIntent.getService(
                        this,
                        REQUEST_CODE,
                        getShareIntent(this, recording.getUri().toString()),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                .build();
        // END: Keep in sync with AOSP

        Notification.Action deleteAction =
                new Notification.Action.Builder(
                                Icon.createWithResource(this, R.drawable.ic_screenrecord),
                                getResources().getString(R.string.screenrecord_delete_label),
                                PendingIntent.getService(
                                        this,
                                        REQUEST_CODE,
                                        getDeleteIntent(this, recording.getUri().toString()),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                                | PendingIntent.FLAG_IMMUTABLE))
                        .build();

        originalBuilder.setActions(shareAction, deleteAction);
        originalBuilder.setAutoCancel(true);

        return originalBuilder.build();
    }

    // Keep in sync with AOSP.
    private Intent getShareIntent(Context context, String path) {
        return new Intent(context, RecordingService.class).setAction(ACTION_SHARE)
                .putExtra(EXTRA_PATH, path);
    }

    @Override
    protected Intent getNotificationIntent(Context context) {
        return new Intent(context, RecordingService.class).setAction(ACTION_STOP_NOTIF);
    }

    private static Intent getDeleteIntent(Context context, String path) {
        return new Intent(context, RecordingService.class)
                .setAction(ACTION_DELETE)
                .putExtra(EXTRA_PATH, path);
    }
}
