package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appsearch.app.DocumentClassFactoryRegistry$$ExternalSyntheticOutline0;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints$LayoutParams;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.wm.shell.R;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import java.util.Locale;

/* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
/* loaded from: classes2.dex */
public class BcSmartspaceCardWeatherForecast extends BcSmartspaceCardSecondary {
    public static final /* synthetic */ int $r8$clinit = 0;

    /* compiled from: go/retraceme 97024faaf470985feb378c0f604e66d2eca678dbbb151206fad2ab4525fd6f86 */
    public interface ItemUpdateFunction {
        void update(View view, int i);
    }

    public BcSmartspaceCardWeatherForecast(Context context) {
        super(context);
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        ConstraintLayout[] constraintLayoutArr = new ConstraintLayout[4];
        for (int i = 0; i < 4; i++) {
            ConstraintLayout constraintLayout = (ConstraintLayout) ViewGroup.inflate(getContext(), R.layout.smartspace_card_weather_forecast_column, null);
            constraintLayout.setId(View.generateViewId());
            constraintLayoutArr[i] = constraintLayout;
        }
        int i2 = 0;
        while (i2 < 4) {
            Constraints$LayoutParams constraints$LayoutParams = new Constraints$LayoutParams(0);
            ConstraintLayout constraintLayout2 = constraintLayoutArr[i2];
            ConstraintLayout constraintLayout3 = i2 > 0 ? constraintLayoutArr[i2 - 1] : null;
            ConstraintLayout constraintLayout4 = i2 < 3 ? constraintLayoutArr[i2 + 1] : null;
            if (i2 == 0) {
                constraints$LayoutParams.startToStart = 0;
                constraints$LayoutParams.horizontalChainStyle = 1;
            } else {
                constraints$LayoutParams.startToEnd = constraintLayout3.getId();
            }
            if (i2 == 3) {
                constraints$LayoutParams.endToEnd = 0;
            } else {
                constraints$LayoutParams.endToStart = constraintLayout4.getId();
            }
            constraints$LayoutParams.topToTop = 0;
            constraints$LayoutParams.bottomToBottom = 0;
            addView(constraintLayout2, constraints$LayoutParams);
            i2++;
        }
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        boolean z;
        final int i = 1;
        final int i2 = 0;
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        Bundle extras = baseAction == null ? null : baseAction.getExtras();
        if (extras == null) {
            return false;
        }
        if (extras.containsKey("temperatureValues")) {
            final String[] stringArray = extras.getStringArray("temperatureValues");
            if (stringArray == null) {
                Log.w("BcSmartspaceCardWeatherForecast", "Temperature values array is null.");
            } else {
                updateFields(new ItemUpdateFunction() { // from class: com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast$$ExternalSyntheticLambda2
                    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast.ItemUpdateFunction
                    public final void update(View view, int i3) {
                        Object[] objArr = stringArray;
                        switch (i) {
                            case 0:
                                int i4 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                                ((TextView) view).setText(((String[]) objArr)[i3]);
                                break;
                            case 1:
                                int i5 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                                ((TextView) view).setText(((String[]) objArr)[i3]);
                                break;
                            default:
                                int i6 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                                ((ImageView) view).setImageBitmap(((Bitmap[]) objArr)[i3]);
                                break;
                        }
                    }
                }, stringArray.length, R.id.temperature_value, "temperature value");
            }
            z = true;
        } else {
            z = false;
        }
        if (extras.containsKey("weatherIcons")) {
            final Bitmap[] bitmapArr = (Bitmap[]) extras.get("weatherIcons");
            if (bitmapArr == null) {
                Log.w("BcSmartspaceCardWeatherForecast", "Weather icons array is null.");
            } else {
                final int i3 = 2;
                updateFields(new ItemUpdateFunction() { // from class: com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast$$ExternalSyntheticLambda2
                    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast.ItemUpdateFunction
                    public final void update(View view, int i32) {
                        Object[] objArr = bitmapArr;
                        switch (i3) {
                            case 0:
                                int i4 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                                ((TextView) view).setText(((String[]) objArr)[i32]);
                                break;
                            case 1:
                                int i5 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                                ((TextView) view).setText(((String[]) objArr)[i32]);
                                break;
                            default:
                                int i6 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                                ((ImageView) view).setImageBitmap(((Bitmap[]) objArr)[i32]);
                                break;
                        }
                    }
                }, bitmapArr.length, R.id.weather_icon, "weather icon");
            }
            z = true;
        }
        if (!extras.containsKey("timestamps")) {
            return z;
        }
        final String[] stringArray2 = extras.getStringArray("timestamps");
        if (stringArray2 == null) {
            Log.w("BcSmartspaceCardWeatherForecast", "Timestamps array is null.");
            return true;
        }
        updateFields(new ItemUpdateFunction() { // from class: com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast$$ExternalSyntheticLambda2
            @Override // com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast.ItemUpdateFunction
            public final void update(View view, int i32) {
                Object[] objArr = stringArray2;
                switch (i2) {
                    case 0:
                        int i4 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                        ((TextView) view).setText(((String[]) objArr)[i32]);
                        break;
                    case 1:
                        int i5 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                        ((TextView) view).setText(((String[]) objArr)[i32]);
                        break;
                    default:
                        int i6 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                        ((ImageView) view).setImageBitmap(((Bitmap[]) objArr)[i32]);
                        break;
                }
            }
        }, stringArray2.length, R.id.timestamp, "timestamp");
        return true;
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public final void setTextColor(final int i) {
        final int i2 = 0;
        updateFields(new ItemUpdateFunction() { // from class: com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast$$ExternalSyntheticLambda0
            @Override // com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast.ItemUpdateFunction
            public final void update(View view, int i3) {
                int i4 = i;
                switch (i2) {
                    case 0:
                        int i5 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                        ((TextView) view).setTextColor(i4);
                        break;
                    default:
                        int i6 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                        ((TextView) view).setTextColor(i4);
                        break;
                }
            }
        }, 4, R.id.temperature_value, "temperature value");
        final int i3 = 1;
        updateFields(new ItemUpdateFunction() { // from class: com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast$$ExternalSyntheticLambda0
            @Override // com.google.android.systemui.smartspace.BcSmartspaceCardWeatherForecast.ItemUpdateFunction
            public final void update(View view, int i32) {
                int i4 = i;
                switch (i3) {
                    case 0:
                        int i5 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                        ((TextView) view).setTextColor(i4);
                        break;
                    default:
                        int i6 = BcSmartspaceCardWeatherForecast.$r8$clinit;
                        ((TextView) view).setTextColor(i4);
                        break;
                }
            }
        }, 4, R.id.timestamp, "timestamp");
    }

    public final void updateFields(ItemUpdateFunction itemUpdateFunction, int i, int i2, String str) {
        if (getChildCount() < 4) {
            Log.w("BcSmartspaceCardWeatherForecast", String.format(Locale.US, DocumentClassFactoryRegistry$$ExternalSyntheticOutline0.m("Missing %d ", str, " view(s) to update."), Integer.valueOf(4 - getChildCount())));
            return;
        }
        if (i < 4) {
            int i3 = 4 - i;
            Log.w("BcSmartspaceCardWeatherForecast", String.format(Locale.US, DocumentClassFactoryRegistry$$ExternalSyntheticOutline0.m("Missing %d ", str, "(s). Hiding incomplete columns."), Integer.valueOf(i3)));
            if (getChildCount() < 4) {
                Log.w("BcSmartspaceCardWeatherForecast", "Missing " + (4 - getChildCount()) + " columns to update.");
            } else {
                int i4 = 3 - i3;
                int i5 = 0;
                while (i5 < 4) {
                    BcSmartspaceTemplateDataUtils.updateVisibility(getChildAt(i5), i5 <= i4 ? 0 : 8);
                    i5++;
                }
                ((ConstraintLayout.LayoutParams) ((ConstraintLayout) getChildAt(0)).getLayoutParams()).horizontalChainStyle = i3 == 0 ? 1 : 0;
            }
        }
        int min = Math.min(4, i);
        for (int i6 = 0; i6 < min; i6++) {
            View findViewById = getChildAt(i6).findViewById(i2);
            if (findViewById == null) {
                Log.w("BcSmartspaceCardWeatherForecast", String.format(Locale.US, DocumentClassFactoryRegistry$$ExternalSyntheticOutline0.m("Missing ", str, " view to update at column: %d."), Integer.valueOf(i6 + 1)));
                return;
            }
            itemUpdateFunction.update(findViewById, i6);
        }
    }

    public BcSmartspaceCardWeatherForecast(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
