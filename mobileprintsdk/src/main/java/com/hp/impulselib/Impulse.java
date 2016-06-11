package com.hp.impulselib;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

public class Impulse {

    public final static int ErrorNone = 0x00;

    /** Error codes (from device) */
    public final static int ErrorBusy = 0x01;
    public final static int ErrorPaperJam = 0x02;
    public final static int ErrorPaperEmpty = 0x03;
    public final static int ErrorPaperMismatch = 0x04;
    public final static int ErrorDataError = 0x05;
    public final static int ErrorCoverOpen = 0x06;
    public final static int ErrorSystemError = 0x07;
    public final static int ErrorBatteryLow = 0x08;
    public final static int ErrorBatteryFault = 0x09;
    public final static int ErrorHighTemperature = 0x0A;
    public final static int ErrorLowTemperature = 0x0B;
    public final static int ErrorCooling = 0x0C;
    public final static int ErrorCancel = 0x0D;

    /** Error codes (from service) */
    public static final int ErrorBluetoothDisabled = 0x100;
    public static final int ErrorBluetoothPermissions = 0x101;
    public static final int ErrorBluetoothDiscovery = 0x102;
    public static final int ErrorConnectionFailed = 0x103;
    public static final int ErrorUnknown = 0x1FF;

    static String[] Permissions = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /** Creates a connection to the impulse service */
    public static ImpulseBinding bind(Context context) {
        return new ImpulseBinding(context);
    }

    /** Return an ImpulseDevice object based on a known MAC address */
    public static ImpulseDevice toDevice(String address) {
        // TODO: This will blow out pretty good if Bluetooth isn't enabled
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(address);
        return new ImpulseDevice.Builder(device).build();
    }

    /** Return the array of permissions required for this service to operate correctly */
    public static String[] getRequiredPermissions() {
        String copy[] = new String[Permissions.length];
        System.arraycopy(Permissions, 0, copy, 0, Permissions.length);
        return copy;
    }
}
