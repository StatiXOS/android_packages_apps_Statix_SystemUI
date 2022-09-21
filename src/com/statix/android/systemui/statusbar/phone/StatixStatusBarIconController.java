package com.statix.android.systemui.statusbar.phone;

import static com.statix.android.systemui.statusbar.phone.StatixStatusBarIconHolder.TYPE_BLUETOOTH;

import android.view.ViewGroup;

import com.android.systemui.flags.FeatureFlags;
import com.android.systemui.statusbar.phone.StatusBarIconHolder;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.phone.StatusBarIconController;

import com.statix.android.systemui.statusbar.StatusBarBluetoothView;
import com.statix.android.systemui.statusbar.phone.StatixPhoneStatusBarPolicy.BluetoothIconState;

public interface StatixStatusBarIconController extends StatusBarIconController {

    /** */
    void setBluetoothIcon(String slot, BluetoothIconState state);

    class StatixIconManager extends StatusBarIconController.IconManager {

        public StatixIconManager(ViewGroup group, FeatureFlags flags) {
            super(group, flags);
        }

        @Override
        protected StatusIconDisplayable addHolder(int index, String slot, boolean blocked,
                StatusBarIconHolder holder) {
            // This is a little hacky, and probably regrettable, but just set `blocked` on any icon
            // that is in our blocked list, then we'll never see it
            if (mBlockList.contains(slot)) {
                blocked = true;
            }
            switch (holder.getType()) {
                case TYPE_BLUETOOTH:
                    return addBluetoothIcon(index, slot, ((StatixStatusBarIconHolder) holder).getBluetoothState());
                default:
                    return super.addHolder(index, slot, blocked, holder);
            }
        }

        protected StatusBarBluetoothView addBluetoothIcon(
                int index, String slot, BluetoothIconState state) {
            StatusBarBluetoothView view = onCreateStatusBarBluetoothView(slot);
            view.applyBluetoothState(state);
            mGroup.addView(view, index, onCreateLayoutParams());
            return view;
        }

        private StatusBarBluetoothView onCreateStatusBarBluetoothView(String slot) {
            StatusBarBluetoothView view = StatusBarBluetoothView.fromContext(mContext, slot);
            return view;
        }

        @Override
        public void onSetIconHolder(int viewIndex, StatusBarIconHolder holder) {
            switch (holder.getType()) {
                case TYPE_BLUETOOTH:
                    onSetBluetoothIcon(viewIndex, ((StatixStatusBarIconHolder) holder).getBluetoothState());
                    return;
                default:
                    super.onSetIconHolder(viewIndex, holder);
                    return;
            }
        }

        public void onSetBluetoothIcon(int viewIndex, BluetoothIconState state) {
            StatusBarBluetoothView view = (StatusBarBluetoothView) mGroup.getChildAt(viewIndex);
            if (view != null) {
                view.applyBluetoothState(state);
            }
        }

    }

}
