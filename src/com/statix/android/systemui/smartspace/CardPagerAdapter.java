package com.google.android.systemui.smartspace;

import android.R;
import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceUtils;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import androidx.compose.foundation.text.input.internal.RecordingInputConnection$$ExternalSyntheticOutline0;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.internal.graphics.ColorUtils;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.systemui.plugins.BcSmartspaceConfigPlugin;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.CardPagerAdapter;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil;
import com.google.android.systemui.smartspace.uitemplate.BaseTemplateCard;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final class CardPagerAdapter extends PagerAdapter {
    public static final Companion Companion = new Companion();
    public Handler bgHandler;
    public BcSmartspaceConfigPlugin configProvider;
    public int currentTextColor;
    public BcSmartspaceDataPlugin dataProvider;
    public float dozeAmount;
    public boolean hasAodLockscreenTransition;
    public boolean hasDifferentTargets;
    public boolean keyguardBypassEnabled;
    public float previousDozeAmount;
    public int primaryTextColor;
    public final BcSmartspaceView root;
    public BcSmartspaceDataPlugin.TimeChangedDelegate timeChangedDelegate;
    public TransitionType transitioningTo;
    public String uiSurface;
    public final SparseArray viewHolders = new SparseArray();
    public final LazyServerFlagLoader enableCardRecycling = new LazyServerFlagLoader("enable_card_recycling");
    public final LazyServerFlagLoader enableReducedCardRecycling = new LazyServerFlagLoader("enable_reduced_card_recycling");
    public final SparseArray recycledCards = new SparseArray();
    public final SparseArray recycledLegacyCards = new SparseArray();
    public final SparseArray recycledRemoteViewsCards = new SparseArray();
    public List smartspaceTargets = new ArrayList();
    public final List _aodTargets = new ArrayList();
    public final List _lockscreenTargets = new ArrayList();
    public final List mediaTargets = new ArrayList();
    public final int dozeColor = -1;

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class TransitionType {
        public static final /* synthetic */ TransitionType[] $VALUES;
        public static final TransitionType NOT_IN_TRANSITION;
        public static final TransitionType TO_AOD;
        public static final TransitionType TO_LOCKSCREEN;

        static {
            TransitionType transitionType = new TransitionType("NOT_IN_TRANSITION", 0);
            NOT_IN_TRANSITION = transitionType;
            TransitionType transitionType2 = new TransitionType("TO_LOCKSCREEN", 1);
            TO_LOCKSCREEN = transitionType2;
            TransitionType transitionType3 = new TransitionType("TO_AOD", 2);
            TO_AOD = transitionType3;
            TransitionType[] transitionTypeArr = {transitionType, transitionType2, transitionType3};
            $VALUES = transitionTypeArr;
            EnumEntriesKt.enumEntries(transitionTypeArr);
        }

        public static TransitionType valueOf(String str) {
            return (TransitionType) Enum.valueOf(TransitionType.class, str);
        }

        public static TransitionType[] values() {
            return (TransitionType[]) $VALUES.clone();
        }
    }

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class ViewHolder {
        public final BaseTemplateCard card;
        public final BcSmartspaceCard legacyCard;
        public final int position;
        public final BcSmartspaceRemoteViewsCard remoteViewsCard;
        public SmartspaceTarget target;

        public ViewHolder(int i, BcSmartspaceCard bcSmartspaceCard, SmartspaceTarget smartspaceTarget, BaseTemplateCard baseTemplateCard, BcSmartspaceRemoteViewsCard bcSmartspaceRemoteViewsCard) {
            this.position = i;
            this.legacyCard = bcSmartspaceCard;
            this.target = smartspaceTarget;
            this.card = baseTemplateCard;
            this.remoteViewsCard = bcSmartspaceRemoteViewsCard;
        }
    }

    public CardPagerAdapter(BcSmartspaceView bcSmartspaceView, BcSmartspaceConfigPlugin bcSmartspaceConfigPlugin) {
        this.root = bcSmartspaceView;
        int attrColor = GraphicsUtils.getAttrColor(R.attr.textColorPrimary, bcSmartspaceView.getContext());
        this.primaryTextColor = attrColor;
        this.currentTextColor = attrColor;
        this.configProvider = bcSmartspaceConfigPlugin;
        this.transitioningTo = TransitionType.NOT_IN_TRANSITION;
    }

    public static final int getBaseLegacyCardRes(int i) {
        return Companion.getBaseLegacyCardRes(i);
    }

    public static final int getLegacySecondaryCardRes(int i) {
        return Companion.getLegacySecondaryCardRes(i);
    }

    public static final boolean useRecycledViewForNewTarget(SmartspaceTarget smartspaceTarget, SmartspaceTarget smartspaceTarget2) {
        return Companion.useRecycledViewForNewTarget(smartspaceTarget, smartspaceTarget2);
    }

    public final void addDefaultDateCardIfEmpty(List list) {
        if (list.isEmpty()) {
            BcSmartspaceView bcSmartspaceView = this.root;
            list.add(new SmartspaceTarget.Builder("date_card_794317_92634", new ComponentName(bcSmartspaceView.getContext(), (Class<?>) CardPagerAdapter.class), bcSmartspaceView.getContext().getUser()).setFeatureType(1).setTemplateData(new BaseTemplateData.Builder(1).build()).build());
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public final void destroyItem(ViewPager viewPager, int i, Object obj) {
        ViewHolder viewHolder = (ViewHolder) obj;
        BcSmartspaceCard bcSmartspaceCard = viewHolder.legacyCard;
        LazyServerFlagLoader lazyServerFlagLoader = this.enableCardRecycling;
        if (bcSmartspaceCard != null) {
            SmartspaceTarget smartspaceTarget = bcSmartspaceCard.mTarget;
            if (smartspaceTarget != null && lazyServerFlagLoader.get()) {
                this.recycledLegacyCards.put(Companion.getFeatureType(smartspaceTarget), bcSmartspaceCard);
            }
            viewPager.removeView(bcSmartspaceCard);
        }
        BaseTemplateCard baseTemplateCard = viewHolder.card;
        if (baseTemplateCard != null) {
            SmartspaceTarget smartspaceTarget2 = baseTemplateCard.mTarget;
            if (smartspaceTarget2 != null && lazyServerFlagLoader.get()) {
                this.recycledCards.put(smartspaceTarget2.getFeatureType(), baseTemplateCard);
            }
            viewPager.removeView(baseTemplateCard);
        }
        BcSmartspaceRemoteViewsCard bcSmartspaceRemoteViewsCard = viewHolder.remoteViewsCard;
        if (bcSmartspaceRemoteViewsCard != null) {
            if (lazyServerFlagLoader.get()) {
                Log.d("SsCardPagerAdapter", "[rmv] Caching RemoteViews card");
                this.recycledRemoteViewsCards.put(Companion.getFeatureType(viewHolder.target), bcSmartspaceRemoteViewsCard);
            }
            Log.d("SsCardPagerAdapter", "[rmv] Removing RemoteViews card");
            viewPager.removeView(bcSmartspaceRemoteViewsCard);
        }
        if (this.viewHolders.get(i) == viewHolder) {
            this.viewHolders.remove(i);
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public final int getCount() {
        return this.smartspaceTargets.size();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public final int getItemPosition(Object obj) {
        ViewHolder viewHolder = (ViewHolder) obj;
        SmartspaceTarget targetAtPosition = getTargetAtPosition(viewHolder.position);
        if (viewHolder.target == targetAtPosition) {
            return -1;
        }
        if (targetAtPosition == null || Companion.getFeatureType(targetAtPosition) != Companion.getFeatureType(viewHolder.target) || !Intrinsics.areEqual(targetAtPosition.getSmartspaceTargetId(), viewHolder.target.getSmartspaceTargetId())) {
            return -2;
        }
        viewHolder.target = targetAtPosition;
        onBindViewHolder(viewHolder);
        return -1;
    }

    public final List getLockscreenTargets() {
        return (this.mediaTargets.isEmpty() || !this.keyguardBypassEnabled) ? this._lockscreenTargets : this.mediaTargets;
    }

    public final SmartspaceTarget getTargetAtPosition(int i) {
        if (this.smartspaceTargets.isEmpty() || i < 0 || i >= this.smartspaceTargets.size()) {
            return null;
        }
        return (SmartspaceTarget) this.smartspaceTargets.get(i);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public final Object instantiateItem(ViewPager viewPager, int i) {
        ViewHolder viewHolder;
        int i2;
        SmartspaceTarget smartspaceTarget = (SmartspaceTarget) this.smartspaceTargets.get(i);
        Log.i("SsCardPagerAdapter", "[rmv] Rendering flag - enabled: true " + ("rmv: " + smartspaceTarget.getRemoteViews()));
        RemoteViews remoteViews = smartspaceTarget.getRemoteViews();
        Companion companion = Companion;
        LazyServerFlagLoader lazyServerFlagLoader = this.enableCardRecycling;
        BcSmartspaceCard bcSmartspaceCard = null;
        if (remoteViews != null) {
            Log.i("SsCardPagerAdapter", "[rmv] Use RemoteViews for the feature: " + smartspaceTarget.getFeatureType());
            BcSmartspaceRemoteViewsCard bcSmartspaceRemoteViewsCard = lazyServerFlagLoader.get() ? (BcSmartspaceRemoteViewsCard) this.recycledRemoteViewsCards.removeReturnOld(Companion.getFeatureType(smartspaceTarget)) : null;
            if (bcSmartspaceRemoteViewsCard == null) {
                bcSmartspaceRemoteViewsCard = new BcSmartspaceRemoteViewsCard(viewPager.getContext());
                bcSmartspaceRemoteViewsCard.setOnLongClickListener(null);
                bcSmartspaceRemoteViewsCard.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            }
            viewHolder = new ViewHolder(i, null, smartspaceTarget, null, bcSmartspaceRemoteViewsCard);
            viewPager.addView(bcSmartspaceRemoteViewsCard);
        } else {
            boolean containsValidTemplateType = BcSmartspaceCardLoggerUtil.containsValidTemplateType(smartspaceTarget.getTemplateData());
            LazyServerFlagLoader lazyServerFlagLoader2 = this.enableReducedCardRecycling;
            if (containsValidTemplateType) {
                Log.i("SsCardPagerAdapter", "Use UI template for the feature: " + smartspaceTarget.getFeatureType());
                BaseTemplateCard baseTemplateCard = lazyServerFlagLoader.get() ? (BaseTemplateCard) this.recycledCards.removeReturnOld(smartspaceTarget.getFeatureType()) : null;
                if (baseTemplateCard == null || (lazyServerFlagLoader2.get() && !companion.useRecycledViewForNewTarget(smartspaceTarget, baseTemplateCard.mTarget))) {
                    BaseTemplateData templateData = smartspaceTarget.getTemplateData();
                    BaseTemplateData.SubItemInfo primaryItem = templateData != null ? templateData.getPrimaryItem() : null;
                    int i3 = (primaryItem == null || (SmartspaceUtils.isEmpty(primaryItem.getText()) && primaryItem.getIcon() == null)) ? com.android.wm.shell.R.layout.smartspace_base_template_card_with_date : com.android.wm.shell.R.layout.smartspace_base_template_card;
                    LayoutInflater from = LayoutInflater.from(viewPager.getContext());
                    BaseTemplateCard baseTemplateCard2 = (BaseTemplateCard) from.inflate(i3, (ViewGroup) viewPager, false);
                    String str = this.uiSurface;
                    baseTemplateCard2.mUiSurface = str;
                    if (baseTemplateCard2.mDateView != null && TextUtils.equals(str, BcSmartspaceDataPlugin.UI_SURFACE_LOCK_SCREEN_AOD)) {
                        IcuDateTextView icuDateTextView = baseTemplateCard2.mDateView;
                        if (icuDateTextView.isAttachedToWindow()) {
                            throw new IllegalStateException("Must call before attaching view to window.");
                        }
                        icuDateTextView.mUpdatesOnAod = true;
                    }
                    Handler handler = this.bgHandler;
                    Handler handler2 = handler != null ? handler : null;
                    baseTemplateCard2.mBgHandler = handler2;
                    IcuDateTextView icuDateTextView2 = baseTemplateCard2.mDateView;
                    if (icuDateTextView2 != null) {
                        icuDateTextView2.mBgHandler = handler2;
                    }
                    BcSmartspaceDataPlugin.TimeChangedDelegate timeChangedDelegate = this.timeChangedDelegate;
                    if (icuDateTextView2 != null) {
                        if (icuDateTextView2.isAttachedToWindow()) {
                            throw new IllegalStateException("Must call before attaching view to window.");
                        }
                        icuDateTextView2.mTimeChangedDelegate = timeChangedDelegate;
                    }
                    if (templateData != null) {
                        switch (templateData.getTemplateType()) {
                            case 2:
                                i2 = com.android.wm.shell.R.layout.smartspace_sub_image_template_card;
                                break;
                            case 3:
                                i2 = com.android.wm.shell.R.layout.smartspace_sub_list_template_card;
                                break;
                            case 4:
                                i2 = com.android.wm.shell.R.layout.smartspace_carousel_template_card;
                                break;
                            case 5:
                                i2 = com.android.wm.shell.R.layout.smartspace_head_to_head_template_card;
                                break;
                            case 6:
                                i2 = com.android.wm.shell.R.layout.smartspace_combined_cards_template_card;
                                break;
                            case 7:
                                i2 = com.android.wm.shell.R.layout.smartspace_sub_card_template_card;
                                break;
                            default:
                                i2 = 0;
                                break;
                        }
                        if (i2 != 0) {
                            BcSmartspaceCardSecondary bcSmartspaceCardSecondary = (BcSmartspaceCardSecondary) from.inflate(i2, (ViewGroup) baseTemplateCard2, false);
                            Log.i("SsCardPagerAdapter", "Secondary card is found");
                            ViewGroup viewGroup = baseTemplateCard2.mSecondaryCardPane;
                            if (viewGroup != null) {
                                baseTemplateCard2.mSecondaryCard = bcSmartspaceCardSecondary;
                                BcSmartspaceTemplateDataUtils.updateVisibility(viewGroup, 8);
                                baseTemplateCard2.mSecondaryCardPane.removeAllViews();
                                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(baseTemplateCard2.getResources().getDimensionPixelSize(com.android.wm.shell.R.dimen.enhanced_smartspace_card_height));
                                layoutParams.setMarginStart(baseTemplateCard2.getResources().getDimensionPixelSize(com.android.wm.shell.R.dimen.enhanced_smartspace_secondary_card_start_margin));
                                layoutParams.startToStart = 0;
                                layoutParams.topToTop = 0;
                                layoutParams.bottomToBottom = 0;
                                baseTemplateCard2.mSecondaryCardPane.addView(bcSmartspaceCardSecondary, layoutParams);
                            }
                        }
                    }
                    baseTemplateCard = baseTemplateCard2;
                }
                viewHolder = new ViewHolder(i, null, smartspaceTarget, baseTemplateCard, null);
                viewPager.addView(baseTemplateCard);
            } else {
                BcSmartspaceCard bcSmartspaceCard2 = lazyServerFlagLoader.get() ? (BcSmartspaceCard) this.recycledLegacyCards.removeReturnOld(Companion.getFeatureType(smartspaceTarget)) : null;
                if (bcSmartspaceCard2 == null || (lazyServerFlagLoader2.get() && !companion.useRecycledViewForNewTarget(smartspaceTarget, bcSmartspaceCard2.mTarget))) {
                    int featureType = Companion.getFeatureType(smartspaceTarget);
                    LayoutInflater from2 = LayoutInflater.from(viewPager.getContext());
                    int baseLegacyCardRes = companion.getBaseLegacyCardRes(featureType);
                    if (baseLegacyCardRes == 0) {
                        RecordingInputConnection$$ExternalSyntheticOutline0.m("No legacy card can be created for feature type: ", "SsCardPagerAdapter", featureType);
                    } else {
                        bcSmartspaceCard = (BcSmartspaceCard) from2.inflate(baseLegacyCardRes, (ViewGroup) viewPager, false);
                        int legacySecondaryCardRes = companion.getLegacySecondaryCardRes(featureType);
                        if (legacySecondaryCardRes != 0) {
                            BcSmartspaceCardSecondary bcSmartspaceCardSecondary2 = (BcSmartspaceCardSecondary) from2.inflate(legacySecondaryCardRes, (ViewGroup) bcSmartspaceCard, false);
                            ViewGroup viewGroup2 = bcSmartspaceCard.mSecondaryCardGroup;
                            if (viewGroup2 != null) {
                                bcSmartspaceCard.mSecondaryCard = bcSmartspaceCardSecondary2;
                                BcSmartspaceTemplateDataUtils.updateVisibility(viewGroup2, 8);
                                bcSmartspaceCard.mSecondaryCardGroup.removeAllViews();
                                ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(bcSmartspaceCard.getResources().getDimensionPixelSize(com.android.wm.shell.R.dimen.enhanced_smartspace_card_height));
                                layoutParams2.setMarginStart(bcSmartspaceCard.getResources().getDimensionPixelSize(com.android.wm.shell.R.dimen.enhanced_smartspace_secondary_card_start_margin));
                                layoutParams2.startToStart = 0;
                                layoutParams2.topToTop = 0;
                                layoutParams2.bottomToBottom = 0;
                                bcSmartspaceCard.mSecondaryCardGroup.addView(bcSmartspaceCardSecondary2, layoutParams2);
                            }
                        }
                    }
                    bcSmartspaceCard2 = bcSmartspaceCard;
                }
                viewHolder = new ViewHolder(i, bcSmartspaceCard2, smartspaceTarget, null, null);
                if (bcSmartspaceCard2 != null) {
                    viewPager.addView(bcSmartspaceCard2);
                }
            }
        }
        onBindViewHolder(viewHolder);
        this.viewHolders.put(i, viewHolder);
        return viewHolder;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public final boolean isViewFromObject(View view, Object obj) {
        ViewHolder viewHolder = (ViewHolder) obj;
        return view == viewHolder.legacyCard || view == viewHolder.card || view == viewHolder.remoteViewsCard;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:216:0x0540  */
    /* JADX WARN: Type inference failed for: r11v14 */
    /* JADX WARN: Type inference failed for: r11v16 */
    /* JADX WARN: Type inference failed for: r11v2, types: [com.google.android.systemui.smartspace.CardPagerAdapter$onBindViewHolder$1] */
    /* JADX WARN: Type inference failed for: r1v7, types: [com.google.android.systemui.smartspace.CardPagerAdapter$onBindViewHolder$1] */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Type inference failed for: r1v9 */
    /* JADX WARN: Type inference failed for: r6v13, types: [com.google.android.systemui.smartspace.CardPagerAdapter$onBindViewHolder$1] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void onBindViewHolder(com.google.android.systemui.smartspace.CardPagerAdapter.ViewHolder r22) {
        /*
            Method dump skipped, instructions count: 1417
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.CardPagerAdapter.onBindViewHolder(com.google.android.systemui.smartspace.CardPagerAdapter$ViewHolder):void");
    }

    public final void setMediaTarget(SmartspaceTarget smartspaceTarget) {
        this.mediaTargets.clear();
        if (smartspaceTarget != null) {
            this.mediaTargets.add(smartspaceTarget);
        }
        updateTargetVisibility();
        notifyDataSetChanged();
    }

    public final void updateCurrentTextColor() {
        this.currentTextColor = ColorUtils.blendARGB(this.primaryTextColor, this.dozeColor, this.dozeAmount);
        int size = this.viewHolders.size();
        for (int i = 0; i < size; i++) {
            SparseArray sparseArray = this.viewHolders;
            ViewHolder viewHolder = (ViewHolder) sparseArray.get(sparseArray.keyAt(i));
            if (viewHolder != null) {
                BcSmartspaceCard bcSmartspaceCard = viewHolder.legacyCard;
                if (bcSmartspaceCard != null) {
                    bcSmartspaceCard.setPrimaryTextColor(this.currentTextColor);
                    bcSmartspaceCard.setDozeAmount(this.dozeAmount);
                }
                BaseTemplateCard baseTemplateCard = viewHolder.card;
                if (baseTemplateCard != null) {
                    baseTemplateCard.setPrimaryTextColor(this.currentTextColor);
                    baseTemplateCard.setDozeAmount(this.dozeAmount);
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x003b  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0053 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0062  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:36:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0057  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0059  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void updateTargetVisibility() {
        /*
            r9 = this;
            java.util.List r0 = r9.mediaTargets
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto Lb
            java.util.List r0 = r9.mediaTargets
            goto L16
        Lb:
            boolean r0 = r9.hasDifferentTargets
            if (r0 == 0) goto L12
            java.util.List r0 = r9._aodTargets
            goto L16
        L12:
            java.util.List r0 = r9.getLockscreenTargets()
        L16:
            java.util.List r1 = r9.getLockscreenTargets()
            java.util.List r2 = r9.smartspaceTargets
            r3 = 0
            r4 = 1052266988(0x3eb851ec, float:0.36)
            r5 = 1065353216(0x3f800000, float:1.0)
            r6 = 1
            if (r2 == r0) goto L38
            float r7 = r9.dozeAmount
            int r8 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r8 != 0) goto L2c
            goto L36
        L2c:
            int r7 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r7 < 0) goto L38
            com.google.android.systemui.smartspace.CardPagerAdapter$TransitionType r7 = r9.transitioningTo
            com.google.android.systemui.smartspace.CardPagerAdapter$TransitionType r8 = com.google.android.systemui.smartspace.CardPagerAdapter.TransitionType.TO_AOD
            if (r7 != r8) goto L38
        L36:
            r7 = r6
            goto L39
        L38:
            r7 = r3
        L39:
            if (r2 == r1) goto L50
            float r2 = r9.dozeAmount
            r8 = 0
            int r8 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r8 != 0) goto L43
            goto L4e
        L43:
            float r5 = r5 - r2
            int r2 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r2 < 0) goto L50
            com.google.android.systemui.smartspace.CardPagerAdapter$TransitionType r2 = r9.transitioningTo
            com.google.android.systemui.smartspace.CardPagerAdapter$TransitionType r4 = com.google.android.systemui.smartspace.CardPagerAdapter.TransitionType.TO_LOCKSCREEN
            if (r2 != r4) goto L50
        L4e:
            r2 = r6
            goto L51
        L50:
            r2 = r3
        L51:
            if (r7 != 0) goto L55
            if (r2 == 0) goto L5f
        L55:
            if (r7 == 0) goto L59
            r2 = r0
            goto L5a
        L59:
            r2 = r1
        L5a:
            r9.smartspaceTargets = r2
            r9.notifyDataSetChanged()
        L5f:
            if (r0 == r1) goto L62
            goto L63
        L62:
            r6 = r3
        L63:
            r9.hasAodLockscreenTransition = r6
            com.android.systemui.plugins.BcSmartspaceConfigPlugin r0 = r9.configProvider
            boolean r0 = r0.isDefaultDateWeatherDisabled()
            if (r0 == 0) goto L86
            java.lang.String r0 = r9.uiSurface
            java.lang.String r1 = "home"
            boolean r0 = kotlin.text.StringsKt__StringsJVMKt.equals(r0, r1, r3)
            if (r0 != 0) goto L86
            java.util.List r0 = r9.smartspaceTargets
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L81
            r3 = 8
        L81:
            com.google.android.systemui.smartspace.BcSmartspaceView r9 = r9.root
            com.google.android.systemui.smartspace.BcSmartspaceTemplateDataUtils.updateVisibility(r9, r3)
        L86:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.CardPagerAdapter.updateTargetVisibility():void");
    }

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class Companion {
        public static int getFeatureType(SmartspaceTarget smartspaceTarget) {
            List actionChips = smartspaceTarget.getActionChips();
            int featureType = smartspaceTarget.getFeatureType();
            return actionChips.isEmpty() ? featureType : (featureType == 13 && actionChips.size() == 1) ? -2 : -1;
        }

        public static boolean useRecycledViewForAction(SmartspaceAction smartspaceAction, SmartspaceAction smartspaceAction2) {
            if (smartspaceAction != null || smartspaceAction2 != null) {
                if (smartspaceAction != null && smartspaceAction2 != null) {
                    Intrinsics.checkNotNull(smartspaceAction);
                    Bundle extras = smartspaceAction.getExtras();
                    Intrinsics.checkNotNull(smartspaceAction2);
                    Bundle extras2 = smartspaceAction2.getExtras();
                    if (extras != null || extras2 != null) {
                        Bundle extras3 = smartspaceAction.getExtras();
                        Bundle extras4 = smartspaceAction2.getExtras();
                        if (extras3 != null && extras4 != null) {
                            Bundle extras5 = smartspaceAction.getExtras();
                            Set<String> keySet = extras5 != null ? extras5.keySet() : null;
                            Bundle extras6 = smartspaceAction2.getExtras();
                            if (Intrinsics.areEqual(keySet, extras6 != null ? extras6.keySet() : null)) {
                            }
                        }
                    }
                }
                return false;
            }
            return true;
        }

        public static boolean useRecycledViewForActionsList(final List list, final List list2) {
            if (list == null && list2 == null) {
                return true;
            }
            if ((list == null || list2 == null) ? false : true) {
                Intrinsics.checkNotNull(list);
                int size = list.size();
                Intrinsics.checkNotNull(list2);
                if (size == list2.size() && IntStream.range(0, list.size()).allMatch(new IntPredicate() { // from class: com.google.android.systemui.smartspace.CardPagerAdapter$Companion$useRecycledViewForActionsList$1
                    @Override // java.util.function.IntPredicate
                    public final boolean test(int i) {
                        return CardPagerAdapter.Companion.useRecycledViewForAction((SmartspaceAction) list.get(i), (SmartspaceAction) list2.get(i));
                    }
                })) {
                    return true;
                }
            }
            return false;
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:25:0x002c. Please report as an issue. */
        public final int getBaseLegacyCardRes(int i) {
            if (i == -2 || i == -1) {
                return com.android.wm.shell.R.layout.smartspace_card;
            }
            if (i == 1) {
                return 0;
            }
            if (i == 2 || i == 3 || i == 4 || i == 6 || i == 18 || i == 20 || i == 30 || i == 9 || i == 10) {
                return com.android.wm.shell.R.layout.smartspace_card;
            }
            switch (i) {
            }
            return com.android.wm.shell.R.layout.smartspace_card;
        }

        public final int getLegacySecondaryCardRes(int i) {
            if (i == -2) {
                return com.android.wm.shell.R.layout.smartspace_card_combination_at_store;
            }
            if (i == -1) {
                return com.android.wm.shell.R.layout.smartspace_card_combination;
            }
            if (i == 1 || i == 2) {
                return 0;
            }
            if (i != 3) {
                if (i == 4) {
                    return com.android.wm.shell.R.layout.smartspace_card_flight;
                }
                if (i == 6) {
                    return 0;
                }
                if (i != 18) {
                    if (i == 20 || i == 30) {
                        return com.android.wm.shell.R.layout.smartspace_card_doorbell;
                    }
                    if (i == 9) {
                        return com.android.wm.shell.R.layout.smartspace_card_sports;
                    }
                    if (i == 10) {
                        return com.android.wm.shell.R.layout.smartspace_card_weather_forecast;
                    }
                    switch (i) {
                    }
                    return 0;
                }
            }
            return com.android.wm.shell.R.layout.smartspace_card_generic_landscape_image;
        }

        public final boolean useRecycledViewForNewTarget(SmartspaceTarget smartspaceTarget, SmartspaceTarget smartspaceTarget2) {
            if (smartspaceTarget2 != null && Intrinsics.areEqual(smartspaceTarget.getSmartspaceTargetId(), smartspaceTarget2.getSmartspaceTargetId()) && useRecycledViewForAction(smartspaceTarget.getHeaderAction(), smartspaceTarget2.getHeaderAction()) && useRecycledViewForAction(smartspaceTarget.getBaseAction(), smartspaceTarget2.getBaseAction()) && useRecycledViewForActionsList(smartspaceTarget.getActionChips(), smartspaceTarget2.getActionChips()) && useRecycledViewForActionsList(smartspaceTarget.getIconGrid(), smartspaceTarget2.getIconGrid())) {
                BaseTemplateData templateData = smartspaceTarget.getTemplateData();
                BaseTemplateData templateData2 = smartspaceTarget2.getTemplateData();
                if ((templateData == null && templateData2 == null) || (templateData != null && templateData2 != null && Intrinsics.areEqual(templateData, templateData2))) {
                    return true;
                }
            }
            return false;
        }

        public static /* synthetic */ void getMAX_FEATURE_TYPE$annotations() {
        }

        public static /* synthetic */ void getMIN_FEATURE_TYPE$annotations() {
        }
    }

    public static /* synthetic */ void getAodTargets$annotations() {
    }

    public static /* synthetic */ void getConfigProvider$annotations() {
    }

    public static /* synthetic */ void getDataProvider$annotations() {
    }

    public static /* synthetic */ void getDozeAmount$annotations() {
    }

    public static /* synthetic */ void getHasAodLockscreenTransition$annotations() {
    }

    public static /* synthetic */ void getHasDifferentTargets$annotations() {
    }

    public static /* synthetic */ void getKeyguardBypassEnabled$annotations() {
    }

    public static /* synthetic */ void getLockscreenTargets$annotations() {
    }

    public static /* synthetic */ void getPrimaryTextColor$annotations() {
    }

    public static /* synthetic */ void getScreenOn$annotations() {
    }

    public static /* synthetic */ void getUiSurface$annotations() {
    }
}
