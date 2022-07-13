package com.google.android.systemui.smartspace;
/* loaded from: classes2.dex */
public enum BcSmartspaceEvent implements EventEnum {
    IGNORE(-1),
    SMARTSPACE_CARD_RECEIVED(759),
    SMARTSPACE_CARD_CLICK(760),
    SMARTSPACE_CARD_DISMISS(761),
    SMARTSPACE_CARD_SEEN(800),
    ENABLED_SMARTSPACE(822),
    DISABLED_SMARTSPACE(823);

    private final int mId;

    BcSmartspaceEvent(int i) {
        this.mId = i;
    }

    @Override // com.google.android.systemui.smartspace.EventEnum
    public int getId() {
        return this.mId;
    }
}
