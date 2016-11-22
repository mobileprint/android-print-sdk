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
import android.os.Build;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.model.PrintPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by panini on 1/21/16.
 */
public class PrintPluginStatusHelper {
    public static final String MOPRIA_PRINT_SERVICE_NAME  = "org.mopria.printplugin.MopriaPrintService";
    public static final String MOPRIA_PRINT_PLUGIN_PACKAGE_NAME = "org.mopria.printplugin";
    public static final String BROTHER_PRINT_PLUGIN_PACKAGE_NAME = "com.brother.printservice";
    public static final String SAMSUNG_PRINT_PLUGIN_PACKAGE_NAME = "com.sec.app.samsungprintservice";
    public static final String OTHER_PRINT_PLUGIN_PACKAGE_NAME = "Other Print Service Plugin";
    private Context context;
    private static PrintPluginStatusHelper instance = null;
    private final Map<String, PrintPlugin> pluginVersionMap;
    private String locale;

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
     *      Excludes google cloud print(default), PrintHand Mobile (pay to use), and ThinkFree (requires Zimbra)
     *      public static final String PRINTERON_PRINT_PLUGIN_PACKAGE_NAME      = "com.printeron.android.printplugin";
     *      public static final String HP_PRINT_PLUGIN_PACKAGE_NAME             = "com.hp.android.printservice";
     *      public static final String MOPRIA_PRINT_PLUGIN_PACKAGE_NAME         = "org.mopria.printplugin";
     *      public static final String LEXMARK_PRINT_PLUGIN_PACKAGE_NAME        = "com.lexmark.print.plugin";
     *      public static final String BROTHER_PRINT_PLUGIN_PACKAGE_NAME        = "com.brother.printservice";
     *      public static final String SAMSUNG_PRINT_PLUGIN_PACKAGE_NAME        = "com.sec.app.samsungprintservice";
     *      public static final String CANON_PRINT_PLUGIN_PACKAGE_NAME          = "jp.co.canon.android.printservice.plugin";
     *      public static final String XEROX_PRINT_PLUGIN_PACKAGE_NAME          = "com.xerox.printservice";
     *      public static final String KONICA_MINOLTA_PRINT_PLUGIN_PACKAGE_NAME = "com.kmbt.printservice";
     *      public static final String PRINTERSHARE_PRINT_PLUGIN_PACKAGE_NAME   = "com.dynamixsoftware.printershare";
     *      public static final String EPSON_PRINT_PLUGIN_PACKAGE_NAME          = "com.epson.mobilephone.android.epsonprintserviceplugin";
     */
    public static String[] packageNames  =   {"com.hp.android.printservice",
                                              MOPRIA_PRINT_PLUGIN_PACKAGE_NAME,
                                              "com.brother.printservice",
                                              SAMSUNG_PRINT_PLUGIN_PACKAGE_NAME,
                                              "jp.co.canon.android.printservice.plugin",
                                              "com.epson.mobilephone.android.epsonprintserviceplugin",
                                              OTHER_PRINT_PLUGIN_PACKAGE_NAME};

    /**
     *  Plugin Versions - Last Updated: 01/14/2016
     *  private static final Integer PRINTERON_PRINT_PLUGIN_PACKAGE_VERSION      = 100;
     *  private static final Integer HP_PRINT_PLUGIN_PACKAGE_VERSION             = 67;
     *  private static final Integer MOPRIA_PRINT_PLUGIN_PACKAGE_VERSION         = 112;
     *  private static final Integer LEXMARK_PRINT_PLUGIN_PACKAGE_VERSION        = 7;
     *  private static final Integer BROTHER_PRINT_PLUGIN_PACKAGE_VERSION        = 1;
     *  private static final Integer SAMSUNG_PRINT_PLUGIN_PACKAGE_VERSION        = 102;
     *  private static final Integer CANON_PRINT_PLUGIN_PACKAGE_VERSION          = 2220;
     *  private static final Integer XEROX_PRINT_PLUGIN_PACKAGE_VERSION          = 42;
     *  private static final Integer KONICA_MINOLTA_PRINT_PLUGIN_PACKAGE_VERSION = 4;
     *  private static final Integer PRINTERSHARE_PRINT_PLUGIN_PACKAGE_VERSION   = 308;
     *  private static final Integer EPSON_PRINT_PLUGIN_PACKAGE_VERSION          = 6;
     */
    // {HP, Mopria, Brother, Samsung, Canon, Eposon, the Other
    private static int[] pluginPackageVersions   = {67, 112, 1, 102, 2220, 6, 0};

