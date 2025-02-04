package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import java.util.List;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class BcSmartspaceCardCombination extends BcSmartspaceCardSecondary {
    public ConstraintLayout mFirstSubCard;
    public ConstraintLayout mSecondSubCard;

    public BcSmartspaceCardCombination(Context context) {
        super(context);
    }

    public final boolean fillSubCard(ConstraintLayout constraintLayout, SmartspaceTarget smartspaceTarget, SmartspaceAction smartspaceAction, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        boolean z;
        TextView textView = (TextView) constraintLayout.findViewById(R.id.sub_card_text);
        ImageView imageView = (ImageView) constraintLayout.findViewById(R.id.sub_card_icon);
        if (textView == null) {
            Log.w("BcSmartspaceCardCombination", "No sub-card text field to update");
            return false;
        }
        if (imageView == null) {
            Log.w("BcSmartspaceCardCombination", "No sub-card image field to update");
            return false;
        }
        BcSmartSpaceUtil.setOnClickListener(constraintLayout, smartspaceTarget, smartspaceAction, smartspaceEventNotifier, "BcSmartspaceCardCombination", bcSmartspaceCardLoggingInfo, 0);
        Icon icon = smartspaceAction.getIcon();
        Context context = getContext();
        Drawable iconDrawableWithCustomSize = BcSmartSpaceUtil.getIconDrawableWithCustomSize(icon, context, context.getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_icon_size));
        boolean z2 = true;
        if (iconDrawableWithCustomSize == null) {
            BcSmartspaceTemplateDataUtils.updateVisibility(imageView, 8);
            z = false;
        } else {
            imageView.setImageDrawable(iconDrawableWithCustomSize);
            BcSmartspaceTemplateDataUtils.updateVisibility(imageView, 0);
            z = true;
        }
        CharSequence title = smartspaceAction.getTitle();
        if (TextUtils.isEmpty(title)) {
            BcSmartspaceTemplateDataUtils.updateVisibility(textView, 8);
            z2 = z;
        } else {
            textView.setText(title);
            BcSmartspaceTemplateDataUtils.updateVisibility(textView, 0);
        }
        constraintLayout.setContentDescription(z2 ? smartspaceAction.getContentDescription() : null);
        if (z2) {
            BcSmartspaceTemplateDataUtils.updateVisibility(constraintLayout, 0);
        } else {
            BcSmartspaceTemplateDataUtils.updateVisibility(constraintLayout, 8);
        }
        return z2;
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.mFirstSubCard = (ConstraintLayout) findViewById(R.id.first_sub_card);
        this.mSecondSubCard = (ConstraintLayout) findViewById(R.id.second_sub_card);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mFirstSubCard, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondSubCard, 8);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        SmartspaceAction smartspaceAction;
        List actionChips = smartspaceTarget.getActionChips();
        if (actionChips == null || actionChips.size() < 1 || (smartspaceAction = (SmartspaceAction) actionChips.get(0)) == null) {
            return false;
        }
        ConstraintLayout constraintLayout = this.mFirstSubCard;
        boolean z = constraintLayout != null && fillSubCard(constraintLayout, smartspaceTarget, smartspaceAction, smartspaceEventNotifier, bcSmartspaceCardLoggingInfo);
        boolean z2 = actionChips.size() > 1 && actionChips.get(1) != null;
        boolean fillSubCard = z2 ? fillSubCard(this.mSecondSubCard, smartspaceTarget, (SmartspaceAction) actionChips.get(1), smartspaceEventNotifier, bcSmartspaceCardLoggingInfo) : true;
        if (getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
            if (z2 && fillSubCard) {
                layoutParams.weight = 3.0f;
            } else {
                layoutParams.weight = 1.0f;
            }
            setLayoutParams(layoutParams);
        }
        return z && fillSubCard;
    }

    public BcSmartspaceCardCombination(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(int i) {
    }
}
