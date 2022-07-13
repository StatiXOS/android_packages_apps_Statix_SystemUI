package com.google.android.systemui.smartspace;

import android.app.smartspace.SmartspaceAction;
import android.app.smartspace.SmartspaceTarget;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
import java.util.Locale;
/* loaded from: classes2.dex */
public class BcSmartspaceCardShoppingList extends BcSmartspaceCardSecondary {
    private static final int[] LIST_ITEM_TEXT_VIEW_IDS = {R.id.list_item_1, R.id.list_item_2, R.id.list_item_3};
    private ImageView mCardPromptIconView;
    private TextView mCardPromptView;
    private TextView mEmptyListMessageView;
    private ImageView mListIconView;

    public BcSmartspaceCardShoppingList(Context context) {
        super(context);
    }

    public BcSmartspaceCardShoppingList(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        Bitmap bitmap = null;
        Bundle extras = baseAction == null ? null : baseAction.getExtras();
        if (extras != null) {
            this.mEmptyListMessageView.setVisibility(8);
            this.mListIconView.setVisibility(8);
            this.mCardPromptIconView.setVisibility(8);
            this.mCardPromptView.setVisibility(8);
            for (int i = 0; i < 3; i++) {
                TextView textView = (TextView) findViewById(LIST_ITEM_TEXT_VIEW_IDS[i]);
                if (textView != null) {
                    textView.setVisibility(8);
                }
            }
            if (extras.containsKey("appIcon")) {
                bitmap = (Bitmap) extras.get("appIcon");
            } else if (extras.containsKey("imageBitmap")) {
                bitmap = (Bitmap) extras.get("imageBitmap");
            }
            setIconBitmap(bitmap);
            if (extras.containsKey("cardPrompt")) {
                setCardPrompt(extras.getString("cardPrompt"));
                this.mCardPromptView.setVisibility(0);
                if (bitmap != null) {
                    this.mCardPromptIconView.setVisibility(0);
                }
                return true;
            } else if (extras.containsKey("emptyListString")) {
                setEmptyListMessage(extras.getString("emptyListString"));
                this.mEmptyListMessageView.setVisibility(0);
                this.mListIconView.setVisibility(0);
                return true;
            } else if (extras.containsKey("listItems")) {
                String[] stringArray = extras.getStringArray("listItems");
                if (stringArray.length == 0) {
                    return false;
                }
                this.mListIconView.setVisibility(0);
                setShoppingItems(stringArray, extras.getInt("listSize", -1));
                return true;
            }
        }
        return false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCardPromptView = (TextView) findViewById(R.id.card_prompt);
        this.mEmptyListMessageView = (TextView) findViewById(R.id.empty_list_message);
        this.mCardPromptIconView = (ImageView) findViewById(R.id.card_prompt_icon);
        this.mListIconView = (ImageView) findViewById(R.id.list_icon);
    }

    void setIconBitmap(Bitmap bitmap) {
        this.mCardPromptIconView.setImageBitmap(bitmap);
        this.mListIconView.setImageBitmap(bitmap);
    }

    void setCardPrompt(String str) {
        TextView textView = this.mCardPromptView;
        if (textView == null) {
            Log.w("BcSmartspaceCardShoppingList", "No card prompt view to update");
        } else {
            textView.setText(str);
        }
    }

    void setEmptyListMessage(String str) {
        TextView textView = this.mEmptyListMessageView;
        if (textView == null) {
            Log.w("BcSmartspaceCardShoppingList", "No empty list message view to update");
        } else {
            textView.setText(str);
        }
    }

    void setShoppingItems(String[] strArr, int i) {
        if (strArr == null) {
            Log.w("BcSmartspaceCardShoppingList", "Shopping list items array is null.");
            return;
        }
        int[] iArr = LIST_ITEM_TEXT_VIEW_IDS;
        if (iArr.length < 3) {
            Log.w("BcSmartspaceCardShoppingList", String.format(Locale.US, "Missing %d list item view(s) to update", Integer.valueOf(3 - iArr.length)));
            return;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            TextView textView = (TextView) findViewById(LIST_ITEM_TEXT_VIEW_IDS[i2]);
            if (textView == null) {
                Log.w("BcSmartspaceCardShoppingList", String.format(Locale.US, "Missing list item view to update at row: %d", Integer.valueOf(i2 + 1)));
                return;
            }
            if (i2 < strArr.length) {
                textView.setVisibility(0);
                textView.setText(strArr[i2]);
            } else {
                textView.setVisibility(8);
                textView.setText("");
            }
        }
    }
}
