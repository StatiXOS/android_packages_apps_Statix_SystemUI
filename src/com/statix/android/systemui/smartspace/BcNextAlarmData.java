package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class BcNextAlarmData {
    public static final SmartspaceAction SHOW_ALARMS_ACTION = new SmartspaceAction.Builder("nextAlarmId", "Next alarm").setIntent(new Intent("android.intent.action.SHOW_ALARMS")).build();
    public String mDescription;
    public Drawable mImage;

    public static void setOnClickListener(View view, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, int i) {
        BcSmartspaceCardLoggingInfo.Builder builder = new BcSmartspaceCardLoggingInfo.Builder();
        builder.mInstanceId = InstanceId.create("upcoming_alarm_card_94510_12684");
        builder.mFeatureType = 23;
        builder.mDisplaySurface = i;
        BcSmartSpaceUtil.setOnClickListener(view, (SmartspaceTarget) null, SHOW_ALARMS_ACTION, smartspaceEventNotifier, "BcNextAlarmData", new BcSmartspaceCardLoggingInfo(builder), 0);
    }
}
