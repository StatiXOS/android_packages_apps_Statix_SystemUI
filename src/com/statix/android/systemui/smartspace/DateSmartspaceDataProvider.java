package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceTargetEvent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.statix.android.systemui.res.R;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class DateSmartspaceDataProvider implements BcSmartspaceDataPlugin {
    public Set<View.OnAttachStateChangeListener> mAttachListeners = new HashSet<>();
    public BcSmartspaceDataPlugin.SmartspaceEventNotifier mEventNotifier;
    public final View.OnAttachStateChangeListener mStateChangeListener =
            new StateChangeListener(this);
    public Set<View> mViews = new HashSet<>();

    public static class StateChangeListener implements View.OnAttachStateChangeListener {
        public final DateSmartspaceDataProvider this$0;

        public StateChangeListener(DateSmartspaceDataProvider provider) {
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
    public BcSmartspaceDataPlugin.SmartspaceView getLargeClockView(ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.date_plus_extras_large, viewGroup, false);
        view.setId(R.id.date_smartspace_view_large);
        view.addOnAttachStateChangeListener(mStateChangeListener);
        return (BcSmartspaceDataPlugin.SmartspaceView) view;
    }

    @Override
    public BcSmartspaceDataPlugin.SmartspaceView getView(ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.date_plus_extras, viewGroup, false);
        view.addOnAttachStateChangeListener(mStateChangeListener);
        return (BcSmartspaceDataPlugin.SmartspaceView) view;
    }

    @Override
    public void notifySmartspaceEvent(SmartspaceTargetEvent smartspaceTargetEvent) {
        if (mEventNotifier != null) {
            mEventNotifier.notifySmartspaceEvent(smartspaceTargetEvent);
        }
    }

    @Override
    public void registerSmartspaceEventNotifier(
            BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier) {
        mEventNotifier = smartspaceEventNotifier;
    }
}
