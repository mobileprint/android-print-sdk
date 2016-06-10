package com.hp.impulselib.bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.hp.impulselib.util.GrowableInputStream;
import com.hp.impulselib.util.Tasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class RfcommClient implements AutoCloseable {
    private static final String LOG_TAG = "RfcommClient";

    private final BluetoothDevice mDevice;
    private final UUID mService;
    private RfcommListener mListener;
    private BluetoothSocket mSocket;
    private Tasks mWriteTasks;
    private AutoCloseable mReadTask;
    GrowableInputStream mToRead = new GrowableInputStream();

    /** Creates an Rfcomm client connection, notifying the listener for interesting events */
    public RfcommClient(BluetoothDevice device, final UUID service, final RfcommListener listener) {
        Log.d(LOG_TAG, "RfcommClient()");
        mService = service;
        mDevice = device;
        mListener = listener;
        
        mWriteTasks = new Tasks();
        mWriteTasks.queue(new Tasks.Task() {
            @Override
            public void run() throws IOException {
                mSocket = mDevice.createRfcommSocketToServiceRecord(mService);
                mSocket.connect();
                Tasks.runMain(new Runnable() {
                    @Override
                    public void run() {
                        listener.onConnect();
                    }
                });

                // Start a separate read task. It will quietly die when the socket is gone.
                mReadTask = Tasks.run(new ReadTask());
            }

            @Override
            public void onError(IOException exception) {
                Log.d(LOG_TAG, "(connect) onError");
                handleError(exception);
            }

            @Override
            public void onDone() {
            }
        });
    }

    private void handleError(IOException e) {
        if (mListener != null) {
            mListener.onError(e);
            mListener = null;
        }
        close();
    }

    /** Close the connection */
    @Override
    public void close() {
        Log.d(LOG_TAG, "close()");
        if (mReadTask != null) {
            try {
                mReadTask.close();
            } catch (Exception ignore) {
            }
            mReadTask = null;
        }

        if (mWriteTasks != null) {
            mWriteTasks.close();
            mWriteTasks = null;
        }

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException ignore) {
            }
            mSocket = null;
        }
    }

    /** Writes data to RFCOMM (when connected) */
    public void write(final byte[] data) {
        Log.d(LOG_TAG, "write() len=" + data.length);
        if (mWriteTasks == null) throw new RuntimeException("No connection");

        mWriteTasks.queue(new Tasks.Task() {
            @Override
            public void run() throws IOException {
                mSocket.getOutputStream().write(data);
            }

            @Override
            public void onError(IOException exception) {
                Log.d(LOG_TAG, "(write) onError()");
                handleError(exception);
            }

            @Override
            public void onDone() {
            }
        });
    }

    private class ReadTask implements Tasks.Task {
            @Override
            public void run() throws IOException {
                //noinspection InfiniteLoopStatement (We know the only way out is an exception)
                while(true) {
                    /** Read all data out of inputstream and onto the end of this stream */
                    InputStream in = mSocket.getInputStream();
                    int first = in.read();
                    final byte newBytes[] = new byte[1 + in.available()];
                    newBytes[0] = (byte) first;
                    if (in.available() > 0) {
                        //noinspection ResultOfMethodCallIgnored (we only read available data)
                        in.read(newBytes, 1, in.available());
                    }

                    // Deliver data to listener
                    Tasks.runMain(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(LOG_TAG, "(readData) checking mReadTasks ");
                            mToRead.add(newBytes);
                            try {
                                mListener.onData(mToRead);
                            } catch (IOException e) {
                                Log.d(LOG_TAG, "(readData)", e);
                                handleError(e);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(IOException exception) {
                Log.d(LOG_TAG, "(read) onError");
                handleError(exception);
            }

            @Override
            public void onDone() {
            }
    }


    /** Listen for important connection-related events. All events arrive on the main thread */
    public interface RfcommListener {
        /** Connection is established */
        void onConnect();

        /** New data is available to be read from the InputStream. */
        void onData(InputStream in) throws IOException;

        /** An exception occurred and the connection is considered closed */
        void onError(IOException e);
    }
}
