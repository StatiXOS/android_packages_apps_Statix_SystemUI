package com.google.android.systemui.smartspace.logging;

public class BcSmartspaceCardLoggingInfo {
    private final int mCardinality;
    private final int mDisplaySurface;
    private int mFeatureType;
    private final int mInstanceId;
    private final int mRank;
    private final int mReceivedLatency;
    private BcSmartspaceSubcardLoggingInfo mSubcardInfo;

    private BcSmartspaceCardLoggingInfo(Builder builder) {
        this.mInstanceId = builder.mInstanceId;
        this.mDisplaySurface = builder.mDisplaySurface;
        this.mRank = builder.mRank;
        this.mCardinality = builder.mCardinality;
        this.mFeatureType = builder.mFeatureType;
        this.mReceivedLatency = builder.mReceivedLatency;
        this.mSubcardInfo = builder.mSubcardInfo;
    }

    public int getInstanceId() {
        return this.mInstanceId;
    }

    public int getDisplaySurface() {
        return this.mDisplaySurface;
    }

    public int getRank() {
        return this.mRank;
    }

    public int getCardinality() {
        return this.mCardinality;
    }

    public int getFeatureType() {
        return this.mFeatureType;
    }

    public int getReceivedLatency() {
        return this.mReceivedLatency;
    }

    public BcSmartspaceSubcardLoggingInfo getSubcardInfo() {
        return this.mSubcardInfo;
    }

    public void setFeatureType(int i) {
        this.mFeatureType = i;
    }

    public void setSubcardInfo(BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo) {
        this.mSubcardInfo = bcSmartspaceSubcardLoggingInfo;
    }

    public String toString() {
        return "instance_id = " + getInstanceId() + ", feature type = " + getFeatureType() + ", display surface = " + getDisplaySurface() + ", rank = " + getRank() + ", cardinality = " + getCardinality() + ", receivedLatencyMillis = " + getReceivedLatency() + ", subcardInfo = " + getSubcardInfo();
    }

    public static class Builder {
        private int mCardinality;
        private int mDisplaySurface = 1;
        private int mFeatureType;
        private int mInstanceId;
        private int mRank;
        private int mReceivedLatency;
        private BcSmartspaceSubcardLoggingInfo mSubcardInfo;

        public Builder setInstanceId(int i) {
            this.mInstanceId = i;
            return this;
        }

        public Builder setDisplaySurface(int i) {
            this.mDisplaySurface = i;
            return this;
        }

        public Builder setRank(int i) {
            this.mRank = i;
            return this;
        }

        public Builder setCardinality(int i) {
            this.mCardinality = i;
            return this;
        }

        public Builder setFeatureType(int i) {
            this.mFeatureType = i;
            return this;
        }

        public Builder setReceivedLatency(int i) {
            this.mReceivedLatency = i;
            return this;
        }

        public Builder setSubcardInfo(BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo) {
            this.mSubcardInfo = bcSmartspaceSubcardLoggingInfo;
            return this;
        }

        public BcSmartspaceCardLoggingInfo build() {
            return new BcSmartspaceCardLoggingInfo(this);
        }
    }
}
