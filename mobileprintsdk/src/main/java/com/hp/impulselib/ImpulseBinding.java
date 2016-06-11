package com.hp.impulselib;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.hp.impulselib.util.BoundServiceConnection;
import com.hp.impulselib.util.Consumer;

import java.util.List;

public class ImpulseBinding extends BoundServiceConnection<ImpulseService> {
    private static final String LOG_TAG = "ImpulseBinding";

    /** Construct a new binding. Get one by calling Impulse.bind() */
    ImpulseBinding(Context context) {
        super(context, ImpulseService.class);
    }

    /** Set up service and retrieve current status */
    public void init(final OperationListener initListener) {
        whenConnected(new Consumer<ImpulseService>() {
            @Override
            public void accept(ImpulseService service) {
                int initResult = service.init();
                if (initResult == Impulse.ErrorNone) {
                    initListener.onDone();
                } else {
                    initListener.onError(initResult);
                }
            }
        });
    }

    /** Continuously attempt to discover devices until stopped or an error occurs */
    public AutoCloseable discover(DiscoverListener listener) {
        Log.d(LOG_TAG, "discover()");
        return new DiscoverOperation(listener).start();
    }

    /**
     * Start tracking a device to receive info about its status. Note that tracking any
     * device will pause discovery
     */
    public AutoCloseable track(ImpulseDevice device, TrackListener listener) {
        Log.d(LOG_TAG, "track() " + device);
        return new TrackOperation(device, listener).start();
    }

    /**
     * Deliver a bitmap to the device for printing. Device resolution is 640x960; plan
     * your bitmap accordingly
     */
    public AutoCloseable send(ImpulseDevice device, Bitmap bitmap,
                              SendListener listener) {
        Log.d(LOG_TAG, "send() " + device);
        return new SendOperation(device, bitmap, listener).start();
    }

    /**
     * Sets options for a device. TrackListener will be called once with results.
     */
    public void setOptions(final ImpulseDevice device, final ImpulseDeviceOptions options, final TrackListener listener) {
        whenConnected(new Consumer<ImpulseService>() {
            @Override
            public void accept(ImpulseService impulse) {
                int initResult = impulse.init();
                if (initResult == Impulse.ErrorNone) {
                    impulse.setOptions(device, options, listener);
                } else {
                    listener.onError(initResult);
                }
            }
        });
    }

    // Operation classes below...

    private class DiscoverOperation extends Operation<DiscoverListener> implements DiscoverListener {
        private final static String LOG_TAG = "DiscoverOperation";

        DiscoverOperation(DiscoverListener listener) {
            super(listener);
        }

        @Override
        public void onStart() {
            Log.d(LOG_TAG, "onStart()");
            getService().discover(this);
        }

        @Override
        public void onClose() {
            Log.d(LOG_TAG, "onClose()");
            getService().stopDiscovery(this);
        }

        @Override
        public void onDevices(List<ImpulseDevice> devices) {
            Log.d(LOG_TAG, "onDevices()");
            mListener.onDevices(devices);
        }
    }

    private class TrackOperation extends Operation<TrackListener> implements TrackListener {
        ImpulseDevice mDevice;

        TrackOperation(ImpulseDevice device, TrackListener listener) {
            super(listener);
            mDevice = device;
        }

        @Override
        public void onStart() {
            getService().track(mDevice, this);
        }

        @Override
        public void onClose() {
            getService().untrack(mDevice, this);
        }

        @Override
        public void onState(ImpulseDeviceState info) {
            mListener.onState(info);
        }
    }

    private class SendOperation extends Operation<SendListener> implements SendListener {
        ImpulseDevice mDevice;
        Bitmap mBitmap;
        ImpulseJob mJob;

        SendOperation(ImpulseDevice device, Bitmap bitmap, SendListener listener) {
            super(listener);
            mDevice = device;
            mBitmap = bitmap;
        }

        @Override // Operation
        public void onStart() {
            mJob = getService().send(mDevice, mBitmap, mListener);
        }

        @Override // Operation
        public void onClose() {
            if (mJob != null) {
                // TODO: If we could issue a cancel we would do it here
            }
        }

        @Override // SendListener
        public void onProgress(int total, int sent) {
            mListener.onProgress(total, sent);
        }

        @Override // SendListener
        public void onDone() {
            mListener.onDone();
            mJob = null;
            close();
        }
    }

    abstract class Operation<L extends ErrorListener> implements AutoCloseable, Consumer<ImpulseService>, ErrorListener {
        L mListener;
        boolean mStarted;

        Operation(L listener) {
            mListener = listener;
        }

        /** Call to begin the operation */
        AutoCloseable start() {
            whenConnected(this);
            return this;
        }

        /** This happens next, when the service is available */
        @Override
        public void accept(ImpulseService impulse) {
            int error = impulse.init();
            if (error != Impulse.ErrorNone) {
                mListener.onError(error);
            } else {
                mStarted = true;
                cleanup(this);
                onStart();
            }
        }

        /** Then, your subclass receives this to get things started */
        abstract public void onStart();

        /** If something goes wrong, this gets called by the service */
        @Override
        public void onError(int errorCode) {
            mStarted = false;
            removeCleanup(this);
            mListener.onError(errorCode);
        }

        /** When someone is ready to cancel this operation, this gets called */
        @Override
        public void close() {
            cancelWhenConnected(this);
            if (mStarted) {
                mStarted = false;
                removeCleanup(this);
                if (getService() != null) onClose();
            }
        }

        /** Called on your subclass to shut things down */
        abstract public void onClose();
    }
}
