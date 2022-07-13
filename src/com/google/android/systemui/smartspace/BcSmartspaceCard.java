package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardMetadataLoggingInfo;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
/* loaded from: classes2.dex */
public class BcSmartspaceCard extends LinearLayout {
    private static final SmartspaceAction SHOW_ALARMS_ACTION = new SmartspaceAction.Builder("nextAlarmId", "Next alarm").setIntent(new Intent("android.intent.action.SHOW_ALARMS")).build();
    private DoubleShadowTextView mBaseActionIconSubtitleView;
    private IcuDateTextView mDateView;
    private ImageView mDndImageView;
    private float mDozeAmount;
    private BcSmartspaceDataPlugin.SmartspaceEventNotifier mEventNotifier;
    private ViewGroup mExtrasGroup;
    private DoubleShadowIconDrawable mIconDrawable;
    private int mIconTintColor;
    private BcSmartspaceCardLoggingInfo mLoggingInfo;
    private ImageView mNextAlarmImageView;
    private TextView mNextAlarmTextView;
    private BcSmartspaceCardSecondary mSecondaryCard;
    private TextView mSubtitleTextView;
    private SmartspaceTarget mTarget;
    private TextView mTitleTextView;
    private int mTopPadding;
    private boolean mUsePageIndicatorUi;

    public BcSmartspaceCard(Context context) {
        this(context, null);
    }

