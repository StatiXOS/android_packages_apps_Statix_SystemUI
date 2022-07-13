package com.google.android.systemui.smartspace.logging;

import java.util.ArrayList;
import java.util.List;

public class BcSmartspaceSubcardLoggingInfo {
    private int mClickedSubcardIndex;
    private List<BcSmartspaceCardMetadataLoggingInfo> mSubcards;

    private BcSmartspaceSubcardLoggingInfo(Builder builder) {
        if (builder.mSubcards == null) {
            this.mSubcards = new ArrayList();
        } else {
            this.mSubcards = builder.mSubcards;
        }
        this.mClickedSubcardIndex = builder.mClickedSubcardIndex;
    }

    public List<BcSmartspaceCardMetadataLoggingInfo> getSubcards() {
        return this.mSubcards;
    }

    public void setSubcards(List<BcSmartspaceCardMetadataLoggingInfo> list) {
        this.mSubcards = list;
    }

    public int getClickedSubcardIndex() {
        return this.mClickedSubcardIndex;
    }

    public void setClickedSubcardIndex(int i) {
        this.mClickedSubcardIndex = i;
    }

    public String toString() {
        return "BcSmartspaceSubcardLoggingInfo{mSubcards=" + this.mSubcards + ", mClickedSubcardIndex=" + this.mClickedSubcardIndex + '}';
    }

    public static class Builder {
        private int mClickedSubcardIndex;
        private List<BcSmartspaceCardMetadataLoggingInfo> mSubcards;

        public Builder setSubcards(List<BcSmartspaceCardMetadataLoggingInfo> list) {
            this.mSubcards = list;
            return this;
        }

        public Builder setClickedSubcardIndex(int i) {
            this.mClickedSubcardIndex = i;
            return this;
        }

        public BcSmartspaceSubcardLoggingInfo build() {
            return new BcSmartspaceSubcardLoggingInfo(this);
        }
    }
}
