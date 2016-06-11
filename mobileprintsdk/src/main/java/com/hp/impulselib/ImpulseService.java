package com.hp.impulselib;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.hp.impulselib.bt.ImpulseClient;
import com.hp.impulselib.util.BoundServiceConnection;
import com.hp.impulselib.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A service to manage access to Impulse devices.
 */
public class ImpulseService extends Service {
    private static final String LOG_TAG = "ImpulseService";

    private List<DiscoverListener> mDiscoverListeners = new ArrayList<>();
    private BroadcastReceiver mReceiver;
    private BluetoothAdapter mBluetoothAdapter;
    private Map<String, ImpulseDevice> mDevices = new HashMap<>();
    private Map<ImpulseDevice, TrackInfo> mTrackInfos = new HashMap<>();
    private List<ImpulseJob> mJobs = new ArrayList<>();
    private List<BluetoothDevice> mUuidQueue = new ArrayList<>();
    private class TrackInfo {
        List<TrackListener> listeners = new ArrayList<>();
        ImpulseClient client;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate()");
        init();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind() " + intent);
        return new BoundServiceConnection.Binder<ImpulseService>() {
            @Override
            protected ImpulseService getService() {
                return ImpulseService.this;
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "onUnbind() " + intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mBluetoothAdapter = null;
            mDevices.clear();
            mReceiver = null;
        }
    }

    /** Set everything up */
    int init() {
        // Already done?
        if (mBluetoothAdapter != null) return Impulse.ErrorNone;

        // Check permissions and fail if any are missing
        for (String permission : Impulse.Permissions) {
            if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, permission)) {
                return Impulse.ErrorBluetoothPermissions;
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter = null;
            return Impulse.ErrorBluetoothDisabled;
        }

        // Preload bonded devices
        Set<BluetoothDevice> bonded = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice: bonded) {
            ImpulseDevice device = new ImpulseDevice.Builder(bluetoothDevice)
                    .setBonded(true)
                    .build();
            if (device.isImpulse()) {
                mDevices.put(device.getAddress(), device);
            }
        }

        mReceiver = new ImpulseBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        registerReceiver(mReceiver, filter);

