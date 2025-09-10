package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.statix.android.systemui.res.R;

import java.util.List;

public class BcSmartspaceCardCombination extends BcSmartspaceCardSecondary {
    public ConstraintLayout mFirstSubCard;
    public ConstraintLayout mSecondSubCard;

    public BcSmartspaceCardCombination(Context context) {
        super(context);
    }

    public BcSmartspaceCardCombination(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFirstSubCard = findViewById(R.id.first_sub_card);
        mSecondSubCard = findViewById(R.id.second_sub_card);
    }

    @Override
    public void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(mFirstSubCard, View.GONE);
        BcSmartspaceTemplateDataUtils.updateVisibility(mSecondSubCard, View.GONE);
    }

    @Override
    public boolean setSmartspaceActions(
            SmartspaceTarget target,
            BcSmartspaceDataPlugin.SmartspaceEventNotifier eventNotifier,
            BcSmartspaceCardLoggingInfo loggingInfo) {
        List<SmartspaceAction> actionChips = target.getActionChips();
        if (actionChips == null || actionChips.size() < 1) {
            return false;
        }

        SmartspaceAction firstAction = actionChips.get(0);
        if (firstAction == null) {
            return false;
        }

        boolean firstCardSet = false;
        if (mFirstSubCard != null) {
            firstCardSet =
                    fillSubCard(mFirstSubCard, target, firstAction, eventNotifier, loggingInfo);
        }

        boolean secondCardAvailable = actionChips.size() > 1 && actionChips.get(1) != null;
        boolean secondCardSet = true;
        if (secondCardAvailable) {
            SmartspaceAction secondAction = actionChips.get(1);
            secondCardSet =
                    fillSubCard(mSecondSubCard, target, secondAction, eventNotifier, loggingInfo);
        }

        if (getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
            params.weight = secondCardAvailable && secondCardSet ? 3.0f : 1.0f;
            setLayoutParams(params);
        }

        return firstCardSet && secondCardSet;
    }

    public boolean fillSubCard(
            ConstraintLayout subCard,
            SmartspaceTarget target,
            SmartspaceAction action,
            BcSmartspaceDataPlugin.SmartspaceEventNotifier eventNotifier,
            BcSmartspaceCardLoggingInfo loggingInfo) {
        TextView textView = subCard.findViewById(R.id.sub_card_text);
        ImageView iconView = subCard.findViewById(R.id.sub_card_icon);

        if (textView == null) {
            Log.w("BcSmartspaceCardCombination", "No sub-card text field to update");
            return false;
        }
        if (iconView == null) {
            Log.w("BcSmartspaceCardCombination", "No sub-card image field to update");
            return false;
        }

        BcSmartSpaceUtil.setOnClickListener(
                subCard,
                target,
                action,
                eventNotifier,
                "BcSmartspaceCardCombination",
                loggingInfo,
                0);

        Drawable iconDrawable =
                action.getIcon() != null
                        ? BcSmartSpaceUtil.getIconDrawableWithCustomSize(
                                action.getIcon(),
                                getContext(),
                                getResources()
                                        .getDimensionPixelSize(
                                                R.dimen.enhanced_smartspace_icon_size))
                        : null;
        boolean hasIcon = iconDrawable != null;
        if (hasIcon) {
            iconView.setImageDrawable(iconDrawable);
            BcSmartspaceTemplateDataUtils.updateVisibility(iconView, View.VISIBLE);
        } else {
            BcSmartspaceTemplateDataUtils.updateVisibility(iconView, View.GONE);
        }

        CharSequence title = action.getTitle();
        boolean hasText = !TextUtils.isEmpty(title);
        if (hasText) {
            textView.setText(title);
            BcSmartspaceTemplateDataUtils.updateVisibility(textView, View.VISIBLE);
        } else {
            BcSmartspaceTemplateDataUtils.updateVisibility(textView, View.GONE);
        }

        subCard.setContentDescription(hasIcon || hasText ? action.getContentDescription() : null);
        boolean isValid = hasIcon || hasText;
        BcSmartspaceTemplateDataUtils.updateVisibility(subCard, isValid ? View.VISIBLE : View.GONE);
        return isValid;
    }

    @Override
    public void setTextColor(int color) {
        // No-op
    }
}
