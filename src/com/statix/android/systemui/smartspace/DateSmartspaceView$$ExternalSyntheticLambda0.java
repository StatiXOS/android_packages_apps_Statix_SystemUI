package com.google.android.systemui.smartspace;

import android.provider.Settings;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final /* synthetic */ class DateSmartspaceView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ DateSmartspaceView f$0;

    public /* synthetic */ DateSmartspaceView$$ExternalSyntheticLambda0(DateSmartspaceView dateSmartspaceView, int i) {
        this.$r8$classId = i;
        this.f$0 = dateSmartspaceView;
    }

    @Override // java.lang.Runnable
    public final void run() {
        int i = this.$r8$classId;
        DateSmartspaceView dateSmartspaceView = this.f$0;
        switch (i) {
            case 0:
                boolean z = DateSmartspaceView.DEBUG;
                dateSmartspaceView.getContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("doze_always_on"), false, dateSmartspaceView.mAodSettingsObserver, -1);
                break;
            default:
                boolean z2 = DateSmartspaceView.DEBUG;
                dateSmartspaceView.getContext().getContentResolver().unregisterContentObserver(dateSmartspaceView.mAodSettingsObserver);
                break;
        }
    }
}
