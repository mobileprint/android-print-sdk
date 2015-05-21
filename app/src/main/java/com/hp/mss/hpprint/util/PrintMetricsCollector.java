package com.hp.mss.hpprint.util;

import android.content.ComponentName;
import android.print.PrintAttributes;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.util.Log;

import com.hp.mss.hpprint.model.PrintMetricsData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.os.Handler;


/**
 * Created by panini on 5/18/15.
 */


class PrintMetricsCollector extends Thread {
    private static final int PRINT_JOB_WAIT_TIME = 1000;
    private static final int MILS = 1000;

    android.print.PrintJob printJob;
    PrintUtil.OnPrintDataCollectedListener collectedListener;
    Handler metricsHandler;

    public PrintMetricsCollector(PrintJob printJob, PrintUtil.OnPrintDataCollectedListener collectedListener) {
        this.printJob = printJob;
        this.collectedListener = collectedListener;
        this.metricsHandler = new Handler();
    }

    @Override
    public void run() {

        if ( printJob == null || collectedListener == null || isJobFailed(printJob) ) {

            return;

        } else if ( hasJobInfo(printJob) ) {

            PrintJobInfo printJobInfo = printJob.getInfo();
            PrintAttributes printJobAttributes = printJobInfo.getAttributes();
            PrinterId printerId = printJobInfo.getPrinterId();

            PrintMetricsData metricsData = new PrintMetricsData();

            try {
                Method gdi = PrintJobInfo.class.getMethod("getDocumentInfo");
                PrintDocumentInfo printDocumentInfo = (PrintDocumentInfo) gdi.invoke(printJobInfo);
                Method gsn = PrinterId.class.getMethod("getServiceName");
                ComponentName componentName = (ComponentName) gsn.invoke(printerId);

                metricsData.printPluginTech = componentName.getPackageName();

                if (printDocumentInfo.getContentType() == PrintDocumentInfo.CONTENT_TYPE_DOCUMENT) {
                    metricsData.paperType = PrintMetricsData.CONTENT_TYPE_DOCUMENT;
                } else if (printDocumentInfo.getContentType() == PrintDocumentInfo.CONTENT_TYPE_PHOTO) {
                    metricsData.paperType = PrintMetricsData.CONTENT_TYPE_PHOTO;
                } else if (printDocumentInfo.getContentType() == PrintDocumentInfo.CONTENT_TYPE_UNKNOWN) {
                    metricsData.paperType = PrintMetricsData.CONTENT_TYPE_UNKNOWN;
                }

                metricsData.blackAndWhiteFilter = String.valueOf(printJobInfo.getAttributes().getColorMode());

                String width = Double.toString(printJobAttributes.getMediaSize().getWidthMils() / (float) MILS);
                String height = Double.toString(printJobAttributes.getMediaSize().getHeightMils() / (float) MILS);

                metricsData.paperSize =  (width + " x " + height).toString();
                metricsData.printerID = printerId.getLocalId();
                metricsData.numberOfCopy = String.valueOf(printJobInfo.getCopies());

                collectedListener.postPrintData(metricsData);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                Log.e("ERROR", "CollectionRunner: " + e.getMessage());
            }

        } else {

            metricsHandler.postDelayed(this, PRINT_JOB_WAIT_TIME);

        }
    }


    private static boolean hasJobInfo(final android.print.PrintJob printJob) {
        return ( printJob.isQueued() || printJob.isCompleted() || printJob.isStarted() );
    }

    private static boolean isJobFailed(final android.print.PrintJob printJob) {
        return ( printJob.isFailed() || printJob.isBlocked() || printJob.isCancelled() );
    }
}


