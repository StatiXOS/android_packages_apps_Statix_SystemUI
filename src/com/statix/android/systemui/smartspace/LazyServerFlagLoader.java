package com.google.android.systemui.smartspace;

import android.provider.DeviceConfig;

import java.util.Set;
import java.util.concurrent.Executors;

public final class LazyServerFlagLoader {
    public final String mPropertyKey;
    public Boolean mValue = null;

    public LazyServerFlagLoader(String str) {
        this.mPropertyKey = str;
    }

    public boolean get() {
        if (this.mValue == null) {
            this.mValue =
                    Boolean.valueOf(DeviceConfig.getBoolean("launcher", this.mPropertyKey, true));
            DeviceConfig.addOnPropertiesChangedListener(
                    "launcher",
                    Executors.newSingleThreadExecutor(),
                    new OnPropertiesChangedListener(this));
        }
        return this.mValue.booleanValue();
    }

    private static class OnPropertiesChangedListener
            implements DeviceConfig.OnPropertiesChangedListener {
        private final LazyServerFlagLoader lazyServerFlagLoader;

        OnPropertiesChangedListener(LazyServerFlagLoader loader) {
            this.lazyServerFlagLoader = loader;
        }

        @Override
        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            Set<String> keyset = properties.getKeyset();
            String str = lazyServerFlagLoader.mPropertyKey;
            if (keyset.contains(str)) {
                lazyServerFlagLoader.mValue = Boolean.valueOf(properties.getBoolean(str, true));
            }
        }
    }
}
