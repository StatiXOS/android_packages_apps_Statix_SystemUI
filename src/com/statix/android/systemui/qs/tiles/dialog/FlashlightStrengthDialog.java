package com.statix.android.systemui.qs.tiles.dialog;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.systemui.R;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.statusbar.phone.SystemUIDialog;

import java.util.concurrent.Executor;

@SysUISingleton
public class FlashlightStrengthDialog extends SystemUIDialog {

    private Context mContext;
    private @Background Executor mBgExecutor;
    private int mMaxLevel;
    protected View mDialogView;
    private String mCameraId;
    private CameraManager mCameraManager;
    private TextView mPercentText;

    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            try {
                if (progress == 0) {
                    mCameraManager.setTorchMode(mCameraId, false);
                    return;
                }
                mCameraManager.turnOnTorchWithStrengthLevel(mCameraId, progress);
                mPercentText.setText((int) (((double) progress/(double) mMaxLevel)*100) + "%\n brightness");
            } catch (CameraAccessException e) {
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Settings.System.putInt(mContext.getContentResolver(), "flashlight_brightness", seekBar.getProgress());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
    };

    public FlashlightStrengthDialog(Context context, @Background Executor bgExecutor, int maxLevel, CameraManager cameraManager, String cameraId) {
        super(context);
        mContext = context;
        mBgExecutor = bgExecutor;
        mCameraManager = cameraManager;
        mCameraId = cameraId;
        mMaxLevel = maxLevel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogView = LayoutInflater.from(mContext).inflate(R.layout.flashlight_strength_dialog,
                null);
        mPercentText = mDialogView.requireViewById(R.id.torch_strength_pct_text);
        final Window window = getWindow();
        window.setContentView(mDialogView);

        window.setWindowAnimations(R.style.Animation_FlashlightStrengthDialog);

        SeekBar seekBar = mDialogView.requireViewById(R.id.torch_strength_seekbar);
        seekBar.setOnSeekBarChangeListener(mSeekBarListener);
        seekBar.setMax(mMaxLevel);
        seekBar.setProgress(Settings.System.getInt(mContext.getContentResolver(), "flashlight_brightness", mMaxLevel));
    }

}
