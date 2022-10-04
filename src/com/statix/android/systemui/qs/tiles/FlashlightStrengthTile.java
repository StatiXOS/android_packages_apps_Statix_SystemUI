package com.statix.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraCharacteristics.Key;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.systemui.R;
import com.android.systemui.animation.DialogLaunchAnimator;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.FlashlightController;

import com.statix.android.systemui.qs.tiles.dialog.FlashlightStrengthDialog;

import java.util.concurrent.Executor;
import javax.inject.Inject;

public class FlashlightStrengthTile extends QSTileImpl<BooleanState> implements
        FlashlightController.FlashlightListener {

    private static Key<Integer> FLASHLIGHT_MAX_BRIGHTNESS_CHARACTERISTIC =
            CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL;
    private static Key<Integer> FLASHLIGHT_DEFAULT_BRIGHTNESS_CHARACTERISTIC =
            CameraCharacteristics.FLASH_INFO_STRENGTH_DEFAULT_LEVEL;

    private static String FLASHLIGHT_BRIGHTNESS_SETTING = "flashlight_brightness";

    private final Icon mIcon = ResourceIcon.get(com.android.internal.R.drawable.ic_qs_flashlight);

    private CameraManager mCameraManager;
    private DialogLaunchAnimator mDialogLaunchAnimator;
    private FlashlightController mFlashlightController;
    private @Main Handler mHandler;
    private @Background Executor mBgExecutor;
    private boolean mSupportsSettingFlashLevel;
    private int mMaxLevel;
    private int mDefaultLevel;

    @Nullable
    private String mCameraId;

    @Inject
    public FlashlightStrengthTile(
            QSHost host,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger,
            FlashlightController flashlightController,
            DialogLaunchAnimator dialogLaunchAnimator,
            @Background Executor bgExecutor
    ) {
        super(host, backgroundLooper, mainHandler, falsingManager,
              metricsLogger, statusBarStateController, activityStarter,
              qsLogger);
        mBgExecutor = bgExecutor;
        mDialogLaunchAnimator = dialogLaunchAnimator;
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        mFlashlightController = flashlightController;
        mFlashlightController.observe(getLifecycle(), this);
        mHandler = mainHandler;
        try {
            mCameraId = getCameraId();
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            mSupportsSettingFlashLevel = flashlightController.isAvailable() && mCameraId != null &&
                    characteristics.get(FLASHLIGHT_MAX_BRIGHTNESS_CHARACTERISTIC) > 1;
            mMaxLevel = (int) characteristics.get(FLASHLIGHT_MAX_BRIGHTNESS_CHARACTERISTIC);
            mDefaultLevel = (int) characteristics.get(FLASHLIGHT_DEFAULT_BRIGHTNESS_CHARACTERISTIC);
        } catch (CameraAccessException e) {
            mCameraId = null;
            mSupportsSettingFlashLevel = false;
            mMaxLevel = 0;
            mDefaultLevel = 0;
        }
    }

    @Override
    public BooleanState newTileState() {
        BooleanState state = new BooleanState();
        state.handlesLongClick = true;
        return state;
    }

    @Override
    protected void handleLongClick(@Nullable View view) {
        if (mSupportsSettingFlashLevel) {
            mHandler.post(() -> openDialogWindow(view));
        } else {
            handleClick(view);
        }
    }

    @Override
    public Intent getLongClickIntent() {
        return new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
    }

    @Override
    protected void handleClick(@Nullable View view) {
        boolean newState = !mState.value;
        refreshState(newState);
        if (mSupportsSettingFlashLevel && newState) {
            try {
                int currentBrightness = Settings.System.getInt(mContext.getContentResolver(), FLASHLIGHT_BRIGHTNESS_SETTING, -1);
                int level = currentBrightness == -1 ? mDefaultLevel : currentBrightness;
                mCameraManager.turnOnTorchWithStrengthLevel(mCameraId, level);
            } catch (CameraAccessException e) {
            }
        } else {
            mFlashlightController.setFlashlight(newState);
        }
    }


    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_flashlight_label);
    }

    @Override
    public boolean isAvailable() {
        return mFlashlightController.hasFlashlight();
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        if (state.slash == null) {
            state.slash = new SlashState();
        }
        state.label = mHost.getContext().getString(R.string.quick_settings_flashlight_label);
        state.secondaryLabel = "";
        state.stateDescription = "";
        if (!mFlashlightController.isAvailable()) {
            state.icon = mIcon;
            state.slash.isSlashed = true;
            state.secondaryLabel = mContext.getString(
                    R.string.quick_settings_flashlight_camera_in_use);
            state.stateDescription = state.secondaryLabel;
            state.state = Tile.STATE_UNAVAILABLE;
            return;
        }
        if (arg instanceof Boolean) {
            boolean value = (Boolean) arg;
            if (value == state.value) {
                return;
            }
            state.value = value;
        } else {
            state.value = mFlashlightController.isEnabled();
        }
        state.icon = mIcon;
        state.slash.isSlashed = !state.value;
        state.contentDescription = mContext.getString(R.string.quick_settings_flashlight_label);
        state.expandedAccessibilityClassName = Switch.class.getName();
        state.state = state.value ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
        if (mSupportsSettingFlashLevel && state.value) {
            int currentBrightness = Settings.System.getInt(mContext.getContentResolver(), FLASHLIGHT_BRIGHTNESS_SETTING, 100);
            state.secondaryLabel = mContext.getString(R.string.brightness_percentage_secondary_label, (int) (((double) currentBrightness/(double) mMaxLevel) * 100));
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.QS_FLASHLIGHT;
    }

    @Override
    public void onFlashlightChanged(boolean enabled) {
        refreshState(enabled);
    }

    @Override
    public void onFlashlightError() {
        refreshState(false);
    }

    @Override
    public void onFlashlightAvailabilityChanged(boolean available) {
        refreshState();
    }

    private void openDialogWindow(@Nullable View view) {
        FlashlightStrengthDialog flashlightDialog = new FlashlightStrengthDialog(mContext, mBgExecutor, mMaxLevel, mCameraManager, mCameraId);
        if (view != null) {
            mDialogLaunchAnimator.showFromView(flashlightDialog, view, true);
        } else {
            flashlightDialog.show();
        }
    }

    private String getCameraId() throws CameraAccessException {
        String[] ids = mCameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null && flashAvailable
                    && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
    }
}
