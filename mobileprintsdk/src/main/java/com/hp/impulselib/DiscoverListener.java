package com.hp.impulselib;

import java.util.List;

/** Interface used to listen for discovery results.  */
public interface DiscoverListener extends ErrorListener {
    void onDevices(List<ImpulseDevice> devices);
}
