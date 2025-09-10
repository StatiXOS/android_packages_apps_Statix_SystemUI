package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceTargetEvent;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.content.ComponentName;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.android.internal.graphics.ColorUtils;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.systemui.plugins.BcSmartspaceConfigPlugin;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.uitemplate.BaseTemplateCard;
import com.statix.android.systemui.res.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CardRecyclerViewAdapter
        extends RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder> implements CardAdapter {
    public static final Set<Integer> legacySecondaryCardResourceIdSet =
            BcSmartSpaceUtil.FEATURE_TYPE_TO_SECONDARY_CARD_RESOURCE_MAP.values().stream()
                    .collect(Collectors.toSet());
    public static final Set<Integer> templateSecondaryCardResourceIdSet =
            BcSmartspaceTemplateDataUtils.TEMPLATE_TYPE_TO_SECONDARY_CARD_RES.values().stream()
                    .collect(Collectors.toSet());
    public final List<SmartspaceTarget> _aodTargets = new ArrayList<>();
    public final List<SmartspaceTarget> _lockscreenTargets = new ArrayList<>();
    public Handler bgHandler;
    public BcSmartspaceConfigPlugin configProvider;
    public int currentTextColor;
    public BcSmartspaceDataPlugin dataProvider;
    public float dozeAmount;
    public final int dozeColor = -1;
    public boolean hasAodLockscreenTransition;
    public boolean hasDifferentTargets;
    public boolean keyguardBypassEnabled;
    public final List<SmartspaceTarget> mediaTargets = new ArrayList<>();
    public Integer nonRemoteViewsHorizontalPadding;
    public float previousDozeAmount;
    public int primaryTextColor;
    public final BcSmartspaceView root;
    public List<SmartspaceTarget> smartspaceTargets = new ArrayList<>();
    public BcSmartspaceDataPlugin.TimeChangedDelegate timeChangedDelegate;
    public TransitionType transitioningTo = TransitionType.NOT_IN_TRANSITION;
    public String uiSurface;
    public final SparseArray<ViewHolder> viewHolders = new SparseArray<>();

    public CardRecyclerViewAdapter(BcSmartspaceView root, BcSmartspaceConfigPlugin configProvider) {
        this.root = root;
        this.configProvider = configProvider;
        this.primaryTextColor =
                GraphicsUtils.getAttrColor(root.getContext(), android.R.attr.textColorPrimary);
        this.currentTextColor = primaryTextColor;
    }

    public static boolean isTemplateCard(SmartspaceTarget target) {
        BaseTemplateData templateData = target.getTemplateData();
        return templateData != null
                && BcSmartspaceCardLoggerUtil.containsValidTemplateType(templateData);
    }

    public void addDefaultDateCardIfEmpty(List<SmartspaceTarget> targets) {
        if (targets.isEmpty()) {
            SmartspaceTarget dateCard =
                    new SmartspaceTarget.Builder(
                                    "date_card_794317_92634",
                                    new ComponentName(
                                            root.getContext(), CardRecyclerViewAdapter.class),
                                    root.getContext().getUser())
                            .setFeatureType(1)
                            .setTemplateData(new BaseTemplateData.Builder(1).build())
                            .build();
            targets.add(dateCard);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SmartspaceCard card;
        Integer secondaryCardResId = null;
        if (templateSecondaryCardResourceIdSet.contains(viewType)
                || viewType == R.layout.smartspace_base_template_card
                || viewType == R.layout.smartspace_base_template_card_with_date) {
            secondaryCardResId =
                    templateSecondaryCardResourceIdSet.contains(viewType) ? viewType : null;
            viewType =
                    R.layout
                            .smartspace_base_template_card; // Use non-date layout for secondary
                                                            // cards
            BaseTemplateCard templateCard =
                    (BaseTemplateCard)
                            LayoutInflater.from(parent.getContext())
                                    .inflate(viewType, parent, false);
            templateCard.mUiSurface = uiSurface;
            if (templateCard.mDateView != null && "lockscreen".equals(uiSurface)) {
                if (!templateCard.mDateView.isAttachedToWindow()) {
                    templateCard.mDateView.mUpdatesOnAod = true;
                } else {
                    throw new IllegalStateException("Must call before attaching view to window.");
                }
            }
            if (nonRemoteViewsHorizontalPadding != null) {
                templateCard.setPaddingRelative(
                        nonRemoteViewsHorizontalPadding,
                        templateCard.getPaddingTop(),
                        nonRemoteViewsHorizontalPadding,
                        templateCard.getPaddingBottom());
            }
            templateCard.mBgHandler = bgHandler;
            if (templateCard.mDateView != null) {
                templateCard.mDateView.mBgHandler = bgHandler;
                if (!templateCard.mDateView.isAttachedToWindow()) {
                    templateCard.mDateView.mTimeChangedDelegate = timeChangedDelegate;
                } else {
                    throw new IllegalStateException("Must call before attaching view to window.");
                }
            }
            card = templateCard;
            if (secondaryCardResId != null) {
                BcSmartspaceCardSecondary secondaryCard =
                        (BcSmartspaceCardSecondary)
                                LayoutInflater.from(parent.getContext())
                                        .inflate(secondaryCardResId, parent, false);
                Log.i("SsCardRecyclerViewAdapter", "Secondary card is found");
                ((BaseTemplateCard) card).setSecondaryCard(secondaryCard);
            }
        } else if (legacySecondaryCardResourceIdSet.contains(viewType)
                || viewType == R.layout.smartspace_card) {
            secondaryCardResId =
                    legacySecondaryCardResourceIdSet.contains(viewType) ? viewType : null;
            viewType = R.layout.smartspace_card;
            BcSmartspaceCard legacyCard =
                    (BcSmartspaceCard)
                            LayoutInflater.from(parent.getContext())
                                    .inflate(viewType, parent, false);
            legacyCard.mUiSurface = uiSurface;
            if (nonRemoteViewsHorizontalPadding != null) {
                legacyCard.setPaddingRelative(
                        nonRemoteViewsHorizontalPadding,
                        legacyCard.getPaddingTop(),
                        nonRemoteViewsHorizontalPadding,
                        legacyCard.getPaddingBottom());
            }
            card = legacyCard;
            if (secondaryCardResId != null) {
                BcSmartspaceCardSecondary secondaryCard =
                        (BcSmartspaceCardSecondary)
                                LayoutInflater.from(parent.getContext())
                                        .inflate(secondaryCardResId, parent, false);
                legacyCard.setSecondaryCard(secondaryCard);
            }
        } else {
            BcSmartspaceRemoteViewsCard remoteViewsCard =
                    new BcSmartspaceRemoteViewsCard(parent.getContext());
            remoteViewsCard.mUiSurface = uiSurface;
            card = remoteViewsCard;
        }
        card.getView()
                .setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SmartspaceTarget target = smartspaceTargets.get(position);
        boolean isTemplateCard = isTemplateCard(target);
        BcSmartspaceCardLoggingInfo.Builder loggingInfoBuilder =
                new BcSmartspaceCardLoggingInfo.Builder()
                        .setInstanceId(InstanceId.create(target))
                        .setFeatureType(target.getFeatureType())
                        .setDisplaySurface(
                                BcSmartSpaceUtil.getLoggingDisplaySurface(uiSurface, dozeAmount))
                        .setRank(position)
                        .setCardinality(smartspaceTargets.size())
                        .setUid(-1);
        if (isTemplateCard) {
            loggingInfoBuilder.setSubcardInfo(
                    BcSmartspaceCardLoggerUtil.createSubcardLoggingInfo(target.getTemplateData()));
        } else {
            loggingInfoBuilder.setSubcardInfo(
                    BcSmartspaceCardLoggerUtil.createSubcardLoggingInfo(target));
        }
        loggingInfoBuilder.setDimensionalInfo(
                BcSmartspaceCardLoggerUtil.createDimensionalLoggingInfo(target.getTemplateData()));
        BcSmartspaceCardLoggingInfo loggingInfo =
                new BcSmartspaceCardLoggingInfo(loggingInfoBuilder);
        SmartspaceCard card = holder.card;
        if (target.getRemoteViews() != null) {
            if (!(card instanceof BcSmartspaceRemoteViewsCard)) {
                Log.w("SsCardRecyclerViewAdapter", "[rmv] No RemoteViews card view can be binded");
                return;
            }
            Log.d("SsCardRecyclerViewAdapter", "[rmv] Refreshing RemoteViews card");
        } else if (isTemplateCard) {
            if (target.getTemplateData() == null) {
                throw new IllegalStateException("Required value was null.");
            }
            BcSmartspaceCardLoggerUtil.tryForcePrimaryFeatureTypeOrUpdateLogInfoFromTemplateData(
                    loggingInfo, target.getTemplateData());
            if (!(card instanceof BaseTemplateCard)) {
                Log.w("SsCardRecyclerViewAdapter", "No ui-template card view can be binded");
                return;
            }
        } else {
            BcSmartspaceCardLoggerUtil.tryForcePrimaryFeatureTypeAndInjectWeatherSubcard(
                    loggingInfo, target);
            if (!(card instanceof BcSmartspaceCard)) {
                Log.w("SsCardRecyclerViewAdapter", "No legacy card view can be binded");
                return;
            }
        }
        BcSmartspaceDataPlugin.SmartspaceEventNotifier notifier =
                dataProvider != null ? event -> dataProvider.notifySmartspaceEvent(event) : null;
        card.bindData(target, notifier, loggingInfo, smartspaceTargets.size() > 1);
        card.setPrimaryTextColor(currentTextColor);
        card.setDozeAmount(dozeAmount);
        viewHolders.put(position, holder);
    }

    @Override
    public int getItemCount() {
        return smartspaceTargets.size();
    }

    @Override
    public int getItemViewType(int position) {
        SmartspaceTarget target = smartspaceTargets.get(position);
        if (target.getRemoteViews() != null) {
            return target.getRemoteViews().getLayoutId();
        }
        if (isTemplateCard(target)) {
            BaseTemplateData templateData = target.getTemplateData();
            BaseTemplateData.SubItemInfo primaryItem = templateData.getPrimaryItem();
            // Use smartspace_base_template_card for non-date targets to avoid date view in
            // secondary cards
            Integer layoutId =
                    BcSmartspaceTemplateDataUtils.TEMPLATE_TYPE_TO_SECONDARY_CARD_RES.get(
                            templateData.getTemplateType());
            return layoutId != null ? layoutId : R.layout.smartspace_base_template_card;
        }
        Integer layoutId =
                BcSmartSpaceUtil.FEATURE_TYPE_TO_SECONDARY_CARD_RESOURCE_MAP.get(
                        BcSmartSpaceUtil.getFeatureType(target));
        return layoutId != null ? layoutId : R.layout.smartspace_card;
    }

    @Override
    public SmartspaceCard getCardAtPosition(int position) {
        ViewHolder holder = viewHolders.get(position);
        return holder != null ? holder.card : null;
    }

    @Override
    public BcSmartspaceCard getLegacyCardAtPosition(int position) {
        SmartspaceCard card = getCardAtPosition(position);
        return card instanceof BcSmartspaceCard ? (BcSmartspaceCard) card : null;
    }

    @Override
    public BcSmartspaceRemoteViewsCard getRemoteViewsCardAtPosition(int position) {
        SmartspaceCard card = getCardAtPosition(position);
        return card instanceof BcSmartspaceRemoteViewsCard
                ? (BcSmartspaceRemoteViewsCard) card
                : null;
    }

    @Override
    public BaseTemplateCard getTemplateCardAtPosition(int position) {
        SmartspaceCard card = getCardAtPosition(position);
        return card instanceof BaseTemplateCard ? (BaseTemplateCard) card : null;
    }

    @Override
    public SmartspaceTarget getTargetAtPosition(int position) {
        if (smartspaceTargets.isEmpty() || position < 0 || position >= smartspaceTargets.size()) {
            return null;
        }
        return smartspaceTargets.get(position);
    }

    @Override
    public int getCount() {
        return smartspaceTargets.size();
    }

    @Override
    public float getDozeAmount() {
        return dozeAmount;
    }

    @Override
    public List<SmartspaceTarget> getSmartspaceTargets() {
        return smartspaceTargets;
    }

    @Override
    public List<SmartspaceTarget> getLockscreenTargets() {
        return !mediaTargets.isEmpty() && keyguardBypassEnabled ? mediaTargets : _lockscreenTargets;
    }

    @Override
    public boolean getHasAodLockscreenTransition() {
        return hasAodLockscreenTransition;
    }

    @Override
    public boolean getHasDifferentTargets() {
        return hasDifferentTargets;
    }

    @Override
    public String getUiSurface() {
        return uiSurface;
    }

    public void setBgHandler(Handler handler) {
        bgHandler = handler;
    }

    public void setConfigProvider(BcSmartspaceConfigPlugin configProvider) {
        this.configProvider = configProvider;
    }

    public void setDataProvider(BcSmartspaceDataPlugin dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void setTimeChangedDelegate(BcSmartspaceDataPlugin.TimeChangedDelegate delegate) {
        timeChangedDelegate = delegate;
    }

    public void setUiSurface(String uiSurface) {
        this.uiSurface = uiSurface;
    }

    public void setNonRemoteViewsHorizontalPadding(Integer padding) {
        nonRemoteViewsHorizontalPadding = padding;
        for (int i = 0; i < viewHolders.size(); i++) {
            int key = viewHolders.keyAt(i);
            BcSmartspaceCard legacyCard = getLegacyCardAtPosition(key);
            if (legacyCard != null) {
                legacyCard.setPaddingRelative(
                        padding,
                        legacyCard.getPaddingTop(),
                        padding,
                        legacyCard.getPaddingBottom());
            }
            BaseTemplateCard templateCard = getTemplateCardAtPosition(key);
            if (templateCard != null) {
                templateCard.setPaddingRelative(
                        padding,
                        templateCard.getPaddingTop(),
                        padding,
                        templateCard.getPaddingBottom());
            }
        }
    }

    public void setPrimaryTextColor(int color) {
        primaryTextColor = color;
        updateCurrentTextColor();
    }

    public void setDozeAmount(float dozeAmount) {
        this.dozeAmount = dozeAmount;
        if (previousDozeAmount > dozeAmount) {
            transitioningTo = TransitionType.TO_LOCKSCREEN;
        } else if (previousDozeAmount < dozeAmount) {
            transitioningTo = TransitionType.TO_AOD;
        } else {
            transitioningTo = TransitionType.NOT_IN_TRANSITION;
        }
        previousDozeAmount = dozeAmount;
        updateTargetVisibility();
        updateCurrentTextColor();
    }

    public void setKeyguardBypassEnabled(boolean enabled) {
        keyguardBypassEnabled = enabled;
        updateTargetVisibility();
    }

    public void setScreenOn(boolean screenOn) {
        for (int i = 0; i < viewHolders.size(); i++) {
            ViewHolder holder = viewHolders.get(viewHolders.keyAt(i));
            if (holder != null) {
                holder.card.setScreenOn(screenOn);
            }
        }
    }

    public void setMediaTarget(SmartspaceTarget target) {
        mediaTargets.clear();
        if (target != null) {
            mediaTargets.add(target);
        }
        updateTargetVisibility();
        notifyDataSetChanged();
    }

    public void updateCurrentTextColor() {
        currentTextColor = ColorUtils.blendARGB(primaryTextColor, dozeColor, dozeAmount);
        for (int i = 0; i < viewHolders.size(); i++) {
            ViewHolder holder = viewHolders.get(viewHolders.keyAt(i));
            if (holder != null) {
                holder.card.setPrimaryTextColor(currentTextColor);
                holder.card.setDozeAmount(dozeAmount);
            }
        }
    }

    public void updateTargetVisibility() {
        List<SmartspaceTarget> targets =
                !mediaTargets.isEmpty()
                        ? mediaTargets
                        : hasDifferentTargets ? _aodTargets : getLockscreenTargets();
        boolean updateTargets = false;
        if (smartspaceTargets != targets) {
            if (dozeAmount == 1.0f
                    || (dozeAmount >= 0.36f && transitioningTo == TransitionType.TO_AOD)) {
                updateTargets = true;
            }
        }
        if (smartspaceTargets != getLockscreenTargets()) {
            if (dozeAmount == 0.0f
                    || (1.0f - dozeAmount >= 0.36f
                            && transitioningTo == TransitionType.TO_LOCKSCREEN)) {
                updateTargets = true;
            }
        }
        if (updateTargets) {
            smartspaceTargets = targets;
            notifyDataSetChanged();
        }
        hasAodLockscreenTransition = targets != getLockscreenTargets();
        if (configProvider.isDefaultDateWeatherDisabled() && !"home".equalsIgnoreCase(uiSurface)) {
            BcSmartspaceTemplateDataUtils.updateVisibility(
                    root, smartspaceTargets.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void setTargets(List<SmartspaceTarget> targets) {
        this.smartspaceTargets = new ArrayList<>(targets);
        addDefaultDateCardIfEmpty(this.smartspaceTargets);
        updateTargetVisibility();
        notifyDataSetChanged();
    }

    public static class onBindViewHolder implements BcSmartspaceDataPlugin.SmartspaceEventNotifier {
        public final CardRecyclerViewAdapter adapter;

        public onBindViewHolder(CardRecyclerViewAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void notifySmartspaceEvent(SmartspaceTargetEvent event) {
            adapter.dataProvider.notifySmartspaceEvent(event);
        }
    }

    public enum TransitionType {
        NOT_IN_TRANSITION,
        TO_LOCKSCREEN,
        TO_AOD
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final SmartspaceCard card;

        public ViewHolder(SmartspaceCard card) {
            super(card.getView());
            this.card = card;
        }
    }
}
