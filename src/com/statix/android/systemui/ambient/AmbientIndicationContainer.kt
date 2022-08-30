package com.statix.android.systemui.ambient

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

import com.android.systemui.AutoReinflateContainer
import com.android.systemui.R

class AmbientIndicationContainer(private val context: Context, attrs: AttributeSet) : AutoReinflateContainer(context, attrs), DozeReceiver, StatusBarStateController.StateListener, NotificationMediaManager.MediaListener {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val iconBounds: Rect = Rect()
    private val wakeLock: WakeLock = DelayedWakeLock(handler, WakeLock.createPartial(context, "AmbientIndication"))
    private var ambientIconOverride: Drawable? = null
    private lateinit var ambientIndicationIconSize: Int
    private lateinit var ambientMusicAnimation: Drawable
    private lateinit var ambientMusicNoteIcon: Drawable
    private lateinit var ambientMusicNoteIconSize: Int
    private lateinit var ambientMusicText: String
    private var ambientSkipUnlock: Boolean = false
    private var bottomMarginPx: Int
    private var dozing: Boolean = false
    private lateinit var favoritingIntent: PendingIntent
    private lateinit var iconDescription: String
    private var iconOverride: Int = -1
    private lateinit var iconView: ImageView
    private lateinit var indicationTextMode: Int
    private lateinit var mediaPlaybackState: Int
    private lateinit var openIntent: PendingIntent
    private lateinit var centralSurfaces: CentralSurfaces
    private var centralSurfacesState: Int
    private var textColor: Int
    private var textColorAnimator: ValueAnimator? = null
    private lateinit var textView: TextView

