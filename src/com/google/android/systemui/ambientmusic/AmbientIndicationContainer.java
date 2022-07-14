package com.google.android.systemui.ambientmusic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.media.MediaMetadata;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.AutoReinflateContainer;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.doze.DozeReceiver;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import java.util.Objects;
/* loaded from: classes2.dex */
public class AmbientIndicationContainer extends AutoReinflateContainer implements DozeReceiver, StatusBarStateController.StateListener, NotificationMediaManager.MediaListener {
    private Drawable mAmbientIconOverride;
    private int mAmbientIndicationIconSize;
    private Drawable mAmbientMusicAnimation;
    private Drawable mAmbientMusicNoteIcon;
    private int mAmbientMusicNoteIconIconSize;
    private CharSequence mAmbientMusicText;
    private boolean mAmbientSkipUnlock;
    private int mBottomMarginPx;
    private int mBurnInPreventionOffset;
    private boolean mDozing;
    private PendingIntent mFavoritingIntent;
    private final Handler mHandler;
    private String mIconDescription;
    private ImageView mIconView;
    private int mIndicationTextMode;
    private int mMediaPlaybackState;
    private boolean mNotificationsHidden;
    private PendingIntent mOpenIntent;
    private Drawable mReverseChargingAnimation;
    private CharSequence mReverseChargingMessage;
    private StatusBar mStatusBar;
    private int mStatusBarState;
    private int mTextColor;
    private ValueAnimator mTextColorAnimator;
    private TextView mTextView;
    private final WakeLock mWakeLock;
    private CharSequence mWirelessChargingMessage;
    private final Rect mIconBounds = new Rect();
    private int mIconOverride = -1;