    public BcSmartspaceCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSecondaryCard = null;
        this.mIconTintColor = GraphicsUtils.getAttrColor(getContext(), 16842806);
        this.mDateView = null;
        this.mTitleTextView = null;
        this.mSubtitleTextView = null;
        this.mBaseActionIconSubtitleView = null;
        this.mExtrasGroup = null;
        this.mDndImageView = null;
        this.mNextAlarmImageView = null;
        this.mNextAlarmTextView = null;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDateView = (IcuDateTextView) findViewById(R.id.date);
        this.mTitleTextView = (TextView) findViewById(R.id.title_text);
        this.mSubtitleTextView = (TextView) findViewById(R.id.subtitle_text);
        this.mBaseActionIconSubtitleView = (DoubleShadowTextView) findViewById(R.id.base_action_icon_subtitle);
        this.mExtrasGroup = (ViewGroup) findViewById(R.id.smartspace_extras_group);
        this.mTopPadding = getPaddingTop();
        ViewGroup viewGroup = this.mExtrasGroup;
        if (viewGroup != null) {
            this.mDndImageView = (ImageView) viewGroup.findViewById(R.id.dnd_icon);
            this.mNextAlarmImageView = (ImageView) this.mExtrasGroup.findViewById(R.id.alarm_icon);
            this.mNextAlarmTextView = (TextView) this.mExtrasGroup.findViewById(R.id.alarm_text);
        }
    }

    public void setEventNotifier(BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier) {
        this.mEventNotifier = smartspaceEventNotifier;
    }

    public void setSmartspaceTarget(SmartspaceTarget smartspaceTarget, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, boolean z) {
        String uuid;
        int i;
        this.mTarget = smartspaceTarget;
        SmartspaceAction headerAction = smartspaceTarget.getHeaderAction();
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        this.mLoggingInfo = bcSmartspaceCardLoggingInfo;
        this.mUsePageIndicatorUi = z;
        if (headerAction != null) {
            BcSmartspaceCardSecondary bcSmartspaceCardSecondary = this.mSecondaryCard;
            if (bcSmartspaceCardSecondary != null) {
                this.mSecondaryCard.setVisibility(bcSmartspaceCardSecondary.setSmartspaceActions(smartspaceTarget, this.mEventNotifier, bcSmartspaceCardLoggingInfo) ? 0 : 8);
            }
            Drawable iconDrawable = BcSmartSpaceUtil.getIconDrawable(headerAction.getIcon(), getContext());
            this.mIconDrawable = iconDrawable == null ? null : new DoubleShadowIconDrawable(iconDrawable, getContext());
            CharSequence title = headerAction.getTitle();
            CharSequence subtitle = headerAction.getSubtitle();
            boolean z2 = smartspaceTarget.getFeatureType() == 1 || !TextUtils.isEmpty(title);
            boolean z3 = !TextUtils.isEmpty(subtitle);
            updateZenVisibility();
            if (!z2) {
                title = subtitle;
            }
            setTitle(title, headerAction.getContentDescription(), z2 != z3);
            if (!z2 || !z3) {
                subtitle = null;
            }
            setSubtitle(subtitle, headerAction.getContentDescription());
            updateIconTint();
        }
        if (baseAction != null && this.mBaseActionIconSubtitleView != null) {
            Drawable iconDrawable2 = baseAction.getIcon() == null ? null : BcSmartSpaceUtil.getIconDrawable(baseAction.getIcon(), getContext());
            if (iconDrawable2 == null) {
                this.mBaseActionIconSubtitleView.setVisibility(4);
                this.mBaseActionIconSubtitleView.setOnClickListener(null);
                this.mBaseActionIconSubtitleView.setContentDescription(null);
            } else {
                iconDrawable2.setTintList(null);
                this.mBaseActionIconSubtitleView.setText(baseAction.getSubtitle());
                this.mBaseActionIconSubtitleView.setCompoundDrawablesRelative(iconDrawable2, null, null, null);
                this.mBaseActionIconSubtitleView.setVisibility(0);
                int subcardType = getSubcardType(baseAction);
                if (subcardType != -1) {
                    i = getClickedIndex(bcSmartspaceCardLoggingInfo, subcardType);
                } else {
                    Log.d("BcSmartspaceCard", String.format("Subcard expected but missing type. loggingInfo=%s, baseAction=%s", bcSmartspaceCardLoggingInfo.toString(), baseAction.toString()));
                    i = 0;
                }
                BcSmartSpaceUtil.setOnClickListener(this.mBaseActionIconSubtitleView, smartspaceTarget, baseAction, "BcSmartspaceCard", this.mEventNotifier, bcSmartspaceCardLoggingInfo, i);
                setFormattedContentDescription(this.mBaseActionIconSubtitleView, baseAction.getSubtitle(), baseAction.getContentDescription());
            }
        }
        if (this.mDateView != null) {
            if (headerAction != null) {
                uuid = headerAction.getId();
            } else if (baseAction != null) {
                uuid = baseAction.getId();
            } else {
                uuid = UUID.randomUUID().toString();
            }
            BcSmartSpaceUtil.setOnClickListener(this.mDateView, smartspaceTarget, new SmartspaceAction.Builder(uuid, "unusedTitle").setIntent(BcSmartSpaceUtil.getOpenCalendarIntent()).build(), "BcSmartspaceCard", this.mEventNotifier, bcSmartspaceCardLoggingInfo);
        }
        if (hasIntent(headerAction)) {
            BcSmartSpaceUtil.setOnClickListener(this, smartspaceTarget, headerAction, "BcSmartspaceCard", this.mEventNotifier, bcSmartspaceCardLoggingInfo, (smartspaceTarget.getFeatureType() == 1 && bcSmartspaceCardLoggingInfo.getFeatureType() == 39) ? getClickedIndex(bcSmartspaceCardLoggingInfo, 1) : 0);
        } else if (hasIntent(baseAction)) {
            BcSmartSpaceUtil.setOnClickListener(this, smartspaceTarget, baseAction, "BcSmartspaceCard", this.mEventNotifier, bcSmartspaceCardLoggingInfo);
        } else {
            BcSmartSpaceUtil.setOnClickListener(this, smartspaceTarget, headerAction, "BcSmartspaceCard", this.mEventNotifier, bcSmartspaceCardLoggingInfo);
        }
    }

    private int getClickedIndex(BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo, int i) {
        if (bcSmartspaceCardLoggingInfo == null || bcSmartspaceCardLoggingInfo.getSubcardInfo() == null || bcSmartspaceCardLoggingInfo.getSubcardInfo().getSubcards() == null) {
            return 0;
        }
        List<BcSmartspaceCardMetadataLoggingInfo> subcards = bcSmartspaceCardLoggingInfo.getSubcardInfo().getSubcards();
        for (int i2 = 0; i2 < subcards.size(); i2++) {
            BcSmartspaceCardMetadataLoggingInfo bcSmartspaceCardMetadataLoggingInfo = subcards.get(i2);
            if (bcSmartspaceCardMetadataLoggingInfo != null && bcSmartspaceCardMetadataLoggingInfo.getCardTypeId() == i) {
                return i2 + 1;
            }
        }
        return 0;
    }

    private int getSubcardType(SmartspaceAction smartspaceAction) {
        if (smartspaceAction == null || smartspaceAction.getExtras() == null || smartspaceAction.getExtras().isEmpty()) {
            return -1;
        }
        return smartspaceAction.getExtras().getInt("subcardType", -1);
    }

    public void setSecondaryCard(BcSmartspaceCardSecondary bcSmartspaceCardSecondary) {
        this.mSecondaryCard = bcSmartspaceCardSecondary;
        if (getChildAt(1) != null) {
            removeViewAt(1);
        }
        if (bcSmartspaceCardSecondary != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_height));
            layoutParams.weight = 3.0f;
            layoutParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_secondary_card_start_margin));
            layoutParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_secondary_card_end_margin));
            addView(bcSmartspaceCardSecondary, 1, layoutParams);
        }
    }

    public void setDozeAmount(float f) {
        this.mDozeAmount = f;
        BcSmartspaceCardSecondary bcSmartspaceCardSecondary = this.mSecondaryCard;
        if (bcSmartspaceCardSecondary != null) {
            bcSmartspaceCardSecondary.setAlpha(1.0f - f);
        }
        if (getTarget() != null && getTarget().getBaseAction() != null && getTarget().getBaseAction().getExtras() != null) {
            Bundle extras = getTarget().getBaseAction().getExtras();
            if (this.mTitleTextView != null && extras.getBoolean("hide_title_on_aod")) {
                this.mTitleTextView.setAlpha(1.0f - f);
            }
            if (this.mSubtitleTextView != null && extras.getBoolean("hide_subtitle_on_aod")) {
                this.mSubtitleTextView.setAlpha(1.0f - f);
            }
        }
        ImageView imageView = this.mDndImageView;
        if (imageView != null) {
            imageView.setAlpha(this.mDozeAmount);
        }
    }

    public void setPrimaryTextColor(int i) {
        TextView textView = this.mTitleTextView;
        if (textView != null) {
            textView.setTextColor(i);
        }
        IcuDateTextView icuDateTextView = this.mDateView;
        if (icuDateTextView != null) {
            icuDateTextView.setTextColor(i);
        }
        TextView textView2 = this.mSubtitleTextView;
        if (textView2 != null) {
            textView2.setTextColor(i);
        }
        DoubleShadowTextView doubleShadowTextView = this.mBaseActionIconSubtitleView;
        if (doubleShadowTextView != null) {
            doubleShadowTextView.setTextColor(i);
        }
        this.mIconTintColor = i;
        updateZenColors();
        updateIconTint();
    }

    @Override // android.view.View
    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityNodeInfo createAccessibilityNodeInfo = super.createAccessibilityNodeInfo();
        AccessibilityNodeInfoCompat.wrap(createAccessibilityNodeInfo).setRoleDescription(" ");
        return createAccessibilityNodeInfo;
    }

    void setTitle(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        boolean z2;
        TextView textView = this.mTitleTextView;
        if (textView == null) {
            Log.w("BcSmartspaceCard", "No title view to update");
            return;
        }
        textView.setText(charSequence);
        SmartspaceAction headerAction = this.mTarget.getHeaderAction();
        Bundle extras = headerAction == null ? null : headerAction.getExtras();
        if (extras != null && extras.containsKey("titleEllipsize")) {
            String string = extras.getString("titleEllipsize");
            try {
                this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.valueOf(string));
            } catch (IllegalArgumentException unused) {
                Log.w("BcSmartspaceCard", "Invalid TruncateAt value: " + string);
            }
        } else if (mTarget.getFeatureType() == 2 && Locale.ENGLISH.getLanguage().equals(mContext.getResources().getConfiguration().locale.getLanguage())) {
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        } else {
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
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
            setFormattedContentDescription(this.mTitleTextView, charSequence, charSequence2);
        }
        this.mTitleTextView.setCompoundDrawablesRelative(z3 ? this.mIconDrawable : null, null, null, null);
    }

    void setSubtitle(CharSequence charSequence, CharSequence charSequence2) {
        TextView textView = this.mSubtitleTextView;
        if (textView == null) {
            Log.w("BcSmartspaceCard", "No subtitle view to update");
            return;
        }
        textView.setText(charSequence);
        this.mSubtitleTextView.setCompoundDrawablesRelative(TextUtils.isEmpty(charSequence) ? null : this.mIconDrawable, null, null, null);
        this.mSubtitleTextView.setMaxLines((this.mTarget.getFeatureType() != 5 || this.mUsePageIndicatorUi) ? 1 : 2);
        setFormattedContentDescription(this.mSubtitleTextView, charSequence, charSequence2);
    }

    void updateIconTint() {
        SmartspaceTarget smartspaceTarget = this.mTarget;
        if (smartspaceTarget == null || this.mIconDrawable == null) {
            return;
        }
        boolean z = true;
        if (smartspaceTarget.getFeatureType() == 1) {
            z = false;
        }
        if (z) {
            this.mIconDrawable.setTint(this.mIconTintColor);
        } else {
            this.mIconDrawable.setTintList(null);
        }
    }

    void updateZenColors() {
        TextView textView = this.mNextAlarmTextView;
        if (textView != null) {
            textView.setTextColor(this.mIconTintColor);
        }
        updateTint(this.mNextAlarmImageView);
        updateTint(this.mDndImageView);
    }

    private void updateTint(ImageView imageView) {
        if (imageView == null || imageView.getDrawable() == null) {
            return;
        }
        imageView.getDrawable().setTint(this.mIconTintColor);
    }

    public void setDnd(Drawable drawable, String str) {
        ImageView imageView = this.mDndImageView;
        if (imageView == null) {
            return;
        }
        if (drawable == null) {
            imageView.setVisibility(8);
        } else {
            imageView.setImageDrawable(new DoubleShadowIconDrawable(drawable.mutate(), getContext()));
            this.mDndImageView.setContentDescription(str);
            this.mDndImageView.setVisibility(0);
        }
        updateZenVisibility();
    }

    public void setNextAlarm(Drawable drawable, String str, SmartspaceTarget smartspaceTarget) {
        ImageView imageView = this.mNextAlarmImageView;
        if (imageView == null || this.mNextAlarmTextView == null) {
            return;
        }
        if (drawable == null) {
            imageView.setVisibility(8);
            this.mNextAlarmTextView.setVisibility(8);
        } else {
            String maybeAppendHolidayInfoToNextAlarm = maybeAppendHolidayInfoToNextAlarm(str, smartspaceTarget);
            this.mNextAlarmImageView.setImageDrawable(new DoubleShadowIconDrawable(drawable.mutate(), getContext()));
            this.mNextAlarmImageView.setVisibility(0);
            this.mNextAlarmTextView.setContentDescription(getContext().getString(R.string.accessibility_next_alarm, maybeAppendHolidayInfoToNextAlarm));
            this.mNextAlarmTextView.setText(maybeAppendHolidayInfoToNextAlarm);
            this.mNextAlarmTextView.setVisibility(0);
            setNextAlarmClickListener(this.mNextAlarmImageView, smartspaceTarget);
            setNextAlarmClickListener(this.mNextAlarmTextView, smartspaceTarget);
        }
        updateZenVisibility();
    }

    private void setNextAlarmClickListener(View view, SmartspaceTarget smartspaceTarget) {
        BcSmartspaceCardLoggingInfo build;
        if (smartspaceTarget == null) {
            build = new BcSmartspaceCardLoggingInfo.Builder().setInstanceId(InstanceId.create("upcoming_alarm_card_94510_12684")).setFeatureType(23).setDisplaySurface(BcSmartSpaceUtil.getLoggingDisplaySurface(getContext().getPackageName(), this.mDozeAmount)).build();
        } else {
            build = new BcSmartspaceCardLoggingInfo.Builder().setInstanceId(InstanceId.create(smartspaceTarget)).setFeatureType(smartspaceTarget.getFeatureType()).setDisplaySurface(BcSmartSpaceUtil.getLoggingDisplaySurface(getContext().getPackageName(), this.mDozeAmount)).build();
        }
        BcSmartSpaceUtil.setOnClickListener(view, smartspaceTarget, SHOW_ALARMS_ACTION, "BcSmartspaceCard", this.mEventNotifier, build);
    }

    private String maybeAppendHolidayInfoToNextAlarm(String str, SmartspaceTarget smartspaceTarget) {
        CharSequence holidayAlarmsText = getHolidayAlarmsText(smartspaceTarget);
        if (!TextUtils.isEmpty(holidayAlarmsText)) {
            return str + " · " + ((Object) holidayAlarmsText);
        }
        return str;
    }

    public static CharSequence getHolidayAlarmsText(SmartspaceTarget smartspaceTarget) {
        SmartspaceAction headerAction;
        if (smartspaceTarget == null || (headerAction = smartspaceTarget.getHeaderAction()) == null) {
            return null;
        }
        return headerAction.getTitle();
    }

    private void updateZenVisibility() {
        if (this.mExtrasGroup == null) {
            return;
        }
        ImageView imageView = this.mDndImageView;
        boolean z = true;
        int i = 0;
        boolean z2 = imageView != null && imageView.getVisibility() == 0;
        ImageView imageView2 = this.mNextAlarmImageView;
        boolean z3 = imageView2 != null && imageView2.getVisibility() == 0;
        if ((!z2 && !z3) || (this.mUsePageIndicatorUi && this.mTarget.getFeatureType() != 1)) {
            z = false;
        }
        int i2 = this.mTopPadding;
        if (!z) {
            this.mExtrasGroup.setVisibility(4);
            i = i2;
        } else {
            this.mExtrasGroup.setVisibility(0);
            updateZenColors();
        }
        setPadding(getPaddingLeft(), i, getPaddingRight(), getPaddingBottom());
    }

    public SmartspaceTarget getTarget() {
        return this.mTarget;
    }

    private void setFormattedContentDescription(TextView textView, CharSequence charSequence, CharSequence charSequence2) {
        if (TextUtils.isEmpty(charSequence)) {
            charSequence = charSequence2;
        } else if (!TextUtils.isEmpty(charSequence2)) {
            charSequence = mContext.getString(R.string.generic_smartspace_concatenated_desc, charSequence2, charSequence);
        }
        textView.setContentDescription(charSequence);
    }

    private boolean hasIntent(SmartspaceAction smartspaceAction) {
        return (smartspaceAction == null || (smartspaceAction.getIntent() == null && smartspaceAction.getPendingIntent() == null)) ? false : true;
    }
}
