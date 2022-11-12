package com.statix.android.systemui.adaptivecharging;

import android.content.Context;
import android.os.HwBinder;
import android.os.LocaleList;
import android.os.RemoteException;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Locale;
import java.util.NoSuchElementException;

import vendor.google.google_battery.V1_2.IGoogleBattery;
import vendor.google.google_battery.V1_0.Result;

public class AdaptiveChargingManager {

    private static final boolean DEBUG = Log.isLoggable("AdaptiveChargingManager", 3);
    private static final String TAG = "AdaptiveChargingManager";

    private Context mContext;

    public AdaptiveChargingManager(Context context) {
        mContext = context;
    }

    public interface AdaptiveChargingStatusReceiver {
        void onDestroyInterface();

        void onReceiveStatus(int seconds, String stage);
    }

    private Locale getLocale() {
        LocaleList locales = mContext.getResources().getConfiguration().getLocales();
        return (locales == null || locales.isEmpty()) ? Locale.getDefault() : locales.get(0);
    }

    public String formatTimeToFull(long j) {
        return DateFormat.format(DateFormat.getBestDateTimePattern(getLocale(), DateFormat.is24HourFormat(mContext) ? "Hm" : "hma"), j).toString();
    }

    public boolean hasAdaptiveChargingFeature() {
        return mContext.getPackageManager().hasSystemFeature("com.google.android.feature.ADAPTIVE_CHARGING");
    }

    public boolean isAvailable() {
        return hasAdaptiveChargingFeature() && DeviceConfig.getBoolean("adaptive_charging", "adaptive_charging_enabled", true);
    }

    public boolean getEnabled() {
        return Settings.Secure.getInt(mContext.getContentResolver(), "adaptive_charging", 1) == 1;
    }

    public void setEnabled(boolean on) {
        Settings.Secure.putInt(mContext.getContentResolver(), "adaptive_charging", on ? 1 : 0);
    }

    public static boolean isStageActive(String stage) {
        return "Active".equals(stage);
    }

    public static boolean isStageEnabled(String stage) {
        return "Enabled".equals(stage);
    }

    public static boolean isStageActiveOrEnabled(String stage) {
       return isStageActive(stage) || isStageEnabled(stage);
    }

    public static boolean isActive(String state, int seconds) {
        return isStageActiveOrEnabled(state) && seconds > 0;
    }

    public boolean setAdaptiveChargingDeadline(int secondsFromNow) {
        IGoogleBattery googBatteryInterface = initHalInterface(null);
        if (googBatteryInterface == null) {
            return false;
        }
        boolean result = false;
        try {
            result = googBatteryInterface.setChargingDeadline(secondsFromNow) == Result.OK;
        } catch (RemoteException e) {
            Log.e(TAG, "setChargingDeadline() failed");
        }
        destroyHalInterface(googBatteryInterface, null);
        return result;
    }

    public void queryStatus(final AdaptiveChargingStatusReceiver adaptiveChargingStatusReceiver) {
        HwBinder.DeathRecipient deathRecipient = new HwBinder.DeathRecipient() {
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
            googBatteryIntf.getChargingStageAndDeadline(new IGoogleBattery.getChargingStageAndDeadlineCallback() {
                @Override
                public void onValues(byte result, String stage, int seconds) {
                    if (result == Result.OK) {
                        adaptiveChargingStatusReceiver.onReceiveStatus(seconds, stage);
                    }
                    destroyHalInterface(googBatteryIntf, deathRecipient);
                    adaptiveChargingStatusReceiver.onDestroyInterface();
                }
            });
        } catch (RemoteException e) {
            Log.e("AdaptiveChargingManager", "Failed to get Adaptive Chaging status: ", e);
            destroyHalInterface(googBatteryIntf, deathRecipient);
            adaptiveChargingStatusReceiver.onDestroyInterface();
        }
    }

    private void destroyHalInterface(IGoogleBattery iGoogleBattery, HwBinder.DeathRecipient deathRecipient) {
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

    private static IGoogleBattery initHalInterface(HwBinder.DeathRecipient deathReceiver) {
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
