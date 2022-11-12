package com.statix.android.systemui.adaptivecharging;

import android.content.Context;
import android.os.IHwBinder;
import android.os.LocaleList;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Locale;
import java.util.NoSuchElementException;

import vendor.google.google_battery.V1_0.IGoogleBattery;

public class AdaptiveChargingManager {

    private static final boolean DEBUG = Log.isLoggable("AdaptiveChargingManager", 3);
    private Context mContext;

    public AdaptiveChargingManager(Context context) {
        mContext = context;
    }

    public interface AdaptiveChargingStatusReceiver {
        void onDestroyInterface();

        void onReceiveStatus(int seconds, String stage);
    }

    public final String formatTimeToFull(long timeToFull) {
        String format;
        if (DateFormat.is24HourFormat(mContext)) {
            format = "Hm";
        } else {
            format = "hma";
        }
        LocaleList locales = mContext.getResources().getConfiguration().getLocales();
        Locale locale;
        if (locales != null && !locales.isEmpty()) {
            locale = locales.get(0);
        } else {
            locale = Locale.getDefault();
        }
        return DateFormat.format(DateFormat.getBestDateTimePattern(locale, format), timeToFull).toString();
    }

    public boolean hasAdaptiveChargingFeature() {
        return mContext.getPackageManager().hasSystemFeature("com.google.android.feature.ADAPTIVE_CHARGING");
    }

    public void queryStatus(final AdaptiveChargingStatusReceiver adaptiveChargingStatusReceiver) {
        IHwBinder.DeathRecipient deathRecipient = new IHwBinder.DeathRecipient() {
                @Override
                public final void serviceDied(long j) {
                    if (DEBUG) {
                        Log.d("AdaptiveChargingManager", "serviceDied");
                    }
                    adaptiveChargingStatusReceiver.onDestroyInterface();
                }
            };
        IGoogleBattery googBatteryIntf = initHalInterface(deathRecipient);
        if (googBatteryIntf == null) {
            adaptiveChargingStatusReceiver.onDestroyInterface();
            return;
        }
        try {
            googBatteryIntf.getChargingStageAndDeadline((byte result, String stage, int seconds) -> {
                    if (result == 0) {
                        adaptiveChargingStatusReceiver.onReceiveStatus(seconds, stage);
                    } else {
                        destroyHalInterface(googBatteryIntf, deathRecipient);
                    }
                }
            );
        } catch (RemoteException e) {
            Log.e("AdaptiveChargingManager", "Failed to get Adaptive Chaging status: ", e);
            destroyHalInterface(googBatteryIntf, deathRecipient);
        }
    }

    private void destroyHalInterface(IGoogleBattery iGoogleBattery, IHwBinder.DeathRecipient deathRecipient) {
        if (DEBUG) {
            Log.d("AdaptiveChargingManager", "destroyHalInterface");
        }
        if (deathRecipient != null) {
            try {
                iGoogleBattery.unlinkToDeath(deathRecipient);
            } catch (RemoteException e) {
                Log.e("AdaptiveChargingManager", "unlinkToDeath failed: ", e);
            }
        }
    }

    private static IGoogleBattery initHalInterface(IHwBinder.DeathRecipient deathReceiver) {
        if (DEBUG) {
            Log.d("AdaptiveChargingManager", "initHalInterface");
        }
        try {
            IGoogleBattery service = IGoogleBattery.getService();
            if (service != null && deathReceiver != null) {
                service.linkToDeath(deathReceiver, 0);
            }
            return service;
        } catch (RemoteException | NoSuchElementException e) {
            Log.e("AdaptiveChargingManager", "failed to get Google Battery HAL: ", e);
            return null;
        }
    }

}
