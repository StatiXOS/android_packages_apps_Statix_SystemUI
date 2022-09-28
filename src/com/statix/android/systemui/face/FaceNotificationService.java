package com.statix.android.systemui.face;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.R;

public class FaceNotificationService {
    private FaceNotificationBroadcastReceiver mBroadcastReceiver;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mNotificationQueued = false;
    private KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback =
        new KeyguardUpdateMonitorCallback() {
            @Override
            public void onBiometricError(int msgId, String errString, BiometricSourceType biometricSourceType) {
                if (msgId == 1004) {
                    Settings.Secure.putIntForUser(mContext.getContentResolver(), "face_unlock_re_enroll", 3, UserHandle.USER_CURRENT);
                }
            }

            @Override
            public void onBiometricHelp(int msgId, String helpString, BiometricSourceType biometricSourceType) {
                if (msgId == 13) {
                    Settings.Secure.putIntForUser(mContext.getContentResolver(), "face_unlock_re_enroll", 1, UserHandle.USER_CURRENT);
                }
            }

            @Override
            public final void onUserUnlocked() {
                boolean reEnroll = false;
                if (mNotificationQueued) {
                    Log.d("FaceNotificationService", "Not showing notification; already queued.");
                    return;
                }
                if (Settings.Secure.getIntForUser(mContext.getContentResolver(), "face_unlock_re_enroll", 0, UserHandle.USER_CURRENT) == 3) {
                    reEnroll = true;
                } else {
                    reEnroll = false;
                }
                if (reEnroll) {
                    mNotificationQueued = true;
                    mHandler.postDelayed((() -> {
                        mNotificationQueued = false;
                        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NotificationManager.class);
                        if (notificationManager == null) {
                            Log.e("FaceNotificationService", "Failed to show notification face_action_show_reenroll_dialog. Notification manager is null!");
                            return;
                        }
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("face_action_show_reenroll_dialog");
                        intentFilter.addAction("face_action_notification_dismissed");
                        mContext.registerReceiver(mBroadcastReceiver, intentFilter, 2);
                        PendingIntent reEnrollIntent = PendingIntent.getBroadcastAsUser(mContext, 0,
                                new Intent("face_action_show_reenroll_dialog"), 0, UserHandle.CURRENT);
                        PendingIntent dismissedIntent = PendingIntent.getBroadcastAsUser(mContext, 0,
                                new Intent("face_action_notification_dismissed"), 0, UserHandle.CURRENT);
                        NotificationChannel notificationChannel = new NotificationChannel(
                                "FaceHiPriNotificationChannel",
                                mContext.getString(R.string.face_notification_name),
                                NotificationManager.IMPORTANCE_HIGH);
                        Notification faceReEnrollNotification = new Notification.Builder(mContext, "FaceHiPriNotificationChannel")
                            .setCategory(Notification.CATEGORY_SYSTEM)
                            .setSmallIcon(com.android.internal.R.drawable.ic_lock)
                            .setContentTitle(mContext.getString(R.string.face_reenroll_notification_title))
                            .setContentText(mContext.getString(R.string.face_reenroll_notification_content))
                            .setSubText(mContext.getString(R.string.face_notification_name))
                            .setContentIntent(reEnrollIntent)
                            .setDeleteIntent(dismissedIntent)
                            .setAutoCancel(true)
                            .setLocalOnly(true)
                            .setOnlyAlertOnce(true)
                            .build();
                        notificationManager.createNotificationChannel(notificationChannel);
                        notificationManager.notifyAsUser("FaceNotificationService", 1, faceReEnrollNotification, UserHandle.CURRENT);
                    }), 10000L);
                }
            }
        };

    public FaceNotificationService(Context context) {
        mContext = context;
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(mKeyguardUpdateMonitorCallback);
        mBroadcastReceiver = new FaceNotificationBroadcastReceiver(mContext);
    }
}
