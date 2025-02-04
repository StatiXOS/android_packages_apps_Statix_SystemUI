package com.google.android.systemui.smartspace.uitemplate;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.uitemplatedata.SubListTemplateData;
import android.app.smartspace.uitemplatedata.Text;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.compose.foundation.text.input.internal.RecordingInputConnection$$ExternalSyntheticOutline0;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.BcSmartSpaceUtil;
import com.google.android.systemui.smartspace.BcSmartspaceCardSecondary;
import com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import java.util.List;
import java.util.Locale;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class SubListTemplateCard extends BcSmartspaceCardSecondary {
    public static final int[] LIST_ITEM_TEXT_VIEW_IDS = {R.id.list_item_1, R.id.list_item_2, R.id.list_item_3};
    public ImageView mListIconView;
    public final TextView[] mListItems;

    public SubListTemplateCard(Context context) {
        super(context);
        this.mListItems = new TextView[3];
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.mListIconView = (ImageView) findViewById(R.id.list_icon);
        for (int i = 0; i < 3; i++) {
            this.mListItems[i] = (TextView) findViewById(LIST_ITEM_TEXT_VIEW_IDS[i]);
        }
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mListIconView, 8);
        for (int i = 0; i < 3; i++) {
            BcSmartspaceTemplateDataUtils.updateVisibility(this.mListItems[i], 8);
        }
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        reset$1(smartspaceTarget.getSmartspaceTargetId());
        SubListTemplateData templateData = smartspaceTarget.getTemplateData();
        if (!BcSmartspaceCardLoggerUtil.containsValidTemplateType(templateData)) {
            Log.w("SubListTemplateCard", "SubListTemplateData is null or contains invalid template type");
            return false;
        }
        if (templateData.getSubListIcon() != null) {
            BcSmartspaceTemplateDataUtils.setIcon(this.mListIconView, templateData.getSubListIcon());
            BcSmartspaceTemplateDataUtils.updateVisibility(this.mListIconView, 0);
        } else {
            BcSmartspaceTemplateDataUtils.updateVisibility(this.mListIconView, 8);
        }
        if (templateData.getSubListTexts() != null) {
            List subListTexts = templateData.getSubListTexts();
            if (subListTexts.isEmpty()) {
                return false;
            }
            int i = 0;
            while (true) {
                if (i >= 3) {
                    break;
                }
                TextView textView = this.mListItems[i];
                if (textView == null) {
                    Locale locale = Locale.US;
                    RecordingInputConnection$$ExternalSyntheticOutline0.m("Missing list item view to update at row: ", "SubListTemplateCard", i + 1);
                    break;
                }
                if (i < subListTexts.size()) {
                    BcSmartspaceTemplateDataUtils.setText(textView, (Text) subListTexts.get(i));
                    BcSmartspaceTemplateDataUtils.updateVisibility(textView, 0);
                } else {
                    textView.setText("");
                    BcSmartspaceTemplateDataUtils.updateVisibility(textView, 8);
                }
                i++;
            }
        }
        if (templateData.getSubListAction() != null) {
            BcSmartSpaceUtil.setOnClickListener(this, smartspaceTarget, templateData.getSubListAction(), smartspaceEventNotifier, "SubListTemplateCard", bcSmartspaceCardLoggingInfo, 0);
        }
        return true;
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(int i) {
        for (int i2 = 0; i2 < 3; i2++) {
            TextView textView = this.mListItems[i2];
            if (textView == null) {
                Locale locale = Locale.US;
                RecordingInputConnection$$ExternalSyntheticOutline0.m("Missing list item view to update at row: ", "SubListTemplateCard", i2 + 1);
                return;
            }
            textView.setTextColor(i);
        }
    }

    public SubListTemplateCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mListItems = new TextView[3];
    }
}
