package com.google.android.systemui.smartspace;

import android.content.BroadcastReceiver;
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
import android.widget.TextView;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.wm.shell.R;
import java.util.Locale;
import java.util.Objects;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class IcuDateTextView extends DoubleShadowTextView {
    public static final /* synthetic */ int $r8$clinit = 0;
    public final AnonymousClass1 mAodSettingsObserver;
    public Handler mBgHandler;
    public DateFormat mFormatter;
    public Handler mHandler;
    public final AnonymousClass2 mIntentReceiver;
    public boolean mIsAodEnabled;
    public boolean mIsInteractive;
    public String mText;
    public final IcuDateTextView$$ExternalSyntheticLambda0 mTimeChangedCallback;
    public BcSmartspaceDataPlugin.TimeChangedDelegate mTimeChangedDelegate;
    public boolean mUpdatesOnAod;

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class DefaultTimeChangedDelegate implements BcSmartspaceDataPlugin.TimeChangedDelegate, Runnable {
        public Handler mHandler;
        public Runnable mTimeChangedCallback;

        @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.TimeChangedDelegate
        public final void register(Runnable runnable) {
            if (this.mTimeChangedCallback != null) {
                unregister();
            }
            this.mTimeChangedCallback = runnable;
            run();
        }

        @Override // java.lang.Runnable
        public final void run() {
            Runnable runnable = this.mTimeChangedCallback;
            if (runnable != null) {
                runnable.run();
                if (this.mHandler != null) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    this.mHandler.postAtTime(this, (60000 - (uptimeMillis % 60000)) + uptimeMillis);
                }
            }
        }

        @Override // com.android.systemui.plugins.BcSmartspaceDataPlugin.TimeChangedDelegate
        public final void unregister() {
            this.mHandler.removeCallbacks(this);
            this.mTimeChangedCallback = null;
        }
    }

    public IcuDateTextView(Context context) {
        this(context, null);
    }

    @Override // android.widget.TextView, android.view.View
    public final void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mUpdatesOnAod) {
            try {
                Handler handler = this.mBgHandler;
                if (handler == null) {
                    Log.wtf("IcuDateTextView", "Must set background handler when mUpdatesOnAod is set to avoid making binder calls on main thread");
                    getContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("doze_always_on"), false, this.mAodSettingsObserver, -1);
                } else {
                    handler.post(new IcuDateTextView$$ExternalSyntheticLambda0(this, 3));
                }
            } catch (Exception e) {
                Log.w("IcuDateTextView", "Unable to register DOZE_ALWAYS_ON content observer: ", e);
            }
            Context context = getContext();
            this.mIsAodEnabled = Settings.Secure.getIntForUser(context.getContentResolver(), "doze_always_on", 0, context.getUserId()) == 1;
        }
        this.mHandler = new Handler();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        Handler handler2 = this.mBgHandler;
        if (handler2 == null) {
            Log.w("IcuDateTextView", "mBgHandler is not set! Fallback to make binder calls on main thread.");
            getContext().registerReceiver(this.mIntentReceiver, intentFilter);
        } else {
            handler2.post(new Runnable() { // from class: com.google.android.systemui.smartspace.IcuDateTextView$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    IcuDateTextView icuDateTextView = IcuDateTextView.this;
                    IntentFilter intentFilter2 = intentFilter;
                    int i = IcuDateTextView.$r8$clinit;
                    icuDateTextView.getContext().registerReceiver(icuDateTextView.mIntentReceiver, intentFilter2);
                }
            });
        }
        if (this.mTimeChangedDelegate == null) {
            Handler handler3 = this.mHandler;
            DefaultTimeChangedDelegate defaultTimeChangedDelegate = new DefaultTimeChangedDelegate();
            defaultTimeChangedDelegate.mHandler = handler3;
            this.mTimeChangedDelegate = defaultTimeChangedDelegate;
        }
        this.mIsInteractive = ((PowerManager) ((TextView) this).mContext.getSystemService(PowerManager.class)).isInteractive();
        onTimeChanged(true);
    }

    @Override // android.view.View
    public final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mHandler != null) {
            Handler handler = this.mBgHandler;
            if (handler == null) {
                Log.w("IcuDateTextView", "mBgHandler is not set! Fallback to make binder calls on main thread.");
                getContext().unregisterReceiver(this.mIntentReceiver);
            } else {
                handler.post(new IcuDateTextView$$ExternalSyntheticLambda0(this, 0));
            }
            this.mTimeChangedDelegate.unregister();
            this.mHandler = null;
        }
        if (this.mUpdatesOnAod) {
            Handler handler2 = this.mBgHandler;
            if (handler2 != null) {
                handler2.post(new IcuDateTextView$$ExternalSyntheticLambda0(this, 1));
            } else {
                Log.wtf("IcuDateTextView", "Must set background handler when mUpdatesOnAod is set to avoid making binder calls on main thread");
                getContext().getContentResolver().unregisterContentObserver(this.mAodSettingsObserver);
            }
        }
    }

    public final void onTimeChanged(boolean z) {
        if (this.mFormatter == null || z) {
            DateFormat instanceForSkeleton = DateFormat.getInstanceForSkeleton(getContext().getString(R.string.smartspace_icu_date_pattern), Locale.getDefault());
            this.mFormatter = instanceForSkeleton;
            instanceForSkeleton.setContext(DisplayContext.CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE);
        }
        if (isShown()) {
            String format = this.mFormatter.format(Long.valueOf(System.currentTimeMillis()));
            if (Objects.equals(this.mText, format)) {
                return;
            }
            this.mText = format;
            setText(format);
            setContentDescription(format);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public final void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        rescheduleTicker();
    }

    public final void rescheduleTicker() {
        if (this.mHandler == null) {
            return;
        }
        this.mTimeChangedDelegate.unregister();
        boolean z = this.mUpdatesOnAod && this.mIsAodEnabled;
        if ((this.mIsInteractive || z) && isAggregatedVisible()) {
            this.mTimeChangedDelegate.register(this.mTimeChangedCallback);
        }
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [com.google.android.systemui.smartspace.IcuDateTextView$1] */
    /* JADX WARN: Type inference failed for: r2v2, types: [com.google.android.systemui.smartspace.IcuDateTextView$2] */
    public IcuDateTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        this.mAodSettingsObserver = new ContentObserver(new Handler()) { // from class: com.google.android.systemui.smartspace.IcuDateTextView.1
            @Override // android.database.ContentObserver
            public final void onChange(boolean z) {
                Context context2 = IcuDateTextView.this.getContext();
                boolean z2 = Settings.Secure.getIntForUser(context2.getContentResolver(), "doze_always_on", 0, context2.getUserId()) == 1;
                IcuDateTextView icuDateTextView = IcuDateTextView.this;
                if (icuDateTextView.mIsAodEnabled == z2) {
                    return;
                }
                icuDateTextView.mIsAodEnabled = z2;
                icuDateTextView.rescheduleTicker();
            }
        };
        this.mIntentReceiver = new BroadcastReceiver() { // from class: com.google.android.systemui.smartspace.IcuDateTextView.2
            @Override // android.content.BroadcastReceiver
            public final void onReceive(Context context2, Intent intent) {
                boolean z = "android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction()) || "android.intent.action.TIME_SET".equals(intent.getAction());
                IcuDateTextView icuDateTextView = IcuDateTextView.this;
                int i = IcuDateTextView.$r8$clinit;
                icuDateTextView.onTimeChanged(z);
            }
        };
        this.mTimeChangedCallback = new IcuDateTextView$$ExternalSyntheticLambda0(this, 2);
    }
}
