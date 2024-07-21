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
    private static final int NOTIF_BASE_ID = 4273;
    private static final String TAG = "StatixRecordingService";
    private static final String CHANNEL_ID = "screen_record";
    private static final String GROUP_KEY = "screen_record_saved";
    private static final String EXTRA_PATH = "extra_path";

    private static final String ACTION_SHARE = "com.android.systemui.screenrecord.SHARE";
    private static final String ACTION_DELETE = "com.android.systemui.screenrecord.DELETE";

    private final NotificationManager mNotificationManager;
    private final UserContextProvider mUserContextTracker;
    private int mNotificationId = NOTIF_BASE_ID;
    private StatixRecordingServiceStrings mStrings;

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_NOT_STICKY;
        }
        String action = intent.getAction();
        Log.d(getTag(), "onStartCommand " + action);

        int currentUserId = mUserContextTracker.getUserContext().getUserId();
        UserHandle currentUser = new UserHandle(currentUserId);
        switch (action) {
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
                return Service.START_STICKY;
            default:
                return super.onStartCommand(intent, flags, startId);
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
                                strings().getShareLabel(),
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
                                strings().getDeleteLabel(),
                                PendingIntent.getService(
                                        this,
                                        REQUEST_CODE,
                                        getDeleteIntent(this, uri.toString()),
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                                | PendingIntent.FLAG_IMMUTABLE))
                        .build();

        Bundle extras = new Bundle();
        extras.putString(Notification.EXTRA_SUBSTITUTE_APP_NAME, strings().getTitle());

        Notification.Builder builder =
                new Notification.Builder(this, getChannelId())
                        .setSmallIcon(R.drawable.ic_screenrecord)
                        .setContentTitle(strings().getSaveTitle())
                        .setContentText(strings().getSaveText())
                        .setContentIntent(
                                PendingIntent.getActivity(
                                        this,
                                        REQUEST_CODE,
                                        viewIntent,
                                        PendingIntent.FLAG_IMMUTABLE))
                        .addAction(shareAction)
                        .addAction(deleteAction)
                        .setAutoCancel(true)
                        .setGroup(GROUP_KEY)
                        .addExtras(extras);

        // Add thumbnail if available
        if (recording.getThumbnail() != null) {
            Notification.BigPictureStyle pictureStyle =
                    new Notification.BigPictureStyle()
                            .bigPicture(recording.getThumbnail())
                            .showBigPictureWhenCollapsed(true);
            builder.setStyle(pictureStyle);
        }
        return builder.build();
    }

    private StatixRecordingServiceStrings strings() {
        if (mStrings == null) {
            mStrings = provideStatixRecordingServiceStrings();
        }
        return mStrings;
    }

    protected StatixRecordingServiceStrings provideStatixRecordingServiceStrings() {
        return new StatixRecordingServiceStrings(getResources());
    }

    private Intent getShareIntent(Context context, String path) {
        return new Intent(context, this.getClass())
                .setAction(ACTION_SHARE)
                .putExtra(EXTRA_PATH, path);
    }

    private Intent getDeleteIntent(Context context, String path) {
        return new Intent(context, this.getClass())
                .setAction(ACTION_DELETE)
                .putExtra(EXTRA_PATH, path);
    }
}
