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
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;
/* loaded from: classes2.dex */
public class BcSmartspaceCardFlight extends BcSmartspaceCardSecondary {
    private ConstraintLayout mBoardingPassUI;
    private ImageView mCardPromptLogoView;
    private TextView mCardPromptView;
    private TextView mGateValueView;
    private ImageView mQrCodeView;
    private TextView mSeatValueView;

    public BcSmartspaceCardFlight(Context context) {
        super(context);
    }

    public BcSmartspaceCardFlight(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.systemui.smartspace.BcSmartspaceCardSecondary
    public boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        Bundle extras = baseAction == null ? null : baseAction.getExtras();
        boolean z = true;
        if (extras != null) {
            this.mBoardingPassUI.setVisibility(8);
            this.mCardPromptView.setVisibility(8);
            this.mCardPromptLogoView.setVisibility(8);
            if (extras.containsKey("cardPrompt") || extras.containsKey("cardPromptBitmap")) {
                if (extras.containsKey("cardPrompt")) {
                    setCardPrompt(extras.getString("cardPrompt"));
                    this.mCardPromptView.setVisibility(0);
                }
                if (!extras.containsKey("cardPromptBitmap")) {
                    return true;
                }
                setCardPromptLogo((Bitmap) extras.get("cardPromptBitmap"));
                this.mCardPromptLogoView.setVisibility(0);
                return true;
            }
            if (extras.containsKey("qrCodeBitmap")) {
                setFlightQrCode((Bitmap) extras.get("qrCodeBitmap"));
                this.mBoardingPassUI.setVisibility(0);
            } else {
                z = false;
            }
            if (extras.containsKey("gate")) {
                setFlightGateText(extras.getString("gate"));
            } else {
                setFlightGateText("-");
            }
            if (extras.containsKey("seat")) {
                setFlightSeatText(extras.getString("seat"));
                return z;
            }
            setFlightSeatText("-");
            return z;
        }
        return false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCardPromptView = (TextView) findViewById(R.id.card_prompt);
        this.mCardPromptLogoView = (ImageView) findViewById(R.id.card_prompt_logo);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.boarding_pass_ui);
        this.mBoardingPassUI = constraintLayout;
        if (constraintLayout != null) {
            this.mGateValueView = (TextView) constraintLayout.findViewById(R.id.gate_value);
            this.mSeatValueView = (TextView) this.mBoardingPassUI.findViewById(R.id.seat_value);
            this.mQrCodeView = (ImageView) this.mBoardingPassUI.findViewById(R.id.flight_qr_code);
        }
    }

    protected void setCardPrompt(String str) {
        TextView textView = this.mCardPromptView;
        if (textView == null) {
            Log.w("BcSmartspaceCardFlight", "No card prompt view to update");
        } else {
            textView.setText(str);
        }
    }

    protected void setCardPromptLogo(Bitmap bitmap) {
        ImageView imageView = this.mCardPromptLogoView;
        if (imageView == null) {
            Log.w("BcSmartspaceCardFlight", "No card prompt logo view to update");
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    void setFlightGateText(CharSequence charSequence) {
        TextView textView = this.mGateValueView;
        if (textView == null) {
            Log.w("BcSmartspaceCardFlight", "No flight gate value view to update");
        } else {
            textView.setText(charSequence);
        }
    }

    void setFlightSeatText(CharSequence charSequence) {
        TextView textView = this.mSeatValueView;
        if (textView == null) {
            Log.w("BcSmartspaceCardFlight", "No flight seat value view to update");
        } else {
            textView.setText(charSequence);
        }
    }

    void setFlightQrCode(Bitmap bitmap) {
        ImageView imageView = this.mQrCodeView;
        if (imageView == null) {
            Log.w("BcSmartspaceCardFlight", "No flight QR code view to update");
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }
}
