package com.hp.impulselib.bt;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.hp.impulselib.ImpulseDeviceOptions;
import com.hp.impulselib.ImpulseDeviceState;
import com.hp.impulselib.util.Bytes;
import com.hp.impulselib.util.Tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ImpulseClient implements AutoCloseable {
    private static final String LOG_TAG = "ImpulseClient";

    private static final UUID UuidSpp = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final long PollingTimeMs = 1000;

    /** Commands exchanged with device */
    public static int CommandPrintReady = 0x0000; // Client, "Print status check for Manta"
    public static int CommandPrintCancel = 0x0001; // Client, "Image data sending cancellation to manta (Android Only)"
    public static int CommandPrintStart = 0x0002;  // Device, "Notice Image Printing of Manta"
    public static int CommandPrintFinish = 0x0003;  // Device "Notice Image Printing complete of Manta (delete by Multi-send issue)"
    public static int CommandGetAccessoryInfo = 0x0100; // Client "Request current device information for Manta"
    public static int CommandSetAccessoryInfo = 0x0101; // Client "Set the device information for Manta"
    public static int CommandAccessoryInfo = 0x0102; // Device "Forward current device information for Manta"
    public static int CommandStartSend = 0x0200; // Device "Notice Image Data Sending start for Manta"
    public static int CommandEndReceive = 0x0201; // Device "Notice Image Data Sending complete for Manta"
    public static int CommandUpgradeReady = 0x0300; // Client "Upgrade status check for Manta"
    public static int CommandUpgradeCancel = 0x0301; // Client "Firmware Data Sending Cancelation for Manta"
    public static int CommandUpgradeAck = 0x0302; // Device "Forward Current Device's Upgrade Information for Manta"
    public static int CommandErrorMessage = 0x0400; // Device "Forward Error status of Manta"
    public static int CommandBulkTransfer = 0x0500; // "Use for data transfer (FW, TMD) via SPP for Manta"

    private final BluetoothDevice mDevice;
    private RfcommClient mRfcomm;
    private ImpulseDeviceState.Builder mState = new ImpulseDeviceState.Builder();
    private Timer mQueryTimer;

    public ImpulseClient(BluetoothDevice device, final ImpulseListener listener) {
        Log.d(LOG_TAG, "ImpulseClient()");
        mDevice = device;
        mRfcomm = new RfcommClient(mDevice, UuidSpp, new RfcommClient.RfcommListener() {
            @Override
            public void onConnect() {
                repeatGetAccessoryInfo(0);
            }

            @Override
            public void onData(InputStream in) throws IOException {
                Log.d(LOG_TAG, "onData() len=" + in.available());
                while (in.available() >= Packet.PacketSize) {
                    final byte data[] = new byte[Packet.PacketSize];
                    //noinspection ResultOfMethodCallIgnored (We know exactly how much is available)
                    in.read(data);
                    handlePacket(listener, new Packet(data));
                }
            }

            @Override
            public void onError(IOException e) {
                Log.d(LOG_TAG, "onError() " + e);
                listener.onError(e);
                close();
            }
        });
    }

    /** Request accessory info on the main thread Get accessory info on the main thread */
    private void repeatGetAccessoryInfo(long delay) {
        Tasks.runMainDelayed(delay, new Runnable() {
            @Override
            public void run() {
                if (mRfcomm != null) {
                    Packet packet = new Packet(CommandGetAccessoryInfo);
                    write(packet);
                    repeatGetAccessoryInfo(PollingTimeMs);
                }
            }
        });
    }

    private void handlePacket(ImpulseListener listener, Packet packet) {
        Log.d(LOG_TAG, "RX " + Bytes.toHex(packet.getBytes()));
        mState.setCommand(packet.getCommand());
        if (packet.getCommand() == CommandAccessoryInfo) {
            mState.setAccessoryInfo(packet.getPayload());
        }
        listener.onInfo(mState.build());
    }

    @Override
    public void close() {
        Log.d(LOG_TAG, "close()");
        if (mQueryTimer != null) {
            mQueryTimer.cancel();
            mQueryTimer = null;
        }
        if (mRfcomm != null) {
            mRfcomm.close();
            mRfcomm = null;
        }
    }

    public void setAccessoryInfo(ImpulseDeviceOptions info) {
        Packet setInfo = new Packet(CommandSetAccessoryInfo);
        byte payload[] = {
                Bytes.toByte(info.getAutoExposure(), 0x00),
                Bytes.toByte(info.getAutoPowerOff(), 0x08),
                Bytes.toByte(info.getPrintMode(), 0x01)
        };
        Log.d(LOG_TAG, "setAccessoryInfo() " + Bytes.toHex(payload));
        setInfo.setPayload(payload);
        write(setInfo);
    }

    private void write(Packet packet) {
        Log.d(LOG_TAG, "TX " + Bytes.toHex(packet.getBytes()));
        mRfcomm.write(packet.getBytes());
    }

    public interface ImpulseListener {
        void onInfo(ImpulseDeviceState info);
        void onError(IOException e);
    }

    static class Packet {
        private static int PacketSize = 34;
        private static int StartCode = 0x1B2A;
        private static int CustomerCode = 0x4341; // Code for Polaroid ZIP
        private static int FromClient = 0x00;
//        private static int FromDevice = 0x01;
        private static int ProductCode = 0x00;

        private static int PosCommand = 6;
        private static int PosPayload = 8;

        byte mData[] = new byte[PacketSize];

        Packet(int command) {
            ByteBuffer out = ByteBuffer.wrap(mData);
            out.clear();
            out.putShort((short)StartCode);
            out.putShort((short)CustomerCode);
            out.put((byte)FromClient);
            out.put((byte)ProductCode);
            out.putShort((short)command);
            // Remaining 26 bytes of payload (not defined)
        }

        /** Create a packet based on input data. Expected to be PacketSize in length. */
        Packet(byte data[]) {
            System.arraycopy(data, 0, mData, 0, PacketSize);
        }

        /** Return all bytes. Do not modify. */
        public byte[] getBytes() {
            return mData;
        }

        /** Return the payload portion */
        public byte[] getPayload() {
            byte payload[] = new byte[Packet.PacketSize - PosPayload];
            System.arraycopy(mData, PosPayload, payload, 0, Packet.PacketSize - PosPayload);
            return payload;
        }

        /** Set payload bytes (up to PacketSize - PosPayload) */
        public void setPayload(byte payload[]) {
            System.arraycopy(payload, 0, mData, PosPayload, payload.length);
        }

        /** Return the command code */
        public int getCommand() {
            return (mData[PosCommand] << 8) | mData[PosCommand+1];
        }
    }
}