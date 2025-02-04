package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class BcSmartspaceCardGenericImage extends BcSmartspaceCardSecondary {
    public ImageView mImageView;

    public BcSmartspaceCardGenericImage(Context context) {
        super(context);
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mImageView = (ImageView) findViewById(R.id.image_view);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public void resetUi() {
        this.mImageView.setImageBitmap(null);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.mImageView.setImageBitmap(bitmap);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        Bundle extras = baseAction == null ? null : baseAction.getExtras();
        if (extras == null || !extras.containsKey("imageBitmap")) {
            return false;
        }
        if (extras.containsKey("imageScaleType")) {
            String string = extras.getString("imageScaleType");
            try {
                this.mImageView.setScaleType(ImageView.ScaleType.valueOf(string));
            } catch (IllegalArgumentException unused) {
                Log.w("SmartspaceGenericImg", "Invalid imageScaleType value: " + string);
            }
        }
        String dimensionRatio = BcSmartSpaceUtil.getDimensionRatio(extras);
        if (dimensionRatio != null) {
            ((ConstraintLayout.LayoutParams) this.mImageView.getLayoutParams()).dimensionRatio = dimensionRatio;
        }
        if (extras.containsKey("imageLayoutWidth")) {
            ((ViewGroup.MarginLayoutParams) ((ConstraintLayout.LayoutParams) this.mImageView.getLayoutParams())).width = extras.getInt("imageLayoutWidth");
        }
        if (extras.containsKey("imageLayoutHeight")) {
            ((ViewGroup.MarginLayoutParams) ((ConstraintLayout.LayoutParams) this.mImageView.getLayoutParams())).height = extras.getInt("imageLayoutHeight");
        }
        setImageBitmap((Bitmap) extras.get("imageBitmap"));
        return true;
    }

    public BcSmartspaceCardGenericImage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public void setTextColor(int i) {
    }
}
