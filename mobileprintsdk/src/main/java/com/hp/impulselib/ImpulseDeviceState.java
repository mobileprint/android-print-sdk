package com.hp.impulselib;

import com.hp.impulselib.util.Bytes;

import java.nio.ByteBuffer;
import java.util.Date;

public class ImpulseDeviceState {

    public int mCommand = -1;
    public long mUpdated = 0;
    private AccessoryInfo mInfo = null;

    private ImpulseDeviceState() {
    }

    private ImpulseDeviceState(ImpulseDeviceState old) {
        mUpdated = old.mUpdated;
        mCommand = old.mCommand;
        mInfo = old.mInfo;

        // Maybe do these together
    }

    /** Return current error code reported if any */
    public int getError() {
        if (mInfo == null) return Impulse.ErrorNone;
        return mInfo.error;
    }

    /** Return the full accessory info or null if not available */
    public AccessoryInfo getInfo() {
        return mInfo;
    }

    /** Return last command given by device (not including CommandAccessoryInfo) */
    public int getCommand() {
        return mCommand;
    }

    /** Return the system clock at the time of the last update */
    public long getUpdated() { return mUpdated; }

    @Override
    public String toString() {
        return "ImpulseDeviceState(" +
                "cmd=" + Bytes.toHex(getCommand()) +
                " " + mInfo +
                ")";
    }

    public static class Builder {
        ImpulseDeviceState mPrototype = new ImpulseDeviceState();
        public Builder() {
            mPrototype = new ImpulseDeviceState();
        }

        public ImpulseDeviceState build() {
            return new ImpulseDeviceState(mPrototype);
        }

        public Builder setCommand(int command) {
            mPrototype.mUpdated = new Date().getTime();
            mPrototype.mCommand = command;
            return this;
        }

        public Builder setAccessoryInfo(byte payload[]) {
            mPrototype.mUpdated = new Date().getTime();
            mPrototype.mInfo = new AccessoryInfo(payload);
            return this;
        }
    }

    public static class AccessoryInfo {
        // Error codes. See Impulse.Error*
        public final int error;
        public final int totalPrints;

        /** 01 to fill paper, 02 to retain whole image */
        public final int printMode;

        /** Percentage (0-100) */
        public final int batteryStatus;

        /** 0 for off, 1 for on */
        public final int autoExposure;

        /** 0x00 for always on, 0x04 for 3 minute, 0x08 for 5 minute, 0x0C for 10 minute */
        public final int autoPowerOff;

        public final byte macAddress[] = new byte[6];

        public final int firmwareVersion;

        public final int hardwareVersion;

        /** For FW Upgrade and TMD Upgrade */
        public final int maxPayloadSize;

        public AccessoryInfo(byte input[]) {
            ByteBuffer in = ByteBuffer.wrap(input);
            error = in.get();
            totalPrints = in.getShort();
            printMode = in.get();
            batteryStatus = in.get();
            autoExposure = in.get();
            autoPowerOff = in.get();
            in.get(macAddress);
            byte version[] = new byte[3];
            in.get(version);
            firmwareVersion = version[0] << 16 | version[1] << 8 | version[0];
            in.get(version);
            hardwareVersion= version[0] << 16 | version[1] << 8 | version[0];
            maxPayloadSize = in.getShort();
        }

        @Override
        public String toString() {
            return "AccessoryInfo(" +
                    "err=" + Bytes.toHex(error) +
                    " prints=" + totalPrints +
                    " mode=" + printMode +
                    " batt=" + batteryStatus +
                    " exp=" + autoExposure +
                    " pwr=" + Bytes.toHex(autoPowerOff) +
                    " mac=" + Bytes.toHex(macAddress) +
                    " fw=" + Bytes.toHex(firmwareVersion) +
                    " hw=" + Bytes.toHex(hardwareVersion) +
                    " size=" + Bytes.toHex(maxPayloadSize) +
                    ")";
        }
    }
}