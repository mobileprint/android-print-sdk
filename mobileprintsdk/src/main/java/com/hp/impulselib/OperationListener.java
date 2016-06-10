package com.hp.impulselib;

/** A simple operation that can complete or end in error */
public interface OperationListener extends ErrorListener {
    void onDone();
}
