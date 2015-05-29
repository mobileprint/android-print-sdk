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
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.v4.print.PrintHelper;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hp.mss.hpprint.activity.PrintPreview;
import com.hp.mss.hpprint.adapter.MultiplePhotoPrintDocumentAdapter;
import com.hp.mss.hpprint.adapter.PhotoPrintDocumentAdapter;
import com.hp.mss.hpprint.model.PrintMetricsData;


public class PrintUtil {

    public static final String IMAGE_SIZE_4x5 = "4x5";
    public static final String IMAGE_SIZE_4x6 = "4x6";
    public static final String IMAGE_SIZE_5x7 = "5x7";

    public static boolean is4x5media;

    static final String HP_PRINT_PLUGIN_PACKAGE_NAME = "com.hp.android.printservice";
    private static final String SHOW_4X5_MESSAGE_KEY = "com.hp.mss.hpprint.Show4x5DialogMessage";
    private static final String GOOGLE_STORE_PACKAGE_NAME = "com.android.vending";
    private static final String PRINT_DATA_STRING = "PRINT_DATA_STRING";
    private static final int MILS = 1000;
    private static final int CURRENT_PRINT_PACKAGE_VERSION_CODE = 62; //Updated as of May 15,2015

    public enum PackageStatus {
        INSTALLED_AND_NOT_UPDATED,
        INSTALLED_AND_UPDATED,
        INSTALLED_AND_ENABLED,
        INSTALLED_AND_DISABLED,
        NOT_INSTALLED
    }


    private PrintUtil() {
    }


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

    public static PackageStatus checkPrintPackageStatus(@NonNull Activity activity) {
        if (activity == null)
            return PackageStatus.NOT_INSTALLED;

        PackageManager packageManager = activity.getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(PrintUtil.HP_PRINT_PLUGIN_PACKAGE_NAME, PackageManager.GET_META_DATA);
            PackageInfo packageInfo = packageManager.getPackageInfo(PrintUtil.HP_PRINT_PLUGIN_PACKAGE_NAME, 0);
            if (packageInfo.versionCode >= CURRENT_PRINT_PACKAGE_VERSION_CODE) {
                return PackageStatus.INSTALLED_AND_UPDATED;
            } else {
                return PackageStatus.INSTALLED_AND_NOT_UPDATED;
            }
            //if we reach below this line, then the package is installed. Otheriwse, the catch block is executed.

        } catch (PackageManager.NameNotFoundException e) {
            return PackageStatus.NOT_INSTALLED;
        }
    }

    private static boolean deviceAlwaysReturnsPluginEnabled() {
        return (Build.MODEL.contains("Nexus") && Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) || //Nexus Lollipop
                (Build.MODEL.equalsIgnoreCase("SM-N900") && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) || //Samsung Note 3 KitKit
                (Build.MODEL.equalsIgnoreCase("GT-I9500") && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT); //Samsung Galaxy S4
    }

    public static void printWithPreview(Activity activity, String photoFileName, ImageView.ScaleType scaleType,
                                        String printJobName, int dpi, int request_id) {
        printWithPreview(activity, photoFileName, false, scaleType,
                printJobName, dpi, request_id);
    }

    public static void setTracker(Tracker tracker) {
        GAUtil.setTracker(tracker);
    }

    public static void printWithPreview(Activity activity, String photoFileName, boolean multiMediaType, ImageView.ScaleType scaleType,
                                        String printJobName, int dpi, int requestId) {
        Intent intent = new Intent(activity, PrintPreview.class);
        intent.putExtra(PrintPreview.PHOTO_FILE_URI, photoFileName);
        intent.putExtra(PrintPreview.PRINT_JOB_NAME, printJobName);
        intent.putExtra(PrintPreview.SCALE_TYPE, scaleType);
        intent.putExtra(PrintPreview.MULTIPLE_MEDIA_TYPES, multiMediaType);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        activity.startActivityForResult(intent, requestId);
    }

    public static void printMultipleMediaTypesWithPreview(Activity activity, String photoFileName, ImageView.ScaleType scaleType,
                                                          String printJobName, int dpi, int requestId) {
        printWithPreview(activity, photoFileName, true, scaleType,
                printJobName, dpi, requestId);

    }

    public static void printWithoutPreview(Activity activity, Bitmap bitmap, ImageView.ScaleType scaleType, String printJobName, final OnPrintDataCollectedListener printDataListener, float paperWidth, float paperHeight) {
        printUsingPrintDocumentAdapter(activity, bitmap, scaleType, printJobName, printDataListener, paperWidth, paperHeight);
    }

    public static void printMultipleMediaTypesWithoutPreview(Activity activity, ImageView.ScaleType scaleType, String printJobName, final OnPrintDataCollectedListener printDataListener, float paperWidth, float paperHeight) {
        printUsingMultiplePhotoPrintDocumentAdapter(activity, scaleType, printJobName, printDataListener, paperWidth, paperHeight);
    }

    private static void printUsingPrintHelper(Activity activity, Bitmap bitmap, int scaleMode, String printJobName) {
        PrintHelper printHelper = new PrintHelper(activity);
        printHelper.setScaleMode(scaleMode);
        printHelper.printBitmap(printJobName, bitmap);
    }

    private static void printUsingMultiplePhotoPrintDocumentAdapter(Activity activity, ImageView.ScaleType scaleType, String printJobName, OnPrintDataCollectedListener printDataListener, float paperWidth, float paperHeight) {
        String mediaLabel = "NA_LETTER";
        if (paperWidth == 5f || paperWidth == 7f) {
            mediaLabel = "na_5x7_5x7in";
        } else if (paperWidth == 4f || paperWidth == 6f) {
            mediaLabel = "na_index-4x6_4x6in";
            if (paperWidth == 4f && paperHeight == 5f) {
                paperHeight = 6f;
            }
        }

        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter adapter = new MultiplePhotoPrintDocumentAdapter(activity, scaleType, is4x5media);
        PrintAttributes printAttributes = new PrintAttributes.Builder().
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                setMediaSize(new PrintAttributes.MediaSize(mediaLabel, "android", (int) (paperWidth * MILS), (int) (paperHeight * MILS))).
                build();
        PrintJob printJob = printManager.print(printJobName, adapter, printAttributes);

        PrintMetricsCollector collector = new PrintMetricsCollector(printJob, printDataListener);
        collector.run();
    }

    private static void printUsingPrintDocumentAdapter(Activity activity, Bitmap bitmap, ImageView.ScaleType scaleType, String printJobName, OnPrintDataCollectedListener printDataListener, float paperWidth, float paperHeight) {
        String mediaLabel = "NA_LETTER";
        if (paperWidth == 5f || paperWidth == 7f) {
            mediaLabel = "na_5x7_5x7in";
        } else if (paperWidth == 4f || paperWidth == 6f) {
            mediaLabel = "na_index-4x6_4x6in";
        }

        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(activity, bitmap, scaleType);
        PrintAttributes printAttributes = new PrintAttributes.Builder().
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                setMediaSize(new PrintAttributes.MediaSize(mediaLabel, "android", (int) (paperWidth * MILS), (int) (paperHeight * MILS))).
                setResolution(new PrintAttributes.Resolution("160", "160", 160, 160)).
                build();
        PrintJob printJob = printManager.print(printJobName, adapter, printAttributes);

        PrintMetricsCollector collector = new PrintMetricsCollector(printJob, printDataListener);
        collector.run();
    }

    public interface OnPrintDataCollectedListener {
        void postPrintData(PrintMetricsData data);
    }
}
