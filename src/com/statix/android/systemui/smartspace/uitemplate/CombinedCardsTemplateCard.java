package com.google.android.systemui.smartspace.uitemplate;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.app.smartspace.uitemplatedata.CombinedCardsTemplateData;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.BcSmartspaceCardSecondary;
import com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import java.util.List;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class CombinedCardsTemplateCard extends BcSmartspaceCardSecondary {
    public ConstraintLayout mFirstSubCard;
    public ConstraintLayout mSecondSubCard;

    public CombinedCardsTemplateCard(Context context) {
        super(context);
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.mFirstSubCard = (ConstraintLayout) findViewById(R.id.first_sub_card_container);
        this.mSecondSubCard = (ConstraintLayout) findViewById(R.id.second_sub_card_container);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mFirstSubCard, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondSubCard, 8);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        reset$1(smartspaceTarget.getSmartspaceTargetId());
        CombinedCardsTemplateData templateData = smartspaceTarget.getTemplateData();
        if (!BcSmartspaceCardLoggerUtil.containsValidTemplateType(templateData) || templateData.getCombinedCardDataList().isEmpty()) {
            Log.w("CombinedCardsTemplateCard", "TemplateData is null or empty or invalid template type");
            return false;
        }
        List combinedCardDataList = templateData.getCombinedCardDataList();
        BaseTemplateData baseTemplateData = (BaseTemplateData) combinedCardDataList.get(0);
        BaseTemplateData baseTemplateData2 = combinedCardDataList.size() > 1 ? (BaseTemplateData) combinedCardDataList.get(1) : null;
        if (setupSubCard(this.mFirstSubCard, baseTemplateData, smartspaceTarget, smartspaceEventNotifier, bcSmartspaceCardLoggingInfo)) {
            return baseTemplateData2 == null || setupSubCard(this.mSecondSubCard, baseTemplateData2, smartspaceTarget, smartspaceEventNotifier, bcSmartspaceCardLoggingInfo);
        }
        return false;
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(int i) {
        if (this.mFirstSubCard.getChildCount() != 0) {
            ((BcSmartspaceCardSecondary) this.mFirstSubCard.getChildAt(0)).setTextColor(i);
        }
        if (this.mSecondSubCard.getChildCount() != 0) {
            ((BcSmartspaceCardSecondary) this.mSecondSubCard.getChildAt(0)).setTextColor(i);
        }
    }

    public final boolean setupSubCard(ViewGroup viewGroup, BaseTemplateData baseTemplateData, SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        int i;
        if (baseTemplateData == null) {
            BcSmartspaceTemplateDataUtils.updateVisibility(viewGroup, 8);
            Log.w("CombinedCardsTemplateCard", "Sub-card templateData is null or empty");
            return false;
        }
        switch (baseTemplateData.getTemplateType()) {
            case 2:
                i = R.layout.smartspace_sub_image_template_card;
                break;
            case 3:
                i = R.layout.smartspace_sub_list_template_card;
                break;
            case 4:
                i = R.layout.smartspace_carousel_template_card;
                break;
            case 5:
                i = R.layout.smartspace_head_to_head_template_card;
                break;
            case 6:
                i = R.layout.smartspace_combined_cards_template_card;
                break;
            case 7:
                i = R.layout.smartspace_sub_card_template_card;
                break;
            default:
                i = 0;
                break;
        }
        if (i == 0) {
            BcSmartspaceTemplateDataUtils.updateVisibility(viewGroup, 8);
            Log.w("CombinedCardsTemplateCard", "Combined sub-card res is null. Cannot set it up");
            return false;
        }
        BcSmartspaceCardSecondary bcSmartspaceCardSecondary = (BcSmartspaceCardSecondary) LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false);
        bcSmartspaceCardSecondary.setSmartspaceActions(new SmartspaceTarget.Builder(smartspaceTarget.getSmartspaceTargetId(), smartspaceTarget.getComponentName(), smartspaceTarget.getUserHandle()).setTemplateData(baseTemplateData).build(), smartspaceEventNotifier, bcSmartspaceCardLoggingInfo);
        viewGroup.removeAllViews();
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_card_height));
        layoutParams.startToStart = 0;
        layoutParams.endToEnd = 0;
        layoutParams.topToTop = 0;
        layoutParams.bottomToBottom = 0;
        BcSmartspaceTemplateDataUtils.updateVisibility(bcSmartspaceCardSecondary, 0);
        viewGroup.addView(bcSmartspaceCardSecondary, layoutParams);
        BcSmartspaceTemplateDataUtils.updateVisibility(viewGroup, 0);
        return true;
    }

    public CombinedCardsTemplateCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
