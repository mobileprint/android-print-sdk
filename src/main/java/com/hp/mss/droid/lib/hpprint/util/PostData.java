package com.hp.mss.droid.lib.hpprint.util;

import org.json.JSONObject;

/**
 * Created by panini on 3/3/15.
 */
public class PostData {
    OnPrintDataCollectedListener printDataCollectedListener;

    public interface OnPrintDataCollectedListener {
        public void postPrintData(JSONObject jsonObject);
    }

}
