package com.hp.impulselib;

import android.graphics.Bitmap;
import android.util.Log;

import com.hp.impulselib.bt.ImpulseClient;
import com.hp.impulselib.bt.ObexClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

class ImpulseJob {
    private final static String LOG_TAG = "ImpulseJob";
    private static final UUID OPP_UUID = UUID
            .fromString("00001105-0000-1000-8000-00805f9b34fb");

    private static final int StateInitial = 0;
    private static final int StateTracking = 1;
    private static final int StateConnectingObex = 2;
    private static final int StateSendingData = 3;
    private static final int StateWaitForPrintStart = 4;
    private static final int StateWaitForPrintEnd = 5;

    int mState  = StateInitial;
    ImpulseService mImpulse;
    ImpulseDevice mDevice;
    TrackListener mTrackListener;
    SendListener mSendListener;
    ObexClient mObexClient;
    Bitmap mBitmap;
    ByteArrayOutputStream mJpgOut;

    public ImpulseJob(ImpulseService impulse, ImpulseDevice device, Bitmap bitmap, SendListener sendListener) {
        mImpulse = impulse;
        mDevice = device;
        mBitmap = bitmap;
        mSendListener = sendListener;
    }

    public void start() {
        // Prepare the JPG in-memory
        mJpgOut = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, mJpgOut);

        // Start tracking to confirm the printer is alive & well
        mTrackListener = new ImpulseTrackListener();
        mImpulse.track(mDevice, mTrackListener);
        mState = StateTracking;
    }

    class ImpulseTrackListener implements TrackListener {
        @Override
        public void onState(ImpulseDeviceState state) {
            Log.d(LOG_TAG, "(TrackListener) state=" + mState + " " + state);
            int error = state.getError();

            switch(mState) {
                case StateTracking:
                    // Ignore if we have no error
                    if (error == Impulse.ErrorNone) {
                        mObexClient = new ObexClient(mDevice.getDevice(), OPP_UUID, new ObexConnectionListener());
                        mState = StateConnectingObex;
                    } else {
                        // Are there cases where we would block? Or should the caller handle that?
                        // How?
                        fail(error);
                    }
                    break;

                case StateConnectingObex:
                    // This one can fail on its own
                    break;

                case StateSendingData:
                    // During this phase we will receive ImpulseClient.CommandStartSend (0x200)
                    // And when OBEX is done successfully, ImpulseClient.CommandEndReceive (0x201)
                    // And when print begins ImpulseClient.CommandPrintStart (0x002)
                    // Eventually printer will go to error state of Impulse.ErrorBusy
                    // and it will stay there during the print; approx 25 sec
                    // Then error state will go to Impulse.ErrorNone and that's it.
                    if (state.getCommand() == ImpulseClient.CommandPrintStart) {
                        mState = StateWaitForPrintStart;
                    } else if (!(error == Impulse.ErrorNone || error == Impulse.ErrorBusy)) {
                        fail(error);
                    }
                    break;

                case StateWaitForPrintStart:
                    // Print has begun but wait for busy
                    if (error == Impulse.ErrorBusy) {
                        mState = StateWaitForPrintEnd;
                    } else if (error != Impulse.ErrorNone) {
                        fail(error);
                    }
                    break;

                case StateWaitForPrintEnd:
                    // Print is actually in progress at this time so wait for busy to be over
                    switch(error) {
                        case Impulse.ErrorBusy:
                            break;
                        case Impulse.ErrorNone:
                            mSendListener.onDone();
                            mImpulse.untrack(mDevice, mTrackListener);
                            mTrackListener = null;
                            mImpulse.onJobEnd(ImpulseJob.this);
                            break;
                        case Impulse.ErrorBatteryLow:
                            // TODO: We see this one go by sometimes during a print. The print comes out anyway
                            // so it doesn't really look like a problem and we could just wait for error none. But
                            // how do we know the error will ever clear?
                        default:
                            fail(error);
                            break;
                    }
                    break;
            }
        }

        @Override
        public void onError(int errorCode) {
            Log.d(LOG_TAG, "(TrackListener) onError() " + errorCode);
            mTrackListener = null;
            fail(errorCode);
        }
    }


    class ObexConnectionListener implements ObexClient.ConnectionListener {
        @Override
        public void onConnect() {
            Log.d(LOG_TAG, "(ObexConnectListener) onConnect()");
            mState = StateSendingData;
            // Start putting a bitmap
            mObexClient.put("img.jpg", "image/jpeg",
                    new ByteArrayInputStream(mJpgOut.toByteArray()),
                    new ObexPutListener());
        }

        @Override
        public void onError(IOException e) {
            Log.d(LOG_TAG, "(ObexConnectListener) onError()", e);
            mObexClient = null;
            fail(Impulse.ErrorConnectionFailed);
        }
    }

    class ObexPutListener implements ObexClient.PutListener {
        @Override
        public void onPutProgress(int sent) {
            Log.d(LOG_TAG, "(ObexPutListener) onPutProgress()");
            mSendListener.onProgress(mJpgOut.size(), sent);
        }

        @Override
        public void onPutSuccess() {
            Log.d(LOG_TAG, "(ObexPutListener) onPutSuccess()");
            Log.d(LOG_TAG, "onPutSuccess()");
            mObexClient.close();
        }

        @Override
        public void onError(IOException e) {
            Log.d(LOG_TAG, "(ObexPutListener) onError()", e);
            // TODO: Detect and signal OBEX error codes which could tell you
            // "door open" "no paper" etc.
            fail(Impulse.ErrorConnectionFailed);
        }
    }

    private void fail(int code) {
        if (mObexClient != null) {
            mObexClient.close();
            mObexClient = null;
        }
        if (mTrackListener != null) {
            mImpulse.untrack(mDevice, mTrackListener);
            mTrackListener = null;
        }
        if (mSendListener != null) {
            mSendListener.onError(code);
        }
        mImpulse.onJobEnd(this);
    }
}
