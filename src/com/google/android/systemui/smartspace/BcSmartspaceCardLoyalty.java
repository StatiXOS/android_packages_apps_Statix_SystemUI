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
/* loaded from: classes2.dex */
public class BcSmartspaceCardLoyalty extends BcSmartspaceCardGenericImage {
    private TextView mCardPromptView;
    private ImageView mLoyaltyProgramLogoView;
    private TextView mLoyaltyProgramNameView;

    public BcSmartspaceCardLoyalty(Context context) {
        super(context);
    }

    public BcSmartspaceCardLoyalty(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        super.setSmartspaceActions(smartspaceTarget, smartspaceEventNotifier, bcSmartspaceCardLoggingInfo);
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        Bundle extras = baseAction == null ? null : baseAction.getExtras();
        this.mImageView.setVisibility(8);
        this.mLoyaltyProgramLogoView.setVisibility(8);
        this.mLoyaltyProgramNameView.setVisibility(8);
        this.mCardPromptView.setVisibility(8);
        if (extras != null) {
            boolean containsKey = extras.containsKey("imageBitmap");
            if (extras.containsKey("cardPrompt")) {
                setCardPrompt(extras.getString("cardPrompt"));
                this.mCardPromptView.setVisibility(0);
                if (containsKey) {
                    this.mImageView.setVisibility(0);
                }
                return true;
            } else if (!extras.containsKey("loyaltyProgramName")) {
                if (containsKey) {
                    this.mLoyaltyProgramLogoView.setVisibility(0);
                }
                return containsKey;
            } else {
                setLoyaltyProgramName(extras.getString("loyaltyProgramName"));
                this.mLoyaltyProgramNameView.setVisibility(0);
                if (containsKey) {
                    this.mLoyaltyProgramLogoView.setVisibility(0);
                }
                return true;
            }
        }
        return false;
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mLoyaltyProgramLogoView = (ImageView) findViewById(R.id.loyalty_program_logo);
        this.mLoyaltyProgramNameView = (TextView) findViewById(R.id.loyalty_program_name);
        this.mCardPromptView = (TextView) findViewById(R.id.card_prompt);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardGenericImage
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        this.mLoyaltyProgramLogoView.setImageBitmap(bitmap);
    }

    void setCardPrompt(String str) {
        TextView textView = this.mCardPromptView;
        if (textView == null) {
            Log.w("BcSmartspaceCardLoyalty", "No card prompt view to update");
        } else {
            textView.setText(str);
        }
    }

    void setLoyaltyProgramName(String str) {
        TextView textView = this.mLoyaltyProgramNameView;
        if (textView == null) {
            Log.w("BcSmartspaceCardLoyalty", "No loyalty program name view to update");
        } else {
            textView.setText(str);
        }
    }
}