    /**
     * Play Store Url
     */
    private static final String[] pluginPlaystoreUrls    = { "https://play.google.com/store/apps/details?id=com.hp.android.printservice",
                                                            "https://play.google.com/store/apps/details?id=org.mopria.printplugin",
                                                            "https://play.google.com/store/apps/details?id=com.brother.printservice",
                                                            "https://play.google.com/store/apps/details?id=com.sec.app.samsungprintservice",
                                                            "https://play.google.com/store/apps/details?id=jp.co.canon.android.printservice.plugin",
                                                            "https://play.google.com/store/apps/details?id=com.epson.mobilephone.android.epsonprintserviceplugin",
                                                            "https://play.google.com/store/search?q=print%20service%20plugin&c=apps" };

    private static final String[] pluginTencentUrls    = {"http://sj.qq.com/myapp/search.htm?kw=HP%20Print%20Service%20plugin"};
    /**
     * Plugin Icon Name
     */
    public static int[] pluginIcons = new int[]{
            R.drawable.hp,
            R.drawable.mopria,
            R.drawable.brother,
            R.drawable.samsung,
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


        if (isChina()) {
            for(int i=0; i < pluginTencentUrls.length; i++) {
                PrintPlugin printPlugin = new PrintPlugin(packageNames[i], pluginPackageVersions[i], pluginTencentUrls[i],
                        context, pluginNames[i], pluginMakers[i], pluginIcons[i]);
                printPlugin.updateStatus();
                result.put(packageNames[i], printPlugin);
            }
        } else {
            for(int i=0; i < packageNames.length; i++) {

                PrintPlugin printPlugin = new PrintPlugin(packageNames[i], pluginPackageVersions[i], pluginPlaystoreUrls[i],
                        context, pluginNames[i], pluginMakers[i], pluginIcons[i]);
                printPlugin.updateStatus();
                result.put(packageNames[i], printPlugin);
            }
        }


        return result;
    }

    /**
     *
     * @return a sorted array of plugin list
     */
    public PrintPlugin[] getSortedPlugins() {
        PrintPlugin[] plugins = new PrintPlugin[pluginVersionMap.size()];

        if(pluginVersionMap.isEmpty())
            return plugins;

        PrintPlugin.PluginStatus[] sortingOrder = {
                                            PrintPlugin.PluginStatus.READY,
                                            PrintPlugin.PluginStatus.DISABLED,
                                            PrintPlugin.PluginStatus.REQUIREUPDATE,
                                            PrintPlugin.PluginStatus.DOWNLOADING,
                                            PrintPlugin.PluginStatus.NOTINSTALLED
        };

        int index = 0;
        // sorting the map according to plugin status
        for(int i = 0; i < sortingOrder.length; i++ ) {
            ArrayList<PrintPlugin> statusArray = getPluginsByStatus(sortingOrder[i]);
            if( statusArray != null && !statusArray.isEmpty()) {
                bubbleSort(statusArray);
                for(int j = 0; j < statusArray.size(); j++) {
                    plugins[index++] = statusArray.get(j);
                }
            }
        }
        if (isChina()) {
            return plugins;
        }
        plugins[index] = pluginVersionMap.get(OTHER_PRINT_PLUGIN_PACKAGE_NAME);
        return plugins;
    }

