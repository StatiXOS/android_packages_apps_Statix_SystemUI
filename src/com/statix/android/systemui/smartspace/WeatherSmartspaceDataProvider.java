package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.SmartspaceTargetEvent;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.statix.android.systemui.res.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeatherSmartspaceDataProvider implements BcSmartspaceDataPlugin {
    public static final boolean DEBUG = Log.isLoggable("WeatherSSDataProvider", Log.DEBUG);

    private SmartspaceEventNotifier mEventNotifier;
    private final Set<SmartspaceTargetListener> mSmartspaceTargetListeners;
    private final List<SmartspaceTarget> mSmartspaceTargets;

    public WeatherSmartspaceDataProvider() {
        mSmartspaceTargetListeners = new HashSet<>();
        mSmartspaceTargets = new ArrayList<>();
        mEventNotifier = null;
    }

    @Override
    public SmartspaceView getLargeClockView(ViewGroup container) {
        Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.weather_large, container, false);
        view.setId(R.id.weather_smartspace_view_large);
        return (SmartspaceView) view;
    }

    @Override
    public SmartspaceView getView(ViewGroup container) {
        Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.weather, container, false);
        return (SmartspaceView) view;
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
                    "WeatherSSDataProvider",
                    this
                            + " onTargetsAvailable called. Callers = "
                            + android.os.Debug.getCallers(3));
            Log.d("WeatherSSDataProvider", " targets.size() = " + targets.size());
            Log.d("WeatherSSDataProvider", " targets = " + targets.toString());
        }

        mSmartspaceTargets.clear();
        for (SmartspaceTarget target : targets) {
            if (target.getFeatureType() == 1) {
                mSmartspaceTargets.add(target);
            }
        }

        mSmartspaceTargetListeners.forEach(
                listener -> listener.onSmartspaceTargetsUpdated(mSmartspaceTargets));
    }

    @Override
    public void registerListener(SmartspaceTargetListener listener) {
        mSmartspaceTargetListeners.add(listener);
        listener.onSmartspaceTargetsUpdated(mSmartspaceTargets);
    }

    @Override
    public void registerSmartspaceEventNotifier(SmartspaceEventNotifier notifier) {
        mEventNotifier = notifier;
    }

    @Override
    public void unregisterListener(SmartspaceTargetListener listener) {
        mSmartspaceTargetListeners.remove(listener);
    }
}
