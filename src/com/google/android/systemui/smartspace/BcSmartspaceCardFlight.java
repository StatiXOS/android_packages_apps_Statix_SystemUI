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
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.systemui.bcsmartspace.R;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.google.android.systemui.smartspace.logging.BcSmartspaceCardLoggingInfo;

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

    @Override
    public boolean setSmartspaceActions(SmartspaceTarget smartspaceTarget, BcSmartspaceDataPlugin.SmartspaceEventNotifier smartspaceEventNotifier, BcSmartspaceCardLoggingInfo bcSmartspaceCardLoggingInfo) {
        SmartspaceAction baseAction = smartspaceTarget.getBaseAction();
        Bundle extras = baseAction == null ? null : baseAction.getExtras();
        boolean z = true;
        if (extras != null) {
            mBoardingPassUI.setVisibility(View.GONE);
            mCardPromptView.setVisibility(View.GONE);
            mCardPromptLogoView.setVisibility(View.GONE);
            if (extras.containsKey("cardPrompt") || extras.containsKey("cardPromptBitmap")) {
                if (extras.containsKey("cardPrompt")) {
                    setCardPrompt(extras.getString("cardPrompt"));
                    mCardPromptView.setVisibility(View.VISIBLE);
                }
                if (!extras.containsKey("cardPromptBitmap")) {
                    return true;
                }
                setCardPromptLogo((Bitmap) extras.get("cardPromptBitmap"));
                mCardPromptLogoView.setVisibility(View.VISIBLE);
                return true;
            }
            if (extras.containsKey("qrCodeBitmap")) {
                setFlightQrCode((Bitmap) extras.get("qrCodeBitmap"));
                mBoardingPassUI.setVisibility(View.VISIBLE);
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
        mCardPromptView = (TextView) findViewById(R.id.card_prompt);
        mCardPromptLogoView = (ImageView) findViewById(R.id.card_prompt_logo);
        mBoardingPassUI = (ConstraintLayout) findViewById(R.id.boarding_pass_ui);
        if (mBoardingPassUI != null) {
            mGateValueView = (TextView) mBoardingPassUI.findViewById(R.id.gate_value);
            mSeatValueView = (TextView) mBoardingPassUI.findViewById(R.id.seat_value);
            mQrCodeView = (ImageView) mBoardingPassUI.findViewById(R.id.flight_qr_code);
        }
    }

    protected void setCardPrompt(String str) {
        if (mCardPromptView == null) {
            Log.w("BcSmartspaceCardFlight", "No card prompt view to update");
        } else {
            mCardPromptView.setText(str);
        }
    }

    protected void setCardPromptLogo(Bitmap bitmap) {
        if (mCardPromptLogoView == null) {
            Log.w("BcSmartspaceCardFlight", "No card prompt logo view to update");
        } else {
            mCardPromptLogoView.setImageBitmap(bitmap);
        }
    }

    void setFlightGateText(CharSequence charSequence) {
        if (mGateValueView == null) {
            Log.w("BcSmartspaceCardFlight", "No flight gate value view to update");
        } else {
            mGateValueView.setText(charSequence);
        }
    }

    void setFlightSeatText(CharSequence charSequence) {
        if (mSeatValueView == null) {
            Log.w("BcSmartspaceCardFlight", "No flight seat value view to update");
        } else {
            mSeatValueView.setText(charSequence);
        }
    }

    void setFlightQrCode(Bitmap bitmap) {
        if (mQrCodeView == null) {
            Log.w("BcSmartspaceCardFlight", "No flight QR code view to update");
        } else {
            mQrCodeView.setImageBitmap(bitmap);
        }
    }
}
