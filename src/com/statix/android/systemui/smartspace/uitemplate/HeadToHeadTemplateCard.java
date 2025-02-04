package com.google.android.systemui.smartspace.uitemplate;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.BcSmartspaceCardSecondary;
import com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class HeadToHeadTemplateCard extends BcSmartspaceCardSecondary {
    public ImageView mFirstCompetitorIcon;
    public TextView mFirstCompetitorText;
    public TextView mHeadToHeadTitle;
    public ImageView mSecondCompetitorIcon;
    public TextView mSecondCompetitorText;

    public HeadToHeadTemplateCard(Context context) {
        super(context);
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.mHeadToHeadTitle = (TextView) findViewById(R.id.head_to_head_title);
        this.mFirstCompetitorText = (TextView) findViewById(R.id.first_competitor_text);
        this.mSecondCompetitorText = (TextView) findViewById(R.id.second_competitor_text);
        this.mFirstCompetitorIcon = (ImageView) findViewById(R.id.first_competitor_icon);
        this.mSecondCompetitorIcon = (ImageView) findViewById(R.id.second_competitor_icon);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mHeadToHeadTitle, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mFirstCompetitorText, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondCompetitorText, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mFirstCompetitorIcon, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondCompetitorIcon, 8);
    }

    /* JADX WARN: Code restructure failed: missing block: B:44:0x00ab, code lost:
    
        if (r1 != false) goto L51;
     */
    /* JADX WARN: Removed duplicated region for block: B:14:0x003b  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x005c  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x009e  */
    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean setSmartspaceActions(android.app.smartspace.SmartspaceTarget r11, com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceEventNotifier r12, com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo r13) {
        /*
            r10 = this;
            android.app.smartspace.uitemplatedata.BaseTemplateData r0 = r11.getTemplateData()
            android.app.smartspace.uitemplatedata.HeadToHeadTemplateData r0 = (android.app.smartspace.uitemplatedata.HeadToHeadTemplateData) r0
            boolean r1 = com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil.containsValidTemplateType(r0)
            java.lang.String r2 = "HeadToHeadTemplateCard"
            r3 = 0
            if (r1 != 0) goto L15
            java.lang.String r10 = "HeadToHeadTemplateData is null or invalid template type"
            android.util.Log.w(r2, r10)
            return r3
        L15:
            android.app.smartspace.uitemplatedata.Text r1 = r0.getHeadToHeadTitle()
            r4 = 1
            if (r1 == 0) goto L34
            android.app.smartspace.uitemplatedata.Text r1 = r0.getHeadToHeadTitle()
            android.widget.TextView r5 = r10.mHeadToHeadTitle
            if (r5 != 0) goto L2a
            java.lang.String r1 = "No head-to-head title view to update"
            android.util.Log.w(r2, r1)
            goto L34
        L2a:
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.setText(r5, r1)
            android.widget.TextView r1 = r10.mHeadToHeadTitle
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.updateVisibility(r1, r3)
            r1 = r4
            goto L35
        L34:
            r1 = r3
        L35:
            android.app.smartspace.uitemplatedata.Text r5 = r0.getHeadToHeadFirstCompetitorText()
            if (r5 == 0) goto L56
            android.app.smartspace.uitemplatedata.Text r5 = r0.getHeadToHeadFirstCompetitorText()
            android.widget.TextView r6 = r10.mFirstCompetitorText
            if (r6 != 0) goto L4d
            java.lang.String r5 = "No first competitor text view to update"
            android.util.Log.w(r2, r5)
            if (r1 == 0) goto L4b
            goto L55
        L4b:
            r1 = r3
            goto L56
        L4d:
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.setText(r6, r5)
            android.widget.TextView r1 = r10.mFirstCompetitorText
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.updateVisibility(r1, r3)
        L55:
            r1 = r4
        L56:
            android.app.smartspace.uitemplatedata.Text r5 = r0.getHeadToHeadSecondCompetitorText()
            if (r5 == 0) goto L77
            android.app.smartspace.uitemplatedata.Text r5 = r0.getHeadToHeadSecondCompetitorText()
            android.widget.TextView r6 = r10.mSecondCompetitorText
            if (r6 != 0) goto L6e
            java.lang.String r5 = "No second competitor text view to update"
            android.util.Log.w(r2, r5)
            if (r1 == 0) goto L6c
            goto L76
        L6c:
            r1 = r3
            goto L77
        L6e:
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.setText(r6, r5)
            android.widget.TextView r1 = r10.mSecondCompetitorText
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.updateVisibility(r1, r3)
        L76:
            r1 = r4
        L77:
            android.app.smartspace.uitemplatedata.Icon r5 = r0.getHeadToHeadFirstCompetitorIcon()
            if (r5 == 0) goto L98
            android.app.smartspace.uitemplatedata.Icon r5 = r0.getHeadToHeadFirstCompetitorIcon()
            android.widget.ImageView r6 = r10.mFirstCompetitorIcon
            if (r6 != 0) goto L8f
            java.lang.String r5 = "No first competitor icon view to update"
            android.util.Log.w(r2, r5)
            if (r1 == 0) goto L8d
            goto L97
        L8d:
            r1 = r3
            goto L98
        L8f:
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.setIcon(r6, r5)
            android.widget.ImageView r1 = r10.mFirstCompetitorIcon
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.updateVisibility(r1, r3)
        L97:
            r1 = r4
        L98:
            android.app.smartspace.uitemplatedata.Icon r5 = r0.getHeadToHeadSecondCompetitorIcon()
            if (r5 == 0) goto Lb8
            android.app.smartspace.uitemplatedata.Icon r5 = r0.getHeadToHeadSecondCompetitorIcon()
            android.widget.ImageView r6 = r10.mSecondCompetitorIcon
            if (r6 != 0) goto Lae
            java.lang.String r5 = "No second competitor icon view to update"
            android.util.Log.w(r2, r5)
            if (r1 == 0) goto Lb7
            goto Lb6
        Lae:
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.setIcon(r6, r5)
            android.widget.ImageView r1 = r10.mSecondCompetitorIcon
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.updateVisibility(r1, r3)
        Lb6:
            r3 = r4
        Lb7:
            r1 = r3
        Lb8:
            if (r1 == 0) goto Lce
            android.app.smartspace.uitemplatedata.TapAction r2 = r0.getHeadToHeadAction()
            if (r2 == 0) goto Lce
            android.app.smartspace.uitemplatedata.TapAction r5 = r0.getHeadToHeadAction()
            r9 = 0
            java.lang.String r7 = "HeadToHeadTemplateCard"
            r3 = r10
            r4 = r11
            r6 = r12
            r8 = r13
            com.google.android.systemui.smartspace.BcSmartSpaceUtil.setOnClickListener(r3, r4, r5, r6, r7, r8, r9)
        Lce:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.uitemplate.HeadToHeadTemplateCard.setSmartspaceActions(android.app.smartspace.SmartspaceTarget, com.android.systemui.plugins.BcSmartspaceDataPlugin$SmartspaceEventNotifier, com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo):boolean");
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(int i) {
        this.mFirstCompetitorText.setTextColor(i);
        this.mSecondCompetitorText.setTextColor(i);
    }

    public HeadToHeadTemplateCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
