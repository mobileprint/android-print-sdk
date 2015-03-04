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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.PrinterId;
import android.widget.ImageView;

import com.hp.mss.droid.lib.hpprint.R;
import com.hp.mss.droid.lib.hpprint.activity.PrintPreview;
import com.hp.mss.droid.lib.hpprint.adapter.PhotoPrintDocumentAdapter;

import org.json.JSONObject;

import java.lang.reflect.Method;


public class PrintUtil {
    OnInstallPackageListener installPackageListener;

    private static String HP_PRINT_PLUGIN_PACKAGE_NAME = "com.hp.android.printservice";
    private static String GOOGLE_STORE_PACKAGE_NAME = "com.android.vending";
    private static String INSTALL_HP_PRINT_PLUGIN_MSG = "To print best quality photos, " +
                                                        "please install HP Print Plugin if you have HP printer(s)." +
                                                        " Please make sure print plugin service is turned on after the installation";

    public static final String PHOTO_FILE_URI = "PHOTO_FILE_URI";
    public static final String DPI = "DPI";
    public static final int MILS = 1000;

    public static PrintJob printJob;

    public void launchPrint(Activity activity, String photoFileName, int dpi, int request_id){

        if ( checkAndInstallHPPrintPlugin(activity) ) {

                Intent intent = new Intent(activity, PrintPreview.class);
                intent.putExtra(PHOTO_FILE_URI, photoFileName);
                intent.putExtra(DPI, dpi);
                activity.startActivityForResult(intent,request_id);
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

    public static void performPrint(Activity activity, Bitmap photo, ImageView.ScaleType scaleType, float paperWidth, float paperHeight) {

        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        String jobName = activity.getString(R.string.app_name);
        //need to give original photo size..
        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(activity, photo, scaleType);

        PrintAttributes printAttributes = new PrintAttributes.Builder().
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                setMediaSize(new PrintAttributes.MediaSize("NA", "android", (int) (paperWidth * 1000), (int) (paperHeight * 1000))).
                setResolution(new PrintAttributes.Resolution("160", "160", 160, 160)).
                build();

        printJob = printManager.print(jobName, adapter, printAttributes);

        final Handler handler = new Handler();

        final PostData postData = new PostData();
        postData.printDataCollectedListener = (PostData.OnPrintDataCollectedListener)activity;

        final Runnable r = new Runnable() {
            public void run() {
                if(printJob.isQueued() || printJob.isCompleted() || printJob.isStarted()){
                    PrintJobInfo printJobInfo = printJob.getInfo();
                    PrintAttributes printJobAttributes = printJobInfo.getAttributes();
                    PrinterId printerId = printJobInfo.getPrinterId();

                    try {
                        Method gdi = PrintJobInfo.class.getMethod("getDocumentInfo");
                        PrintDocumentInfo printDocumentInfo = (PrintDocumentInfo) gdi.invoke(printJobInfo);
                        Method gsn = PrinterId.class.getMethod("getServiceName");
                        ComponentName componentName = (ComponentName) gsn.invoke(printerId);

                        JSONObject jsonObject = new JSONObject();
                        if (printDocumentInfo.getContentType() == PrintDocumentInfo.CONTENT_TYPE_DOCUMENT) {
                            jsonObject.put("paper_type", "Document");
                        } else if (printDocumentInfo.getContentType() == PrintDocumentInfo.CONTENT_TYPE_PHOTO) {
                            jsonObject.put("paper_type", "Photo Paper");
                        } else if (printDocumentInfo.getContentType() == PrintDocumentInfo.CONTENT_TYPE_UNKNOWN) {
                            jsonObject.put("paper_type", "Unknown");
                        }
                        jsonObject.put("print_plugin_tech", componentName.getPackageName());

                        String width = Double.toString(printJobAttributes.getMediaSize().getWidthMils()/MILS);
                        String height = Double.toString(printJobAttributes.getMediaSize().getHeightMils()/MILS);

                        jsonObject.put("paper_size", width + " x " + height);
                        jsonObject.put("printer_id", printerId.getLocalId());

                        postData.printDataCollectedListener.postPrintData(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (printJob.isFailed() || printJob.isBlocked() || printJob.isCancelled()) {
                    //do nothing
                } else {
                    handler.postDelayed(this, 1000);
                }


            }
        };

        handler.postDelayed(r, 1000);

    }

}
