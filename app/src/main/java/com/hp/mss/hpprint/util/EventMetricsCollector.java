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
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hp.mss.hpprint.model.ApplicationMetricsData;

import java.util.HashMap;
import java.util.Map;

public class EventMetricsCollector {

    public static enum PrintFlowEventTypes {
        ENTERED_PRINT_SDK (1),
        OPENED_PLUGIN_HELPER (2),
        SENT_TO_GOOGLE_PLAY_STORE (3),
        OPENED_PREVIEW (4),
        SENT_TO_PRINT_DIALOG(5),
        SENT_TO_PRINT_SETTING(6);

        private int id;

        private PrintFlowEventTypes(int i) {
            this.id = i;
        }

        public int getId() {
            return this.id;
        }

        public static Map<Integer, PrintFlowEventTypes> buildMap() {
            Map<Integer, PrintFlowEventTypes> map = new HashMap<Integer, PrintFlowEventTypes>();
            PrintFlowEventTypes[] values = PrintFlowEventTypes.values();
            for (PrintFlowEventTypes value : values) {
                map.put(value.getId(), value);
            }
            return map;
        }
    }

    private static final String TAG = "EventMetricsCollector";
    private static final String API_METHOD_NAME = "/v2/events";

    private static final String PRINT_SESSION_ID_LABEL = "print_session_id";
    private static final String EVENT_COUNTER_LABEL = "event_count";
    private static final String EVENT_TYPE_ID_LABLE = "event_type_id";
    private static final String NOT_AVAILABLE = "Not Available";
    private static final String NUM_OF_PLUGINS_INSTALLED = "num_of_plugins_installed";
    private static final String NUM_OF_PLUGINS_ENABLED = "num_of_plugins_enabled";
    private static final String CUSTOM_DATA = "custom_data";

    String printSessionId;
    String eventCount;
    String eventTypeId;
    String numOfPluginsInstalled;
    String numOfPluginsEnabled;
    String customData;
    Activity hostActivity;
    PrintPluginStatusHelper pluginStatusHelper;

    /**
     * Called inside the Print SDK to send printing related data to HP server.
     *
     * @param activity
     */
    private EventMetricsCollector(Activity activity) {
        pluginStatusHelper = PrintPluginStatusHelper.getInstance(activity);
        this.hostActivity = activity;
    }

    public static void postMetricsToHPServer(final Activity activity,
                                             PrintFlowEventTypes eventType) {
        if (!PrintUtil.sendPrintMetrics)
            return;

        EventMetricsCollector metricsCollector = new EventMetricsCollector(activity);
        metricsCollector.init(eventType);

        Context context = activity.getApplicationContext();

        RequestQueue queue = Volley.newRequestQueue(context);

        final Map<String, String> eventMap = metricsCollector.getMetricsParams();

        StringRequest sr = new StringRequest(Request.Method.POST, MetricsUtil.getMetricsServer(context) + API_METHOD_NAME, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(TAG, response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Log.i(TAG, eventMap.toString());
                return eventMap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String authorizationString = MetricsUtil.getAuthorizationString();

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", authorizationString);
                return params;
            }
        };
        queue.add(sr);
    }

    private Map<String, String> getMetricsParams() {


        ApplicationMetricsData appData = new ApplicationMetricsData(this.hostActivity.getApplicationContext());

        HashMap<String, String> combinedMetrics = appData.toEventOnlyMap();
        if(printSessionId != null) combinedMetrics.put(PRINT_SESSION_ID_LABEL, printSessionId);
        if(eventCount != null) combinedMetrics.put(EVENT_COUNTER_LABEL, eventCount);
        if(eventTypeId != null) combinedMetrics.put(EVENT_TYPE_ID_LABLE, eventTypeId);
        if(numOfPluginsEnabled != null) combinedMetrics.put(NUM_OF_PLUGINS_ENABLED, numOfPluginsEnabled);
        if(numOfPluginsInstalled != null) combinedMetrics.put(NUM_OF_PLUGINS_INSTALLED, numOfPluginsInstalled);
        if(customData != null) combinedMetrics.put(CUSTOM_DATA,customData);

        return combinedMetrics;
    }

    private void init(PrintFlowEventTypes type) {

        this.numOfPluginsEnabled = String.valueOf(pluginStatusHelper.getNumOfPluginsEnabled());
        this.numOfPluginsInstalled = String.valueOf(pluginStatusHelper.getNumOfPluginsInstalled());

        if(PrintUtil.customData.isEmpty())
            this.customData = "N/A";
        else
            this.customData = PrintUtil.customData.toString();

        switch (type) {
            case ENTERED_PRINT_SDK:
                this.printSessionId = String.valueOf(MetricsUtil.getNextEventCounter(hostActivity,
                        PrintFlowEventTypes.ENTERED_PRINT_SDK.name()));
                this.eventCount = this.printSessionId;
                this.eventTypeId = String.valueOf(PrintFlowEventTypes.ENTERED_PRINT_SDK.getId());
                break;
            case OPENED_PLUGIN_HELPER:
                this.printSessionId = String.valueOf(MetricsUtil.getCurrentSessionCounter(hostActivity));
                this.eventCount = String.valueOf(MetricsUtil.getNextEventCounter(hostActivity,
                        PrintFlowEventTypes.OPENED_PLUGIN_HELPER.name()));
                this.eventTypeId = String.valueOf(PrintFlowEventTypes.OPENED_PLUGIN_HELPER.getId());
                break;
            case SENT_TO_GOOGLE_PLAY_STORE:
                this.printSessionId = String.valueOf(MetricsUtil.getCurrentSessionCounter(hostActivity));
                this.eventCount = String.valueOf(MetricsUtil.getNextEventCounter(hostActivity,
                        PrintFlowEventTypes.SENT_TO_GOOGLE_PLAY_STORE.name()));
                this.eventTypeId = String.valueOf(PrintFlowEventTypes.SENT_TO_GOOGLE_PLAY_STORE.getId());
                break;
            case OPENED_PREVIEW:
                this.printSessionId = String.valueOf(MetricsUtil.getCurrentSessionCounter(hostActivity));
                this.eventCount = String.valueOf(MetricsUtil.getNextEventCounter(hostActivity,
                        PrintFlowEventTypes.OPENED_PREVIEW.name()));
                this.eventTypeId = String.valueOf(PrintFlowEventTypes.OPENED_PREVIEW.getId());
                break;
            case SENT_TO_PRINT_DIALOG:
                this.printSessionId = String.valueOf(MetricsUtil.getCurrentSessionCounter(hostActivity));
                this.eventCount = String.valueOf(MetricsUtil.getNextEventCounter(hostActivity,
                        PrintFlowEventTypes.SENT_TO_PRINT_DIALOG.name()));
                this.eventTypeId = String.valueOf(PrintFlowEventTypes.SENT_TO_PRINT_DIALOG.getId());
                break;
            case SENT_TO_PRINT_SETTING:
                this.printSessionId = String.valueOf(MetricsUtil.getCurrentSessionCounter(hostActivity));
                this.eventCount = String.valueOf(MetricsUtil.getNextEventCounter(hostActivity,
                        PrintFlowEventTypes.SENT_TO_PRINT_SETTING.name()));
                this.eventTypeId = String.valueOf(PrintFlowEventTypes.SENT_TO_PRINT_SETTING.getId());
                break;
            default:
                this.eventTypeId = NOT_AVAILABLE;
                this.eventCount = NOT_AVAILABLE;
                this.eventTypeId = NOT_AVAILABLE;
                break;
        }

    }




}

