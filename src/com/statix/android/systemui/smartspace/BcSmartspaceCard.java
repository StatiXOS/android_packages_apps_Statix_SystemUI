package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.PathInterpolator;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.app.animation.Interpolators;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardMetadataLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceSubcardLoggingInfo;
import com.google.android.systemui.smartspace.utils.ContentDescriptionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class BcSmartspaceCard extends ConstraintLayout {
    public final DoubleShadowIconDrawable mBaseActionIconDrawable;
    public DoubleShadowTextView mBaseActionIconSubtitleView;
    public float mDozeAmount;
    public CardPagerAdapter$onBindViewHolder$1 mEventNotifier;
    public final DoubleShadowIconDrawable mIconDrawable;
    public int mIconTintColor;
    public String mPrevSmartspaceTargetId;
    public BcSmartspaceCardSecondary mSecondaryCard;
    public ViewGroup mSecondaryCardGroup;
    public TextView mSubtitleTextView;
    public SmartspaceTarget mTarget;
    public ViewGroup mTextGroup;
    public TextView mTitleTextView;
    public boolean mUsePageIndicatorUi;
    public boolean mValidSecondaryCard;

    public BcSmartspaceCard(Context context) {
        this(context, null);
    }

    public static int getClickedIndex(BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, int i) {
        List list;
        BcSmartspaceSubcardLoggingInfo bcSmartspaceSubcardLoggingInfo = bcSmartspaceCardLoggingInfo.mSubcardInfo;
        if (bcSmartspaceSubcardLoggingInfo == null || (list = bcSmartspaceSubcardLoggingInfo.mSubcards) == null) {
            return 0;
        }
        int i2 = 0;
        while (true) {
            ArrayList arrayList = (ArrayList) list;
            if (i2 >= arrayList.size()) {
                return 0;
            }
            BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = (BcSmartspaceCardMetadataLoggingInfo) arrayList.get(i2);
            if (bcSmartspaceCardMetadataLoggingInfo != null && bcSmartspaceCardMetadataLoggingInfo.mCardTypeId == i) {
                return i2 + 1;
            }
            i2++;
        }
    }

    @Override // android.view.View
    public final AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityNodeInfo createAccessibilityNodeInfo = super.createAccessibilityNodeInfo();
        createAccessibilityNodeInfo.getExtras().putCharSequence("AccessibilityNodeInfo.roleDescription", " ");
        return createAccessibilityNodeInfo;
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.mTextGroup = (ViewGroup) findViewById(R.id.text_group);
        this.mSecondaryCardGroup = (ViewGroup) findViewById(R.id.secondary_card_group);
        this.mTitleTextView = (TextView) findViewById(R.id.title_text);
        this.mSubtitleTextView = (TextView) findViewById(R.id.subtitle_text);
        this.mBaseActionIconSubtitleView = (DoubleShadowTextView) findViewById(R.id.base_action_icon_subtitle);
    }

    public final void setDozeAmount(float f) {
        this.mDozeAmount = f;
        SmartspaceTarget smartspaceTarget = this.mTarget;
        if (smartspaceTarget != null && smartspaceTarget.getBaseAction() != null && this.mTarget.getBaseAction().getExtras() != null) {
            Bundle extras = this.mTarget.getBaseAction().getExtras();
            if (this.mTitleTextView != null && extras.getBoolean("hide_title_on_aod")) {
                this.mTitleTextView.setAlpha(1.0f - f);
            }
            if (this.mSubtitleTextView != null && extras.getBoolean("hide_subtitle_on_aod")) {
                this.mSubtitleTextView.setAlpha(1.0f - f);
            }
        }
        if (this.mTextGroup == null) {
            return;
        }
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mSecondaryCardGroup, (this.mDozeAmount > 1.0f ? 1 : (this.mDozeAmount == 1.0f ? 0 : -1)) == 0 || !this.mValidSecondaryCard ? 8 : 0);
        SmartspaceTarget smartspaceTarget2 = this.mTarget;
        if (smartspaceTarget2 == null || smartspaceTarget2.getFeatureType() != 30) {
            ViewGroup viewGroup = this.mSecondaryCardGroup;
            if (viewGroup == null || viewGroup.getVisibility() == 8) {
                this.mTextGroup.setTranslationX(0.0f);
            } else {
                this.mTextGroup.setTranslationX(((PathInterpolator) Interpolators.EMPHASIZED).getInterpolation(this.mDozeAmount) * this.mSecondaryCardGroup.getWidth() * (isRtl$1() ? 1 : -1));
                this.mSecondaryCardGroup.setAlpha(Math.max(0.0f, Math.min(1.0f, ((1.0f - this.mDozeAmount) * 9.0f) - 6.0f)));
            }
        }
    }

    public final void setPrimaryOnClickListener(SmartspaceTarget smartspaceTarget, SmartspaceAction smartspaceAction, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        if (smartspaceAction != null) {
            BcSmartSpaceUtil.setOnClickListener(this, smartspaceTarget, smartspaceAction, this.mEventNotifier, "BcSmartspaceCard", bcSmartspaceCardLoggingInfo, 0);
            TextView textView = this.mTitleTextView;
            if (textView != null) {
                BcSmartSpaceUtil.setOnClickListener(textView, smartspaceTarget, smartspaceAction, this.mEventNotifier, "BcSmartspaceCard", bcSmartspaceCardLoggingInfo, 0);
            }
            TextView textView2 = this.mSubtitleTextView;
            if (textView2 != null) {
                BcSmartSpaceUtil.setOnClickListener(textView2, smartspaceTarget, smartspaceAction, this.mEventNotifier, "BcSmartspaceCard", bcSmartspaceCardLoggingInfo, 0);
            }
        }
    }

    public final void setPrimaryTextColor(int i) {
        TextView textView = this.mTitleTextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
        TextView textView2 = this.mSubtitleTextView;
        if (textView2 != null) {
            textView2.setTextColor(i);
        }
        DoubleShadowTextView doubleShadowTextView = this.mBaseActionIconSubtitleView;
        if (doubleShadowTextView != null) {
            doubleShadowTextView.setTextColor(i);
        }
        BcSmartspaceCardSecondary bcSmartspaceCardSecondary = this.mSecondaryCard;
        if (bcSmartspaceCardSecondary != null) {
            bcSmartspaceCardSecondary.setTextColor(i);
        }
        this.mIconTintColor = i;
        updateIconTint();
    }

    public final void setSubtitle(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        TextView textView = this.mSubtitleTextView;
        if (textView == null) {
            Log.w("BcSmartspaceCard", "No subtitle view to update");
            return;
        }
        textView.setText(charSequence);
        this.mSubtitleTextView.setCompoundDrawablesRelative((TextUtils.isEmpty(charSequence) || !z) ? null : this.mIconDrawable, null, null, null);
        TextView textView2 = this.mSubtitleTextView;
        SmartspaceTarget smartspaceTarget = this.mTarget;
        textView2.setMaxLines((smartspaceTarget == null || smartspaceTarget.getFeatureType() != 5 || this.mUsePageIndicatorUi) ? 1 : 2);
        ContentDescriptionUtil.setFormattedContentDescription("BcSmartspaceCard", this.mSubtitleTextView, charSequence, charSequence2);
        BcSmartspaceTemplateDataUtils.offsetTextViewForIcon(this.mSubtitleTextView, z ? this.mIconDrawable : null, isRtl$1());
    }

    public final void setTitle(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        boolean z2;
        TextView textView = this.mTitleTextView;
        if (textView == null) {
            Log.w("BcSmartspaceCard", "No title view to update");
            return;
        }
        textView.setText(charSequence);
        SmartspaceTarget smartspaceTarget = this.mTarget;
        SmartspaceAction headerAction = smartspaceTarget == null ? null : smartspaceTarget.getHeaderAction();
        Bundle extras = headerAction == null ? null : headerAction.getExtras();
        if (extras == null || !extras.containsKey("titleEllipsize")) {
            SmartspaceTarget smartspaceTarget2 = this.mTarget;
            if (smartspaceTarget2 != null && smartspaceTarget2.getFeatureType() == 2 && Locale.ENGLISH.getLanguage().equals(((ViewGroup) this).mContext.getResources().getConfiguration().locale.getLanguage())) {
                this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            } else {
                this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            }
        } else {
            String string = extras.getString("titleEllipsize");
            try {
                this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.valueOf(string));
            } catch (IllegalArgumentException unused) {
                Log.w("BcSmartspaceCard", "Invalid TruncateAt value: " + string);
            }
        }
        boolean z3 = false;
        if (extras != null) {
            int i = extras.getInt("titleMaxLines");
            if (i != 0) {
                this.mTitleTextView.setMaxLines(i);
            }
            z2 = extras.getBoolean("disableTitleIcon");
        } else {
            z2 = false;
        }
        if (z && !z2) {
            z3 = true;
        }
        if (z3) {
            ContentDescriptionUtil.setFormattedContentDescription("BcSmartspaceCard", this.mTitleTextView, charSequence, charSequence2);
        }
        this.mTitleTextView.setCompoundDrawablesRelative(z3 ? this.mIconDrawable : null, null, null, null);
        BcSmartspaceTemplateDataUtils.offsetTextViewForIcon(this.mTitleTextView, z3 ? this.mIconDrawable : null, isRtl$1());
    }

    public final void updateIconTint() {
        DoubleShadowIconDrawable doubleShadowIconDrawable;
        SmartspaceTarget smartspaceTarget = this.mTarget;
        if (smartspaceTarget == null || (doubleShadowIconDrawable = this.mIconDrawable) == null) {
            return;
        }
        if (smartspaceTarget.getFeatureType() != 1) {
            doubleShadowIconDrawable.setTint(this.mIconTintColor);
        } else {
            doubleShadowIconDrawable.setTintList(null);
        }
        DoubleShadowIconDrawable doubleShadowIconDrawable2 = this.mBaseActionIconDrawable;
        if (doubleShadowIconDrawable2 == null) {
            return;
        }
        SmartspaceAction baseAction = this.mTarget.getBaseAction();
        int i = -1;
        if (baseAction != null && baseAction.getExtras() != null && !baseAction.getExtras().isEmpty()) {
            i = baseAction.getExtras().getInt("subcardType", -1);
        }
        if (i != 1) {
            doubleShadowIconDrawable2.setTint(this.mIconTintColor);
        } else {
            doubleShadowIconDrawable2.setTintList(null);
        }
    }

    public BcSmartspaceCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSecondaryCard = null;
        this.mPrevSmartspaceTargetId = "";
        this.mIconTintColor = GraphicsUtils.getAttrColor(android.R.attr.textColorPrimary, getContext());
        this.mTextGroup = null;
        this.mSecondaryCardGroup = null;
        this.mTitleTextView = null;
        this.mSubtitleTextView = null;
        this.mBaseActionIconSubtitleView = null;
        context.getTheme().applyStyle(R.style.Smartspace, false);
        this.mIconDrawable = new DoubleShadowIconDrawable(context);
        this.mBaseActionIconDrawable = new DoubleShadowIconDrawable(context);
        setDefaultFocusHighlightEnabled(false);
    }
}
