package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceTarget;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.android.internal.util.LatencyTracker;
import com.android.wm.shell.R;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class BcSmartspaceCardDoorbell extends BcSmartspaceCardGenericImage {
    public static final /* synthetic */ int $r8$clinit = 0;
    public int mGifFrameDurationInMs;
    public final LatencyInstrumentContext mLatencyInstrumentContext;
    public ImageView mLoadingIcon;
    public ViewGroup mLoadingScreenView;
    public String mPreviousTargetId;
    public ProgressBar mProgressBar;
    public final Map mUriToDrawable;

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class DrawableWithUri extends DrawableWrapper {
        public final Path mClipPath;
        public final ContentResolver mContentResolver;
        public Drawable mDrawable;
        public final int mHeightInPx;
        public final WeakReference mImageViewWeakReference;
        public final WeakReference mLoadingScreenWeakReference;
        public final float mScaledCornerRadius;
        public final RectF mTempRect;
        public final Uri mUri;

        public DrawableWithUri(Uri uri, ContentResolver contentResolver, int i, float f, WeakReference weakReference, WeakReference weakReference2) {
            super(new ColorDrawable(0));
            this.mTempRect = new RectF();
            this.mClipPath = new Path();
            this.mScaledCornerRadius = f;
            this.mUri = uri;
            this.mHeightInPx = i;
            this.mContentResolver = contentResolver;
            this.mImageViewWeakReference = weakReference;
            this.mLoadingScreenWeakReference = weakReference2;
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public final void draw(Canvas canvas) {
            int save = canvas.save();
            canvas.clipPath(this.mClipPath);
            super.draw(canvas);
            canvas.restoreToCount(save);
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public final void onBoundsChange(Rect rect) {
            this.mTempRect.set(getBounds());
            this.mClipPath.reset();
            Path path = this.mClipPath;
            RectF rectF = this.mTempRect;
            float f = this.mScaledCornerRadius;
            path.addRoundRect(rectF, f, f, Path.Direction.CCW);
            super.onBoundsChange(rect);
        }
    }

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class LatencyInstrumentContext {
        public final LatencyTracker mLatencyTracker;
        public final Set mUriSet = new HashSet();

        public LatencyInstrumentContext(Context context) {
            this.mLatencyTracker = LatencyTracker.getInstance(context);
        }

        public final void cancelInstrument() {
            if (this.mUriSet.isEmpty()) {
                return;
            }
            this.mLatencyTracker.onActionCancel(22);
            this.mUriSet.clear();
        }
    }

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class LoadUriTask extends AsyncTask {
        public final LatencyInstrumentContext mInstrumentContext;

        public LoadUriTask(LatencyInstrumentContext latencyInstrumentContext) {
            this.mInstrumentContext = latencyInstrumentContext;
        }

        @Override // android.os.AsyncTask
        public final Object doInBackground(Object[] objArr) {
            DrawableWithUri[] drawableWithUriArr = (DrawableWithUri[]) objArr;
            Drawable drawable = null;
            if (drawableWithUriArr.length <= 0) {
                return null;
            }
            DrawableWithUri drawableWithUri = drawableWithUriArr[0];
            try {
                InputStream openInputStream = drawableWithUri.mContentResolver.openInputStream(drawableWithUri.mUri);
                try {
                    final int i = drawableWithUri.mHeightInPx;
                    int i2 = BcSmartspaceCardDoorbell.$r8$clinit;
                    try {
                        drawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource((Resources) null, openInputStream), new ImageDecoder.OnHeaderDecodedListener() { // from class: com.google.android.systemui.smartspace.BcSmartspaceCardDoorbell$$ExternalSyntheticLambda0
                            @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                            public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                                int i3 = i;
                                int i4 = BcSmartspaceCardDoorbell.$r8$clinit;
                                imageDecoder.setAllocator(3);
                                imageDecoder.setTargetSize((int) (i3 * (imageInfo.getSize().getHeight() != 0 ? r2.getWidth() / r2.getHeight() : 0.0f)), i3);
                            }
                        });
                    } catch (IOException e) {
                        Log.e("BcSmartspaceCardBell", "Unable to decode stream: " + e);
                    }
                    drawableWithUri.mDrawable = drawable;
                    if (openInputStream != null) {
                        openInputStream.close();
                    }
                } finally {
                }
            } catch (Exception e2) {
                Log.w("BcSmartspaceCardBell", "open uri:" + drawableWithUri.mUri + " got exception:" + e2);
            }
            return drawableWithUri;
        }

        @Override // android.os.AsyncTask
        public final void onCancelled() {
            this.mInstrumentContext.cancelInstrument();
        }

        @Override // android.os.AsyncTask
        public final void onPostExecute(Object obj) {
            DrawableWithUri drawableWithUri = (DrawableWithUri) obj;
            if (drawableWithUri == null) {
                return;
            }
            Drawable drawable = drawableWithUri.mDrawable;
            if (drawable != null) {
                drawableWithUri.setDrawable(drawable);
                ImageView imageView = (ImageView) drawableWithUri.mImageViewWeakReference.get();
                int intrinsicWidth = drawableWithUri.mDrawable.getIntrinsicWidth();
                if (imageView.getLayoutParams().width != intrinsicWidth) {
                    Log.d("BcSmartspaceCardBell", "imageView requestLayout " + drawableWithUri.mUri);
                    imageView.getLayoutParams().width = intrinsicWidth;
                    imageView.requestLayout();
                }
                LatencyInstrumentContext latencyInstrumentContext = this.mInstrumentContext;
                Uri uri = drawableWithUri.mUri;
                if (!latencyInstrumentContext.mUriSet.isEmpty()) {
                    if (uri == null || !latencyInstrumentContext.mUriSet.remove(uri)) {
                        latencyInstrumentContext.cancelInstrument();
                    } else if (latencyInstrumentContext.mUriSet.isEmpty()) {
                        latencyInstrumentContext.mLatencyTracker.onActionEnd(22);
                    }
                }
            } else {
                BcSmartspaceTemplateDataUtils.updateVisibility((ImageView) drawableWithUri.mImageViewWeakReference.get(), 8);
                this.mInstrumentContext.cancelInstrument();
            }
            BcSmartspaceTemplateDataUtils.updateVisibility((View) drawableWithUri.mLoadingScreenWeakReference.get(), 8);
        }
    }

    public BcSmartspaceCardDoorbell(Context context) {
        this(context, null);
    }

    public final void maybeResetImageView(SmartspaceTarget smartspaceTarget) {
        boolean equals = smartspaceTarget.getSmartspaceTargetId().equals(this.mPreviousTargetId);
        this.mPreviousTargetId = smartspaceTarget.getSmartspaceTargetId();
        if (equals) {
            return;
        }
        this.mImageView.getLayoutParams().width = -2;
        this.mImageView.setImageDrawable(null);
        this.mUriToDrawable.clear();
    }

    public final void maybeUpdateLayoutHeight(Bundle bundle, View view, String str) {
        if (bundle.containsKey(str)) {
            view.getLayoutParams().height = (int) (getContext().getResources().getDisplayMetrics().density * bundle.getInt(str));
        }
    }

    public final void maybeUpdateLayoutWidth(Bundle bundle, View view, String str) {
        if (bundle.containsKey(str)) {
            view.getLayoutParams().width = (int) (getContext().getResources().getDisplayMetrics().density * bundle.getInt(str));
        }
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.mLoadingScreenView = (ViewGroup) findViewById(R.id.loading_screen);
        this.mProgressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        this.mLoadingIcon = (ImageView) findViewById(R.id.loading_screen_icon);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        super.resetUi();
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mImageView, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mLoadingScreenView, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mProgressBar, 8);
        BcSmartspaceTemplateDataUtils.updateVisibility(this.mLoadingIcon, 8);
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x0268  */
    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean setSmartspaceActions(android.app.smartspace.SmartspaceTarget r16, com.android.systemui.plugins.BcSmartspaceDataPlugin.SmartspaceEventNotifier r17, com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo r18) {
        /*
            Method dump skipped, instructions count: 622
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.smartspace.BcSmartspaceCardDoorbell.setSmartspaceActions(android.app.smartspace.SmartspaceTarget, com.android.systemui.plugins.BcSmartspaceDataPlugin$SmartspaceEventNotifier, com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo):boolean");
    }

    public BcSmartspaceCardDoorbell(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mUriToDrawable = new HashMap();
        this.mGifFrameDurationInMs = 200;
        this.mLatencyInstrumentContext = new LatencyInstrumentContext(context);
    }
}
