package com.google.android.systemui.smartspace;

import com.android.systemui.plugins.BcSmartspaceConfigPlugin;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class DefaultBcSmartspaceConfigProvider implements BcSmartspaceConfigPlugin {
    @Override // com.android.systemui.plugins.BcSmartspaceConfigPlugin
    public final boolean isDefaultDateWeatherDisabled() {
        return false;
    }

    @Override // com.android.systemui.plugins.BcSmartspaceConfigPlugin
    public final boolean isViewPager2Enabled() {
        return false;
    }
}
