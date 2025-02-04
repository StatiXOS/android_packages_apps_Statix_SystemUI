package com.google.android.systemui.smartspace.logging;

import androidx.activity.BackEventCompat$$ExternalSyntheticOutline0;
import java.util.Objects;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class BcSmartspaceCardMetadataLoggingInfo {
    public final int mCardTypeId;
    public final int mInstanceId;

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class Builder {
        public int mCardTypeId;
        public int mInstanceId;
    }

    public BcSmartspaceCardMetadataLoggingInfo(Builder builder) {
        this.mInstanceId = builder.mInstanceId;
        this.mCardTypeId = builder.mCardTypeId;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BcSmartspaceCardMetadataLoggingInfo)) {
            return false;
        }
        BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = (BcSmartspaceCardMetadataLoggingInfo) obj;
        return this.mInstanceId == bcSmartspaceCardMetadataLoggingInfo.mInstanceId && this.mCardTypeId == bcSmartspaceCardMetadataLoggingInfo.mCardTypeId;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(this.mInstanceId), Integer.valueOf(this.mCardTypeId));
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("BcSmartspaceCardMetadataLoggingInfo{mInstanceId=");
        sb.append(this.mInstanceId);
        sb.append(", mCardTypeId=");
        return BackEventCompat$$ExternalSyntheticOutline0.m(sb, this.mCardTypeId, '}');
    }
}
