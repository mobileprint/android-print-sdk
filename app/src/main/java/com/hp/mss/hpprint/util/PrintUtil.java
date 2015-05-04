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

package com.hp.mss.hpprint.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.PrinterId;
import android.widget.ImageView;
import android.support.v4.print.PrintHelper;

import com.hp.mss.hpprint.activity.PrintPreview;
import com.hp.mss.hpprint.adapter.MultiplePhotoPrintDocumentAdapter;
import com.hp.mss.hpprint.adapter.PhotoPrintDocumentAdapter;

import org.json.JSONObject;

import java.lang.reflect.Method;


public class PrintUtil {

    public static final String IMAGE_SIZE_4x5 = "4x5";
    public static final String IMAGE_SIZE_4x6 = "4x6";
    public static final String IMAGE_SIZE_5x7 = "5x7";

    public static boolean is4x5media;

    public static final String HP_PRINT_PLUGIN_PACKAGE_NAME = "com.hp.android.printservice";
    public static final String GOOGLE_STORE_PACKAGE_NAME = "com.android.vending";
    public static final String PRINT_DATA_STRING = "PRINT_DATA_STRING";
    public static final int MILS = 1000;
    public static final int PRINT_JOB_WAIT_TIME = 1000;
    public static PrintJob printJob;

    public static enum PackageStatus {
        INSTALLED_AND_ENABLED,
        INSTALLED_AND_DISABLED,
        NOT_INSTALLED
    }


    private PrintUtil() {}


    public static PackageStatus checkHPPrintPluginStatus(Activity activity) {
        return checkPackageStatus(activity, HP_PRINT_PLUGIN_PACKAGE_NAME);
    }

    public static PackageStatus checkGooglePlayStoreStatus(Activity activity) {
        return checkPackageStatus(activity, GOOGLE_STORE_PACKAGE_NAME);
    }

