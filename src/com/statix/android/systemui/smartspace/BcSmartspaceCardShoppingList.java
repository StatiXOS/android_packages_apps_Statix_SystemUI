package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;

import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import com.statix.android.systemui.res.R;

import java.util.Locale;

public class BcSmartspaceCardShoppingList extends BcSmartspaceCardSecondary {
    public static final int[] LIST_ITEM_TEXT_VIEW_IDS = {
        R.id.list_item_1, R.id.list_item_2, R.id.list_item_3
    };

    public ImageView mCardPromptIconView;
    public TextView mCardPromptView;
    public TextView mEmptyListMessageView;
    public ImageView mListIconView;
    public final TextView[] mListItems;

    public BcSmartspaceCardShoppingList(Context context) {
        super(context);
        mListItems = new TextView[3];
    }

    public BcSmartspaceCardShoppingList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mListItems = new TextView[3];
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCardPromptView = (TextView) findViewById(R.id.card_prompt);
        mEmptyListMessageView = (TextView) findViewById(R.id.empty_list_message);
        mCardPromptIconView = (ImageView) findViewById(R.id.card_prompt_icon);
        mListIconView = (ImageView) findViewById(R.id.list_icon);

        for (int i = 0; i < 3; i++) {
            mListItems[i] = (TextView) findViewById(LIST_ITEM_TEXT_VIEW_IDS[i]);
        }
    }

    public void resetUi() {
        BcSmartspaceTemplateDataUtils.updateVisibility(mEmptyListMessageView, View.GONE);
        BcSmartspaceTemplateDataUtils.updateVisibility(mListIconView, View.GONE);
        BcSmartspaceTemplateDataUtils.updateVisibility(mCardPromptIconView, View.GONE);
        BcSmartspaceTemplateDataUtils.updateVisibility(mCardPromptView, View.GONE);

        for (int i = 0; i < 3; i++) {
            BcSmartspaceTemplateDataUtils.updateVisibility(mListItems[i], View.GONE);
        }
    }

    public boolean setSmartspaceActions(
            SmartspaceTarget target,
            BcSmartspaceDataPlugin.SmartspaceEventNotifier notifier,
            BcSmartspaceCardLoggingInfo loggingInfo) {
        SmartspaceAction action = target.getBaseAction();
        Bundle extras = action != null ? action.getExtras() : null;

        if (extras == null) {
            return false;
        }

        Bitmap bitmap = null;
        if (extras.containsKey("appIcon")) {
            bitmap = (Bitmap) extras.get("appIcon");
        } else if (extras.containsKey("imageBitmap")) {
            bitmap = (Bitmap) extras.get("imageBitmap");
        }

        mCardPromptIconView.setImageBitmap(bitmap);
        mListIconView.setImageBitmap(bitmap);

        if (extras.containsKey("cardPrompt")) {
            String prompt = extras.getString("cardPrompt");
            if (mCardPromptView == null) {
                Log.w("BcSmartspaceCardShoppingList", "No card prompt view to update");
            } else {
                mCardPromptView.setText(prompt);
            }
            BcSmartspaceTemplateDataUtils.updateVisibility(mCardPromptView, View.VISIBLE);
            if (bitmap != null) {
                BcSmartspaceTemplateDataUtils.updateVisibility(mCardPromptIconView, View.VISIBLE);
            }
            return true;
        } else if (extras.containsKey("emptyListString")) {
            String emptyMessage = extras.getString("emptyListString");
            if (mEmptyListMessageView == null) {
                Log.w("BcSmartspaceCardShoppingList", "No empty list message view to update");
            } else {
                mEmptyListMessageView.setText(emptyMessage);
            }
            BcSmartspaceTemplateDataUtils.updateVisibility(mEmptyListMessageView, View.VISIBLE);
            BcSmartspaceTemplateDataUtils.updateVisibility(mListIconView, View.VISIBLE);
            return true;
        } else if (extras.containsKey("listItems")) {
            String[] items = extras.getStringArray("listItems");
            if (items.length == 0) {
                return false;
            }

            BcSmartspaceTemplateDataUtils.updateVisibility(mListIconView, View.VISIBLE);

            for (int i = 0; i < 3; i++) {
                TextView itemView = mListItems[i];
                if (itemView == null) {
                    Log.w(
                            "BcSmartspaceCardShoppingList",
                            String.format(
                                    Locale.US,
                                    "Missing list item view to update at row: %d",
                                    i + 1));
                    return true;
                }

                if (i < items.length) {
                    BcSmartspaceTemplateDataUtils.updateVisibility(itemView, View.VISIBLE);
                    itemView.setText(items[i]);
                } else {
                    BcSmartspaceTemplateDataUtils.updateVisibility(itemView, View.GONE);
                    itemView.setText("");
                }
            }
            return true;
        }
        return false;
    }

    public void setTextColor(int color) {
        mCardPromptView.setTextColor(color);
        mEmptyListMessageView.setTextColor(color);

        for (int i = 0; i < 3; i++) {
            TextView itemView = mListItems[i];
            if (itemView == null) {
                Log.w(
                        "BcSmartspaceCardShoppingList",
                        String.format(
                                Locale.US, "Missing list item view to update at row: %d", i + 1));
                continue;
            }
            itemView.setTextColor(color);
        }
    }
}
