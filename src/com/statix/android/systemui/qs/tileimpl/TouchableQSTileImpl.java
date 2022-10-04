package com.statix.android.systemui.qs.tileimpl;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.android.internal.logging.MetricsLogger;

import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSTile.State;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.tileimpl.QSTileImpl;


// For use with SliderQSTileViewImpl
public abstract class TouchableQSTileImpl<TState extends State> extends QSTileImpl<TState> {

    public TouchableQSTileImpl(
            QSHost host,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger
    ) {
        super(host, backgroundLooper, mainHandler, falsingManager,
              metricsLogger, statusBarStateController, activityStarter,
              qsLogger);
    }

    public abstract View.OnTouchListener getTouchListener();

    public abstract void registerPercentUpdateListener(Callback cb);

    public interface Callback {
        void onPercentUpdated(float newPercent);
    }

}
