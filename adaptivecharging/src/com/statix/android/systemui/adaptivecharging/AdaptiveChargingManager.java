package com.statix.android.systemui.adaptivecharging;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.ParcelFormatException;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Locale;
import java.util.NoSuchElementException;

import vendor.lineage.health.ChargingControlSupportedMode;
import vendor.lineage.health.ChargingStage;
import vendor.lineage.health.StageDescription;
import vendor.lineage.health.IChargingControl;

public class AdaptiveChargingManager {

    private static final boolean DEBUG = Log.isLoggable("AdaptiveChargingManager", Log.DEBUG);
    private static final String TAG = "AdaptiveChargingManager";

    private Context mContext;

    public AdaptiveChargingManager(Context context) {
        mContext = context;
    }

    public interface AdaptiveChargingStatusReceiver {
        void onDestroyInterface();

        void onReceiveStatus(int seconds, @StageDescription int stage);
    }

    private Locale getLocale() {
        LocaleList locales = mContext.getResources().getConfiguration().getLocales();
        return (locales == null || locales.isEmpty()) ? Locale.getDefault() : locales.get(0);
    }

    public String formatTimeToFull(long j) {
        return DateFormat.format(DateFormat.getBestDateTimePattern(getLocale(), DateFormat.is24HourFormat(mContext) ? "Hm" : "hma"), j).toString();
    }

    public boolean hasAdaptiveChargingFeature() {
        IChargingControl chargingControlIntf = initHalInterface(null);
        if (chargingControlIntf == null) {
            return false;
        }
        try {
            return (chargingControlIntf.getSupportedMode() & ChargingControlSupportedMode.DEADLINE) == ChargingControlSupportedMode.DEADLINE;
        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
    }

    public boolean isAvailable() {
        return hasAdaptiveChargingFeature();
    }

    public boolean getEnabled() {
        return Settings.Secure.getInt(mContext.getContentResolver(), "adaptive_charging", 1) == 1;
    }

    public void setEnabled(boolean on) {
        Settings.Secure.putInt(mContext.getContentResolver(), "adaptive_charging", on ? 1 : 0);
    }

    public static boolean isStageActive(@StageDescription int stage) {
        return stage == StageDescription.ACTIVE;
    }

    public static boolean isStageEnabled(@StageDescription int stage) {
        return stage == StageDescription.ENABLED;
    }

    public static boolean isStageActiveOrEnabled(@StageDescription int stage) {
       return isStageActive(stage) || isStageEnabled(stage);
    }

    public static boolean isActive(@StageDescription int stage, int seconds) {
        return isStageActiveOrEnabled(stage) && seconds > 0;
    }

    public boolean setAdaptiveChargingDeadline(int secondsFromNow) {
        IChargingControl chargingControlInterface = initHalInterface(null);
        if (chargingControlInterface == null) {
            return false;
        }
        boolean result = false;
        try {
            chargingControlInterface.setChargingDeadline(secondsFromNow);
            result = true;
        } catch (RemoteException e) {
            Log.e(TAG, "setChargingDeadline() failed");
        }
        destroyHalInterface(chargingControlInterface, null);
        return result;
    }

    public void queryStatus(final AdaptiveChargingStatusReceiver adaptiveChargingStatusReceiver) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
                @Override
                public final void binderDied() {
                    if (DEBUG) {
                        Log.d("AdaptiveChargingManager", "serviceDied");
                    }
                    adaptiveChargingStatusReceiver.onDestroyInterface();
                }
            };
        IChargingControl chargingControlIntf = initHalInterface(deathRecipient);
        if (chargingControlIntf == null) {
            adaptiveChargingStatusReceiver.onDestroyInterface();
            return;
        }
        try {
            ChargingStage stage = chargingControlIntf.getChargingStageAndDeadline();
            adaptiveChargingStatusReceiver.onReceiveStatus(stage.deadlineSecs, stage.stage);
        } catch (RemoteException | ParcelFormatException e) {
            Log.e("AdaptiveChargingManager", "Failed to get Adaptive Charging status: ", e);
        }
        destroyHalInterface(chargingControlIntf, deathRecipient);
        adaptiveChargingStatusReceiver.onDestroyInterface();
    }

    private static void destroyHalInterface(IChargingControl intf, IBinder.DeathRecipient deathRecipient) {
        if (DEBUG) {
            Log.d("AdaptiveChargingManager", "destroyHalInterface");
        }
        if (deathRecipient != null && intf != null) {
            intf.asBinder().unlinkToDeath(deathRecipient, 0);
        }
    }

    private static IChargingControl initHalInterface(IBinder.DeathRecipient deathReceiver) {
        if (DEBUG) {
            Log.d("AdaptiveChargingManager", "initHalInterface");
        }
        try {
            IBinder binder = Binder.allowBlocking(ServiceManager.waitForDeclaredService(IChargingControl.DESCRIPTOR + "/default"));
            IChargingControl batteryInterface = null;
            if (binder != null) {
                batteryInterface = IChargingControl.Stub.asInterface(binder);
                if (batteryInterface != null && deathReceiver != null) {
                    binder.linkToDeath(deathReceiver, 0);
                }
            }
            return batteryInterface;
        } catch (RemoteException | NoSuchElementException | SecurityException e) {
            Log.e("AdaptiveChargingManager", "failed to get Lineage Health HAL: ", e);
            return null;
        }
    }

}