        return Impulse.ErrorNone;
    }

    /** Start continuous discovery of devices, reporting them to the callback */
    void discover(DiscoverListener discoverListener) {
        Log.d(LOG_TAG, "discover() " + discoverListener);
        // Signal back the current device set
        discoverListener.onDevices(
                Collections.unmodifiableList(new ArrayList<>(mDevices.values())));
        mDiscoverListeners.add(discoverListener);
        updateDiscovery();
    }

    /** Stop discovery of devices for the specified callback */
    void stopDiscovery(DiscoverListener discoverListener) {
        Log.d(LOG_TAG, "stopDiscovery()");
        mDiscoverListeners.remove(discoverListener);
        updateDiscovery();
    }

    private void updateDiscovery() {
        // Could be null if shutting down or if Bluetooth is disabled
        if (mBluetoothAdapter == null) return;

        if (mBluetoothAdapter.isDiscovering() &&
            (mDiscoverListeners.isEmpty() || !canDiscover())) {
            Log.d(LOG_TAG, "Cancelling discovery");
            mBluetoothAdapter.cancelDiscovery();
        } else if (!mBluetoothAdapter.isDiscovering() && !mDiscoverListeners.isEmpty() && canDiscover()) {
            Log.d(LOG_TAG, "Starting discovery");
            boolean result = mBluetoothAdapter.startDiscovery();
            if (!result) {
                Log.w(LOG_TAG, "Attempt to launch bluetooth discovery failed");
                // TODO: Consider if this happens to keep listeners around and try again later if Bluetooth becomes enabled.
                for (DiscoverListener listener: mDiscoverListeners) {
                    listener.onError(Impulse.ErrorBluetoothDiscovery);
                }
                mDiscoverListeners.clear();
            }
        }
    }

    private boolean canDiscover() {
        return mTrackInfos.isEmpty();
    }

    void track(final ImpulseDevice device, TrackListener listener) {
        Log.d(LOG_TAG, "track() " + device);
        TrackInfo info = mTrackInfos.get(device);
        if (info == null) {
            info = new TrackInfo();
            final TrackInfo trackInfo = info;
            mTrackInfos.put(device, info);

            info.client = new ImpulseClient(device.getDevice(), new ImpulseClient.ImpulseListener() {
                @Override
                public void onInfo(ImpulseDeviceState info) {
                    Log.d(LOG_TAG, "(track) onState()");
                    for (TrackListener listener : new ArrayList<>(trackInfo.listeners)) {
                        listener.onState(info);
                    }
                }

                @Override
                public void onError(IOException e) {
                    Log.d(LOG_TAG, "(track) onError()");
                    for (TrackListener listener : new ArrayList<>(trackInfo.listeners)) {
                        listener.onError(Impulse.ErrorConnectionFailed);
                    }
                    mTrackInfos.remove(device);
                }
            });
        }
        info.listeners.add(listener);
        updateDiscovery();
    }

    /**
     * Set options to the device. TrackListener will be called once with results and the operation
     * will be considered complete.
     */
    void setOptions(final ImpulseDevice device, final ImpulseDeviceOptions options, final TrackListener listener) {
        // Track
        track(device, new TrackListener() {
            boolean mSent = false;
            @Override
            public void onError(int errorCode) {
                listener.onError(errorCode);
            }

            @Override
            public void onState(ImpulseDeviceState info) {
                if (!mSent) {
                    // Read any missing state
                    ImpulseDeviceOptions.Builder builder = new ImpulseDeviceOptions.Builder(options);
                    if (options.getAutoExposure() == null) {
                        builder.setAutoExposure(info.getInfo().autoExposure);
                    }
                    if (options.getAutoPowerOff() == null) {
                        builder.setAutoPowerOff(info.getInfo().autoPowerOff);
                    }
                    if (options.getPrintMode() == null) {
                        builder.setPrintMode(info.getInfo().printMode);
                    }
                    mTrackInfos.get(device).client.setAccessoryInfo(builder.build());
                    mSent = true;
                } else {
                    if (info.getCommand() == ImpulseClient.CommandAccessoryInfo) {
                        listener.onState(info);
                        untrack(device, this);
                    } else {
                        Log.d(LOG_TAG, "Got command " + Bytes.toHex(info.getCommand()) + " so waiting...");
                    }
                }
            }
        });
    }

    void untrack(ImpulseDevice device, TrackListener listener) {
        Log.d(LOG_TAG, "untrack() " + device);
        TrackInfo info = mTrackInfos.get(device);
        if (info != null) {
            info.listeners.remove(listener);
            if (info.listeners.size() == 0) {
                mTrackInfos.remove(device);
                info.client.close();
            }
        }
        updateDiscovery();
    }

    ImpulseJob send(ImpulseDevice device, Bitmap bitmap, final SendListener listener) {
        Log.d(LOG_TAG, "sendBitmap() width=" + bitmap.getWidth() + " height=" + bitmap.getHeight());

        ImpulseJob job = new ImpulseJob(this, device, bitmap, listener);
        mJobs.add(job);
        job.start();
        return job;
    }

    void onJobEnd(ImpulseJob job) {
        mJobs.remove(job);
    }

    private class ImpulseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ImpulseDevice.isImpulse(device)) {
                    ImpulseDevice impulseDevice = new ImpulseDevice.Builder(intent)
                            .setBonded(mBluetoothAdapter.getBondedDevices())
                            .build();
                    discoverDevice(impulseDevice);
                } else if (ImpulseDevice.isImpulseClass(device)) {
                    Log.d(LOG_TAG, "Might have UUID, postponing device " + device.getName());
                    if (!mUuidQueue.contains(device)) {
                        mUuidQueue.add(device);
                    }
                } else {
                    Log.d(LOG_TAG, "Found non-Impulse device " + device.getName());
                }
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.d(LOG_TAG, action);
                queueNextUuidRequest();
            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                Log.d(LOG_TAG, action);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                Log.d(LOG_TAG, action + " device=" + device + " bondState=" + bondState);
                reloadBonded();
            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                Log.d(LOG_TAG, action);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ImpulseDevice.isImpulse(device)) {
                    Log.d(LOG_TAG, "Observed pairing request for an Impulse device; approving");
                    device.setPairingConfirmation(true);
                } else {
                    Log.d(LOG_TAG, "Observed pairing request for a non-impulse device; ignoring");
                }
            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_ACL_CONNECTED)) {
                Log.d(LOG_TAG, action);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.d(LOG_TAG, "need to create bond");
                    boolean bonding = device.createBond();
                    Log.d(LOG_TAG, "creating bond started " + bonding);
                }
            } else if (TextUtils.equals(action, BluetoothDevice.ACTION_UUID)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ImpulseDevice.isImpulse(device)) {
                    ImpulseDevice impulseDevice = new ImpulseDevice.Builder(intent)
                            .setBonded(mBluetoothAdapter.getBondedDevices())
                            .build();
                    discoverDevice(impulseDevice);
                }
                queueNextUuidRequest();
            } else {
                // Whatever
                Log.d(LOG_TAG, action);
            }
        }

        private void queueNextUuidRequest() {
            if (!mUuidQueue.isEmpty()) {
                BluetoothDevice device = mUuidQueue.remove(0);
                boolean result = device.fetchUuidsWithSdp();
                Log.d(LOG_TAG, "Fetching UUIDs over SDP for " + device.getName() + " result=" + result);
            } else {
                updateDiscovery();
            }
        }
    }

    private void reloadBonded() {
        Set<BluetoothDevice> allBonded = mBluetoothAdapter.getBondedDevices();

        // Build a hashset of bonded addresses
        Set<String> bondedAddresses = new HashSet<>();
        for (BluetoothDevice bondedDevice: allBonded) {
            bondedAddresses.add(bondedDevice.getAddress());
        }

        for (ImpulseDevice device: new ArrayList<>(mDevices.values())) {
            boolean currentlyBonded = bondedAddresses.contains(device.getAddress());
            if (device.getBonded() != currentlyBonded) {
                // Bonding state has changed so update
                ImpulseDevice updated = new ImpulseDevice.Builder(device)
                        .setBonded(currentlyBonded)
                        .build();
                discoverDevice(updated);
            }
        }
    }

    private void discoverDevice(ImpulseDevice device) {
        mDevices.put(device.getAddress(), device);
        notifyDevices();
    }

    private void notifyDevices() {
        List<ImpulseDevice> devices = Collections.unmodifiableList(new ArrayList<>(mDevices.values()));
        for (DiscoverListener listener: mDiscoverListeners) {
            Log.d(LOG_TAG, "Notifying listener about new devices: " + listener);
            listener.onDevices(devices);
        }
    }
}
