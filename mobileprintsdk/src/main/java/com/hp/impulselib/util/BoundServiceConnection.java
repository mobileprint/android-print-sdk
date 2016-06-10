package com.hp.impulselib.util;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Simplifies the process of managing a bound service.
 */
public class BoundServiceConnection<T extends Service> implements AutoCloseable {
    private static final String LOG_TAG = "BoundServiceConnection";

    private final Context mContext;
    private ServiceConnection mConnection;
    private T mService;
    private List<Consumer<T>> mCallbacks = new ArrayList<>();
    private List<AutoCloseable> mCleanups = new ArrayList<>();

    /** On bind, the service must return an instance of ServiceBinder */
    public static abstract class Binder<T> extends android.os.Binder {
        protected abstract T getService();
    }
    public BoundServiceConnection(Context context, Class<T> cls) {
        mContext = context;
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mService = ((Binder<T>) iBinder).getService();
                for (Consumer<T> callback : mCallbacks) {
                    callback.accept(mService);
                }
                mCallbacks.clear();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mService = null;
                mConnection = null;
            }
        };

        context.bindService(new Intent(context, cls),
                mConnection, Context.BIND_AUTO_CREATE);
    }

    /** Return the service or null if not connected */
    public T getService() {
        return mService;
    }

    /** Do something when connected, or immediately if already connected */
    public void whenConnected(Consumer<T> callback) {
        if (mService != null) {
            callback.accept(mService);
        } else {
            mCallbacks.add(callback);
        }
    }

    /** Abort a pending request if possible */
    public void cancelWhenConnected(Consumer<T> callback) {
        mCallbacks.remove(callback);
    }

    protected void cleanup(AutoCloseable closer) {
        mCleanups.add(closer);
    }

    protected void removeCleanup(AutoCloseable closer) {
        mCleanups.remove(closer);
    }

    /** Clear all activity managed through this binding */
    public void clear() {
        while(!mCleanups.isEmpty()) {
            try {
                mCleanups.remove(0).close();
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    public void close() {
        if (mConnection != null) {
            clear();
            mContext.unbindService(mConnection);
            mConnection = null;
            mService = null;
            mCallbacks.clear();
        }
    }
}
