package com.google.android.systemui.smartspace;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.statix.android.systemui.res.R;

import java.util.Locale;
import java.util.Objects;

public class IcuDateTextView extends DoubleShadowTextView {
    public final ContentObserver mAodSettingsObserver;
    public Handler mBgHandler;
    public DateFormat mFormatter;
    public Handler mHandler;
    public final BroadcastReceiver mIntentReceiver;
    public boolean mIsAodEnabled;
    public boolean mIsInteractive;
    public String mText;
    public final Runnable mTimeChangedCallback;
    public BcSmartspaceDataPlugin.TimeChangedDelegate mTimeChangedDelegate;
    public boolean mUpdatesOnAod;

    public IcuDateTextView(Context context) {
        this(context, null);
    }

    public IcuDateTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mAodSettingsObserver = new IcuDateTextViewObserver(this, new Handler());
        mIntentReceiver = new IcuDateTextViewReceiver(this);
        mTimeChangedCallback = () -> onTimeChanged(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mUpdatesOnAod) {
            try {
                if (mBgHandler == null) {
                    Log.wtf(
                            "IcuDateTextView",
                            "Must set background handler when mUpdatesOnAod is set to avoid making"
                                + " binder calls on main thread");
                    ContentResolver resolver = getContext().getContentResolver();
                    resolver.registerContentObserver(
                            Settings.Secure.getUriFor("doze_always_on"),
                            false,
                            mAodSettingsObserver,
                            -1);
                } else {
                    mBgHandler.post(
                            () -> {
                                ContentResolver resolver = getContext().getContentResolver();
                                resolver.registerContentObserver(
                                        Settings.Secure.getUriFor("doze_always_on"),
                                        false,
                                        mAodSettingsObserver,
                                        -1);
                            });
                }
            } catch (Exception e) {
                Log.w("IcuDateTextView", "Unable to register DOZE_ALWAYS_ON content observer: ", e);
            }
            mIsAodEnabled =
                    Settings.Secure.getIntForUser(
                                    getContext().getContentResolver(),
                                    "doze_always_on",
                                    0,
                                    getContext().getUserId())
                            == 1;
        }

        mHandler = new Handler();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        if (mBgHandler == null) {
            Log.w(
                    "IcuDateTextView",
                    "mBgHandler is not set! Fallback to make binder calls on main thread.");
            getContext().registerReceiver(mIntentReceiver, filter);
        } else {
            mBgHandler.post(() -> getContext().registerReceiver(mIntentReceiver, filter));
        }

        if (mTimeChangedDelegate == null) {
            mTimeChangedDelegate = new DefaultTimeChangedDelegate(mHandler);
        }

        PowerManager powerManager = getContext().getSystemService(PowerManager.class);
        mIsInteractive = powerManager != null ? powerManager.isInteractive() : false;
        if (powerManager == null) {
            Log.w("IcuDateTextView", "PowerManager is null. Fallback isInteractive as false");
        }

        onTimeChanged(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            if (mBgHandler == null) {
                Log.w(
                        "IcuDateTextView",
                        "mBgHandler is not set! Fallback to make binder calls on main thread.");
                getContext().unregisterReceiver(mIntentReceiver);
            } else {
                mBgHandler.post(
                        () -> {
                            try {
                                getContext().unregisterReceiver(mIntentReceiver);
                            } catch (IllegalArgumentException e) {
                                Log.w(
                                        "IcuDateTextView",
                                        "Receiver not registered: " + e.getMessage());
                            }
                        });
            }
            mTimeChangedDelegate.unregister();
            mHandler = null;
        }

        if (mUpdatesOnAod) {
            if (mBgHandler == null) {
                Log.wtf(
                        "IcuDateTextView",
                        "Must set background handler when mUpdatesOnAod is set to avoid making"
                            + " binder calls on main thread");
                getContext().getContentResolver().unregisterContentObserver(mAodSettingsObserver);
            } else {
                mBgHandler.post(
                        () ->
                                getContext()
                                        .getContentResolver()
                                        .unregisterContentObserver(mAodSettingsObserver));
            }
        }
    }

    public void onTimeChanged(boolean forceUpdateFormatter) {
        if (mFormatter == null || forceUpdateFormatter) {
            String skeleton = getContext().getString(R.string.smartspace_icu_date_pattern);
            mFormatter = DateFormat.getInstanceForSkeleton(skeleton, Locale.getDefault());
            mFormatter.setContext(DisplayContext.CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE);
        }

        if (!isShown()) {
            return;
        }

        String newText = mFormatter.format(System.currentTimeMillis());
        if (!Objects.equals(mText, newText)) {
            mText = newText;
            setText(newText);
            setContentDescription(newText);
        }
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        rescheduleTicker();
    }

    public void rescheduleTicker() {
        if (mHandler == null) {
            return;
        }
        mHandler.post(
                () -> { // STX edit
                    mTimeChangedDelegate.unregister();
                    if (!(mIsInteractive || (mUpdatesOnAod && mIsAodEnabled))
                            || !isAggregatedVisible()) {
                        return;
                    }
                    mTimeChangedDelegate.register(mTimeChangedCallback);
                }); // STX edit
    }

    public class IcuDateTextViewObserver extends ContentObserver {
        public final IcuDateTextView outer;

        public IcuDateTextViewObserver(IcuDateTextView outer, Handler handler) {
            super(handler);
            this.outer = outer;
        }

        @Override
        public void onChange(boolean selfChange) {
            boolean isAodEnabled =
                    Settings.Secure.getIntForUser(
                                    outer.getContext().getContentResolver(),
                                    "doze_always_on",
                                    0,
                                    outer.getContext().getUserId())
                            == 1;
            if (outer.mIsAodEnabled != isAodEnabled) {
                outer.mIsAodEnabled = isAodEnabled;
                outer.rescheduleTicker();
            }
        }
    }

    public class IcuDateTextViewReceiver extends BroadcastReceiver {
        public final IcuDateTextView outer;

        public IcuDateTextViewReceiver(IcuDateTextView outer) {
            this.outer = outer;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean updateFormatter =
                    "android.intent.action.TIMEZONE_CHANGED".equals(action)
                            || "android.intent.action.TIME_SET".equals(action);
            outer.onTimeChanged(updateFormatter);
        }
    }

    public class DefaultTimeChangedDelegate
            implements BcSmartspaceDataPlugin.TimeChangedDelegate, Runnable {
        public Handler mHandler;
        public Runnable mTimeChangedCallback;

        public DefaultTimeChangedDelegate(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void register(Runnable callback) {
            if (mTimeChangedCallback != null) {
                unregister();
            }
            mTimeChangedCallback = callback;
            run();
        }

        @Override
        public void run() {
            if (mTimeChangedCallback != null) {
                mTimeChangedCallback.run();
                if (mHandler != null) {
                    long now = SystemClock.uptimeMillis();
                    long delay = 60000 - (now % 60000);
                    mHandler.postAtTime(this, now + delay);
                }
            }
        }

        @Override
        public void unregister() {
            mHandler.removeCallbacks(this);
            mTimeChangedCallback = null;
        }
    }
}
