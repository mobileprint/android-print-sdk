package com.hp.impulselib;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Known info concerning an ImpulseDevice
 */
public class ImpulseDevice {
    private static final String LOG_TAG = "ImpulseDevice";

    /** UUIDs we have seen */
    private static final List<ParcelUuid> ImpulseUuids = new ArrayList<ParcelUuid>() {{
        add(new ParcelUuid(UUID.fromString("ffcacade-afde-cade-defa-cade00000000")));
        add(new ParcelUuid(UUID.fromString("00000000-deca-fade-deca-deafdecacaff")));
    }};

    private BluetoothDevice mBluetoothDevice;
    private short mRssi = 0;
    private long mRssiAt = Long.MIN_VALUE; // System clock at last rssi capture if any
    private boolean mBonded = false;
    private ImpulseDeviceState mState; // If known

    private ImpulseDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }

    private ImpulseDevice(ImpulseDevice other) {
        mBluetoothDevice = other.mBluetoothDevice;
        mRssi = other.mRssi;
        mRssiAt = other.mRssiAt;
        mBonded = other.mBonded;
        mState = other.mState;
    }

    public boolean getBonded() {
        return mBonded;
    }

    /** Return true if the device was seen within the timeout */
    public boolean isAvailable(long timeoutMs) {
        long now = new Date().getTime();
        return (mState != null && now - mState.getUpdated() < timeoutMs) ||
                (mRssi != 0 && now - mRssiAt < timeoutMs);
    }

    public int getRssi() {
        return mRssi;
    }

    public ImpulseDeviceState getState() {
        return mState;
    }

    public static class Builder {
        final ImpulseDevice mPrototype;

        public Builder(ImpulseDevice device) {
            mPrototype = new ImpulseDevice(device);
        }

        public Builder(BluetoothDevice device) {
            mPrototype = new ImpulseDevice(device);
        }

        public Builder(Intent intent) {
            mPrototype = new ImpulseDevice((BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
            mPrototype.mRssiAt = new Date().getTime();
            mPrototype.mRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
        }

        public Builder setBonded(Set<BluetoothDevice> bondList) {
            for (BluetoothDevice device: bondList) {
                if (device.getAddress().equals(mPrototype.mBluetoothDevice.getAddress())) {
                    mPrototype.mBonded = true;
                    return this;
                }
            }
            mPrototype.mBonded = false;
            return this;
        }

        public Builder setBonded(boolean bonded) {
            mPrototype.mBonded = bonded;
            return this;
        }

        public Builder setState(ImpulseDeviceState state) {
            mPrototype.mState = state;
            return this;
        }

        public ImpulseDevice build() {
            return new ImpulseDevice(mPrototype);
        }
    }

    public String getAddress() {
        return mBluetoothDevice.getAddress();
    }

    public BluetoothDevice getDevice() {
        return mBluetoothDevice;
    }

    /**
     * Return true if the device appears to be an Impulse
     */
    public boolean isImpulse() {
        return isImpulse(mBluetoothDevice);
    }

    /**
     * Return true if the device might be an Impulse. The search is based only on Class Of Device;
     * for a positive ID use "isImpulse" but this requires UUIDs be present.
     */
    public static boolean isImpulseClass(BluetoothDevice device) {
        BluetoothClass btClass = device.getBluetoothClass();
        return (btClass.hasService(BluetoothClass.Service.OBJECT_TRANSFER) &&
                (btClass.getMajorDeviceClass() == BluetoothClass.Device.Major.IMAGING) &&
                ((btClass.getDeviceClass() & 0xF0) == 0x80)); // 0x80 is the PRINTER bit
    }

    /**
     * Return true if we can be sure this device appears to be an Impulse
     */
    public static boolean isImpulse(BluetoothDevice device) {
        if (!isImpulseClass(device)) return false;
        ParcelUuid uuidArray[] = device.getUuids();
        if (uuidArray == null) return false;

        List<ParcelUuid> uuidList = Arrays.asList(uuidArray);
        for (ParcelUuid uuid : ImpulseUuids) {
            if (uuidList.contains(uuid)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ImpulseDevice(btDev=" + mBluetoothDevice +
                " name=" + mBluetoothDevice.getName() +
                " rssi=" + mRssi +
                " bonded=" + mBonded +
                " state=" + mState +
                ")";
    }
}
