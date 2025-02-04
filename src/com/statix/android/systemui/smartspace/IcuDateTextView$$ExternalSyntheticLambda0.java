package com.google.android.systemui.smartspace;

import android.provider.Settings;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final /* synthetic */ class IcuDateTextView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ IcuDateTextView f$0;

    public /* synthetic */ IcuDateTextView$$ExternalSyntheticLambda0(IcuDateTextView icuDateTextView, int i) {
        this.$r8$classId = i;
        this.f$0 = icuDateTextView;
    }

    @Override // java.lang.Runnable
    public final void run() {
        int i = this.$r8$classId;
        IcuDateTextView icuDateTextView = this.f$0;
        switch (i) {
            case 0:
                int i2 = IcuDateTextView.$r8$clinit;
                try {
                    icuDateTextView.getContext().unregisterReceiver(icuDateTextView.mIntentReceiver);
                    break;
                } catch (IllegalArgumentException unused) {
                    return;
                }
            case 1:
                int i3 = IcuDateTextView.$r8$clinit;
                icuDateTextView.getContext().getContentResolver().unregisterContentObserver(icuDateTextView.mAodSettingsObserver);
                break;
            case 2:
                int i4 = IcuDateTextView.$r8$clinit;
                icuDateTextView.onTimeChanged(false);
                break;
            default:
                int i5 = IcuDateTextView.$r8$clinit;
                icuDateTextView.getContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("doze_always_on"), false, icuDateTextView.mAodSettingsObserver, -1);
                break;
        }
    }
}
