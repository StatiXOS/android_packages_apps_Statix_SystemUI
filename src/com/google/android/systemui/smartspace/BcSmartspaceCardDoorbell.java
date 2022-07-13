package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.android.launcher3.icons.RoundDrawableWrapper;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.BcSmartspaceCardDoorbell;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class BcSmartspaceCardDoorbell extends BcSmartspaceCardGenericImage {
    private ImageView mLoadingIcon;
    private CardView mLoadingScreenView;
    private ProgressBar mProgressBar;
    private final Map<Uri, DrawableWithUri> mUriToDrawable = new HashMap();
    private int mGifFrameDurationInMs = 200;

    public BcSmartspaceCardDoorbell(Context context) {
        super(context);
    }

    public BcSmartspaceCardDoorbell(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        if (!isSysUiContext()) {
            return false;
        }
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        Bundle extras = baseAction == null ? null : baseAction.getExtras();
        List<Uri> imageUris = getImageUris(smartspaceTarget);
        if (!imageUris.isEmpty()) {
            if (extras != null && extras.containsKey("frameDurationMs")) {
                this.mGifFrameDurationInMs = extras.getInt("frameDurationMs");
            }
            loadImageUris(imageUris);
            hideLoadingState(extras);
            Log.d("BcSmartspaceCardBell", "imageUri is set");
            return true;
        } else if (extras != null && extras.containsKey("imageBitmap")) {
            setRoundedBitmapDrawable((Bitmap) extras.get("imageBitmap"));
            hideLoadingState(extras);
            Log.d("BcSmartspaceCardBell", "imageBitmap is set");
            return true;
        } else if (extras == null || !extras.containsKey("loadingScreenState")) {
            return false;
        } else {
            int i = extras.getInt("loadingScreenState");
            String dimensionRatio = BcSmartSpaceUtil.getDimensionRatio(extras);
            if (dimensionRatio == null) {
                return false;
            }
            showLoadingScreen(dimensionRatio, i);
            return true;
        }
    }

    private void hideLoadingState(Bundle bundle) {
        if (bundle == null || bundle.getBoolean("hideLoadingScreen")) {
            this.mLoadingScreenView.setVisibility(8);
        }
        if (this.mLoadingScreenView.getVisibility() == 0) {
            this.mProgressBar.setVisibility(8);
            this.mLoadingIcon.setVisibility(8);
        }
    }

    private void showLoadingScreen(String str, int i) {
        ((ConstraintLayout.LayoutParams) this.mLoadingScreenView.getLayoutParams()).dimensionRatio = str;
        this.mLoadingScreenView.setVisibility(0);
        toggleProgressBarAndLoadingIcon(i);
    }

    private void toggleProgressBarAndLoadingIcon(int i) {
        if (i == 2) {
            this.mLoadingIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.videocam));
        }
        if (i == 3) {
            this.mLoadingIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.videocam_off));
        }
        boolean z = true;
        int i2 = 0;
        if (i != 1) {
            z = false;
        }
        this.mProgressBar.setVisibility(z ? 0 : 8);
        ImageView imageView = this.mLoadingIcon;
        if (z) {
            i2 = 8;
        }
        imageView.setVisibility(i2);
    }

    private void setRoundedBitmapDrawable(Bitmap bitmap) {
        if (bitmap.getHeight() != 0) {
            int dimension = (int) getResources().getDimension(R.dimen.enhanced_smartspace_height);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (dimension * (bitmap.getWidth() / bitmap.getHeight())), dimension, true);
        }
        RoundedBitmapDrawable create = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        create.setCornerRadius(getResources().getDimension(R.dimen.enhanced_smartspace_secondary_card_corner_radius));
        this.mImageView.setImageDrawable(create);
    }

    private void loadImageUris(List<Uri> list) {
        final ContentResolver contentResolver = getContext().getApplicationContext().getContentResolver();
        final int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.enhanced_smartspace_height);
        final float dimension = getResources().getDimension(R.dimen.enhanced_smartspace_secondary_card_corner_radius);
        addFramesToAnimatedDrawable((List) list.stream().map(uri -> {
                DrawableWithUri drawableWithUri = new DrawableWithUri(uri, contentResolver, dimensionPixelOffset, dimension);
		new LoadUriTask().execute(drawableWithUri);
		return drawableWithUri;
            }).filter(drawableWithUri -> drawableWithUri != null).collect(Collectors.toList()));
    }

    private void addFramesToAnimatedDrawable(List<Drawable> list) {
        AnimationDrawable animationDrawable = new AnimationDrawable();
        for (Drawable drawable : list) {
            animationDrawable.addFrame(drawable, this.mGifFrameDurationInMs);
        }
        this.mImageView.setImageDrawable(animationDrawable);
        animationDrawable.start();
    }

    private List<Uri> getImageUris(SmartspaceTarget smartspaceTarget) {
        return (List<Uri>) smartspaceTarget.getIconGrid().stream().filter(smartspaceAction -> smartspaceAction.getExtras().containsKey("imageUri")).map(smartspaceAction -> smartspaceAction.getExtras().getString("imageUri")).map(uri -> Uri.parse(uri)).collect(Collectors.toList());
    }

    private boolean isSysUiContext() {
        return getContext().getPackageName().equals("com.android.systemui");
    }

    /* loaded from: classes2.dex */
    public static class DrawableWithUri extends RoundDrawableWrapper {
        ContentResolver mContentResolver;
        Drawable mDrawable;
        int mHeightInPx;
        Uri mUri;

        DrawableWithUri(Uri uri, ContentResolver contentResolver, int i, float f) {
            super(new ColorDrawable(0), f);
            this.mUri = uri;
            this.mHeightInPx = i;
            this.mContentResolver = contentResolver;
        }
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLoadingScreenView = (CardView) findViewById(R.id.loading_screen);
        this.mProgressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        this.mLoadingIcon = (ImageView) findViewById(R.id.loading_screen_icon);
    }

    public static Drawable getSampleBitmapDrawable(InputStream inputStream, final int i) {
        try {
            return ImageDecoder.decodeDrawable(ImageDecoder.createSource((Resources) null, inputStream), new ImageDecoder.OnHeaderDecodedListener() { // from class: com.google.android.systemui.smartspace.BcSmartspaceCardDoorbell..ExternalSyntheticLambda0
                @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                    imageDecoder.setAllocator(3);
                    imageDecoder.setTargetSize((int) (i * getTargetRatio(imageInfo)), i);
                }
            });
        } catch (IOException e) {
            Log.e("BcSmartspaceCardBell", "Unable to decode stream: " + e);
            return null;
        }
    }

    private static float getTargetRatio(ImageDecoder.ImageInfo imageInfo) {
        Size size = imageInfo.getSize();
        if (size.getHeight() != 0) {
            return size.getWidth() / size.getHeight();
        }
        return 0.0f;
    }

    /* loaded from: classes2.dex */
    public static class LoadUriTask extends AsyncTask<DrawableWithUri, Void, DrawableWithUri> {
        private LoadUriTask() {
        }

        public DrawableWithUri doInBackground(DrawableWithUri... drawableWithUriArr) {
            if (drawableWithUriArr.length > 0) {
                DrawableWithUri drawableWithUri = drawableWithUriArr[0];
                try {
                    drawableWithUri.mDrawable = BcSmartspaceCardDoorbell.getSampleBitmapDrawable(drawableWithUri.mContentResolver.openInputStream(drawableWithUri.mUri), drawableWithUri.mHeightInPx);
                } catch (Exception e) {
                    Log.w("BcSmartspaceCardBell", "open uri:" + drawableWithUri.mUri + " got exception:" + e);
                }
                return drawableWithUri;
            }
            return null;
        }

        public void onPostExecute(DrawableWithUri drawableWithUri) {
            Drawable drawable;
            if (drawableWithUri == null || (drawable = drawableWithUri.mDrawable) == null) {
                return;
            }
            drawableWithUri.setDrawable(drawable);
        }
    }
}
