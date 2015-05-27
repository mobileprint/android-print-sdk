package com.hp.mss.hpprint.util;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class GAUtil {
    public static final String EVENT_CATEGORY_FULFILLMENT = "Fulfillment";
    public static final String EVENT_ACTION_PRINT = "Print";
    public static Tracker tracker;

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }
    public static void sendEvent(Context context, String category, String action, String label) {
        if(tracker != null)
            tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
}
