package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.statix.android.systemui.res.R;

import java.util.Locale;

public class BcSmartspaceCardWeatherForecast extends BcSmartspaceCardSecondary {

    public BcSmartspaceCardWeatherForecast(Context context) {
        super(context);
    }

    public BcSmartspaceCardWeatherForecast(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ConstraintLayout[] columns = new ConstraintLayout[4];
        for (int i = 0; i < 4; i++) {
            ConstraintLayout column =
                    (ConstraintLayout)
                            View.inflate(
                                    getContext(),
                                    R.layout.smartspace_card_weather_forecast_column,
                                    null);
            column.setId(View.generateViewId());
            columns[i] = column;
        }

        for (int i = 0; i < 4; i++) {
            ConstraintLayout.LayoutParams params =
                    new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT, 0);
            ConstraintLayout prevColumn = i > 0 ? columns[i - 1] : null;
            ConstraintLayout nextColumn = i < 3 ? columns[i + 1] : null;

            if (i == 0) {
                params.startToStart = 0;
                params.horizontalChainStyle = 1;
            } else {
                params.startToEnd = prevColumn.getId();
            }

            if (i == 3) {
                params.endToEnd = 0;
            } else {
                params.endToStart = nextColumn.getId();
            }

            params.topToTop = 0;
            params.bottomToBottom = 0;
            addView(columns[i], params);
        }
    }

    @Override
    public boolean setSmartspaceActions(
            SmartspaceTarget target,
            BcSmartspaceDataPlugin.SmartspaceEventNotifier eventNotifier,
            BcSmartspaceCardLoggingInfo loggingInfo) {
        Bundle extras = target.getBaseAction() != null ? target.getBaseAction().getExtras() : null;
        if (extras == null) {
            return false;
        }

        boolean updated = false;

        if (extras.containsKey("temperatureValues")) {
            String[] temperatureValues = extras.getStringArray("temperatureValues");
            if (temperatureValues == null) {
                Log.w("BcSmartspaceCardWeatherForecast", "Temperature values array is null.");
            } else {
                updateFields(
                        (view, index) -> ((TextView) view).setText(temperatureValues[index]),
                        temperatureValues.length,
                        R.id.temperature_value,
                        "temperature value");
                updated = true;
            }
        }

        if (extras.containsKey("weatherIcons")) {
            Bitmap[] weatherIcons = (Bitmap[]) extras.get("weatherIcons");
            if (weatherIcons == null) {
                Log.w("BcSmartspaceCardWeatherForecast", "Weather icons array is null.");
            } else {
                updateFields(
                        (view, index) -> ((ImageView) view).setImageBitmap(weatherIcons[index]),
                        weatherIcons.length,
                        R.id.weather_icon,
                        "weather icon");
                updated = true;
            }
        }

        if (extras.containsKey("timestamps")) {
            String[] timestamps = extras.getStringArray("timestamps");
            if (timestamps == null) {
                Log.w("BcSmartspaceCardWeatherForecast", "Timestamps array is null.");
                return true; // Return true as per bytecode, even if timestamps are null
            } else {
                updateFields(
                        (view, index) -> ((TextView) view).setText(timestamps[index]),
                        timestamps.length,
                        R.id.timestamp,
                        "timestamp");
                return true;
            }
        }

        return updated;
    }

    @Override
    public void setTextColor(int color) {
        updateFields(
                (view, index) -> ((TextView) view).setTextColor(color),
                4,
                R.id.temperature_value,
                "temperature value");
        updateFields(
                (view, index) -> ((TextView) view).setTextColor(color),
                4,
                R.id.timestamp,
                "timestamp");
    }

    private void updateFields(
            ItemUpdateFunction updateFunction, int count, int viewId, String fieldName) {
        int childCount = getChildCount();
        if (childCount < 4) {
            Log.w(
                    "BcSmartspaceCardWeatherForecast",
                    String.format(
                            Locale.US,
                            "Missing %d %s view(s) to update.",
                            4 - childCount,
                            fieldName));
            return;
        }

        int columnCount = Math.min(4, count);
        if (count < 4) {
            Log.w(
                    "BcSmartspaceCardWeatherForecast",
                    String.format(
                            Locale.US,
                            "Missing %d %s(s). Hiding incomplete columns.",
                            4 - count,
                            fieldName));
            for (int i = 0; i < 4; i++) {
                View column = getChildAt(i);
                BcSmartspaceTemplateDataUtils.updateVisibility(
                        column, i <= (3 - (4 - count)) ? View.VISIBLE : View.GONE);
            }
            ConstraintLayout firstColumn = (ConstraintLayout) getChildAt(0);
            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) firstColumn.getLayoutParams();
            params.horizontalChainStyle = count == 4 ? 1 : 0;
        }

        for (int i = 0; i < columnCount; i++) {
            View column = getChildAt(i);
            View targetView = column.findViewById(viewId);
            if (targetView == null) {
                Log.w(
                        "BcSmartspaceCardWeatherForecast",
                        String.format(
                                Locale.US,
                                "Missing %s view to update at column: %d.",
                                fieldName,
                                i + 1));
                return;
            }
            updateFunction.update(targetView, i);
        }
    }

    public interface ItemUpdateFunction {
        void update(View view, int index);
    }
}
