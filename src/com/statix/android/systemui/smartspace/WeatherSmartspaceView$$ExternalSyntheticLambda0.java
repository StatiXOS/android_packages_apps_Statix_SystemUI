package com.google.android.systemui.smartspace;

import android.provider.Settings;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final /* synthetic */ class WeatherSmartspaceView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ WeatherSmartspaceView f$0;

    public /* synthetic */ WeatherSmartspaceView$$ExternalSyntheticLambda0(WeatherSmartspaceView weatherSmartspaceView, int i) {
        this.$r8$classId = i;
        this.f$0 = weatherSmartspaceView;
    }

    @Override // java.lang.Runnable
    public final void run() {
        int i = this.$r8$classId;
        WeatherSmartspaceView weatherSmartspaceView = this.f$0;
        switch (i) {
            case 0:
                boolean z = WeatherSmartspaceView.DEBUG;
                weatherSmartspaceView.getContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("doze_always_on"), false, weatherSmartspaceView.mAodSettingsObserver, -1);
                break;
            default:
                boolean z2 = WeatherSmartspaceView.DEBUG;
                weatherSmartspaceView.getContext().getContentResolver().unregisterContentObserver(weatherSmartspaceView.mAodSettingsObserver);
                break;
        }
    }
}