    public static PackageStatus checkPackageStatus(Activity activity, String packageName) {
        if (activity == null || packageName == null)
            return PackageStatus.NOT_INSTALLED;

        PackageManager packageManager = activity.getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            //if we reach below this line, then the package is installed. Otheriwse, the catch block is executed.

            if (packageName.equals(PrintUtil.HP_PRINT_PLUGIN_PACKAGE_NAME) && deviceAlwaysReturnsPluginEnabled()) {
                //Because of a bug either in the Nexus (Lollipop) or in the HP Print Plugin code that always says the app is enabled
                // even if it is disabled, we will return INSTALLED_AND_DISABLED all the time. Note that the
                // Text in the UI telling people to enable the plugin is properly worded in light of this bug.
                return PackageStatus.INSTALLED_AND_DISABLED;
            }

            if (appInfo.enabled)
                return PackageStatus.INSTALLED_AND_ENABLED;
            else
                return PackageStatus.INSTALLED_AND_DISABLED;

        } catch (PackageManager.NameNotFoundException e) {
            return PackageStatus.NOT_INSTALLED;
        }
    }

    private  static boolean deviceAlwaysReturnsPluginEnabled() {
        if ((Build.MODEL.contains("Nexus") && Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) || //Nexus Lollipop
                (Build.MODEL.equalsIgnoreCase("SM-N900") && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) || //Samsung Note 3 KitKit
                (Build.MODEL.equalsIgnoreCase("GT-I9500") && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT))  //Samsung Galaxy S4
            return true;
        else
            return false;
    }





    public static void printWithPreview(Activity activity, String photoFileName, ImageView.ScaleType scaleType,
                                        String printJobName, int dpi, int request_id){
        printWithPreview(activity, photoFileName, false, scaleType,
                printJobName, dpi, request_id);
    }

    public static void printWithPreview(Activity activity, String photoFileName, boolean multiMediaType, ImageView.ScaleType scaleType,
                                        String printJobName, int dpi, int request_id) {
        Intent intent = new Intent(activity, PrintPreview.class);
        intent.putExtra(PrintPreview.PHOTO_FILE_URI, photoFileName);
        intent.putExtra(PrintPreview.PRINT_JOB_NAME, printJobName);
        intent.putExtra(PrintPreview.SCALE_TYPE, scaleType);
        intent.putExtra(PrintPreview.MULTIPLE_MEDIA_TYPES, multiMediaType);
        activity.startActivityForResult(intent, request_id);
    }

    public static void printMultipleMediaTypesWithPreview(Activity activity, String photoFileName, ImageView.ScaleType scaleType,
                                                          String printJobName, int dpi, int request_id){
        printWithPreview(activity, photoFileName, true, scaleType,
                printJobName, dpi, request_id);

    }

    public static void printWithoutPreview(Activity activity, Bitmap bitmap, ImageView.ScaleType scaleType, String printJobName, final OnPrintDataCollectedListener printDataListener, float paperWidth, float paperHeight){
        printUsingPrintDocumentAdapter(activity, bitmap, scaleType, printJobName, printDataListener, paperWidth, paperHeight);
    }

    public static void printMultipleMediaTypesWithoutPreview(Activity activity, ImageView.ScaleType scaleType, String printJobName, final OnPrintDataCollectedListener printDataListener, float paperWidth, float paperHeight){
        printUsingMultiplePhotoPrintDocumentAdapter(activity, scaleType, printJobName, printDataListener, paperWidth, paperHeight);
    }

    private static void printUsingPrintHelper(Activity activity, Bitmap bitmap, int scaleMode, String printJobName) {
        PrintHelper printHelper = new PrintHelper(activity);
        printHelper.setScaleMode(scaleMode);
        printHelper.printBitmap(printJobName, bitmap);
    }

    private static void printUsingMultiplePhotoPrintDocumentAdapter(Activity activity, ImageView.ScaleType scaleType, String printJobName, OnPrintDataCollectedListener printDataListener, float paperWidth, float paperHeight){
        String mediaLabel = "NA_LETTER";
        if (paperWidth == 5f || paperWidth == 7f) {
            mediaLabel ="na_5x7_5x7in";
        } else if (paperWidth == 4f || paperWidth == 6f) {
            mediaLabel ="na_index-4x6_4x6in";
            if(paperWidth == 4f && paperHeight == 5f) {
                paperHeight = 6f;
            }
        }

        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter adapter = new MultiplePhotoPrintDocumentAdapter(activity, scaleType, is4x5media);
        PrintAttributes printAttributes = new PrintAttributes.Builder().
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                setMediaSize(new PrintAttributes.MediaSize(mediaLabel, "android", (int) (paperWidth * MILS), (int) (paperHeight * MILS))).
                build();
        printJob = printManager.print(printJobName, adapter, printAttributes);

        doPrintMetrics(printDataListener);
    }

    private static void printUsingPrintDocumentAdapter(Activity activity, Bitmap bitmap, ImageView.ScaleType scaleType, String printJobName, OnPrintDataCollectedListener printDataListener, float paperWidth, float paperHeight){
        String mediaLabel = "NA_LETTER";
        if (paperWidth == 5f || paperWidth == 7f) {
            mediaLabel ="na_5x7_5x7in";
        } else if (paperWidth == 4f || paperWidth == 6f) {
            mediaLabel ="na_index-4x6_4x6in";
        }

        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(activity, bitmap, scaleType);
        PrintAttributes printAttributes = new PrintAttributes.Builder().
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                setMediaSize(new PrintAttributes.MediaSize(mediaLabel, "android", (int) (paperWidth * MILS), (int) (paperHeight * MILS))).
                setResolution(new PrintAttributes.Resolution("160", "160", 160, 160)).
                build();
        printJob = printManager.print(printJobName, adapter, printAttributes);

        doPrintMetrics(printDataListener);
    }

    private static void doPrintMetrics(final OnPrintDataCollectedListener printDataListener){
        final Handler handler = new Handler();
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

                        String width = Double.toString(printJobAttributes.getMediaSize().getWidthMils()/(float)MILS);
                        String height = Double.toString(printJobAttributes.getMediaSize().getHeightMils()/(float)MILS);

                        jsonObject.put("paper_size", width + " x " + height);
                        jsonObject.put("printer_id", printerId.getLocalId());

                        printDataListener.postPrintData(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (printJob.isFailed() || printJob.isBlocked() || printJob.isCancelled()) {
                    //do nothing
                } else {
                    handler.postDelayed(this, PRINT_JOB_WAIT_TIME);
                }
            }
        };

        handler.postDelayed(r, PRINT_JOB_WAIT_TIME);
    }

    public interface OnPrintDataCollectedListener {
        public void postPrintData(JSONObject jsonObject);
    }
}
