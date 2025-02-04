package com.google.android.systemui.smartspace.uitemplate;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.util.Log;
import android.widget.ImageView;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public final /* synthetic */ class SubImageTemplateCard$$ExternalSyntheticLambda0 implements Icon.OnDrawableLoadedListener {
    public final /* synthetic */ SubImageTemplateCard f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Map f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ List f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ WeakReference f$7;

    public /* synthetic */ SubImageTemplateCard$$ExternalSyntheticLambda0(SubImageTemplateCard subImageTemplateCard, String str, String str2, Map map, int i, List list, int i2, WeakReference weakReference) {
        this.f$0 = subImageTemplateCard;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = map;
        this.f$4 = i;
        this.f$5 = list;
        this.f$6 = i2;
        this.f$7 = weakReference;
    }

    @Override // android.graphics.drawable.Icon.OnDrawableLoadedListener
    public final void onDrawableLoaded(Drawable drawable) {
        SubImageTemplateCard subImageTemplateCard = this.f$0;
        String str = this.f$1;
        String str2 = this.f$2;
        Map map = this.f$3;
        int i = this.f$4;
        List list = this.f$5;
        final int i2 = this.f$6;
        WeakReference weakReference = this.f$7;
        int i3 = SubImageTemplateCard.$r8$clinit;
        if (!str.equals(subImageTemplateCard.mPrevSmartspaceTargetId)) {
            Log.d("SubImageTemplateCard", "SmartspaceTarget has changed. Skip the loaded result...");
            return;
        }
        subImageTemplateCard.mIconDrawableCache.put(str2, drawable);
        map.put(Integer.valueOf(i), drawable);
        if (map.size() == list.size()) {
            final AnimationDrawable animationDrawable = new AnimationDrawable();
            List list2 = (List) map.values().stream().filter(new SubImageTemplateCard$$ExternalSyntheticLambda1()).collect(Collectors.toList());
            if (list2.isEmpty()) {
                Log.w("SubImageTemplateCard", "All images are failed to load. Reset imageView");
                ImageView imageView = subImageTemplateCard.mImageView;
                if (imageView == null) {
                    return;
                }
                imageView.getLayoutParams().width = -2;
                subImageTemplateCard.mImageView.setImageDrawable(null);
                subImageTemplateCard.mImageView.setBackgroundTintList(null);
                return;
            }
            list2.forEach(new Consumer() { // from class: com.google.android.systemui.smartspace.uitemplate.SubImageTemplateCard$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AnimationDrawable animationDrawable2 = animationDrawable;
                    int i4 = i2;
                    int i5 = SubImageTemplateCard.$r8$clinit;
                    animationDrawable2.addFrame((Drawable) obj, i4);
                }
            });
            ImageView imageView2 = (ImageView) weakReference.get();
            imageView2.setImageDrawable(animationDrawable);
            int intrinsicWidth = animationDrawable.getIntrinsicWidth();
            if (imageView2.getLayoutParams().width != intrinsicWidth) {
                Log.d("SubImageTemplateCard", "imageView requestLayout");
                imageView2.getLayoutParams().width = intrinsicWidth;
                imageView2.requestLayout();
            }
            animationDrawable.start();
        }
    }
}
