package com.google.android.systemui.smartspace.logging;

public class BcSmartspaceCardMetadataLoggingInfo {
    private final int mCardTypeId;
    private final int mInstanceId;

    private BcSmartspaceCardMetadataLoggingInfo(Builder builder) {
        this.mInstanceId = builder.mInstanceId;
        this.mCardTypeId = builder.mCardTypeId;
    }

    public int getInstanceId() {
        return this.mInstanceId;
    }

    public int getCardTypeId() {
        return this.mCardTypeId;
    }

    public String toString() {
        return "BcSmartspaceCardMetadataLoggingInfo{mInstanceId=" + this.mInstanceId + ", mCardTypeId=" + this.mCardTypeId + '}';
    }

    public static class Builder {
        private int mCardTypeId;
        private int mInstanceId;

        public Builder setInstanceId(int i) {
            this.mInstanceId = i;
            return this;
        }

        public Builder setCardTypeId(int i) {
            this.mCardTypeId = i;
            return this;
        }

        public BcSmartspaceCardMetadataLoggingInfo build() {
            return new BcSmartspaceCardMetadataLoggingInfo(this);
        }
    }
}
