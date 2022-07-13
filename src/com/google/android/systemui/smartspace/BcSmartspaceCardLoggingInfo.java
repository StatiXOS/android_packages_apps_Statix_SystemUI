package com.google.android.systemui.smartspace;

public class BcSmartspaceCardLoggingInfo {
    private final int mCardinality;
    private final int mDisplaySurface;
    private final int mInstanceId;
    private final int mLoggingCardType;
    private final int mRank;

    public static class Builder {
        private int mCardinality;
        private int mDisplaySurface = 1;
        private int mInstanceId;
        private int mLoggingCardType;
        private int mRank;

        public BcSmartspaceCardLoggingInfo build() {
            return new BcSmartspaceCardLoggingInfo(this);
        }

        public Builder setCardinality(int i) {
            mCardinality = i;
            return this;
        }

        public Builder setDisplaySurface(int i) {
            mDisplaySurface = i;
            return this;
        }

        public Builder setInstanceId(int i) {
            mInstanceId = i;
            return this;
        }

        public Builder setLoggingCardType(int i) {
            mLoggingCardType = i;
            return this;
        }

        public Builder setRank(int i) {
            mRank = i;
            return this;
        }
    }

    private BcSmartspaceCardLoggingInfo(Builder builder) {
        mInstanceId = builder.mInstanceId;
        mLoggingCardType = builder.mLoggingCardType;
        mDisplaySurface = builder.mDisplaySurface;
        mRank = builder.mRank;
        mCardinality = builder.mCardinality;
    }

    public int getCardinality() {
        return mCardinality;
    }

    public int getDisplaySurface() {
        return mDisplaySurface;
    }

    public int getInstanceId() {
        return mInstanceId;
    }

    public int getLoggingCardType() {
        return mLoggingCardType;
    }

    public int getRank() {
        return mRank;
    }

    public String toString() {
        return "instance_id = " + getInstanceId() + ", card type = " + getLoggingCardType() + ", display surface = " + getDisplaySurface() + ", rank = " + getRank() + ", cardinality = " + getCardinality();
    }
}
