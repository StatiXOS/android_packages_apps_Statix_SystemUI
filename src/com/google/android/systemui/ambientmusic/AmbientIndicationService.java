package com.google.android.systemui.ambientmusic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
/* loaded from: classes2.dex */
public class AmbientIndicationService extends BroadcastReceiver {
    private final AlarmManager mAlarmManager;
    private final AmbientIndicationContainer mAmbientIndicationContainer;
    private final Context mContext;
    private final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationService.1
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onUserSwitchComplete(int i) {
            onUserSwitched();
        }
    };
    private final AlarmManager.OnAlarmListener mHideIndicationListener = new AlarmManager.OnAlarmListener() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationService$$ExternalSyntheticLambda0
        @Override // android.app.AlarmManager.OnAlarmListener
        public final void onAlarm() {
            mAmbientIndicationContainer.hideAmbientMusic();
        }
    };

    public AmbientIndicationService(Context context, AmbientIndicationContainer ambientIndicationContainer, AlarmManager alarmManager) {
        mContext = context;
        mAmbientIndicationContainer = ambientIndicationContainer;
        mAlarmManager = alarmManager;
        start();
    }

    void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW");
        intentFilter.addAction("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE");
        mContext.registerReceiverAsUser(this, UserHandle.ALL, intentFilter, "com.google.android.ambientindication.permission.AMBIENT_INDICATION", null);
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(mCallback);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!isForCurrentUser()) {
            Log.i("AmbientIndication", "Suppressing ambient, not for this user.");
        } else if (!verifyAmbientApiVersion(intent)) {
        } else {
            String action = intent.getAction();
            action.hashCode();
            if (action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE")) {
                mAlarmManager.cancel(mHideIndicationListener);
                mAmbientIndicationContainer.hideAmbientMusic();
                Log.i("AmbientIndication", "Hiding ambient indication.");
            } else if (!action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW")) {
            } else {
                long min = Math.min(Math.max(intent.getLongExtra("com.google.android.ambientindication.extra.TTL_MILLIS", 180000L), 0L), 180000L);
                boolean booleanExtra = intent.getBooleanExtra("com.google.android.ambientindication.extra.SKIP_UNLOCK", false);
                int intExtra = intent.getIntExtra("com.google.android.ambientindication.extra.ICON_OVERRIDE", 0);
                String stringExtra = intent.getStringExtra("com.google.android.ambientindication.extra.ICON_DESCRIPTION");
                mAmbientIndicationContainer.setAmbientMusic(intent.getCharSequenceExtra("com.google.android.ambientindication.extra.TEXT"), (PendingIntent) intent.getParcelableExtra("com.google.android.ambientindication.extra.OPEN_INTENT"), (PendingIntent) intent.getParcelableExtra("com.google.android.ambientindication.extra.FAVORITING_INTENT"), intExtra, booleanExtra, stringExtra);
                mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + min, "AmbientIndication", mHideIndicationListener, null);
                Log.i("AmbientIndication", "Showing ambient indication.");
            }
        }
    }

    private boolean verifyAmbientApiVersion(Intent intent) {
        int intExtra = intent.getIntExtra("com.google.android.ambientindication.extra.VERSION", 0);
        if (intExtra != 1) {
            Log.e("AmbientIndication", "AmbientIndicationApi.EXTRA_VERSION is 1, but received an intent with version " + intExtra + ", dropping intent.");
            return false;
        }
        return true;
    }

    boolean isForCurrentUser() {
        return getSendingUserId() == getCurrentUser() || getSendingUserId() == -1;
    }

    int getCurrentUser() {
        return KeyguardUpdateMonitor.getCurrentUser();
    }

    void onUserSwitched() {
        mAmbientIndicationContainer.hideAmbientMusic();
    }
}
