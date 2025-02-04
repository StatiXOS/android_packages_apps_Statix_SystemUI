package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceTargetEvent;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.viewpager.widget.ViewPager;
import com.android.systemui.plugins.BcSmartspaceConfigPlugin;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.plugins.FalsingManager;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.CardPagerAdapter;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLogger;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.google.android.systemui.smartspace.uitemplate.BaseTemplateCard;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class BcSmartspaceView extends FrameLayout implements BcSmartspaceDataPlugin.SmartspaceTargetListener, BcSmartspaceDataPlugin.SmartspaceView {
    public static final boolean DEBUG = Log.isLoggable("BcSmartspaceView", 3);
    public final CardPagerAdapter mAdapter;
    public final AnonymousClass1 mAodObserver;
    public Handler mBgHandler;
    public int mCardPosition;
    public BcSmartspaceConfigPlugin mConfigProvider;
    public BcSmartspaceDataPlugin mDataProvider;
    public boolean mIsAodEnabled;
    public final ArraySet mLastReceivedTargets;
    public final AnonymousClass2 mOnPageChangeListener;
    public PageIndicator mPageIndicator;
    public List mPendingTargets;
    public float mPreviousDozeAmount;
    public int mScrollState;
    public boolean mSplitShadeEnabled;
    public ViewPager mViewPager;

    /* JADX WARN: Type inference failed for: r1v4, types: [com.google.android.systemui.smartspace.BcSmartspaceView$1] */
    /* JADX WARN: Type inference failed for: r1v6, types: [com.google.android.systemui.smartspace.BcSmartspaceView$2] */
    public BcSmartspaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mConfigProvider = new DefaultBcSmartspaceConfigProvider();
        this.mLastReceivedTargets = new ArraySet();
        this.mIsAodEnabled = false;
        this.mCardPosition = 0;
        this.mPreviousDozeAmount = 0.0f;
        this.mScrollState = 0;
        this.mSplitShadeEnabled = false;
        this.mAodObserver = new ContentObserver(new Handler()) { // from class: com.google.android.systemui.smartspace.BcSmartspaceView.1
            @Override // android.database.ContentObserver
            public final void onChange(boolean z) {
                BcSmartspaceView bcSmartspaceView = BcSmartspaceView.this;
                boolean z2 = BcSmartspaceView.DEBUG;
                Context context2 = bcSmartspaceView.getContext();
                bcSmartspaceView.mIsAodEnabled = Settings.Secure.getIntForUser(context2.getContentResolver(), "doze_always_on", 0, context2.getUserId()) == 1;
            }
        };
        this.mAdapter = new CardPagerAdapter(this, this.mConfigProvider);
        this.mOnPageChangeListener = new ViewPager.OnPageChangeListener() { // from class: com.google.android.systemui.smartspace.BcSmartspaceView.2
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public final void onPageScrollStateChanged(int i) {
                List list;
                BcSmartspaceView bcSmartspaceView = BcSmartspaceView.this;
                bcSmartspaceView.mScrollState = i;
                if (i != 0 || (list = bcSmartspaceView.mPendingTargets) == null) {
                    return;
                }
                bcSmartspaceView.onSmartspaceTargetsUpdated(list);
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public final void onPageScrolled(int i, float f, int i2) {
                PageIndicator pageIndicator = BcSmartspaceView.this.mPageIndicator;
                if (pageIndicator != null) {
                    pageIndicator.setPageOffset(i, f);
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public final void onPageSelected(int i) {
                BcSmartspaceView bcSmartspaceView = BcSmartspaceView.this;
                SmartspaceTarget targetAtPosition = bcSmartspaceView.mAdapter.getTargetAtPosition(bcSmartspaceView.mCardPosition);
                bcSmartspaceView.mCardPosition = i;
                SmartspaceTarget targetAtPosition2 = bcSmartspaceView.mAdapter.getTargetAtPosition(i);
                bcSmartspaceView.logSmartspaceEvent(targetAtPosition2, bcSmartspaceView.mCardPosition, BcSmartspaceEvent.SMARTSPACE_CARD_SEEN);
                if (bcSmartspaceView.mDataProvider == null) {
                    Log.w("BcSmartspaceView", "Cannot notify target hidden/shown smartspace events: data provider null");
                    return;
                }
                if (targetAtPosition == null) {
                    Log.w("BcSmartspaceView", "Cannot notify target hidden smartspace event: previous target is null.");
                } else {
                    SmartspaceTargetEvent.Builder builder = new SmartspaceTargetEvent.Builder(3);
                    builder.setSmartspaceTarget(targetAtPosition);
                    SmartspaceAction baseAction = targetAtPosition.getBaseAction();
                    if (baseAction != null) {
                        builder.setSmartspaceActionId(baseAction.getId());
                    }
                    bcSmartspaceView.mDataProvider.notifySmartspaceEvent(builder.build());
                }
                SmartspaceTargetEvent.Builder builder2 = new SmartspaceTargetEvent.Builder(2);
                builder2.setSmartspaceTarget(targetAtPosition2);
                SmartspaceAction baseAction2 = targetAtPosition2.getBaseAction();
                if (baseAction2 != null) {
                    builder2.setSmartspaceActionId(baseAction2.getId());
                }
                bcSmartspaceView.mDataProvider.notifySmartspaceEvent(builder2.build());
            }
        };
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final int getCurrentCardTopPadding() {
        CardPagerAdapter.ViewHolder viewHolder = (CardPagerAdapter.ViewHolder) this.mAdapter.viewHolders.get(this.mViewPager.mCurItem);
        if ((viewHolder != null ? viewHolder.legacyCard : null) != null) {
            CardPagerAdapter.ViewHolder viewHolder2 = (CardPagerAdapter.ViewHolder) this.mAdapter.viewHolders.get(this.mViewPager.mCurItem);
            return (viewHolder2 != null ? viewHolder2.legacyCard : null).getPaddingTop();
        }
        CardPagerAdapter.ViewHolder viewHolder3 = (CardPagerAdapter.ViewHolder) this.mAdapter.viewHolders.get(this.mViewPager.mCurItem);
        if ((viewHolder3 != null ? viewHolder3.card : null) != null) {
            CardPagerAdapter.ViewHolder viewHolder4 = (CardPagerAdapter.ViewHolder) this.mAdapter.viewHolders.get(this.mViewPager.mCurItem);
            return (viewHolder4 != null ? viewHolder4.card : null).getPaddingTop();
        }
        CardPagerAdapter.ViewHolder viewHolder5 = (CardPagerAdapter.ViewHolder) this.mAdapter.viewHolders.get(this.mViewPager.mCurItem);
        if ((viewHolder5 != null ? viewHolder5.remoteViewsCard : null) == null) {
            return 0;
        }
        CardPagerAdapter.ViewHolder viewHolder6 = (CardPagerAdapter.ViewHolder) this.mAdapter.viewHolders.get(this.mViewPager.mCurItem);
        return (viewHolder6 != null ? viewHolder6.remoteViewsCard : null).getPaddingTop();
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final int getSelectedPage() {
        return this.mViewPager.mCurItem;
    }

    public final void logSmartspaceEvent(SmartspaceTarget smartspaceTarget, int i, BcSmartspaceEvent bcSmartspaceEvent) {
        int i2;
        if (bcSmartspaceEvent == BcSmartspaceEvent.SMARTSPACE_CARD_RECEIVED) {
            try {
                i2 = (int) Instant.now().minusMillis(smartspaceTarget.getCreationTimeMillis()).toEpochMilli();
            } catch (ArithmeticException | DateTimeException e) {
                Log.e("BcSmartspaceView", "received_latency_millis will be -1 due to exception ", e);
                i2 = -1;
            }
        } else {
            i2 = 0;
        }
        boolean containsValidTemplateType = BcSmartspaceCardLoggerUtil.containsValidTemplateType(smartspaceTarget.getTemplateData());
        BcSmartspaceCardLoggingInfo.Builder builder = new BcSmartspaceCardLoggingInfo.Builder();
        builder.mInstanceId = InstanceId.create(smartspaceTarget);
        builder.mFeatureType = smartspaceTarget.getFeatureType();
        CardPagerAdapter cardPagerAdapter = this.mAdapter;
        builder.mDisplaySurface = BcSmartSpaceUtil.getLoggingDisplaySurface(cardPagerAdapter.uiSurface, cardPagerAdapter.dozeAmount);
        builder.mRank = i;
        builder.mCardinality = this.mAdapter.smartspaceTargets.size();
        builder.mReceivedLatency = i2;
        getContext().getPackageManager();
        builder.mUid = -1;
        builder.mSubcardInfo = containsValidTemplateType ? BcSmartspaceCardLoggerUtil.createSubcardLoggingInfo(smartspaceTarget.getTemplateData()) : BcSmartspaceCardLoggerUtil.createSubcardLoggingInfo(smartspaceTarget);
        builder.mDimensionalInfo = BcSmartspaceCardLoggerUtil.createDimensionalLoggingInfo(smartspaceTarget.getTemplateData());
        BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo = new BcSmartspaceCardLoggingInfo(builder);
        if (containsValidTemplateType) {
            BcSmartspaceCardLoggerUtil.tryForcePrimaryFeatureTypeOrUpdateLogInfoFromTemplateData(bcSmartspaceCardLoggingInfo, smartspaceTarget.getTemplateData());
        } else {
            BcSmartspaceCardLoggerUtil.tryForcePrimaryFeatureTypeAndInjectWeatherSubcard(bcSmartspaceCardLoggingInfo, smartspaceTarget);
        }
        BcSmartspaceCardLogger.log(bcSmartspaceEvent, bcSmartspaceCardLoggingInfo);
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mViewPager.setAdapter(this.mAdapter);
        ViewPager viewPager = this.mViewPager;
        AnonymousClass2 anonymousClass2 = this.mOnPageChangeListener;
        if (viewPager.mOnPageChangeListeners == null) {
            viewPager.mOnPageChangeListeners = new ArrayList();
        }
        viewPager.mOnPageChangeListeners.add(anonymousClass2);
        this.mPageIndicator.setNumPages(this.mAdapter.smartspaceTargets.size(), isLayoutRtl());
        if (TextUtils.equals(this.mAdapter.uiSurface, BcSmartspaceDataPlugin.UI_SURFACE_LOCK_SCREEN_AOD)) {
            try {
                Handler handler = this.mBgHandler;
                if (handler == null) {
                    throw new IllegalStateException("Must set background handler to avoid making binder calls on main thread");
                }
                handler.post(new BcSmartspaceView$$ExternalSyntheticLambda0(this, 1));
                Context context = getContext();
                this.mIsAodEnabled = Settings.Secure.getIntForUser(context.getContentResolver(), "doze_always_on", 0, context.getUserId()) == 1;
            } catch (Exception e) {
                Log.w("BcSmartspaceView", "Unable to register Doze Always on content observer.", e);
            }
        }
        BcSmartspaceDataPlugin bcSmartspaceDataPlugin = this.mDataProvider;
        if (bcSmartspaceDataPlugin != null) {
            registerDataProvider(bcSmartspaceDataPlugin);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Handler handler = this.mBgHandler;
        if (handler == null) {
            throw new IllegalStateException("Must set background handler to avoid making binder calls on main thread");
        }
        handler.post(new BcSmartspaceView$$ExternalSyntheticLambda0(this, 0));
        ViewPager viewPager = this.mViewPager;
        AnonymousClass2 anonymousClass2 = this.mOnPageChangeListener;
        List list = viewPager.mOnPageChangeListeners;
        if (list != null) {
            list.remove(anonymousClass2);
        }
        BcSmartspaceDataPlugin bcSmartspaceDataPlugin = this.mDataProvider;
        if (bcSmartspaceDataPlugin != null) {
            bcSmartspaceDataPlugin.unregisterListener(this);
        }
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.mViewPager = (ViewPager) findViewById(R.id.smartspace_card_pager);
        this.mPageIndicator = (PageIndicator) findViewById(R.id.smartspace_page_indicator);
    }

    @Override // android.widget.FrameLayout, android.view.View
    public final void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i2);
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.enhanced_smartspace_height);
        if (size <= 0 || size >= dimensionPixelSize) {
            super.onMeasure(i, i2);
            setScaleX(1.0f);
            setScaleY(1.0f);
            resetPivot();
            return;
        }
        float f = size;
        float f2 = dimensionPixelSize;
        float f3 = f / f2;
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.round(View.MeasureSpec.getSize(i) / f3), 1073741824), View.MeasureSpec.makeMeasureSpec(dimensionPixelSize, 1073741824));
        setScaleX(f3);
        setScaleY(f3);
        setPivotX(0.0f);
        setPivotY(f2 / 2.0f);
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceTargetListener
    public final void onSmartspaceTargetsUpdated(List list) {
        int i;
        Bundle extras;
        if (DEBUG) {
            Log.d("BcSmartspaceView", "@" + Integer.toHexString(hashCode()) + ", onTargetsAvailable called. Callers = " + Debug.getCallers(5));
            StringBuilder sb = new StringBuilder("    targets.size() = ");
            sb.append(list.size());
            Log.d("BcSmartspaceView", sb.toString());
            Log.d("BcSmartspaceView", "    targets = " + list.toString());
        }
        if (this.mScrollState != 0 && this.mAdapter.smartspaceTargets.size() > 1) {
            this.mPendingTargets = list;
            return;
        }
        this.mPendingTargets = null;
        boolean isLayoutRtl = isLayoutRtl();
        int i2 = this.mViewPager.mCurItem;
        if (isLayoutRtl) {
            i = this.mAdapter.smartspaceTargets.size() - i2;
            ArrayList arrayList = new ArrayList(list);
            Collections.reverse(arrayList);
            list = arrayList;
        } else {
            i = i2;
        }
        CardPagerAdapter cardPagerAdapter = this.mAdapter;
        cardPagerAdapter._aodTargets.clear();
        cardPagerAdapter._lockscreenTargets.clear();
        cardPagerAdapter.hasDifferentTargets = false;
        for (SmartspaceTarget smartspaceTarget : list) {
            Intrinsics.checkNotNull(smartspaceTarget);
            if (smartspaceTarget.getFeatureType() != 34) {
                SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
                int i3 = (baseAction == null || (extras = baseAction.getExtras()) == null) ? 3 : extras.getInt("SCREEN_EXTRA", 3);
                if ((i3 & 2) != 0) {
                    cardPagerAdapter._aodTargets.add(smartspaceTarget);
                }
                if ((i3 & 1) != 0) {
                    cardPagerAdapter._lockscreenTargets.add(smartspaceTarget);
                }
                if (i3 != 3) {
                    cardPagerAdapter.hasDifferentTargets = true;
                }
            }
        }
        if (!cardPagerAdapter.configProvider.isDefaultDateWeatherDisabled()) {
            cardPagerAdapter.addDefaultDateCardIfEmpty(cardPagerAdapter._aodTargets);
            cardPagerAdapter.addDefaultDateCardIfEmpty(cardPagerAdapter._lockscreenTargets);
        }
        cardPagerAdapter.updateTargetVisibility();
        cardPagerAdapter.notifyDataSetChanged();
        int size = this.mAdapter.smartspaceTargets.size();
        PageIndicator pageIndicator = this.mPageIndicator;
        if (pageIndicator != null) {
            pageIndicator.setNumPages(size, isLayoutRtl);
        }
        if (isLayoutRtl) {
            setSelectedPage(Math.max(0, Math.min(size - 1, size - i)));
        }
        for (int i4 = 0; i4 < size; i4++) {
            SmartspaceTarget targetAtPosition = this.mAdapter.getTargetAtPosition(i4);
            if (!this.mLastReceivedTargets.contains(targetAtPosition.getSmartspaceTargetId())) {
                logSmartspaceEvent(targetAtPosition, i4, BcSmartspaceEvent.SMARTSPACE_CARD_RECEIVED);
                SmartspaceTargetEvent.Builder builder = new SmartspaceTargetEvent.Builder(8);
                builder.setSmartspaceTarget(targetAtPosition);
                SmartspaceAction baseAction2 = targetAtPosition.getBaseAction();
                if (baseAction2 != null) {
                    builder.setSmartspaceActionId(baseAction2.getId());
                }
                this.mDataProvider.notifySmartspaceEvent(builder.build());
            }
        }
        this.mLastReceivedTargets.clear();
        this.mLastReceivedTargets.addAll((Collection) this.mAdapter.smartspaceTargets.stream().map(new BcSmartspaceView$$ExternalSyntheticLambda2()).collect(Collectors.toList()));
    }

    @Override // android.view.View
    public final void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        BcSmartspaceDataPlugin bcSmartspaceDataPlugin = this.mDataProvider;
        if (bcSmartspaceDataPlugin != null) {
            bcSmartspaceDataPlugin.notifySmartspaceEvent(new SmartspaceTargetEvent.Builder(z ? 6 : 7).build());
        }
        if (this.mScrollState != 0) {
            this.mScrollState = 0;
            List list = this.mPendingTargets;
            if (list != null) {
                onSmartspaceTargetsUpdated(list);
            }
        }
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void registerConfigProvider(BcSmartspaceConfigPlugin bcSmartspaceConfigPlugin) {
        this.mConfigProvider = bcSmartspaceConfigPlugin;
        this.mAdapter.configProvider = bcSmartspaceConfigPlugin;
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void registerDataProvider(BcSmartspaceDataPlugin bcSmartspaceDataPlugin) {
        BcSmartspaceDataPlugin bcSmartspaceDataPlugin2 = this.mDataProvider;
        if (bcSmartspaceDataPlugin2 != null) {
            bcSmartspaceDataPlugin2.unregisterListener(this);
        }
        this.mDataProvider = bcSmartspaceDataPlugin;
        bcSmartspaceDataPlugin.registerListener(this);
        this.mAdapter.dataProvider = this.mDataProvider;
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setBgHandler(Handler handler) {
        this.mBgHandler = handler;
        this.mAdapter.bgHandler = handler;
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setDozeAmount(float f) {
        List list;
        float f2;
        CardPagerAdapter cardPagerAdapter = this.mAdapter;
        List list2 = cardPagerAdapter.smartspaceTargets;
        cardPagerAdapter.dozeAmount = f;
        float f3 = cardPagerAdapter.previousDozeAmount;
        cardPagerAdapter.transitioningTo = f3 > f ? CardPagerAdapter.TransitionType.TO_LOCKSCREEN : f3 < f ? CardPagerAdapter.TransitionType.TO_AOD : CardPagerAdapter.TransitionType.NOT_IN_TRANSITION;
        cardPagerAdapter.previousDozeAmount = f;
        cardPagerAdapter.updateTargetVisibility();
        cardPagerAdapter.updateCurrentTextColor();
        if (!this.mAdapter.smartspaceTargets.isEmpty()) {
            BcSmartspaceTemplateDataUtils.updateVisibility(this, 0);
        }
        float f4 = 1.0f;
        if (this.mAdapter.hasAodLockscreenTransition) {
            float f5 = this.mPreviousDozeAmount;
            if (f == f5) {
                f4 = getAlpha();
            } else {
                float f6 = f5 > f ? 1.0f - f : f;
                float f7 = 0.36f;
                if (f6 < 0.36f) {
                    f2 = 0.36f - f6;
                } else {
                    f2 = f6 - 0.36f;
                    f7 = 0.64f;
                }
                f4 = f2 / f7;
            }
        }
        setAlpha(f4);
        PageIndicator pageIndicator = this.mPageIndicator;
        if (pageIndicator != null) {
            pageIndicator.setNumPages(this.mAdapter.smartspaceTargets.size(), isLayoutRtl());
            this.mPageIndicator.setAlpha(f4);
        }
        this.mPreviousDozeAmount = f;
        CardPagerAdapter cardPagerAdapter2 = this.mAdapter;
        if (cardPagerAdapter2.hasDifferentTargets && (list = cardPagerAdapter2.smartspaceTargets) != list2 && list.size() > 0) {
            setSelectedPage(isLayoutRtl() ? this.mAdapter.smartspaceTargets.size() - 1 : 0);
        }
        CardPagerAdapter cardPagerAdapter3 = this.mAdapter;
        int loggingDisplaySurface = BcSmartSpaceUtil.getLoggingDisplaySurface(cardPagerAdapter3.uiSurface, cardPagerAdapter3.dozeAmount);
        if (loggingDisplaySurface == -1) {
            return;
        }
        if (loggingDisplaySurface != 3 || this.mIsAodEnabled) {
            if (DEBUG) {
                Log.d("BcSmartspaceView", "@" + Integer.toHexString(hashCode()) + ", setDozeAmount: Logging SMARTSPACE_CARD_SEEN, currentSurface = " + loggingDisplaySurface);
            }
            SmartspaceTarget targetAtPosition = this.mAdapter.getTargetAtPosition(this.mCardPosition);
            if (targetAtPosition == null) {
                Log.w("BcSmartspaceView", "Current card is not present in the Adapter; cannot log.");
            } else {
                logSmartspaceEvent(targetAtPosition, this.mCardPosition, BcSmartspaceEvent.SMARTSPACE_CARD_SEEN);
            }
        }
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setDozing(boolean z) {
        if (z || !this.mSplitShadeEnabled) {
            return;
        }
        CardPagerAdapter cardPagerAdapter = this.mAdapter;
        if (cardPagerAdapter.hasAodLockscreenTransition && cardPagerAdapter.getLockscreenTargets().isEmpty()) {
            BcSmartspaceTemplateDataUtils.updateVisibility(this, 8);
        }
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setFalsingManager(FalsingManager falsingManager) {
        BcSmartSpaceUtil.sFalsingManager = falsingManager;
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setIntentStarter(BcSmartspaceDataPlugin.IntentStarter intentStarter) {
        BcSmartSpaceUtil.sIntentStarter = intentStarter;
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setKeyguardBypassEnabled(boolean z) {
        CardPagerAdapter cardPagerAdapter = this.mAdapter;
        cardPagerAdapter.keyguardBypassEnabled = z;
        cardPagerAdapter.updateTargetVisibility();
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setMediaTarget(SmartspaceTarget smartspaceTarget) {
        this.mAdapter.setMediaTarget(smartspaceTarget);
    }

    @Override // android.view.View
    public final void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mViewPager.setOnLongClickListener(onLongClickListener);
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setPrimaryTextColor(int i) {
        CardPagerAdapter cardPagerAdapter = this.mAdapter;
        cardPagerAdapter.primaryTextColor = i;
        cardPagerAdapter.updateCurrentTextColor();
        PageIndicator pageIndicator = this.mPageIndicator;
        pageIndicator.mPrimaryColor = i;
        for (int i2 = 0; i2 < pageIndicator.getChildCount(); i2++) {
            ((ImageView) pageIndicator.getChildAt(i2)).getDrawable().setTint(pageIndicator.mPrimaryColor);
        }
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setScreenOn(boolean z) {
        BaseTemplateCard baseTemplateCard;
        IcuDateTextView icuDateTextView;
        if (this.mScrollState != 0) {
            this.mScrollState = 0;
            List list = this.mPendingTargets;
            if (list != null) {
                onSmartspaceTargetsUpdated(list);
            }
        }
        CardPagerAdapter cardPagerAdapter = this.mAdapter;
        int size = cardPagerAdapter.viewHolders.size();
        for (int i = 0; i < size; i++) {
            SparseArray sparseArray = cardPagerAdapter.viewHolders;
            CardPagerAdapter.ViewHolder viewHolder = (CardPagerAdapter.ViewHolder) sparseArray.get(sparseArray.keyAt(i));
            if (viewHolder != null && (baseTemplateCard = viewHolder.card) != null && (icuDateTextView = baseTemplateCard.mDateView) != null) {
                icuDateTextView.mIsInteractive = z;
                icuDateTextView.rescheduleTicker();
            }
        }
    }

    public final void setSelectedPage(int i) {
        this.mViewPager.setCurrentItem(i, false);
        this.mPageIndicator.setPageOffset(i, 0.0f);
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setSplitShadeEnabled(boolean z) {
        this.mSplitShadeEnabled = z;
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setTimeChangedDelegate(BcSmartspaceDataPlugin.TimeChangedDelegate timeChangedDelegate) {
        this.mAdapter.timeChangedDelegate = timeChangedDelegate;
    }

    @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceView
    public final void setUiSurface(String str) {
        if (isAttachedToWindow()) {
            throw new IllegalStateException("Must call before attaching view to window.");
        }
        this.mAdapter.uiSurface = str;
    }
}