    public fun initializeView(centralSurfaces: CentralSurfaces) {
        this.centralSurfaces = centralSurfaces
        addInflateListener(object : AutoReinflateContainer.InflateListener {
            override fun onInflated(view: View) {
                textView = findViewById<TextView>(R.id.ambient_indication_text)
                iconView = findViewById<ImageView>(R.id.ambient_indication_icon)
                ambientMusicAnimation = context.getDrawable(R.anim.audioanim_animation)
                ambientMusicNoteIcon = context.getDrawable(R.drawable.ic_music_note)
                textColor = textView.currentTextColor
                ambientIndicationIconSize = resources.getDimensionPixelSize(R.dimen.ambient.indication_icon_size)
                ambientMusicNoteIconSize = resources.getDimensionPixelSize(R.dimen.ambient_indication_note_icon_size)
                textView.enabled = !dozing
                updateColors()
                updatePill()
                textView.setOnClickListener({v -> onTextClick(v)})
                iconView.setOnClickListener({v -> onIconClick(v)})
            }
        })
        addOnLayoutChangeListener({
            v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom -> updateBottomSpacing()
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (Dependency.get(StatusBarStateController::class.java) as StatusBarStateController).addCallback(this)
        (Dependency.get(NotificationMediaManager::class.java) as NotificationMediaManager).addCallback(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (Dependency.get(StatusBarStateController::class.java) as StatusBarStateController).removeCallback(this)
        (Dependency.get(NotificationMediaManager::class.java) as NotificationMediaManager).removeCallback(this)
    }

    fun setAmbientMusic(text: String, openIntent: PendingIntent, favoriteIntent: PendingIntent, skipUnlock: boolean, iconOverride: Int, iconDescription: String) {
        if (this.ambientMusicText != text || this.openIntent != openIntent || this.favoritingIntent != favoriteIntent || this.iconOverride != iconOverride || this.ambientSkipUnlock != skipUnlock || this.iconDescription != iconDescription) {
            this.ambientMusicText = text
            this.openIntent = openIntent
            this.favoritingIntent = favoriteIntent
            this.ambientSkipUnlock = skipUnlock
            this.iconOverride = iconOverride
            this.iconDescription = iconDescription
            this.ambientIconOverride = getAmbientIconOverride(iconOverride)
            updatePill()
        }
    }

    private fun getAmbientIconOverride(iconOverride: Int): Drawable? {
        when(iconOverride) {
            1 -> context.getDrawable(R.drawable.ic_music_search)
            2 -> null
            3 -> context.getDrawable(R.drawable.ic_music_not_found)
            4 -> context.getDrawable(R.drawable.ic_cloud_off)
            5 -> context.getDrawable(R.drawable.ic_favorite)
            6 -> context.getDrawable(R.drawable.ic_favorite_border)
            7 -> context.getDrawable(R.drawable.ic_error)
            8 -> context.getDrawable(R.drawable.ic_favorite_note)
            else -> null
        }
    }

    private fun updatePill() {
        val oldIndicationTextMode = this.indicationTextMode;
        boolean updatePill = true;
        mIndicationTextMode = 1;
        CharSequence text = mAmbientMusicText;
        boolean textVisible = mTextView.getVisibility() == View.VISIBLE;
        Drawable icon = textVisible ? mAmbientMusicNoteIcon : mAmbientMusicAnimation;
        if (mAmbientIconOverride != null) {
            icon = mAmbientIconOverride;
        }
        boolean showAmbientMusicText = mAmbientMusicText != null && mAmbientMusicText.length() == 0;
        mTextView.setClickable(mOpenIntent != null);
        mIconView.setClickable(mFavoritingIntent != null || mOpenIntent != null);
        CharSequence iconDescription = TextUtils.isEmpty(mIconDescription) ? text : mIconDescription;
        if (!TextUtils.isEmpty(mReverseChargingMessage)) {
            mIndicationTextMode = 2;
            text = mReverseChargingMessage;
            icon = mReverseChargingAnimation;
            mTextView.setClickable(false);
            mIconView.setClickable(false);
            showAmbientMusicText = false;
            iconDescription = null;
        } else if (!TextUtils.isEmpty(mWirelessChargingMessage)) {
            mIndicationTextMode = 3;
            text = mWirelessChargingMessage;
            mTextView.setClickable(false);
            mIconView.setClickable(false);
            showAmbientMusicText = false;
            icon = null;
            iconDescription = null;
        }
        mTextView.setText(text);
        mTextView.setContentDescription(text);
        mIconView.setContentDescription(iconDescription);
        Drawable drawableWrapper = null;
        if (icon != null) {
            mIconBounds.set(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            MathUtils.fitRect(mIconBounds, icon == mAmbientMusicNoteIcon ? mAmbientMusicNoteIconIconSize : mAmbientIndicationIconSize);
            drawableWrapper = new DrawableWrapper(icon) {
                @Override
                public int getIntrinsicWidth() {
                    return mIconBounds.width();
                }

                @Override
                public int getIntrinsicHeight() {
                    return mIconBounds.height();
                }
            };
            int i3 = !TextUtils.isEmpty(text) ? (int) (getResources().getDisplayMetrics().density * 24.0f) : 0;
            mTextView.setPaddingRelative(mTextView.getPaddingStart(), mTextView.getPaddingTop(), i3, mTextView.getPaddingBottom());
        } else {
            mTextView.setPaddingRelative(mTextView.getPaddingStart(), mTextView.getPaddingTop(), 0, mTextView.getPaddingBottom());
        }
        mIconView.setImageDrawable(drawableWrapper);
        if ((TextUtils.isEmpty(text) && !showAmbientMusicText)) {
            updatePill = false;
        }
        int vis = View.VISIBLE;
        if (!updatePill) {
            vis = View.GONE;
        }
        mTextView.setVisibility(vis);
        if (icon == null) {
            mIconView.setVisibility(View.GONE);
        } else {
            mIconView.setVisibility(vis);
        }
        if (!updatePill) {
            mTextView.animate().cancel();
            if (icon instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable) icon).reset();
            }
            mHandler.post(mWakeLock.wrap(() -> {}));
        } else if (!textVisible) {
            mWakeLock.acquire("AmbientIndication");
            if (icon instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable) icon).start();
            }
            mTextView.setTranslationY(mTextView.getHeight() / 2);
            mTextView.setAlpha(0.0f);
            mTextView.animate().alpha(1.0f).translationY(0.0f).setStartDelay(150L).setDuration(100L).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    mWakeLock.release("AmbientIndication");
                    mTextView.animate().setListener(null);
                }
            }).setInterpolator(Interpolators.DECELERATE_QUINT).start();
        } else if (indicationTextMode != mIndicationTextMode) {
            if (icon instanceof AnimatedVectorDrawable) {
                mWakeLock.acquire("AmbientIndication");
                ((AnimatedVectorDrawable) icon).start();
                mWakeLock.release("AmbientIndication");
            }
        } else {
            mHandler.post(mWakeLock.wrap(() -> {}));
        }
        updateBottomSpacing();
    }

    private fun updateBottomSpacing() {
        val marginBottom = resources.getDimensionPixelSize(R.dimen.ambient_indication_margin_bottom)
        if (bottomMarginPx != marginBottom) {
            bottomMarginPx = marginBottom
            layoutParams.bottomMargin = bottomMarginPx
        }
        centralSurfaces.panelController.setAmbientIndicationTop(top, textView.visibility == View.VISIBLE)
    }

    public fun hideAmbientMusic() {
        setAmbientMusic(null, null, null, 0, false, null)
    }

    fun onTextClick(view: View) {
        openIntent?.let {
            centralSurfaces.wakeUpIfDozing(SystemClock.uptimeMillis(), view, "AMBIENT_MUSIC_CLICK")
            if (ambientSkipUnlock) {
                sendBroadcastWithoutDismissingKeyguard(it)
            } else {
                centralSurfaces.startPendingIntentDismissingKeyguard(openIntent)
            }
        }
    }

    fun onIconClick(view: View) {
        favoriteIntent?.let {
            centralSurfaces.wakeUpIfDozing(SystemClock.uptimeMillis(), view, "AMBIENT_MUSIC_CLICK")
            sendBroadcastWithoutDismissingKeyguard(it)
            return
        }
        onTextClick(view)
    }

    override fun onDozingChanged(isDozing: Boolean) {
        dozing = isDozing
        updateVisibility()
        textView?.let {
            it.enabled = !isDozing
            updateColors()
        }
    }

    override fun dozeTimeTick() = updatePill()

    fun updateColors() {
        textAnimator?.let {
            if (it.isRunning()) {
                it.cancel()
            }
        }
        val defaultColor = textView.textColors.defaultColor
        val dozeColor = if (dozing) { -1 } else { textColor }
        if (dozeColor == defaultColor) {
            textView.color = dozeColor
            textView.imageTintList = ColorStateList.valueOf(dozeColor)
        }
        textColorAnimator = ValueAnimator.ofArgb(defaultColor, dozeColor)
        textColorAnimator.interpolator = Interpolators.LINEAR_OUT_SLOW_IN
        textColorAnimator.duration = 500L
        textColorAnimator.addUpdateListener({_ -> 
            textView.textColor = textColorAnimator!!.animatedValue
            textView.imageTintList = ColorStateList.valueOf(textColorAnimator!!.animatedValue)
        })
        textColorAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                textColorAnimator = null
            }
        })
        textColorAnimator.start()
    }

    override fun onStateChanged(state: Int) {
        centralSurfacesState = state
        updateVisibility()
    }

    private fun sendBroadcastWithoutDismissingKeyguard(pendingIntent: PendingIntent) {
        if (pendingIntent.isActivity()) {
            return
        }
        try {
            pendingIntent.send()
        } catch (PendingIntent.CanceledException e) {
            Log.w("AmbientIndication", "Sending intent failed: " + e)
        }
    }

    private fun updateVisibility() {
        if (centralSurfacesState == 1) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.INVISIBLE;
        }
    }

    override fun onPrimaryMetadataOrStateChanged(mediaMetadata: MediaMetadata, mediaState: Int) {
        if (mediaPlaybackState != mediaState) {
            mediaPlaybackState = mediaState
            if (!isMediaPlaying()) {
                return
            }
            hideAmbientMusic()
        }
    }

    private fun isMediaPlaying(): Boolean {
        return NotificationMediaManager.isPlayingState(mediaPlaybackState)
    }

    public fun setReverseChargingMessage(str: String, visible: Boolean) {
        if (TextUtils.isEmpty(str)) {
            return
        }
        textView.let {
            it.text = str
            it.visibility = if (visible) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
