package com.statix.android.systemui.face;

import android.annotation.Nullable;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.util.Log;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.theme.ThemeOverlayApplier;

public class FaceNotificationBroadcastReceiver extends BroadcastReceiver {

    private final Context mContext;
    private boolean mDidShowFailureDialog = false;

    public FaceNotificationBroadcastReceiver(Context context) {
        mContext = context;
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.e("FaceNotificationBCR", "Received broadcast with null action.");
            mContext.unregisterReceiver(this);
            return;
        }
        if (action.equals("face_action_show_reenroll_dialog")) {
            SystemUIDialog systemUIDialog = new SystemUIDialog(mContext);
            systemUIDialog.setTitle(mContext.getString(R.string.face_reenroll_dialog_title));
            systemUIDialog.setMessage(mContext.getString(R.string.face_reenroll_dialog_content));
            systemUIDialog.setPositiveButton(R.string.face_reenroll_dialog_confirm, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FaceManager faceManager = (FaceManager) mContext.getSystemService(FaceManager.class);
                    if (faceManager == null) {
                        Log.e("FaceNotificationDialogF", "Not launching enrollment. Face manager was null!");
                        SystemUIDialog systemUIDialog2 = new SystemUIDialog(mContext);
                        systemUIDialog2.setMessage(mContext.getText(R.string.face_reenroll_failure_dialog_content));
                        systemUIDialog2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {}
                        });
                        systemUIDialog2.show();
                        return;
                    }
                    faceManager.remove(new Face("", 0, 0L), mContext.getUserId(), new FaceManager.RemovalCallback() {
                        @Override
                        public final void onRemovalError(Face face, int errMsgId, CharSequence errString) {
                            Log.e("FaceNotificationDialogF", "Not launching enrollment. Failed to remove existing face(s).");
                            if (!mDidShowFailureDialog) {
                                mDidShowFailureDialog = true;
                                SystemUIDialog systemUIDialog3 = new SystemUIDialog(mContext);
                                systemUIDialog3.setMessage(mContext.getText(R.string.face_reenroll_failure_dialog_content));
                                systemUIDialog3.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {}
                                });
                                systemUIDialog3.show();
                            }
                        }

                        @Override
                        public final void onRemovalSucceeded(@Nullable Face face, int remaining) {
                            if (!mDidShowFailureDialog && remaining == 0) {
                                Intent biometricsIntent = new Intent("android.settings.BIOMETRIC_ENROLL");
                                biometricsIntent.setPackage("com.android.settings");
                                biometricsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(biometricsIntent);
                            }
                        }
                    });
                }
            });
            systemUIDialog.setNegativeButton(R.string.face_reenroll_dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {}
            });
            systemUIDialog.show();
        }
        mContext.unregisterReceiver(this);
    }
}
