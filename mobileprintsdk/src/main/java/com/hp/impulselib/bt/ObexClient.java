package com.hp.impulselib.bt;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.hp.impulselib.util.Bytes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ObexClient implements AutoCloseable {
    public static final String LOG_TAG = "ObexClient";

    private static final int DEFAULT_MAX_PACKET_LENGTH = 256;
    private static final int MAXIMUM_PACKET_LENGTH = 0x8000;

    private RfcommClient mRfcomm;
    private boolean mConnected = false;
    private int mMaxPacketLen = DEFAULT_MAX_PACKET_LENGTH;
    private List<Operation> mOperations = new LinkedList<>();

    public ObexClient(BluetoothDevice device, UUID service, final ConnectionListener connectionListener) {
        Log.d(LOG_TAG, "ObexClient() device=" + device);
        mRfcomm = new RfcommClient(device, service, new RfcommClient.RfcommListener() {
            @Override
            public void onConnect() {
                Log.d(LOG_TAG, "onConnect() (rfcomm)");
                // Connect OBEX
                queue(new ConnectOperation(connectionListener));
            }

            @Override
            public void onData(InputStream in) throws IOException {
                // Deliver received data to the current operation
                if (!mOperations.isEmpty()) mOperations.get(0).handleData(in);
            }

            @Override
            public void onError(IOException e) {
                Log.d(LOG_TAG, "onError() (rfcomm)");
                connectionListener.onError(e);
            }
        });
    }

    /**
     * Put an object to the device. On success, call the connection listener with
     * onPutSuccess and leave the connection open. On failure, call connection listener with
     * onError and close the connection.
     *
     * @param in Input data. Must be possible to read all of this data without blocking.
     */
    public void put(final String name, final String mimeType, final InputStream in, final PutListener putListener) {
        if (!mConnected) {
            throw new RuntimeException("OBEX connection not open");
        }
        queue(new PutOperation(name, mimeType, in, putListener));
    }

    @Override
    public void close() {
        mOperations.clear();
        if (mRfcomm != null) {
            mConnected = false;
            mRfcomm.close();
            mRfcomm = null;
        }
    }

    private void queue(Operation op) {
        mOperations.add(op);
        if (mOperations.size() == 1) {
            mOperations.get(0).start();
        }
    }

    private void dequeue(Operation op) {
        int index = mOperations.indexOf(op);
        mOperations.remove(op);
        if (index == 0) {
            mOperations.get(0).start();
        }
    }

    /** The current OBEX operation */
    private abstract class Operation {
        abstract void start();
        abstract void handleData(InputStream in) throws IOException;
    }

    private class ConnectOperation extends Operation {
        ConnectionListener mListener;
        public ConnectOperation(final ConnectionListener listener) {
            mListener = listener;
        }
        public void start() {
            Log.d(LOG_TAG, "(connect) start()");
            ByteBuffer extras = ByteBuffer.allocate(4)
                    .put((byte) 0x10) // Version 1.0
                    .put((byte) 0) // No flags
                    .putShort((short) 0xFF00);
            Obex.Packet connectReq = new Obex.Packet(Obex.OPCODE_CONNECT, extras.array());
            mRfcomm.write(connectReq.getBytes());
        }

        @Override
        public void handleData(InputStream in) throws IOException {
            Log.d(LOG_TAG, "(connect) handleData()");
            Obex.Packet connectRsp = Obex.Packet.read(in, 4);
            if (connectRsp == null) return;

            if (connectRsp.getCode() != Obex.RESPONSE_SUCCESS) {
                throw new IOException("Bad connect response " + connectRsp.getCode() + " expected " + Obex.RESPONSE_SUCCESS);
            }

            mMaxPacketLen = ByteBuffer.wrap(connectRsp.getExtras()).getShort(2) & 0xFFFF;
            mMaxPacketLen = Math.min(MAXIMUM_PACKET_LENGTH, mMaxPacketLen);
            mConnected = true;
            mListener.onConnect();
            dequeue(this);
        }
    }

    class PutOperation extends Operation {
        int mSent = 0;
        InputStream mIn;
        PutListener mListener;
        Obex.Packet mFirstPacket;

        public PutOperation(String name, String mimeType, InputStream in, PutListener listener) {
            // Safely read length from input stream
            int length;
            try {
                length = in.available();
            } catch (IOException e) {
                throw new RuntimeException("Request to get available data threw", e);
            }
            Log.d(LOG_TAG, "PutOperation() name=" + name + " type=" + mimeType + " length=" + length);

            mIn = in;
            mFirstPacket = new Obex.Packet(Obex.OPCODE_PUT)
                    .addHeader(new Obex.StringHeader(Obex.HEADER_NAME, name))
                    .addHeader(new Obex.StringHeader(Obex.HEADER_TYPE, mimeType))
                    .addHeader(new Obex.IntHeader(Obex.HEADER_LENGTH, length));
            mListener = listener;
        }

        @Override
        public void start() {
            Log.d(LOG_TAG, "(put) start()");
            // First packet just contains metadata
            mRfcomm.write(mFirstPacket.getBytes());
        }

        @Override
        public void handleData(InputStream obexData) throws IOException {
            Log.d(LOG_TAG, "(put) handleData()");
            Obex.Packet response = Obex.Packet.read(obexData, 0);
            if (response == null) return;
            try {
                handlePacket(response);
            } catch (IOException e) {
                mListener.onError(e);
                mIn.close();
                close();
            }
        }

        private void handlePacket(Obex.Packet response) throws IOException {
            if (response.getCode() == Obex.RESPONSE_CONTINUE) {
                mListener.onPutProgress(mSent);
                if (mIn.available() > 0) {
                    // Intermediate packets contain body
                    Obex.Packet packet = new Obex.Packet(Obex.OPCODE_PUT);
                    if (response.getCode() != Obex.RESPONSE_CONTINUE) {
                        throw new IOException("Bad response " + Bytes.toHex((byte) response.getCode()) +
                                " expected " + Bytes.toHex((byte) Obex.RESPONSE_CONTINUE));
                    }
                    // Remaining, with room for body header type/length
                    int remaining = packet.remaining(mMaxPacketLen) - 3;
                    byte data[] = new byte[Math.min(mIn.available(), remaining)];
                    int len = mIn.read(data);
                    if (len != data.length) {
                        throw new IOException("Could not read " + data.length +
                                " from input stream; length not " + mIn.available() + "?");
                    }
                    mSent += len;
                    packet.addHeader(new Obex.BytesHeader(Obex.HEADER_BODY, data));
                    mRfcomm.write(packet.getBytes());
                } else {
                    // Final packet contains end-of-body
                    Obex.Packet packet = new Obex.Packet(Obex.OPCODE_PUT_FINAL)
                            .addHeader(new Obex.BytesHeader(Obex.HEADER_END_OF_BODY, new byte[0]));
                    mRfcomm.write(packet.getBytes());
                }
            } else if (response.getCode() == Obex.RESPONSE_SUCCESS) {
                // We are done
                mListener.onPutSuccess();
                dequeue(this);
            } else {
                throw new IOException("Bad response " + Bytes.toHex((byte) response.getCode()) +
                        " expected " + Bytes.toHex((byte) Obex.RESPONSE_CONTINUE));
            }
        }
    }

    public interface ErrorListener {
        void onError(IOException e);
    }

    public interface ConnectionListener extends ErrorListener {
        void onConnect();
    }

    public interface PutListener extends ErrorListener {
        void onPutProgress(int bytes);
        void onPutSuccess();
    }
}