    public AmbientIndicationContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Handler handler = new Handler(Looper.getMainLooper());
        mHandler = handler;
        mWakeLock = createWakeLock(mContext, handler);
    }

    @VisibleForTesting
    WakeLock createWakeLock(Context context, Handler handler) {
        return new DelayedWakeLock(handler, WakeLock.createPartial(context, "AmbientIndication"));
    }

    public void initializeView(StatusBar statusBar) {
        mStatusBar = statusBar;
        addInflateListener(new AutoReinflateContainer.InflateListener() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationContainer$$ExternalSyntheticLambda4
            @Override // com.android.systemui.AutoReinflateContainer.InflateListener
            public final void onInflated(View view) {
                mTextView = (TextView) findViewById(R.id.ambient_indication_text);
                mIconView = (ImageView) findViewById(R.id.ambient_indication_icon);
                mAmbientMusicAnimation = mContext.getDrawable(R.anim.audioanim_animation);
                mAmbientMusicNoteIcon = mContext.getDrawable(R.drawable.ic_music_note);
                mReverseChargingAnimation = mContext.getDrawable(R.anim.reverse_charging_animation);
                mTextColor = mTextView.getCurrentTextColor();
                mAmbientIndicationIconSize = getResources().getDimensionPixelSize(R.dimen.ambient_indication_icon_size);
                mAmbientMusicNoteIconIconSize = getResources().getDimensionPixelSize(R.dimen.ambient_indication_note_icon_size);
                mBurnInPreventionOffset = getResources().getDimensionPixelSize(R.dimen.default_burn_in_prevention_offset);
                mTextView.setEnabled(!mDozing);
                updateColors();
                updatePill();
                mTextView.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationContainer$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        onTextClick(view2);
                    }
                });
                mIconView.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationContainer$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        onIconClick(view2);
                    }
                });
            }
        });
        addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationContainer$$ExternalSyntheticLambda3
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                updateBottomSpacing();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.AutoReinflateContainer, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        ((NotificationMediaManager) Dependency.get(NotificationMediaManager.class)).addCallback(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.AutoReinflateContainer, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).removeCallback(this);
        ((NotificationMediaManager) Dependency.get(NotificationMediaManager.class)).removeCallback(this);
        mMediaPlaybackState = 0;
    }

    public void setAmbientMusic(CharSequence charSequence, PendingIntent pendingIntent, PendingIntent pendingIntent2, int i, boolean z, String str) {
        if (!mAmbientMusicText.equals(charSequence) || !mOpenIntent.equals(pendingIntent) || !mFavoritingIntent.equals(pendingIntent2) || mIconOverride != i || !mIconDescription.equals(str) || mAmbientSkipUnlock != z) {
            mAmbientMusicText = charSequence;
            mOpenIntent = pendingIntent;
            mFavoritingIntent = pendingIntent2;
            mAmbientSkipUnlock = z;
            mIconOverride = i;
            mIconDescription = str;
            mAmbientIconOverride = getAmbientIconOverride(i, mContext);
            updatePill();
        }
    }

    private Drawable getAmbientIconOverride(int i, Context context) {
        switch (i) {
            case 1:
                return context.getDrawable(R.drawable.ic_music_search);
            case 2:
            default:
                return null;
            case 3:
                return context.getDrawable(R.drawable.ic_music_not_found);
            case 4:
                return context.getDrawable(R.drawable.ic_cloud_off);
            case 5:
                return context.getDrawable(R.drawable.ic_favorite);
            case 6:
                return context.getDrawable(R.drawable.ic_favorite_border);
            case 7:
                return context.getDrawable(R.drawable.ic_error);
            case 8:
                return context.getDrawable(R.drawable.ic_favorite_note);
        }
    }

    public void setWirelessChargingMessage(CharSequence charSequence) {
        if (!Objects.equals(mWirelessChargingMessage, charSequence) || mReverseChargingMessage != null) {
            mWirelessChargingMessage = charSequence;
            mReverseChargingMessage = null;
            updatePill();
        }
    }

    public void setReverseChargingMessage(CharSequence charSequence) {
        if (!Objects.equals(mReverseChargingMessage, charSequence) || mWirelessChargingMessage != null) {
            mWirelessChargingMessage = null;
            mReverseChargingMessage = charSequence;
            updatePill();
        }
    }

    private void updatePill() {
        Drawable drawable;
        int i = mIndicationTextMode;
        boolean z = true;
        mIndicationTextMode = 1;
        CharSequence charSequence = mAmbientMusicText;
        int i2 = 0;
        boolean z2 = mTextView.getVisibility() == 0;
        Drawable drawable2 = z2 ? mAmbientMusicNoteIcon : mAmbientMusicAnimation;
        Drawable drawable3 = mAmbientIconOverride;
        if (drawable3 != null) {
            drawable2 = drawable3;
        }
        CharSequence charSequence2 = mAmbientMusicText;
        boolean z3 = charSequence2 != null && charSequence2.length() == 0;
        mTextView.setClickable(mOpenIntent != null);
        mIconView.setClickable((mFavoritingIntent == null && mOpenIntent == null) ? false : true);
        CharSequence charSequence3 = TextUtils.isEmpty(mIconDescription) ? charSequence : mIconDescription;
        if (!TextUtils.isEmpty(mReverseChargingMessage)) {
            mIndicationTextMode = 2;
            charSequence = mReverseChargingMessage;
            drawable2 = mReverseChargingAnimation;
            mTextView.setClickable(false);
            mIconView.setClickable(false);
            z3 = false;
            charSequence3 = null;
        } else if (!TextUtils.isEmpty(mWirelessChargingMessage)) {
            mIndicationTextMode = 3;
            charSequence = mWirelessChargingMessage;
            mTextView.setClickable(false);
            mIconView.setClickable(false);
            z3 = false;
            drawable2 = null;
            charSequence3 = null;
        }
        mTextView.setText(charSequence);
        mTextView.setContentDescription(charSequence);
        mIconView.setContentDescription(charSequence3);
        if (drawable2 != null) {
            mIconBounds.set(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
            MathUtils.fitRect(mIconBounds, drawable2 == mAmbientMusicNoteIcon ? mAmbientMusicNoteIconIconSize : mAmbientIndicationIconSize);
            drawable = new DrawableWrapper(drawable2) { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationContainer.1
                @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
                public int getIntrinsicWidth() {
                    return mIconBounds.width();
                }

                @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
                public int getIntrinsicHeight() {
                    return mIconBounds.height();
                }
            };
            int i3 = !TextUtils.isEmpty(charSequence) ? (int) (getResources().getDisplayMetrics().density * 24.0f) : 0;
            TextView textView2 = mTextView;
            textView2.setPaddingRelative(textView2.getPaddingStart(), mTextView.getPaddingTop(), i3, mTextView.getPaddingBottom());
        } else {
            TextView textView3 = mTextView;
            textView3.setPaddingRelative(textView3.getPaddingStart(), mTextView.getPaddingTop(), 0, mTextView.getPaddingBottom());
            drawable = drawable2;
        }
        mIconView.setImageDrawable(drawable);
        if ((TextUtils.isEmpty(charSequence) && !z3) || mNotificationsHidden) {
            z = false;
        }
        if (!z) {
            i2 = 8;
        }
        mTextView.setVisibility(i2);
        if (drawable2 == null) {
            mIconView.setVisibility(8);
        } else {
            mIconView.setVisibility(i2);
        }
        if (!z) {
            mTextView.animate().cancel();
            if (drawable2 != null && (drawable2 instanceof AnimatedVectorDrawable)) {
                ((AnimatedVectorDrawable) drawable2).reset();
            }
            mHandler.post(mWakeLock.wrap(new Runnable() {
                @Override
                public void run() {}
            }));
        } else if (!z2) {
            mWakeLock.acquire("AmbientIndication");
            if (drawable2 != null && (drawable2 instanceof AnimatedVectorDrawable)) {
                ((AnimatedVectorDrawable) drawable2).start();
            }
            mTextView.setTranslationY(mTextView.getHeight() / 2);
            mTextView.setAlpha(0.0f);
            mTextView.animate().alpha(1.0f).translationY(0.0f).setStartDelay(150L).setDuration(100L).setListener(new AnimatorListenerAdapter() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationContainer.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    mWakeLock.release("AmbientIndication");
                    mTextView.animate().setListener(null);
                }
            }).setInterpolator(Interpolators.DECELERATE_QUINT).start();
        } else if (i != mIndicationTextMode) {
            if (drawable2 != null && (drawable2 instanceof AnimatedVectorDrawable)) {
                mWakeLock.acquire("AmbientIndication");
                ((AnimatedVectorDrawable) drawable2).start();
                mWakeLock.release("AmbientIndication");
            }
        } else {
            mHandler.post(mWakeLock.wrap(new Runnable() {
                @Override
                public void run() {}
            }));
        }
        updateBottomSpacing();
    }

    private void updateBottomSpacing() {
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.ambient_indication_margin_bottom);
        if (mBottomMarginPx != dimensionPixelSize) {
            mBottomMarginPx = dimensionPixelSize;
            ((FrameLayout.LayoutParams) getLayoutParams()).bottomMargin = mBottomMarginPx;
        }
        mStatusBar.getPanelController().setAmbientIndicationTop(getTop(), mTextView.getVisibility() == 0);
    }

    public void hideAmbientMusic() {
        setAmbientMusic(null, null, null, 0, false, null);
    }

    public void onTextClick(View view) {
        if (mOpenIntent != null) {
            mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), view, "AMBIENT_MUSIC_CLICK");
            if (mAmbientSkipUnlock) {
                sendBroadcastWithoutDismissingKeyguard(mOpenIntent);
            } else {
                mStatusBar.startPendingIntentDismissingKeyguard(mOpenIntent);
            }
        }
    }

    public void onIconClick(View view) {
        if (mFavoritingIntent != null) {
            mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), view, "AMBIENT_MUSIC_CLICK");
            sendBroadcastWithoutDismissingKeyguard(mFavoritingIntent);
            return;
        }
        onTextClick(view);
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean z) {
        mDozing = z;
        updateVisibility();
        TextView textView = mTextView;
        if (textView != null) {
            textView.setEnabled(!z);
            updateColors();
        }
    }

    @Override // com.android.systemui.doze.DozeReceiver
    public void dozeTimeTick() {
        updatePill();
    }

    private void updateColors() {
        ValueAnimator valueAnimator = mTextColorAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            mTextColorAnimator.cancel();
        }
        int defaultColor = mTextView.getTextColors().getDefaultColor();
        int i = mDozing ? -1 : mTextColor;
        if (defaultColor == i) {
            mTextView.setTextColor(i);
            mIconView.setImageTintList(ColorStateList.valueOf(i));
            return;
        }
        ValueAnimator ofArgb = ValueAnimator.ofArgb(defaultColor, i);
        mTextColorAnimator = ofArgb;
        ofArgb.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        mTextColorAnimator.setDuration(500L);
        mTextColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationContainer$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                mTextView.setTextColor(intValue);
                mIconView.setImageTintList(ColorStateList.valueOf(intValue));
            }
        });
        mTextColorAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.systemui.ambientmusic.AmbientIndicationContainer.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                mTextColorAnimator = null;
            }
        });
        mTextColorAnimator.start();
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int i) {
        mStatusBarState = i;
        updateVisibility();
    }

    private void sendBroadcastWithoutDismissingKeyguard(PendingIntent pendingIntent) {
        if (pendingIntent.isActivity()) {
            return;
        }
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.w("AmbientIndication", "Sending intent failed: " + e);
        }
    }

    private void updateVisibility() {
        if (mStatusBarState == 1) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    @Override // com.android.systemui.statusbar.NotificationMediaManager.MediaListener
    public void onPrimaryMetadataOrStateChanged(MediaMetadata mediaMetadata, int i) {
        if (mMediaPlaybackState != i) {
            mMediaPlaybackState = i;
            if (!isMediaPlaying()) {
                return;
            }
            hideAmbientMusic();
        }
    }

    protected boolean isMediaPlaying() {
        return NotificationMediaManager.isPlayingState(mMediaPlaybackState);
    }
}
