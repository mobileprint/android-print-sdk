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

package com.hp.mss.hpprint.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.util.EventMetricsCollector;
import com.hp.mss.hpprint.util.PrintPluginStatusHelper;

/**
 * Created by panini on 1/20/16.
 */
public class PrintPlugin {
    public enum PluginStatus { READY, NOTINSTALLED, REQUIREUPDATE, DISABLED, DOWNLOADING }

    private String packageName;
    private int  minimumVersion;
    private String playStoreUrl;
    private PluginStatus status;
    private String name;
    private String maker;
    private int icon;
    private Context context;

    /**
     * Represent a print plugin object
     * @param packageName       package name that is used in Google play store
     * @param minimumVersion    minimum version of this plugin that is useful
     * @param playStoreUrl      playstore download url
     * @param context           application context
     * @param name              display name
     * @param maker             manufacture of the print plugin
     * @param icon              print plugin icon
     */
    public PrintPlugin(String packageName, int minimumVersion, String playStoreUrl, Context context, String name, String maker, int icon) {
        this.context = context;
        this.packageName = packageName;
        this.minimumVersion = minimumVersion;
        this.playStoreUrl =playStoreUrl;
        this.name = name;
        this.maker = maker;
        this.icon = icon;
        updateStatus();
    }

    /**
     *
     * @return the plugin status
     */
    public PluginStatus getStatus() {
        return status;
    }

    /**
     *
     * @return plugin name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return plugin maker
     */
    public String getMaker() {
        return maker;
    }

    /**
     *
     * @return icon
     */

    public int getIcon() {
        return icon;
    }

    /**
     *
     * @return package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * sent user to Google playstore to download or update the plugin
     */
    public void goToPlayStoreForPlugin() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        EventMetricsCollector.postMetricsToHPServer((Activity) context, EventMetricsCollector.PrintFlowEventTypes.SENT_TO_GOOGLE_PLAY_STORE);
    }

    /**
     * check the plugin status again
     */
    public void updateStatus() {
        final PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0); // This will throw an exception if the package does not exist
            int versionNumber = packageInfo.versionCode;
            if (versionNumber < minimumVersion) {
                status = PluginStatus.REQUIREUPDATE;
            } else {
                if (checkPluginEnabled()) {
                    status =  PluginStatus.READY;
                } else {
                    status =  PluginStatus.DISABLED;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            if (status != PluginStatus.DOWNLOADING) {
                status = PluginStatus.NOTINSTALLED;
            }
        }
    }

    /**
     * set plugin status as downloading
     */
    public void setStatusToDownloading() {
        status = PluginStatus.DOWNLOADING;
    }

    /**
     *
     * @return if the installed plugin is enabled
     */
    private boolean checkPluginEnabled() {
        //MOPRIA STILL HAS DEFECT - Disabling the print plugin will not disable the running service
        //Using ugly work around specifically for Mopria org.mopria.printplugin/org.mopria.printplugin.MopriaPrintService
        //I'm sorry :(
        //This defect was fixed in Nougat
        String printServiceName = packageName;
        if (packageName == PrintPluginStatusHelper.MOPRIA_PRINT_PLUGIN_PACKAGE_NAME && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            printServiceName = PrintPluginStatusHelper.MOPRIA_PRINT_SERVICE_NAME;
        }
        String enabledPrintServices = Settings.Secure.getString(context.getContentResolver(), "enabled_print_services");
        String disabledPrintServices = Settings.Secure.getString(context.getContentResolver(), "disabled_print_services");

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            if (null != enabledPrintServices)
                return enabledPrintServices.toLowerCase().contains(printServiceName.toLowerCase());
            else
                return false;
        } else {
            if (null != disabledPrintServices)
                return !disabledPrintServices.toLowerCase().contains(printServiceName.toLowerCase());
            else
                return true;
        }

    }

    /**
     *
     * @return return predefined status icon
     */
    public int getNextActionIcon() {
        switch (this.status) {
            case NOTINSTALLED:
                return R.drawable.down_arrow;
            case DOWNLOADING:
                return R.drawable.downloading_arrow;
            case REQUIREUPDATE:
                return R.drawable.update;
            case DISABLED:
                return R.drawable.disabled;
            case READY:
                return R.drawable.enabled;
            default:
                return R.drawable.down_arrow;
        }
    }


}