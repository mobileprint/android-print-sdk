package com.hp.mss.droid.lib.hpprint.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.hp.mss.droid.lib.hpprint.activity.PrintPreview;

/**
 * Copyright 2015 Hewlett-Packard, Co.
 */

public class PrintUtil {
    OnInstallPackageListener installPackageListener;

    private static String HP_PRINT_PLUGIN_PACKAGE_NAME = "com.hp.android.printservice";
    private static String GOOGLE_STORE_PACKAGE_NAME = "com.android.vending";
    private static String INSTALL_HP_PRINT_PLUGIN_MSG = "To print best quality photos, " +
                                                        "please install HP Print Plugin if you have HP printer(s)." +
                                                        " Please make sure print plugin service is turned on after the installation";


    public void launchPrint(Activity activity, String photoFileName){

        if ( checkAndInstallHPPrintPlugin(activity) ) {

                Intent intent = new Intent(activity, PrintPreview.class);
                intent.putExtra("photoFileUri", photoFileName);
                activity.startActivity(intent);
        }
    }


    public boolean checkAndInstallHPPrintPlugin(Activity activity) {

        boolean isInstalled = false;

        if ( isPackageInstalled(activity, HP_PRINT_PLUGIN_PACKAGE_NAME) )
//                ||  isPackageInstalled(activity, MOPRIA_PRINT_PLUGIN_PACKAGE_NAME))
        {

            isInstalled = true;

        } else {

            dispatchInstallMsgDialog(activity, INSTALL_HP_PRINT_PLUGIN_MSG);

        }

        return isInstalled;
    }

    public boolean isPackageInstalled(Activity activity, String packageName) {

        boolean isInstalled = false;

        PackageManager packageManager = activity.getPackageManager();

        try {

            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            isInstalled = true;

        } catch (PackageManager.NameNotFoundException e) { }

        return isInstalled;
    }


    public void dispatchInstallPluginIntent(Activity activity, String packageName) {

        String url;

        if ( isPackageInstalled(activity, GOOGLE_STORE_PACKAGE_NAME) ) {

            url = "market://details?id=" + packageName;

        } else {

            url = "https://play.google.com/store/apps/details?id=" + packageName;

        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }


    private void dispatchInstallMsgDialog(final Activity activity, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
               .setCancelable(false)
               .setPositiveButton("Install HP Print Plugin", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dispatchInstallPluginIntent(activity, HP_PRINT_PLUGIN_PACKAGE_NAME);
                   }
               })
               .setNeutralButton("Skip", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       installPackageListener = (OnInstallPackageListener) activity;
                       installPackageListener.ignoreWarningMsg(true);
                   }
               })
        .create()
        .show();

    }

    public interface OnInstallPackageListener {
        public void ignoreWarningMsg(boolean ignore);
    }

}
