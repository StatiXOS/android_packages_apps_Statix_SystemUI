package com.statix.android.systemui.statusbar.phone;

import static com.android.systemui.statusbar.phone.StatusBarIconHolder.TYPE_ICON;

import android.annotation.Nullable;

import com.android.systemui.statusbar.phone.StatusBarIconHolder;

import com.statix.android.systemui.statusbar.phone.StatixPhoneStatusBarPolicy.BluetoothIconState;

public class StatixStatusBarIconHolder extends StatusBarIconHolder {

    public static final int TYPE_BLUETOOTH = 3;

    private int mType = TYPE_ICON;

    private BluetoothIconState mBluetoothState;

    protected StatixStatusBarIconHolder() {
       super();
    }

    public static StatusBarIconHolder fromBluetoothIconState(BluetoothIconState state) {
        StatixStatusBarIconHolder holder = new StatixStatusBarIconHolder();
        holder.mBluetoothState = state;
        holder.mType = TYPE_BLUETOOTH;
        return holder;
    }

    @Nullable
    public BluetoothIconState getBluetoothState() {
        return mBluetoothState;
    }

    public void setBluetoothState(BluetoothIconState state) {
        mBluetoothState = state;
    }

    @Override
    public boolean isVisible() {
        switch(mType) {
            case TYPE_BLUETOOTH:
                return mBluetoothState.visible;
            default:
                return super.isVisible();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (isVisible() == visible) {
            return;
        }
        switch(mType) {
            case TYPE_BLUETOOTH:
                mBluetoothState.visible = visible;
                return;
            default:
                super.setVisible(visible);
                return;
        }
    }

}
