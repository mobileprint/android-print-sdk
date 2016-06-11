package com.hp.impulselib;

/** Interface used to listen for tracking updates */
public interface TrackListener extends ErrorListener {
    void onState(ImpulseDeviceState state);
}
