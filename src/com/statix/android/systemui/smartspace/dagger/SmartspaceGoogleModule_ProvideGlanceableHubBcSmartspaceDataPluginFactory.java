package com.google.android.systemui.smartspace.dagger;

import com.google.android.systemui.smartspace.BcSmartspaceDataProvider;
import dagger.internal.Provider;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public abstract class SmartspaceGoogleModule_ProvideGlanceableHubBcSmartspaceDataPluginFactory implements Provider {
    public static BcSmartspaceDataProvider provideGlanceableHubBcSmartspaceDataPlugin() {
        return new BcSmartspaceDataProvider();
    }
}
