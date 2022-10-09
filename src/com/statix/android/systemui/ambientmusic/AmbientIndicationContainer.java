package com.google.android.systemui.ambientmusic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
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
import com.android.systemui.animation.Interpolators;
import com.android.systemui.doze.DozeReceiver;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.CentralSurfaces;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import com.android.wm.shell.R;
import java.util.Objects;

public class AmbientIndicationContainer extends AutoReinflateContainer implements DozeReceiver, StatusBarStateController.StateListener, NotificationMediaManager.MediaListener {
    public Drawable mAmbientIconOverride;
    public int mAmbientIndicationBottomPadding;
    public int mIndicationBottomPadding;
    public int mAmbientIndicationIconSize;
    public Drawable mAmbientMusicAnimation;
    public Drawable mAmbientMusicNoteIcon;
    public int mAmbientMusicNoteIconIconSize;
    public CharSequence mAmbientMusicText;
    public boolean mAmbientSkipUnlock;
    public int mBottomMarginPx;
    public CentralSurfaces mCentralSurfaces;
    public boolean mDozing;
    public PendingIntent mFavoritingIntent;
    public final Handler mHandler;
    public final Rect mIconBounds;
    public String mIconDescription;
    public int mIconOverride;
    public ImageView mIconView;
    public int mIndicationTextMode;
    public int mMediaPlaybackState;
    public PendingIntent mOpenIntent;
    public Drawable mReverseChargingAnimation;
    public CharSequence mReverseChargingMessage;
    public int mStatusBarState;
    public int mTextColor;
    public ValueAnimator mTextColorAnimator;
    public TextView mTextView;
    public final WakeLock mWakeLock;
    public CharSequence mWirelessChargingMessage;

    @VisibleForTesting
    public WakeLock createWakeLock(Context context, Handler handler) {
        return new DelayedWakeLock(handler, WakeLock.createPartial(context, "AmbientIndication"));
    }

    @Override
    public final void onDozingChanged(boolean z) {
        mDozing = z;
        if (mStatusBarState == 1) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
        TextView textView = mTextView;
        if (textView != null) {
            textView.setEnabled(!z);
            updateColors();
        }
    }

    @Override
    public final void onPrimaryMetadataOrStateChanged(MediaMetadata mediaMetadata, int i) {
        if (mMediaPlaybackState != i) {
            mMediaPlaybackState = i;
            if (NotificationMediaManager.isPlayingState(i)) {
                setAmbientMusic(null, null, null, 0, false, null);
            }
        }
    }

