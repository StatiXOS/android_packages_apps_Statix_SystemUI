package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceUtils;
import android.app.smartspace.uitemplatedata.Icon;
import android.app.smartspace.uitemplatedata.Text;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public abstract class BcSmartspaceTemplateDataUtils {
    public static void offsetTextViewForIcon(TextView textView, DoubleShadowIconDrawable doubleShadowIconDrawable, boolean z) {
        if (doubleShadowIconDrawable == null) {
            textView.setTranslationX(0.0f);
        } else {
            textView.setTranslationX((z ? 1 : -1) * doubleShadowIconDrawable.mIconInsetSize);
        }
    }

    public static void setIcon(ImageView imageView, Icon icon) {
        if (imageView == null) {
            Log.w("BcSmartspaceTemplateDataUtils", "Cannot set. The image view is null");
            return;
        }
        if (icon == null) {
            Log.w("BcSmartspaceTemplateDataUtils", "Cannot set. The given icon is null");
            updateVisibility(imageView, 8);
        }
        imageView.setImageIcon(icon.getIcon());
        if (icon.getContentDescription() != null) {
            imageView.setContentDescription(icon.getContentDescription());
        }
    }

    public static void setText(TextView textView, Text text) {
        if (textView == null) {
            Log.w("BcSmartspaceTemplateDataUtils", "Cannot set. The text view is null");
            return;
        }
        if (SmartspaceUtils.isEmpty(text)) {
            Log.w("BcSmartspaceTemplateDataUtils", "Cannot set. The given text is empty");
            updateVisibility(textView, 8);
        } else {
            textView.setText(text.getText());
            textView.setEllipsize(text.getTruncateAtType());
            textView.setMaxLines(text.getMaxLines());
        }
    }

    public static void updateVisibility(View view, int i) {
        if (view == null || view.getVisibility() == i) {
            return;
        }
        view.setVisibility(i);
    }
}
