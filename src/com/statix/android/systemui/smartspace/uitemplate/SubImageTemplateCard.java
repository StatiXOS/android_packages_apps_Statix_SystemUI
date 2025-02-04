package com.google.android.systemui.smartspace.uitemplate;

import android.app.smartspace.SmartspaceTarget;
import android.app.smartspace.uitemplatedata.Icon;
import android.app.smartspace.uitemplatedata.SubImageTemplateData;
import android.app.smartspace.uitemplatedata.TapAction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.BcSmartSpaceUtil;
import com.google.android.systemui.smartspace.BcSmartspaceCardSecondary;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggerUtil;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class SubImageTemplateCard extends BcSmartspaceCardSecondary {
    public static final /* synthetic */ int $r8$clinit = 0;
    public final Handler mHandler;
    public final Map mIconDrawableCache;
    public final int mImageHeight;
    public ImageView mImageView;

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class DrawableWrapper {
        public final ContentResolver mContentResolver;
        public Drawable mDrawable;
        public final int mHeightInPx;
        public final SubImageTemplateCard$$ExternalSyntheticLambda0 mListener;
        public final Uri mUri;

        public DrawableWrapper(Uri uri, ContentResolver contentResolver, int i, SubImageTemplateCard$$ExternalSyntheticLambda0 subImageTemplateCard$$ExternalSyntheticLambda0) {
            this.mUri = uri;
            this.mHeightInPx = i;
            this.mContentResolver = contentResolver;
            this.mListener = subImageTemplateCard$$ExternalSyntheticLambda0;
        }
    }

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public final class LoadUriTask extends AsyncTask {
        @Override // android.os.AsyncTask
        public final Object doInBackground(Object[] objArr) {
            DrawableWrapper[] drawableWrapperArr = (DrawableWrapper[]) objArr;
            Drawable drawable = null;
            if (drawableWrapperArr.length <= 0) {
                return null;
            }
            DrawableWrapper drawableWrapper = drawableWrapperArr[0];
            try {
                InputStream openInputStream = drawableWrapper.mContentResolver.openInputStream(drawableWrapper.mUri);
                final int i = drawableWrapper.mHeightInPx;
                int i2 = SubImageTemplateCard.$r8$clinit;
                try {
                    drawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource((Resources) null, openInputStream), new ImageDecoder.OnHeaderDecodedListener() { // from class: com.google.android.systemui.smartspace.uitemplate.SubImageTemplateCard$$ExternalSyntheticLambda3
                        @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                        public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                            int i3 = i;
                            int i4 = SubImageTemplateCard.$r8$clinit;
                            imageDecoder.setAllocator(3);
                            imageDecoder.setTargetSize((int) (i3 * (imageInfo.getSize().getHeight() != 0 ? r2.getWidth() / r2.getHeight() : 0.0f)), i3);
                        }
                    });
                } catch (IOException e) {
                    Log.e("SubImageTemplateCard", "Unable to decode stream: " + e);
                }
                drawableWrapper.mDrawable = drawable;
            } catch (Exception e2) {
                Log.w("SubImageTemplateCard", "open uri:" + drawableWrapper.mUri + " got exception:" + e2);
            }
            return drawableWrapper;
        }

        @Override // android.os.AsyncTask
        public final void onPostExecute(Object obj) {
            DrawableWrapper drawableWrapper = (DrawableWrapper) obj;
            drawableWrapper.mListener.onDrawableLoaded(drawableWrapper.mDrawable);
        }
    }

    public SubImageTemplateCard(Context context) {
        this(context, null);
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.mImageView = (ImageView) findViewById(R.id.image_view);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void resetUi() {
        Map map = this.mIconDrawableCache;
        if (map != null) {
            map.clear();
        }
        ImageView imageView = this.mImageView;
        if (imageView == null) {
            return;
        }
        imageView.getLayoutParams().width = -2;
        this.mImageView.setImageDrawable(null);
        this.mImageView.setBackgroundTintList(null);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        int i;
        String str;
        WeakReference weakReference;
        TapAction tapAction;
        String sb;
        SubImageTemplateData templateData = smartspaceTarget.getTemplateData();
        if (!BcSmartspaceCardLoggerUtil.containsValidTemplateType(templateData) || templateData.getSubImages() == null || templateData.getSubImages().isEmpty()) {
            Log.w("SubImageTemplateCard", "SubImageTemplateData is null or has no SubImage or invalid template type");
            return false;
        }
        List subImages = templateData.getSubImages();
        TapAction subImageAction = templateData.getSubImageAction();
        if (this.mImageView == null) {
            Log.w("SubImageTemplateCard", "No image view can be updated. Skipping background update...");
        } else if (subImageAction != null && subImageAction.getExtras() != null) {
            Bundle extras = subImageAction.getExtras();
            String string = extras.getString("imageDimensionRatio", "");
            if (!TextUtils.isEmpty(string)) {
                this.mImageView.getLayoutParams().width = 0;
                ((ConstraintLayout.LayoutParams) this.mImageView.getLayoutParams()).dimensionRatio = string;
            }
            if (extras.getBoolean("shouldShowBackground", false)) {
                this.mImageView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.smartspace_button_background)));
            }
        }
        int i2 = 200;
        if (subImageAction != null && subImageAction.getExtras() != null) {
            i2 = subImageAction.getExtras().getInt("GifFrameDurationMillis", 200);
        }
        int i3 = i2;
        ContentResolver contentResolver = getContext().getApplicationContext().getContentResolver();
        TreeMap treeMap = new TreeMap();
        WeakReference weakReference2 = new WeakReference(this.mImageView);
        String str2 = this.mPrevSmartspaceTargetId;
        int i4 = 0;
        while (i4 < subImages.size()) {
            Icon icon = (Icon) subImages.get(i4);
            if (icon == null || icon.getIcon() == null) {
                i = i4;
                str = str2;
                weakReference = weakReference2;
                tapAction = subImageAction;
            } else {
                android.graphics.drawable.Icon icon2 = icon.getIcon();
                StringBuilder sb2 = new StringBuilder(icon2.getType());
                switch (icon2.getType()) {
                    case 1:
                    case 5:
                        sb2.append(icon2.getBitmap().hashCode());
                        sb = sb2.toString();
                        break;
                    case 2:
                        sb2.append(icon2.getResPackage());
                        sb2.append(String.format("0x%08x", Integer.valueOf(icon2.getResId())));
                        sb = sb2.toString();
                        break;
                    case 3:
                        sb2.append(Arrays.hashCode(icon2.getDataBytes()));
                        sb = sb2.toString();
                        break;
                    case 4:
                    case 6:
                        sb2.append(icon2.getUriString());
                        sb = sb2.toString();
                        break;
                    default:
                        sb = sb2.toString();
                        break;
                }
                String str3 = sb;
                tapAction = subImageAction;
                i = i4;
                str = str2;
                weakReference = weakReference2;
                SubImageTemplateCard$$ExternalSyntheticLambda0 subImageTemplateCard$$ExternalSyntheticLambda0 = new SubImageTemplateCard$$ExternalSyntheticLambda0(this, str2, str3, treeMap, i4, subImages, i3, weakReference2);
                if (this.mIconDrawableCache.containsKey(str3) && this.mIconDrawableCache.get(str3) != null) {
                    subImageTemplateCard$$ExternalSyntheticLambda0.onDrawableLoaded((Drawable) this.mIconDrawableCache.get(str3));
                } else if (icon2.getType() == 4) {
                    new LoadUriTask().execute(new DrawableWrapper(icon2.getUri(), contentResolver, this.mImageHeight, subImageTemplateCard$$ExternalSyntheticLambda0));
                } else {
                    icon2.loadDrawableAsync(getContext(), subImageTemplateCard$$ExternalSyntheticLambda0, this.mHandler);
                }
            }
            i4 = i + 1;
            subImageAction = tapAction;
            str2 = str;
            weakReference2 = weakReference;
        }
        TapAction tapAction2 = subImageAction;
        if (tapAction2 == null) {
            return true;
        }
        BcSmartSpaceUtil.setOnClickListener(this, smartspaceTarget, tapAction2, smartspaceEventNotifier, "SubImageTemplateCard", bcSmartspaceCardLoggingInfo, 0);
        return true;
    }

    public SubImageTemplateCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIconDrawableCache = new HashMap();
        this.mHandler = new Handler();
        this.mImageHeight = getResources().getDimensionPixelOffset(R.dimen.enhanced_smartspace_card_height);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(int i) {
    }
}
