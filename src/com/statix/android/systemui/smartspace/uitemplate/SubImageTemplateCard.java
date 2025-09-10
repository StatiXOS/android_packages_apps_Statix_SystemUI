package com.google.android.systemui.smartspace.uitemplate;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.uitemplatedata.Icon;
import android.app.smartspace.uitemplatedata.SubImageTemplateData;
import android.app.smartspace.uitemplatedata.TapAction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.google.android.systemui.smartspace.BcSmartSpaceUtil;
import com.google.android.systemui.smartspace.BcSmartspaceCardSecondary;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.statix.android.systemui.res.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SubImageTemplateCard extends BcSmartspaceCardSecondary {
    public final Handler mHandler;
    public final Map<String, Drawable> mIconDrawableCache;
    public final int mImageHeight;
    public ImageView mImageView;

    public SubImageTemplateCard(Context context) {
        this(context, null);
    }

    public SubImageTemplateCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIconDrawableCache = new HashMap<>();
        mHandler = new Handler();
        mImageHeight =
                getResources().getDimensionPixelOffset(R.dimen.enhanced_smartspace_card_height);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImageView = findViewById(R.id.image_view);
    }

    @Override
    public void resetUi() {
        if (mIconDrawableCache != null) {
            mIconDrawableCache.clear();
        }
        if (mImageView != null) {
            mImageView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mImageView.setImageDrawable(null);
            mImageView.setBackgroundTintList(null);
        }
    }

    @Override
    public boolean setSmartspaceActions(
            SmartspaceTarget target,
            BcSmartspaceDataPlugin.SmartspaceEventNotifier eventNotifier,
            BcSmartspaceCardLoggingInfo loggingInfo) {
        SubImageTemplateData templateData = (SubImageTemplateData) target.getTemplateData();
        if (!BcSmartspaceCardLoggerUtil.containsValidTemplateType(templateData)
                || templateData.getSubImages() == null
                || templateData.getSubImages().isEmpty()) {
            Log.w(
                    "SubImageTemplateCard",
                    "SubImageTemplateData is null or has no SubImage or invalid template type");
            return false;
        }

        List<Icon> subImages = templateData.getSubImages();
        TapAction tapAction = templateData.getSubImageAction();

        if (mImageView != null && tapAction != null) {
            Bundle extras = tapAction.getExtras();
            if (extras != null) {
                String dimensionRatio = extras.getString("imageDimensionRatio", "");
                if (!TextUtils.isEmpty(dimensionRatio)) {
                    mImageView.getLayoutParams().width = 0;
                    ((ConstraintLayout.LayoutParams) mImageView.getLayoutParams()).dimensionRatio =
                            dimensionRatio;
                }
                if (extras.getBoolean("shouldShowBackground", false)) {
                    mImageView.setBackgroundTintList(
                            ColorStateList.valueOf(
                                    getContext().getColor(R.color.smartspace_button_background)));
                }
            }
        }

        int frameDurationMillis = 200;
        if (tapAction != null && tapAction.getExtras() != null) {
            frameDurationMillis = tapAction.getExtras().getInt("GifFrameDurationMillis", 200);
        }

        ContentResolver contentResolver = getContext().getApplicationContext().getContentResolver();
        Map<Integer, Drawable> frameMap = new TreeMap<>();
        WeakReference<ImageView> imageViewRef = new WeakReference<>(mImageView);
        String prevTargetId = mPrevSmartspaceTargetId;

        for (int i = 0; i < subImages.size(); i++) {
            Icon subImage = subImages.get(i);
            if (subImage == null || subImage.getIcon() == null) {
                continue;
            }

            android.graphics.drawable.Icon icon = subImage.getIcon();
            StringBuilder keyBuilder = new StringBuilder(String.valueOf(icon.getType()));
            String cacheKey;
            switch (icon.getType()) {
                case android.graphics.drawable.Icon.TYPE_BITMAP:
                case android.graphics.drawable.Icon.TYPE_ADAPTIVE_BITMAP:
                    keyBuilder.append(icon.getBitmap().hashCode());
                    cacheKey = keyBuilder.toString();
                    break;
                case android.graphics.drawable.Icon.TYPE_RESOURCE:
                    keyBuilder
                            .append(icon.getResPackage())
                            .append(String.format("0x%08x", icon.getResId()));
                    cacheKey = keyBuilder.toString();
                    break;
                case android.graphics.drawable.Icon.TYPE_DATA:
                    keyBuilder.append(Arrays.hashCode(icon.getDataBytes()));
                    cacheKey = keyBuilder.toString();
                    break;
                case android.graphics.drawable.Icon.TYPE_URI:
                case android.graphics.drawable.Icon.TYPE_URI_ADAPTIVE_BITMAP:
                    keyBuilder.append(icon.getUriString());
                    cacheKey = keyBuilder.toString();
                    break;
                default:
                    cacheKey = keyBuilder.toString();
                    break;
            }

            OnDrawableLoadedListener listener =
                    new OnDrawableLoadedListener(
                            this,
                            prevTargetId,
                            cacheKey,
                            mIconDrawableCache,
                            i,
                            subImages,
                            frameDurationMillis,
                            imageViewRef);

            if (mIconDrawableCache.containsKey(cacheKey)
                    && mIconDrawableCache.get(cacheKey) != null) {
                listener.onDrawableLoaded(mIconDrawableCache.get(cacheKey));
            } else if (icon.getType() == android.graphics.drawable.Icon.TYPE_URI
                    || icon.getType() == android.graphics.drawable.Icon.TYPE_URI_ADAPTIVE_BITMAP) {
                DrawableWrapper wrapper =
                        new DrawableWrapper(icon.getUri(), contentResolver, mImageHeight, listener);
                new LoadUriTask().execute(wrapper);
            } else {
                icon.loadDrawableAsync(getContext(), listener, mHandler);
            }
        }

        if (tapAction != null) {
            BcSmartSpaceUtil.setOnClickListener(
                    this, target, tapAction, eventNotifier, "SubImageTemplateCard", loggingInfo, 0);
        }

        return true;
    }

    @Override
    public void setTextColor(int color) {
        // No-op
    }

    public static class OnDrawableLoadedListener
            implements android.graphics.drawable.Icon.OnDrawableLoadedListener {
        public final SubImageTemplateCard card;
        public final String prevTargetId;
        public final String cacheKey;
        public final Map<String, Drawable> iconDrawableCache;
        public final int index;
        public final List<Icon> subImages;
        public final int frameDurationMillis;
        public final WeakReference<ImageView> imageViewRef;

        public OnDrawableLoadedListener(
                SubImageTemplateCard card,
                String prevTargetId,
                String cacheKey,
                Map<String, Drawable> iconDrawableCache,
                int index,
                List<Icon> subImages,
                int frameDurationMillis,
                WeakReference<ImageView> imageViewRef) {
            this.card = card;
            this.prevTargetId = prevTargetId;
            this.cacheKey = cacheKey;
            this.iconDrawableCache = iconDrawableCache;
            this.index = index;
            this.subImages = subImages;
            this.frameDurationMillis = frameDurationMillis;
            this.imageViewRef = imageViewRef;
        }

        @Override
        public void onDrawableLoaded(Drawable drawable) {
            if (!prevTargetId.equals(card.mPrevSmartspaceTargetId)) {
                Log.d(
                        "SubImageTemplateCard",
                        "SmartspaceTarget has changed. Skip the loaded result...");
                return;
            }

            iconDrawableCache.put(cacheKey, drawable);
            iconDrawableCache.put(String.valueOf(index), drawable);

            if (iconDrawableCache.size() == subImages.size()) {
                AnimationDrawable animationDrawable = new AnimationDrawable();
                List<Drawable> drawables =
                        iconDrawableCache.values().stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                if (drawables.isEmpty()) {
                    Log.w("SubImageTemplateCard", "All images are failed to load. Reset imageView");
                    if (card.mImageView != null) {
                        card.mImageView.getLayoutParams().width =
                                ViewGroup.LayoutParams.WRAP_CONTENT;
                        card.mImageView.setImageDrawable(null);
                        card.mImageView.setBackgroundTintList(null);
                    }
                    return;
                }

                drawables.forEach(d -> animationDrawable.addFrame(d, frameDurationMillis));
                ImageView imageView = imageViewRef.get();
                imageView.setImageDrawable(animationDrawable);
                int intrinsicWidth = animationDrawable.getIntrinsicWidth();
                if (imageView.getLayoutParams().width != intrinsicWidth) {
                    Log.d("SubImageTemplateCard", "imageView requestLayout");
                    imageView.getLayoutParams().width = intrinsicWidth;
                    imageView.requestLayout();
                }
                animationDrawable.start();
            }
        }
    }

    public static class DrawableWrapper {
        public final Uri mUri;
        public final ContentResolver mContentResolver;
        public final int mHeightInPx;
        public final OnDrawableLoadedListener mListener;
        public Drawable mDrawable;

        public DrawableWrapper(
                Uri uri,
                ContentResolver contentResolver,
                int heightInPx,
                OnDrawableLoadedListener listener) {
            mUri = uri;
            mContentResolver = contentResolver;
            mHeightInPx = heightInPx;
            mListener = listener;
        }
    }

    public static class LoadUriTask extends AsyncTask<DrawableWrapper, Void, DrawableWrapper> {
        @Override
        protected DrawableWrapper doInBackground(DrawableWrapper... wrappers) {
            if (wrappers.length == 0) {
                return null;
            }
            DrawableWrapper wrapper = wrappers[0];
            try {
                InputStream inputStream = wrapper.mContentResolver.openInputStream(wrapper.mUri);
                ImageDecoder.Source source = ImageDecoder.createSource(null, inputStream);
                ImageDecoder.decodeDrawable(
                        source,
                        (decoder, info, src) -> {
                            decoder.setAllocator(ImageDecoder.ALLOCATOR_HARDWARE);
                            float aspectRatio =
                                    info.getSize().getHeight() != 0
                                            ? (float) info.getSize().getWidth()
                                                    / info.getSize().getHeight()
                                            : 0;
                            int targetWidth = (int) (wrapper.mHeightInPx * aspectRatio);
                            decoder.setTargetSize(targetWidth, wrapper.mHeightInPx);
                        });
                wrapper.mDrawable = ImageDecoder.decodeDrawable(source);
            } catch (IOException e) {
                Log.e("SubImageTemplateCard", "Unable to decode stream: " + e);
            } catch (Exception e) {
                Log.w("SubImageTemplateCard", "open uri:" + wrapper.mUri + " got exception:" + e);
            }
            return wrapper;
        }

        @Override
        protected void onPostExecute(DrawableWrapper wrapper) {
            wrapper.mListener.onDrawableLoaded(wrapper.mDrawable);
        }
    }
}
