/*
 * Hewlett-Packard Company
 * All rights reserved.
 *
 * This file, its contents, concepts, methods, behavior, and operation
 * (collectively the "Software") are protected by trade secret, patent,
 * and copyright laws. The use of the Software is governed by a license
 * agreement. Disclosure of the Software to third parties, in any form,
 * in whole or in part, is expressly prohibited except as authorized by
 * the license agreement.
 */

package com.hp.mss.hpprint.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.util.Base64;

public class MetricsUtil {

    private static final String PRINT_METRICS_PRODUCTION_SERVER = "https://print-metrics-w1.twosmiles.com/api";
    private static final String PRINT_METRICS_TEST_SERVER = "http://print-metrics-test.twosmiles.com/api";
    private static final String PRINT_METRICS_LOCAL_SERVER = "http://10.0.2.2:4567/api";
    private static final String PRINT_METRICS_USER_NAME = "hpmobileprint";
    private static final String PRINT_METRICS_PASSWORD = "print1t";


    public static boolean isDebuggable(Context context) {
        return ( 0 != ( context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
    }

    public static String getMetricsServer(Context context) {
        return (isDebuggable(context) ? PRINT_METRICS_TEST_SERVER : PRINT_METRICS_PRODUCTION_SERVER);
    }

    public static String getAuthorizationString() {
        String authorizationString = "Basic " + Base64.encodeToString((PRINT_METRICS_USER_NAME + ":" + PRINT_METRICS_PASSWORD).getBytes(), Base64.NO_WRAP);
        return authorizationString;
    }

    public static int getNextEventCounter(Activity hostActivity, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(hostActivity.getApplicationContext());

        int id = preferences.getInt(key, 0);
        id += 1;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, id).commit();

        return id;
    }

    public static int getCurrentSessionCounter(Activity hostActivity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(hostActivity.getApplicationContext());

        return preferences.getInt(EventMetricsCollector.PrintFlowEventTypes.ENTERED_PRINT_SDK.name(), 1);
    }
}