    @Override
    public final void onStateChanged(int i) {
        mStatusBarState = i;
        if (i == 1) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    public final void onTextClick(View view) {
        if (mOpenIntent != null) {
            mCentralSurfaces.wakeUpIfDozing(SystemClock.uptimeMillis(), view, "AMBIENT_MUSIC_CLICK");
            if (mAmbientSkipUnlock) {
                sendBroadcastWithoutDismissingKeyguard(mOpenIntent);
            } else {
                mCentralSurfaces.startPendingIntentDismissingKeyguard(mOpenIntent);
            }
        }
    }

    public final void setAmbientMusic(CharSequence charSequence, PendingIntent pendingIntent, PendingIntent pendingIntent2, int i, boolean z, String str) {
        Drawable drawable;
        if (Objects.equals(mAmbientMusicText, charSequence) && Objects.equals(mOpenIntent, pendingIntent) && Objects.equals(mFavoritingIntent, pendingIntent2) && mIconOverride == i && Objects.equals(mIconDescription, str) && mAmbientSkipUnlock == z) {
            return;
        }
        mAmbientMusicText = charSequence;
        mOpenIntent = pendingIntent;
        mFavoritingIntent = pendingIntent2;
        mAmbientSkipUnlock = z;
        mIconOverride = i;
        mIconDescription = str;
        Context context = ((FrameLayout) this).getContext();
        switch (i) {
            case 1:
                drawable = context.getDrawable(R.drawable.ic_music_search);
                break;
            case 2:
            default:
                drawable = null;
                break;
            case 3:
                drawable = context.getDrawable(R.drawable.ic_music_not_found);
                break;
            case 4:
                drawable = context.getDrawable(R.drawable.ic_cloud_off);
                break;
            case 5:
                drawable = context.getDrawable(R.drawable.ic_favorite);
                break;
            case FalsingManager.VERSION /* 6 */:
                drawable = context.getDrawable(R.drawable.ic_favorite_border);
                break;
            case 7:
                drawable = context.getDrawable(R.drawable.ic_error);
                break;
            case 8:
                drawable = context.getDrawable(R.drawable.ic_favorite_note);
                break;
        }
        mAmbientIconOverride = drawable;
        updatePill();
    }

    public final void updateColors() {
        int i;
        ValueAnimator valueAnimator = mTextColorAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            mTextColorAnimator.cancel();
        }
        int defaultColor = mTextView.getTextColors().getDefaultColor();
        if (mDozing) {
            i = -1;
        } else {
            i = mTextColor;
        }
        if (defaultColor == i) {
            mTextView.setTextColor(i);
            mIconView.setImageTintList(ColorStateList.valueOf(i));
            return;
        }
        ValueAnimator ofArgb = ValueAnimator.ofArgb(defaultColor, i);
        mTextColorAnimator = ofArgb;
        ofArgb.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        mTextColorAnimator.setDuration(500L);
        mTextColorAnimator.addUpdateListener(  
        new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                    mTextView.setTextColor(intValue);
                    mIconView.setImageTintList(ColorStateList.valueOf(intValue));
            }
        }

        );
        mTextColorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public final void onAnimationEnd(Animator animator) {
                mTextColorAnimator = null;
            }
        });
        mTextColorAnimator.start();
    }

    public final void updatePill() {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        CharSequence charSequence;
        Drawable drawable;
        Drawable drawable2;
        TextView textView;
        int i;
        int i2;
        TextView textView2 = mTextView;
        if (textView2 == null) {
            return;
        }
        int i3 = mIndicationTextMode;
        boolean z5 = true;
        mIndicationTextMode = 1;
        CharSequence charSequence2 = mAmbientMusicText;
        int i4 = 0;
        if (textView2.getVisibility() == 0) {
            z = true;
        } else {
            z = false;
        }
        CharSequence charSequence3 = mAmbientMusicText;
        if (charSequence3 != null && charSequence3.length() == 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        TextView textView3 = mTextView;
        if (mOpenIntent != null) {
            z3 = true;
        } else {
            z3 = false;
        }
        textView3.setClickable(z3);
        ImageView imageView = mIconView;
        if (mFavoritingIntent == null && mOpenIntent == null) {
            z4 = false;
        } else {
            z4 = true;
        }
        imageView.setClickable(z4);
        if (TextUtils.isEmpty(mIconDescription)) {
            charSequence = charSequence2;
        } else {
            charSequence = mIconDescription;
        }
        Drawable drawable3 = null;
        if ((!TextUtils.isEmpty(charSequence2) || z2) && (drawable3 = mAmbientIconOverride) == null) {
            if (z) {
                if (mAmbientMusicNoteIcon == null) {
                    mAmbientMusicNoteIcon = ((FrameLayout) this).getContext().getDrawable(R.drawable.ic_music_note);
                }
                drawable = mAmbientMusicNoteIcon;
            } else {
                if (mAmbientMusicAnimation == null) {
                    mAmbientMusicAnimation = ((FrameLayout) this).getContext().getDrawable(R.anim.audioanim_animation);
                }
                drawable = mAmbientMusicAnimation;
            }
            drawable3 = drawable;
        }
        mTextView.setText(charSequence2);
        mTextView.setContentDescription(charSequence2);
        mIconView.setContentDescription(charSequence);
        if (drawable3 != null) {
            mIconBounds.set(0, 0, drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight());
            Rect rect = mIconBounds;
            if (drawable3 == mAmbientMusicNoteIcon) {
                i = mAmbientMusicNoteIconIconSize;
            } else {
                i = mAmbientIndicationIconSize;
            }
            MathUtils.fitRect(rect, i);
            drawable2 = new DrawableWrapper(drawable3) {
                @Override
                public final int getIntrinsicHeight() {
                    return mIconBounds.height();
                }

                @Override
                public final int getIntrinsicWidth() {
                    return mIconBounds.width();
                }
            };
            if (!TextUtils.isEmpty(charSequence2)) {
                i2 = (int) (getResources().getDisplayMetrics().density * 24.0f);
            } else {
                i2 = 0;
            }
            TextView textView4 = mTextView;
            textView4.setPaddingRelative(textView4.getPaddingStart(), mTextView.getPaddingTop(), i2, mTextView.getPaddingBottom());
        } else {
            TextView textView5 = mTextView;
            textView5.setPaddingRelative(textView5.getPaddingStart(), mTextView.getPaddingTop(), 0, mTextView.getPaddingBottom());
            drawable2 = drawable3;
        }
        mIconView.setImageDrawable(drawable2);
        if (TextUtils.isEmpty(charSequence2) && !z2) {
            z5 = false;
        }
        if (!z5) {
            i4 = 8;
        }
        mTextView.setVisibility(i4);
        if (drawable3 == null) {
            mIconView.setVisibility(8);
        } else {
            mIconView.setVisibility(i4);
        }
        if (z5) {
            if (!z) {
                mWakeLock.acquire("AmbientIndication");
                if (drawable3 != null && (drawable3 instanceof AnimatedVectorDrawable)) {
                    ((AnimatedVectorDrawable) drawable3).start();
                }
                mTextView.setTranslationY(textView.getHeight() / 2);
                mTextView.setAlpha(0.0f);
                mTextView.animate().alpha(1.0f).translationY(0.0f).setStartDelay(150L).setDuration(100L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public final void onAnimationEnd(Animator animator) {
                        mWakeLock.release("AmbientIndication");
                        mTextView.animate().setListener(null);
                    }
                }).setInterpolator(Interpolators.DECELERATE_QUINT).start();
            } else if (i3 != mIndicationTextMode) {
                if (drawable3 != null && (drawable3 instanceof AnimatedVectorDrawable)) {
                    mWakeLock.acquire("AmbientIndication");
                    ((AnimatedVectorDrawable) drawable3).start();
                    mWakeLock.release("AmbientIndication");
                }
            } else {
                mHandler.post(mWakeLock.wrap(new Runnable() {
                    @Override
                    public final void run() {
                    }
                }));
            }
        } else {
            mTextView.animate().cancel();
            if (drawable3 != null && (drawable3 instanceof AnimatedVectorDrawable)) {
                ((AnimatedVectorDrawable) drawable3).reset();
            }
            mHandler.post(mWakeLock.wrap(new Runnable() {
                @Override
                public final void run() {
                }
            }));
        }
        updateBottomSpacing();
    }

    public AmbientIndicationContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mIconBounds = new Rect();
        mIconOverride = -1;
        Handler handler = new Handler(Looper.getMainLooper());
        mHandler = handler;
        mWakeLock = createWakeLock(((FrameLayout) this).getContext(), handler);
    }

    public static void sendBroadcastWithoutDismissingKeyguard(PendingIntent pendingIntent) {
        if (pendingIntent.isActivity()) {
            return;
        }
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.w("AmbientIndication", "Sending intent failed: " + e);
        }
    }

    @Override
    public final void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        ((NotificationMediaManager) Dependency.get(NotificationMediaManager.class)).addCallback(this);
    }

    @Override
    public final void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateBottomSpacing();
    }

    @Override
    public final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).removeCallback(this);
        ((NotificationMediaManager) Dependency.get(NotificationMediaManager.class)).mMediaListeners.remove(this);
        mMediaPlaybackState = 0;
    }

    public final void updateBottomSpacing() {
        boolean z;
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.ambient_indication_margin_bottom);
        if (mBottomMarginPx != dimensionPixelSize) {
            mBottomMarginPx = dimensionPixelSize;
            ((FrameLayout.LayoutParams) getLayoutParams()).bottomMargin = mBottomMarginPx;
        }
        int i = 0;
        if (mTextView.getVisibility() == 0) {
            z = true;
        } else {
            z = false;
        }
        NotificationPanelViewController panelController = mCentralSurfaces.getPanelController();
        int top = getTop();
        if (z) {
            i = panelController.mNotificationStackScrollLayoutController.mView.getBottom() - top;
        }

        float max = f - Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding);
        if (max <= 0.0f) {
            return 0.0f;
        }
        return Math.min(this.mNotificationShelfController.mView.getHeight(), max);

        mIndicationBottomPadding = mResources.getDimensionPixelSize(R.dimen.keyguard_indication_bottom_padding);
        float max = Math.max(f3, Math.max(mIndicationBottomPadding, mAmbientIndicationBottomPadding));
        if (panelController.mAmbientIndicationBottomPadding != i) {
            panelController.mAmbientIndicationBottomPadding = i;
            panelController.updateMaxDisplayedNotifications(true);
        }
    }

    @Override
    public final void dozeTimeTick() {
        updatePill();
    }
}
