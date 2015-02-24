//
// Hewlett-Packard Company
// All rights reserved.
//
// This file, its contents, concepts, methods, behavior, and operation
// (collectively the "Software") are protected by trade secret, patent,
// and copyright laws. The use of the Software is governed by a license
// agreement. Disclosure of the Software to third parties, in any form,
// in whole or in part, is expressly prohibited except as authorized by
// the license agreement.
//

package com.hp.mss.droid.lib.hpprint.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.widget.ImageView;

import com.hp.mss.droid.lib.hpprint.R;
import com.hp.mss.droid.lib.hpprint.activity.PrintPreview;
import com.hp.mss.droid.lib.hpprint.adapter.PhotoPrintDocumentAdapter;


public class PrintUtil {
    OnInstallPackageListener installPackageListener;

    private static String HP_PRINT_PLUGIN_PACKAGE_NAME = "com.hp.android.printservice";
    private static String GOOGLE_STORE_PACKAGE_NAME = "com.android.vending";
    private static String INSTALL_HP_PRINT_PLUGIN_MSG = "To print best quality photos, " +
                                                        "please install HP Print Plugin if you have HP printer(s)." +
                                                        " Please make sure print plugin service is turned on after the installation";

    public static final String PHOTO_FILE_URI = "PHOTO_FILE_URI";
    public static final String DPI = "DPI";


    public void launchPrint(Activity activity, String photoFileName, int dpi){

        if ( checkAndInstallHPPrintPlugin(activity) ) {

                Intent intent = new Intent(activity, PrintPreview.class);
                intent.putExtra(PHOTO_FILE_URI, photoFileName);
                intent.putExtra(DPI, dpi);
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

    public static void performPrint(Activity activity, Bitmap photo, ImageView.ScaleType scaleType) {

        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        String jobName = activity.getString(R.string.app_name);
        //need to give original photo size..
        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(activity, photo, scaleType);

        PrintAttributes printAttributes = new PrintAttributes.Builder().
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                setResolution(new PrintAttributes.Resolution("160", "160", 160, 160)).
                build();

        PrintJob printJob = printManager.print(jobName, adapter, printAttributes);

    }
}
