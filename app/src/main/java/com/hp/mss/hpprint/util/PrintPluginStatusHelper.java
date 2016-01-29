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

import android.content.Context;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.model.PrintPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by panini on 1/21/16.
 */
public class PrintPluginStatusHelper {
    public static final String MOPRIA_PRINT_SERVICE_NAME  = "org.mopria.printplugin.MopriaPrintService";
    public static final String MOPRIA_PRINT_PLUGIN_PACKAGE_NAME = "org.mopria.printplugin";
    public static final String OTHER_PRINT_PLUGIN_PACKAGE_NAME = "Other Print Service Plugin";
    private Context context;
    private static PrintPluginStatusHelper instance = null;
    private final Map<String, PrintPlugin> pluginVersionMap;

    protected PrintPluginStatusHelper(Context context) {

        this.context = context;
        pluginVersionMap = createPluginVersionMap();
    }

    /**
     *
     * @param context the application context this help is in
     * @return the singleton object of the PrintPluginStatusHelper
     */
    public static PrintPluginStatusHelper getInstance(Context context) {
        if( instance == null) {
            instance = new PrintPluginStatusHelper(context);
        }
        return instance;
    }

    /**
     *
     * @param packageName the package name, ex. HP print plugin it com.hp.android.printservice
     * @return the plugin object for given package
     */
    public PrintPlugin getPrintPlugin(String packageName) {
        return pluginVersionMap.get(packageName);
    }

    /**
     *
     * @param packageName the package name, ex. HP print plugin it com.hp.android.printservice
     * @return the plugin status string
     */
    public String getPrintPluginStatus(String packageName) {
        return pluginVersionMap.get(packageName).getStatus().toString();
    }

    /**
     *
     * @param packageName the package name, ex. HP print plugin it com.hp.android.printservice
     */
    public void updatePrintPluginStatus(String packageName) {
        pluginVersionMap.get(packageName).updateStatus();
    }

    /**
     * update all the plugin status in the plugin hashmap
     */
    public void updateAllPrintPluginStatus() {

            Iterator it = pluginVersionMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                updatePrintPluginStatus(pair.getKey().toString());
            }

    }

    /**
     *
     * @param packageName the package name, ex. HP print plugin it com.hp.android.printservice
     */
    public void setPrintPluginStatusToDownloading(String packageName) {
        pluginVersionMap.get(packageName).setStatusToDownloading();
    }

    /**
     *      Plugin Package Names - Last Updated: 01/14/2016
     */
    public static String[] packageNames  =   {"com.hp.android.printservice",
                                              MOPRIA_PRINT_PLUGIN_PACKAGE_NAME,
                                              "com.brother.printservice",
                                              "jp.co.canon.android.printservice.plugin",
                                              "com.epson.mobilephone.android.epsonprintserviceplugin",
                                              OTHER_PRINT_PLUGIN_PACKAGE_NAME};

    /**
     * Plugin Versions - Last Updated: 01/14/2016
     */
    private static int[] pluginPackageVersions   = {67, 112, 1, 2220, 6, 0};

    /**
     * Play Store Url
     */
    private static final String[] pluginPlaystoreUrls    = { "https://play.google.com/store/apps/details?id=com.hp.android.printservice",
                                                            "https://play.google.com/store/apps/details?id=org.mopria.printplugin",
                                                            "https://play.google.com/store/apps/details?id=com.brother.printservice",
                                                            "https://play.google.com/store/apps/details?id=jp.co.canon.android.printservice.plugin",
                                                            "https://play.google.com/store/apps/details?id=jp.co.canon.android.printservice.plugin",
                                                            "https://play.google.com/store/search?q=print%20service%20plugin&c=apps" };

    /**
     * Plugin Icon Name
     */
    public static int[] pluginIcons = new int[]{
            R.drawable.hp,
            R.drawable.mopria,
            R.drawable.brother,
            R.drawable.canon,
            R.drawable.epson,
            R.drawable.other
    };

    /**
     *
     * @return create predefined plugin hashmap
     */
    private Map<String, PrintPlugin> createPluginVersionMap() {

        String[] pluginNames = context.getResources().getStringArray(R.array.plugin_names);
        String[] pluginMakers = context.getResources().getStringArray(R.array.plugin_makers);

        Map<String, PrintPlugin> result = new HashMap<String, PrintPlugin>();

        for(int i=0; i < packageNames.length; i++) {
            PrintPlugin printPlugin = new PrintPlugin(packageNames[i], pluginPackageVersions[i], pluginPlaystoreUrls[i],
                                                        context, pluginNames[i], pluginMakers[i], pluginIcons[i]);
            printPlugin.updateStatus();
            result.put(packageNames[i], printPlugin);
        }

        return result;
    }

    /**
     *
     * @return a sorted array of plugin list
     */
    public PrintPlugin[] getPluginsSortedByStatus() {
        PrintPlugin[] plugins = new PrintPlugin[pluginVersionMap.size()];

        if(pluginVersionMap.isEmpty())
            return plugins;

        int index = 0;

        PrintPlugin.PluginStatus[] sortingOrder = {
                                            PrintPlugin.PluginStatus.READY,
                                            PrintPlugin.PluginStatus.DISABLED,
                                            PrintPlugin.PluginStatus.REQUIREUPDATE,
                                            PrintPlugin.PluginStatus.DOWNLOADING,
                                            PrintPlugin.PluginStatus.NOTINSTALLED
        };

        Map<String, PrintPlugin> result = new HashMap<String, PrintPlugin>();

        // sorting the map according to plagin status
        for(int i = 0; i < sortingOrder.length; i++ ) {
            Iterator it = pluginVersionMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if ( ((PrintPlugin) pair.getValue()).getStatus() == sortingOrder[i] &&
                        !pair.getKey().equals(OTHER_PRINT_PLUGIN_PACKAGE_NAME)) {
                    plugins[index++] = (PrintPlugin) pair.getValue();
                }
            }
        }

        plugins[index] = pluginVersionMap.get(OTHER_PRINT_PLUGIN_PACKAGE_NAME);
        return plugins;
    }


    /**
     *
     * @return if there is any insatalled and enabled plugin
     */
    public boolean readyToPrint() {
        boolean isReady = false;

        PrintPlugin[] list = getPluginsSortedByStatus();

        if (list != null) {

            for (int i = 0; i < list.length; i++) {
                PrintPlugin plugin = (PrintPlugin) list[i];
                plugin.updateStatus();
                if (plugin.getStatus() == PrintPlugin.PluginStatus.READY)
                    isReady = true;
            }
        }

        return isReady;
    }

    /**
     *
     * @param printPlugin for a given plugin
     * @return if help tip doalog box need to be displayed before users get redirected to google play store
     */
    public boolean showBeforeDownloadDialog(PrintPlugin printPlugin) {
        PrintPlugin.PluginStatus pluginStatus = printPlugin.getStatus();

        return (pluginStatus == PrintPlugin.PluginStatus.NOTINSTALLED ||
                pluginStatus == PrintPlugin.PluginStatus.REQUIREUPDATE ||
                pluginStatus == PrintPlugin.PluginStatus.DOWNLOADING);
    }
}
