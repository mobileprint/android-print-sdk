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
import android.os.Build;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;

import com.hp.mss.hpprint.activity.PrintPreview;
import com.hp.mss.hpprint.adapter.HPPrintDocumentAdapter;
import com.hp.mss.hpprint.model.PrintMetricsData;

public class PrintUtil {

    public static boolean is4x5media;
    public static boolean showPluginHelper = true;

    public static final String PLAY_STORE_PRINT_SERVICES_URL = "https://play.google.com/store/apps/collection/promotion_3000abc_print_services";
    public static final String HAS_METRICS_LISTENER = "has_metrics_listener";
    public static final int START_PREVIEW_ACTIVITY_REQUEST = 100;

    private static com.hp.mss.hpprint.model.PrintJob printJob;

    public static PrintMetricsListener metricsListener;

    public static void print(Activity activity){
        metricsListener = null;

        if (checkIfActivityImplementsInterface(activity, PrintUtil.PrintMetricsListener.class)) {
            metricsListener = (PrintMetricsListener) activity;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (PrintUtil.showPluginHelper) {
                showPluginHelper(activity);
            } else {
                createPrintJob(activity);
            }
        } else {
            startPrintPreviewActivity(activity);
        }
    }

    public static void createPrintJob(Activity activity) {
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter adapter = new HPPrintDocumentAdapter(activity, printJob, false);

        PrintJob androidPrintJob = printManager.print(printJob.getJobName(), adapter, printJob.getPrintDialogOptions());

        PrintMetricsCollector collector = new PrintMetricsCollector(activity, androidPrintJob);
        collector.run();
    }

    public static void setPrintJob(com.hp.mss.hpprint.model.PrintJob printJobData){
        printJob = printJobData;
    }

    public static com.hp.mss.hpprint.model.PrintJob getPrintJob(){
        return printJob;
    }

    public interface PrintMetricsListener {
        void onPrintMetricsDataPosted(PrintMetricsData printMetricsData);
    }

    private static void showPluginHelper(final Activity activity) {
        PrintPluginHelper.PluginHelperListener printPluginListener = new PrintPluginHelper.PluginHelperListener() {
            @Override
            public void printPluginHelperSkippedByPreference() {
                createPrintJob(activity);
            }

            @Override
            public void printPluginHelperSkipped() {
                createPrintJob(activity);
            }

            @Override
            public void printPluginHelperSelected() {
            }

            @Override
            public void printPluginHelperCanceled() {
            }
        };
        PrintPluginHelper.showPluginHelper(activity, printPluginListener);
    }

    private static void startPrintPreviewActivity(Activity activity) {
        Intent intent = new Intent(activity, PrintPreview.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean has_metrics_listener = (metricsListener != null) ? true : false;
        intent.putExtra(HAS_METRICS_LISTENER, has_metrics_listener);

        activity.startActivityForResult(intent, START_PREVIEW_ACTIVITY_REQUEST);
    }

    private static boolean checkIfActivityImplementsInterface(Activity theActivity, Class theInterface){
        for (Class i : theActivity.getClass().getInterfaces())
            if (i.toString().equals(theInterface.toString()))
                return true;
        return false;
    }

}
