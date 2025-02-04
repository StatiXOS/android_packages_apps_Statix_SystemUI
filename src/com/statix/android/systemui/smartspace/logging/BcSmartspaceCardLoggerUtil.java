package com.google.android.systemui.smartspace.logging;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.os.Bundle;
import com.android.systemui.smartspace.nano.SmartspaceProto$SmartspaceCardDimensionalInfo;
import com.android.systemui.smartspace.nano.SmartspaceProto$SmartspaceFeatureDimension;
import com.google.android.systemui.smartspace.InstanceId;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardMetadataLoggingInfo;
import java.util.ArrayList;
import java.util.List;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public abstract class BcSmartspaceCardLoggerUtil {
    public static boolean containsValidTemplateType(BaseTemplateData baseTemplateData) {
        return (baseTemplateData == null || baseTemplateData.getTemplateType() == 0 || baseTemplateData.getTemplateType() == 8) ? false : true;
    }

    public static SmartspaceProto$SmartspaceCardDimensionalInfo createDimensionalLoggingInfo(BaseTemplateData baseTemplateData) {
        if (baseTemplateData == null || baseTemplateData.getPrimaryItem() == null || baseTemplateData.getPrimaryItem().getTapAction() == null) {
            return null;
        }
        Bundle extras = baseTemplateData.getPrimaryItem().getTapAction().getExtras();
        ArrayList arrayList = new ArrayList();
        if (extras != null && !extras.isEmpty()) {
            ArrayList<Integer> integerArrayList = extras.getIntegerArrayList("ss_card_dimension_ids");
            ArrayList<Integer> integerArrayList2 = extras.getIntegerArrayList("ss_card_dimension_values");
            if (integerArrayList != null && integerArrayList2 != null && integerArrayList.size() == integerArrayList2.size()) {
                for (int i = 0; i < integerArrayList.size(); i++) {
                    SmartspaceProto$SmartspaceFeatureDimension smartspaceProto$SmartspaceFeatureDimension = new SmartspaceProto$SmartspaceFeatureDimension();
                    smartspaceProto$SmartspaceFeatureDimension.featureDimensionId = integerArrayList.get(i).intValue();
                    smartspaceProto$SmartspaceFeatureDimension.featureDimensionValue = integerArrayList2.get(i).intValue();
                    arrayList.add(smartspaceProto$SmartspaceFeatureDimension);
                }
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        SmartspaceProto$SmartspaceCardDimensionalInfo smartspaceProto$SmartspaceCardDimensionalInfo = new SmartspaceProto$SmartspaceCardDimensionalInfo();
        smartspaceProto$SmartspaceCardDimensionalInfo.featureDimensions = (SmartspaceProto$SmartspaceFeatureDimension[]) arrayList.toArray(new SmartspaceProto$SmartspaceFeatureDimension[arrayList.size()]);
        return smartspaceProto$SmartspaceCardDimensionalInfo;
    }

    public static BcSmartspaceSubcardLoggingInfo createSubcardLoggingInfo(SmartspaceTarget smartspaceTarget) {
        if (smartspaceTarget == null || smartspaceTarget.getBaseAction() == null || smartspaceTarget.getBaseAction().getExtras() == null || smartspaceTarget.getBaseAction().getExtras().isEmpty() || smartspaceTarget.getBaseAction().getExtras().getInt("subcardType", -1) == -1) {
            return null;
        }
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        int create = InstanceId.create(baseAction.getExtras().getString("subcardId"));
        int i = baseAction.getExtras().getInt("subcardType");
        BcSmartspaceCardMetadataLoggingInfo.Builder builder = new BcSmartspaceCardMetadataLoggingInfo.Builder();
        builder.mInstanceId = create;
        builder.mCardTypeId = i;
        BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = new BcSmartspaceCardMetadataLoggingInfo(builder);
        ArrayList arrayList = new ArrayList();
        arrayList.add(bcSmartspaceCardMetadataLoggingInfo);
        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = new BcSmartspaceSubcardLoggingInfo();
        bcSmartspaceSubcardLoggingInfo.mSubcards = arrayList;
        bcSmartspaceSubcardLoggingInfo.mClickedSubcardIndex = 0;
        return bcSmartspaceSubcardLoggingInfo;
    }

    public static void createSubcardLoggingInfoHelper(List list, BaseTemplateData.SubItemInfo subItemInfo) {
        if (subItemInfo == null || subItemInfo.getLoggingInfo() == null) {
            return;
        }
        BaseTemplateData.SubItemLoggingInfo loggingInfo = subItemInfo.getLoggingInfo();
        BcSmartspaceCardMetadataLoggingInfo.Builder builder = new BcSmartspaceCardMetadataLoggingInfo.Builder();
        builder.mCardTypeId = loggingInfo.getFeatureType();
        builder.mInstanceId = loggingInfo.getInstanceId();
        list.add(new BcSmartspaceCardMetadataLoggingInfo(builder));
    }

    public static void tryForcePrimaryFeatureTypeAndInjectWeatherSubcard(BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, SmartspaceTarget smartspaceTarget) {
        if (bcSmartspaceCardLoggingInfo.mFeatureType != 1) {
            return;
        }
        bcSmartspaceCardLoggingInfo.mFeatureType = 39;
        bcSmartspaceCardLoggingInfo.mInstanceId = InstanceId.create("date_card_794317_92634");
        if (smartspaceTarget == null || "date_card_794317_92634".equals(smartspaceTarget.getSmartspaceTargetId())) {
            return;
        }
        if (bcSmartspaceCardLoggingInfo.mSubcardInfo == null) {
            ArrayList arrayList = new ArrayList();
            BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = new BcSmartspaceSubcardLoggingInfo();
            bcSmartspaceSubcardLoggingInfo.mSubcards = arrayList;
            bcSmartspaceSubcardLoggingInfo.mClickedSubcardIndex = 0;
            bcSmartspaceCardLoggingInfo.mSubcardInfo = bcSmartspaceSubcardLoggingInfo;
        }
        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo2 = bcSmartspaceCardLoggingInfo.mSubcardInfo;
        if (bcSmartspaceSubcardLoggingInfo2.mSubcards == null) {
            bcSmartspaceSubcardLoggingInfo2.mSubcards = new ArrayList();
        }
        if (((ArrayList) bcSmartspaceCardLoggingInfo.mSubcardInfo.mSubcards).size() == 0 || !(((ArrayList) bcSmartspaceCardLoggingInfo.mSubcardInfo.mSubcards).get(0) == null || ((BcSmartspaceCardMetadataLoggingInfo) ((ArrayList) bcSmartspaceCardLoggingInfo.mSubcardInfo.mSubcards).get(0)).mCardTypeId == 1)) {
            List list = bcSmartspaceCardLoggingInfo.mSubcardInfo.mSubcards;
            BcSmartspaceCardMetadataLoggingInfo.Builder builder = new BcSmartspaceCardMetadataLoggingInfo.Builder();
            builder.mInstanceId = InstanceId.create(smartspaceTarget);
            builder.mCardTypeId = 1;
            list.add(0, new BcSmartspaceCardMetadataLoggingInfo(builder));
            BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo3 = bcSmartspaceCardLoggingInfo.mSubcardInfo;
            int i = bcSmartspaceSubcardLoggingInfo3.mClickedSubcardIndex;
            if (i > 0) {
                bcSmartspaceSubcardLoggingInfo3.mClickedSubcardIndex = i + 1;
            }
        }
    }

    public static void tryForcePrimaryFeatureTypeOrUpdateLogInfoFromTemplateData(BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, BaseTemplateData baseTemplateData) {
        if (bcSmartspaceCardLoggingInfo.mFeatureType == 1) {
            bcSmartspaceCardLoggingInfo.mFeatureType = 39;
            bcSmartspaceCardLoggingInfo.mInstanceId = InstanceId.create("date_card_794317_92634");
            return;
        }
        if (baseTemplateData == null || baseTemplateData.getPrimaryItem() == null || baseTemplateData.getPrimaryItem().getLoggingInfo() == null) {
            return;
        }
        int featureType = baseTemplateData.getPrimaryItem().getLoggingInfo().getFeatureType();
        if (featureType > 0) {
            bcSmartspaceCardLoggingInfo.mFeatureType = featureType;
        }
        int instanceId = baseTemplateData.getPrimaryItem().getLoggingInfo().getInstanceId();
        if (instanceId > 0) {
            bcSmartspaceCardLoggingInfo.mInstanceId = instanceId;
        }
    }

    public static BcSmartspaceSubcardLoggingInfo createSubcardLoggingInfo(BaseTemplateData baseTemplateData) {
        if (baseTemplateData == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        createSubcardLoggingInfoHelper(arrayList, baseTemplateData.getSubtitleItem());
        createSubcardLoggingInfoHelper(arrayList, baseTemplateData.getSubtitleSupplementalItem());
        createSubcardLoggingInfoHelper(arrayList, baseTemplateData.getSupplementalLineItem());
        if (arrayList.isEmpty()) {
            return null;
        }
        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = new BcSmartspaceSubcardLoggingInfo();
        bcSmartspaceSubcardLoggingInfo.mSubcards = arrayList;
        bcSmartspaceSubcardLoggingInfo.mClickedSubcardIndex = 0;
        return bcSmartspaceSubcardLoggingInfo;
    }
}
