package com.google.android.systemui.ambientmusic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

public final class AmbientIndicationService extends BroadcastReceiver {
    public final AlarmManager mAlarmManager;
    public final AmbientIndicationContainer mAmbientIndicationContainer;
    public final Context mContext;
    public boolean mStarted;
    public final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback() {
        @Override
        public final void onUserSwitchComplete(int i) {
            AmbientIndicationService.onUserSwitched();
        }
    };
    public final AlarmManager.OnAlarmListener mHideIndicationListener = new AlarmManager.OnAlarmListener() {
        @Override
        public final void onAlarm() {
            AmbientIndicationService.mAmbientIndicationContainer.setAmbientMusic(null, null, null, 0, false, null);
        }
    };

    @Override
    public final void onReceive(Context context, Intent intent) {
        if (!isForCurrentUser()) {
            Log.i("AmbientIndication", "Suppressing ambient, not for this user.");
            return;
        }
        int intExtra = intent.getIntExtra("com.google.android.ambientindication.extra.VERSION", 0);
        boolean z = true;
        if (intExtra != 1) {
            Log.e("AmbientIndication", "AmbientIndicationApi.EXTRA_VERSION is 1, but received an intent with version " + intExtra + ", dropping intent.");
            z = false;
        }
        if (!z) {
            return;
        }
        String action = intent.getAction();
        action.getClass();
        if (!action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE")) {
            if (action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW")) {
                long min = Math.min(Math.max(intent.getLongExtra("com.google.android.ambientindication.extra.TTL_MILLIS", 180000L), 0L), 180000L);
                boolean booleanExtra = intent.getBooleanExtra("com.google.android.ambientindication.extra.SKIP_UNLOCK", false);
                int intExtra2 = intent.getIntExtra("com.google.android.ambientindication.extra.ICON_OVERRIDE", 0);
                String stringExtra = intent.getStringExtra("com.google.android.ambientindication.extra.ICON_DESCRIPTION");
                mAmbientIndicationContainer.setAmbientMusic(intent.getCharSequenceExtra("com.google.android.ambientindication.extra.TEXT"), (PendingIntent) intent.getParcelableExtra("com.google.android.ambientindication.extra.OPEN_INTENT"), (PendingIntent) intent.getParcelableExtra("com.google.android.ambientindication.extra.FAVORITING_INTENT"), intExtra2, booleanExtra, stringExtra);
                mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + min, "AmbientIndication", mHideIndicationListener, null);
                Log.i("AmbientIndication", "Showing ambient indication.");
                return;
            }
            return;
        }
        mAlarmManager.cancel(mHideIndicationListener);
        mAmbientIndicationContainer.setAmbientMusic(null, null, null, 0, false, null);
        Log.i("AmbientIndication", "Hiding ambient indication.");
    }

    public void onUserSwitched() {
        mAmbientIndicationContainer.setAmbientMusic(null, null, null, 0, false, null);
    }

    public AmbientIndicationService(Context context, AmbientIndicationContainer ambientIndicationContainer, AlarmManager alarmManager) {
        mContext = context;
        d = ambientIndicationContainer;
        mAlarmManager = alarmManager;
    }

    public int getCurrentUser() {
        return KeyguardUpdateMonitor.getCurrentUser();
    }

    public boolean isForCurrentUser() {
        if (getSendingUserId() != getCurrentUser() && getSendingUserId() != -1) {
            return false;
        }
        return true;
    }
}
