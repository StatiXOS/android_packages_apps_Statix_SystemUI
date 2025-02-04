package com.google.android.systemui.smartspace.logging;

import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.smartspace.SmartspaceProtoLite$SmartSpaceCardMetadata;
import com.android.systemui.smartspace.SmartspaceProtoLite$SmartSpaceSubcards;
import com.android.systemui.smartspace.nano.SmartspaceProto$SmartspaceCardDimensionalInfo;
import com.google.android.systemui.smartspace.BcSmartspaceEvent;
import com.google.protobuf.nano.MessageNano;
import java.util.ArrayList;
import java.util.List;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public abstract class BcSmartspaceCardLogger {
    public static void log(BcSmartspaceEvent bcSmartspaceEvent, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        byte[] bArr;
        List list;
        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = bcSmartspaceCardLoggingInfo.mSubcardInfo;
        if (bcSmartspaceSubcardLoggingInfo == null || (list = bcSmartspaceSubcardLoggingInfo.mSubcards) == null || list.isEmpty()) {
            bArr = null;
        } else {
            ArrayList arrayList = new ArrayList();
            List list2 = bcSmartspaceSubcardLoggingInfo.mSubcards;
            int i = 0;
            while (true) {
                ArrayList arrayList2 = (ArrayList) list2;
                if (i >= arrayList2.size()) {
                    break;
                }
                BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = (BcSmartspaceCardMetadataLoggingInfo) arrayList2.get(i);
                SmartspaceProtoLite$SmartSpaceCardMetadata.Builder newBuilder = SmartspaceProtoLite$SmartSpaceCardMetadata.newBuilder();
                int i2 = bcSmartspaceCardMetadataLoggingInfo.mInstanceId;
                newBuilder.copyOnWrite();
                SmartspaceProtoLite$SmartSpaceCardMetadata.m866$$Nest$msetInstanceId((SmartspaceProtoLite$SmartSpaceCardMetadata) newBuilder.instance, i2);
                newBuilder.copyOnWrite();
                SmartspaceProtoLite$SmartSpaceCardMetadata.m865$$Nest$msetCardTypeId((SmartspaceProtoLite$SmartSpaceCardMetadata) newBuilder.instance, bcSmartspaceCardMetadataLoggingInfo.mCardTypeId);
                arrayList.add((SmartspaceProtoLite$SmartSpaceCardMetadata) newBuilder.build());
                i++;
            }
            SmartspaceProtoLite$SmartSpaceSubcards.Builder newBuilder2 = SmartspaceProtoLite$SmartSpaceSubcards.newBuilder();
            int i3 = bcSmartspaceSubcardLoggingInfo.mClickedSubcardIndex;
            newBuilder2.copyOnWrite();
            SmartspaceProtoLite$SmartSpaceSubcards.m868$$Nest$msetClickedSubcardIndex((SmartspaceProtoLite$SmartSpaceSubcards) newBuilder2.instance, i3);
            newBuilder2.copyOnWrite();
            SmartspaceProtoLite$SmartSpaceSubcards.m867$$Nest$maddAllSubcards((SmartspaceProtoLite$SmartSpaceSubcards) newBuilder2.instance, arrayList);
            bArr = ((SmartspaceProtoLite$SmartSpaceSubcards) newBuilder2.build()).toByteArray();
        }
        SmartspaceProto$SmartspaceCardDimensionalInfo smartspaceProto$SmartspaceCardDimensionalInfo = bcSmartspaceCardLoggingInfo.mDimensionalInfo;
        SysUiStatsLog.write(bcSmartspaceEvent.getId(), bcSmartspaceCardLoggingInfo.mInstanceId, bcSmartspaceCardLoggingInfo.mDisplaySurface, bcSmartspaceCardLoggingInfo.mRank, bcSmartspaceCardLoggingInfo.mCardinality, bcSmartspaceCardLoggingInfo.mFeatureType, bcSmartspaceCardLoggingInfo.mUid, 0, 0, bcSmartspaceCardLoggingInfo.mReceivedLatency, bArr, smartspaceProto$SmartspaceCardDimensionalInfo != null ? MessageNano.toByteArray(smartspaceProto$SmartspaceCardDimensionalInfo) : null);
    }
}
