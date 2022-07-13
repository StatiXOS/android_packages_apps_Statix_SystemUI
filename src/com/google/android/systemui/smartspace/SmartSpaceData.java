package com.google.android.systemui.smartspace;

import android.util.Log;
/* loaded from: classes2.dex */
public class SmartSpaceData {
    SmartSpaceCard mCurrentCard;
    SmartSpaceCard mWeatherCard;

    public boolean hasWeather() {
        return this.mWeatherCard != null;
    }

    public boolean hasCurrent() {
        return this.mCurrentCard != null;
    }

    public long getExpirationRemainingMillis() {
        long expiration;
        long currentTimeMillis = System.currentTimeMillis();
        if (hasCurrent() && hasWeather()) {
            expiration = Math.min(this.mCurrentCard.getExpiration(), this.mWeatherCard.getExpiration());
        } else if (hasCurrent()) {
            expiration = this.mCurrentCard.getExpiration();
        } else if (!hasWeather()) {
            return 0L;
        } else {
            expiration = this.mWeatherCard.getExpiration();
        }
        return expiration - currentTimeMillis;
    }

    public long getExpiresAtMillis() {
        if (hasCurrent() && hasWeather()) {
            return Math.min(this.mCurrentCard.getExpiration(), this.mWeatherCard.getExpiration());
        }
        if (hasCurrent()) {
            return this.mCurrentCard.getExpiration();
        }
        if (!hasWeather()) {
            return 0L;
        }
        return this.mWeatherCard.getExpiration();
    }

    public void clear() {
        this.mWeatherCard = null;
        this.mCurrentCard = null;
    }

    public boolean handleExpire() {
        boolean z;
        if (!hasWeather() || !this.mWeatherCard.isExpired()) {
            z = false;
        } else {
            if (SmartSpaceController.DEBUG) {
                Log.d("SmartspaceData", "weather expired " + this.mWeatherCard.getExpiration());
            }
            this.mWeatherCard = null;
            z = true;
        }
        if (!hasCurrent() || !this.mCurrentCard.isExpired()) {
            return z;
        }
        if (SmartSpaceController.DEBUG) {
            Log.d("SmartspaceData", "current expired " + this.mCurrentCard.getExpiration());
        }
        this.mCurrentCard = null;
        return true;
    }

    public String toString() {
        return "{" + this.mCurrentCard + "," + this.mWeatherCard + "}";
    }

    public SmartSpaceCard getWeatherCard() {
        return this.mWeatherCard;
    }

    public SmartSpaceCard getCurrentCard() {
        return this.mCurrentCard;
    }
}
