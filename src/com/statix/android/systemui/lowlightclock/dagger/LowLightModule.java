package com.statix.android.systemui.lowlightclock.dagger;

import android.content.res.Resources;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Main;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class LowLightModule {

    public static final String LOW_LIGHT_MAX_BURN_IN_OFFSET = "low_light_max_burn_in_offset";
    public static final String LOW_LIGHT_TIME_UNTIL_FULL_JITTER_MILLIS =
            "low_light_time_until_full_jitter_millis";

    @Provides
    @Named(LOW_LIGHT_MAX_BURN_IN_OFFSET)
    static int providesMaxBurnInOffset(@Main Resources resources) {
        return resources.getDimensionPixelSize(R.dimen.default_burn_in_prevention_offset);
    }

    @Provides
    @Named(LOW_LIGHT_TIME_UNTIL_FULL_JITTER_MILLIS)
    static long providesTimeUntilFullJitterMillis(@Main Resources resources) {
        return resources.getInteger(R.integer.config_dreamOverlayMillisUntilFullJitter);
    }

}
