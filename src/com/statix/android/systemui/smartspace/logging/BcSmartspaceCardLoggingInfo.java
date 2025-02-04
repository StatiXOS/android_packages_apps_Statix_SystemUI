package com.google.android.systemui.smartspace.logging;

import com.android.systemui.smartspace.nano.SmartspaceProto$SmartspaceCardDimensionalInfo;
import java.util.Objects;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class BcSmartspaceCardLoggingInfo {
    public final int mCardinality;
    public final SmartspaceProto$SmartspaceCardDimensionalInfo mDimensionalInfo;
    public final int mDisplaySurface;
    public int mFeatureType;
    public int mInstanceId;
    public final int mRank;
    public final int mReceivedLatency;
    public BcSmartspaceSubcardLoggingInfo mSubcardInfo;
    public final int mUid;

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class Builder {
        public int mCardinality;
        public SmartspaceProto$SmartspaceCardDimensionalInfo mDimensionalInfo;
        public int mDisplaySurface = 1;
        public int mFeatureType;
        public int mInstanceId;
        public int mRank;
        public int mReceivedLatency;
        public BcSmartspaceSubcardLoggingInfo mSubcardInfo;
        public int mUid;
    }

    public BcSmartspaceCardLoggingInfo(Builder builder) {
        this.mInstanceId = builder.mInstanceId;
        this.mDisplaySurface = builder.mDisplaySurface;
        this.mRank = builder.mRank;
        this.mCardinality = builder.mCardinality;
        this.mFeatureType = builder.mFeatureType;
        this.mReceivedLatency = builder.mReceivedLatency;
        this.mUid = builder.mUid;
        this.mSubcardInfo = builder.mSubcardInfo;
        this.mDimensionalInfo = builder.mDimensionalInfo;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BcSmartspaceCardLoggingInfo)) {
            return false;
        }
        BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo = (BcSmartspaceCardLoggingInfo) obj;
        return this.mInstanceId == bcSmartspaceCardLoggingInfo.mInstanceId && this.mDisplaySurface == bcSmartspaceCardLoggingInfo.mDisplaySurface && this.mRank == bcSmartspaceCardLoggingInfo.mRank && this.mCardinality == bcSmartspaceCardLoggingInfo.mCardinality && this.mFeatureType == bcSmartspaceCardLoggingInfo.mFeatureType && this.mReceivedLatency == bcSmartspaceCardLoggingInfo.mReceivedLatency && this.mUid == bcSmartspaceCardLoggingInfo.mUid && Objects.equals(this.mSubcardInfo, bcSmartspaceCardLoggingInfo.mSubcardInfo) && Objects.equals(this.mDimensionalInfo, bcSmartspaceCardLoggingInfo.mDimensionalInfo);
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(this.mInstanceId), Integer.valueOf(this.mDisplaySurface), Integer.valueOf(this.mRank), Integer.valueOf(this.mCardinality), Integer.valueOf(this.mFeatureType), Integer.valueOf(this.mReceivedLatency), Integer.valueOf(this.mUid), this.mSubcardInfo);
    }

    public final String toString() {
        return "instance_id = " + this.mInstanceId + ", feature type = " + this.mFeatureType + ", display surface = " + this.mDisplaySurface + ", rank = " + this.mRank + ", cardinality = " + this.mCardinality + ", receivedLatencyMillis = " + this.mReceivedLatency + ", uid = " + this.mUid + ", subcardInfo = " + this.mSubcardInfo + ", dimensionalInfo = " + this.mDimensionalInfo;
    }
}
