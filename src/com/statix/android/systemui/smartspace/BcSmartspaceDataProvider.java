package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceTargetEvent;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.systemui.plugins.BcSmartspaceConfigPlugin;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.statix.android.systemui.res.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class BcSmartspaceDataProvider implements BcSmartspaceDataPlugin {
    public static final boolean DEBUG = Log.isLoggable("BcSmartspaceDataPlugin", Log.DEBUG);

    public final Set<BcSmartspaceDataPlugin.SmartspaceTargetListener> mSmartspaceTargetListeners =
            new HashSet<>();
    public final List<SmartspaceTarget> mSmartspaceTargets = new ArrayList<>();
    public final Set<View> mViews = new HashSet<>();
    public final Set<View.OnAttachStateChangeListener> mAttachListeners = new HashSet<>();
    public BcSmartspaceDataPlugin.SmartspaceEventNotifier mEventNotifier;
    public BcSmartspaceConfigPlugin mConfigProvider = new DefaultBcSmartspaceConfigProvider();
    public final View.OnAttachStateChangeListener mStateChangeListener =
            new StateChangeListener(this);

    public BcSmartspaceDataProvider() {}

    @Override
    public void addOnAttachStateChangeListener(View.OnAttachStateChangeListener listener) {
        mAttachListeners.add(listener);
        Iterator<View> iterator = mViews.iterator();
        while (iterator.hasNext()) {
            View view = iterator.next();
            listener.onViewAttachedToWindow(view);
        }
    }

    @Override
    public BcSmartspaceDataPlugin.SmartspaceView getView(ViewGroup parent) {
        int layoutRes =
                mConfigProvider.isViewPager2Enabled()
                        ? R.layout.smartspace_enhanced2
                        : R.layout.smartspace_enhanced;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutRes, parent, false);
        view.addOnAttachStateChangeListener(mStateChangeListener);
        return (BcSmartspaceDataPlugin.SmartspaceView) view;
    }

    @Override
    public void notifySmartspaceEvent(SmartspaceTargetEvent event) {
        if (mEventNotifier != null) {
            mEventNotifier.notifySmartspaceEvent(event);
        }
    }

    @Override
    public void onTargetsAvailable(List<SmartspaceTarget> targets) {
        if (DEBUG) {
            Log.d(
                    "BcSmartspaceDataPlugin",
                    this + " onTargetsAvailable called. Callers = " + Debug.getCallers(3));
            Log.d("BcSmartspaceDataPlugin", " targets.size() = " + targets.size());
            Log.d("BcSmartspaceDataPlugin", " targets = " + targets.toString());
        }

        mSmartspaceTargets.clear();
        for (SmartspaceTarget target : targets) {
            if (target.getFeatureType() != 15) {
                mSmartspaceTargets.add(target);
            }
        }

        mSmartspaceTargetListeners.forEach(
                listener -> listener.onSmartspaceTargetsUpdated(mSmartspaceTargets));
    }

    @Override
    public void registerConfigProvider(BcSmartspaceConfigPlugin configProvider) {
        mConfigProvider = configProvider;
    }

    @Override
    public void registerListener(BcSmartspaceDataPlugin.SmartspaceTargetListener listener) {
        mSmartspaceTargetListeners.add(listener);
        listener.onSmartspaceTargetsUpdated(mSmartspaceTargets);
    }

    @Override
    public void registerSmartspaceEventNotifier(
            BcSmartspaceDataPlugin.SmartspaceEventNotifier notifier) {
        mEventNotifier = notifier;
    }

    @Override
    public void unregisterListener(BcSmartspaceDataPlugin.SmartspaceTargetListener listener) {
        mSmartspaceTargetListeners.remove(listener);
    }

    public static class StateChangeListener implements View.OnAttachStateChangeListener {
        public final BcSmartspaceDataProvider this$0;

        public StateChangeListener(BcSmartspaceDataProvider provider) {
            this.this$0 = provider;
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            this$0.mViews.add(view);
            Iterator<View.OnAttachStateChangeListener> iterator =
                    this$0.mAttachListeners.iterator();
            while (iterator.hasNext()) {
                View.OnAttachStateChangeListener listener = iterator.next();
                listener.onViewAttachedToWindow(view);
            }
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            this$0.mViews.remove(view);
            Iterator<View.OnAttachStateChangeListener> iterator =
                    this$0.mAttachListeners.iterator();
            while (iterator.hasNext()) {
                View.OnAttachStateChangeListener listener = iterator.next();
                listener.onViewDetachedFromWindow(view);
            }
        }
    }
}
