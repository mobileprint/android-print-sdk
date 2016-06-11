package com.hp.impulselib;

public class ImpulseDeviceOptions {
    private Integer mPrintMode;

    private Integer mAutoExposure;

    private Integer mAutoPowerOff;

    private ImpulseDeviceOptions() {
    }

    private ImpulseDeviceOptions(ImpulseDeviceOptions other) {
        mPrintMode = other.mPrintMode;
        mAutoExposure = other.mAutoExposure;
        mAutoPowerOff = other.mAutoPowerOff;
    }

    /** 1 to fill paper, 2 to retain whole image */
    public Integer getPrintMode() {
        return mPrintMode;
    }

    /** 0 for off, 1 for on */
    public Integer getAutoExposure() {
        return mAutoExposure;
    }

    /** 0x00 for always on, 0x04 for 3 minute, 0x08 for 5 minute, 0x0C for 10 minute */
    public Integer getAutoPowerOff() {
        return mAutoPowerOff;
    }

    public static class Builder {
        private final ImpulseDeviceOptions mPrototype;

        public Builder(ImpulseDeviceOptions options) {
            mPrototype = new ImpulseDeviceOptions(options);
        }

        public Builder() {
            mPrototype = new ImpulseDeviceOptions();
        }

        public Builder setAutoPowerOff(Integer autoPowerOff) {
            mPrototype.mAutoPowerOff = autoPowerOff;
            return this;
        }

        public Builder setAutoExposure(Integer autoExposure) {
            mPrototype.mAutoExposure = autoExposure;
            return this;
        }

        public Builder setPrintMode(Integer printMode) {
            mPrototype.mPrintMode = printMode;
            return this;
        }

        public ImpulseDeviceOptions build() {
            return new ImpulseDeviceOptions(mPrototype);
        }
    }
}