    // return all the plugins in the map with the same status
    private ArrayList<PrintPlugin> getPluginsByStatus(PrintPlugin.PluginStatus status) {
        if(pluginVersionMap.isEmpty())
            return null;

        ArrayList<PrintPlugin> list = new ArrayList<PrintPlugin>();
        Iterator it = pluginVersionMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if( status == ((PrintPlugin) pair.getValue()).getStatus() ) {
                if( !pair.getKey().equals(OTHER_PRINT_PLUGIN_PACKAGE_NAME) )
                    list.add((PrintPlugin) pair.getValue());
            }
        }
        return list;
    }

    // bubble sort the plugins that have same status
    private void bubbleSort(ArrayList<PrintPlugin> arrayList) {

        // sorting without the other plugins
        Map<String, Integer> sortingMap  = new HashMap<String, Integer>();
        sortingMap.put("com.hp.android.printservice", 1);
        sortingMap.put(PrintPluginStatusHelper.MOPRIA_PRINT_PLUGIN_PACKAGE_NAME, 2);
        sortingMap.put("com.brother.printservice", 3);
        sortingMap.put("com.sec.app.samsungprintservice", 4);
        sortingMap.put("jp.co.canon.android.printservice.plugin", 5);
        sortingMap.put("com.epson.mobilephone.android.epsonprintserviceplugin", 6);

        boolean swapped= true;
        int j = 0;
        PrintPlugin temp;

        while(swapped) {
            swapped = false;
            j++;
            for(int i = 0; i < arrayList.size() - j ; i++) {
                int a = sortingMap.get(arrayList.get(i).getPackageName()).intValue();
                int b = sortingMap.get(arrayList.get(i+1).getPackageName()).intValue();

                if(a > b) {
                    temp = arrayList.get(i);
                    arrayList.set(i, arrayList.get(i+1));
                    arrayList.set(i+1, temp);
                    swapped = true;
                }
            }

        }
    }

    /**
     *
     * @return if there is any insatalled and enabled plugin
     */
    public boolean readyToPrint() {
        boolean isReady = false;
        if (android.os.Build.MANUFACTURER.equals("Amazon")) {
            return true;
        }

        Collection<PrintPlugin> list = pluginVersionMap.values();

        Iterator<PrintPlugin> iterator = list.iterator();
        while (iterator.hasNext()) {

                PrintPlugin plugin = (PrintPlugin) iterator.next();
                plugin.updateStatus();
                if (plugin.getStatus() == PrintPlugin.PluginStatus.READY)
                    isReady = true;

        }

        return isReady;
    }

    /**
     *
     * @param printPlugin for a given plugin
     * @return if help tip doalog box need to be displayed before users get redirected to google play store
     */
    public boolean showBeforeEnableDialog(PrintPlugin printPlugin) {
        PrintPlugin.PluginStatus pluginStatus = printPlugin.getStatus();

        return (pluginStatus == PrintPlugin.PluginStatus.DISABLED );
    }

    /**
     *
     * @param printPlugin
     * @return direct user to Google Playstore
     */
    public boolean goToGoogleStore(PrintPlugin printPlugin) {
        PrintPlugin.PluginStatus pluginStatus = printPlugin.getStatus();

        return (pluginStatus == PrintPlugin.PluginStatus.NOTINSTALLED ||
                pluginStatus == PrintPlugin.PluginStatus.REQUIREUPDATE ||
                pluginStatus == PrintPlugin.PluginStatus.DOWNLOADING);
    }

    /**
     *
     * @return the number of the plugins installed at the moment
     */
    public int getNumOfPluginsInstalled() {
        int num = 0;

        if (pluginVersionMap == null)
            return num;

        Collection<PrintPlugin> list = pluginVersionMap.values();

        Iterator<PrintPlugin> iterator = list.iterator();
        while (iterator.hasNext()) {

            PrintPlugin plugin = (PrintPlugin) iterator.next();
            plugin.updateStatus();
            if (plugin.getStatus() == PrintPlugin.PluginStatus.READY ||
                    plugin.getStatus() == PrintPlugin.PluginStatus.DISABLED ||
                    plugin.getStatus() == PrintPlugin.PluginStatus.REQUIREUPDATE)
                num++;

        }

        return num;

    }

    /**
     *
     * @return the number of the plugins enabled at the moment
     */
    public int getNumOfPluginsEnabled() {
        int num = 0;

        if (pluginVersionMap == null)
            return num;

        Collection<PrintPlugin> list = pluginVersionMap.values();

        Iterator<PrintPlugin> iterator = list.iterator();
        while (iterator.hasNext()) {

            PrintPlugin plugin = (PrintPlugin) iterator.next();
            plugin.updateStatus();
            if (plugin.getStatus() == PrintPlugin.PluginStatus.READY)
                num++;

        }

        return num;

    }

    private boolean isChina() {
        if (locale == null) {
            locale = context.getResources().getConfiguration().locale.getISO3Country();
        }
        return locale.equals("CHN") || locale.equals("HKG");
    }

}

