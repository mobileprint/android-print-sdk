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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * PrintJobData is required for using the HP Print SDK workflow. It contains all the relevant info
 * needed to build the print job.
 */
public class PrintJobData implements Parcelable{
    private String jobName = "default";

    private Map<PrintAttributes.MediaSize, PrintItem> printItems;

    private PrintItem defaultPrintItem;

    private PrintAttributes printDialogOptions;

    private Context context;
    String previewPaperSize = "N/A";

    /**
     * Use this constructor to create an instance of the PrintJobData object with a default print item.
     * @param context The context.
     * @param defaultPrintItem The minimum required print item for the HP Print SDK print flow.
     */
    //TODO: validate defaultAssetUri is consistent with itemType
    public PrintJobData(Context context, PrintItem defaultPrintItem) {
        setDefaultPrintItem(defaultPrintItem);
        if(defaultPrintItem == null) {
            throw new NullPointerException("defaultPrintItem is required to be set.");
        }
        this.context = context;
    }

    /**
     * Gets the print job name.
     * @return The job name string.
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets the print job name.
     * @param jobName The print job name.
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets the default {@link PrintItem}.
     * @return The default print item.
     */
    public PrintItem getDefaultPrintItem() {
        return defaultPrintItem;
    }

    /**
     * Sets the default {@link PrintItem}.
     * @param defaultPrintItem The default print item.
     */
    public void setDefaultPrintItem(PrintItem defaultPrintItem) {
        this.defaultPrintItem = defaultPrintItem;
    }

    /**
     * Gets the number of print items.
     * @return The number of print items.
     */
    public int numPrintItems(){
        if(printItems == null){
            return 0;
        }
        return printItems.size();
    }

    /**
     * Gets the print item hash map.
     * @return The hash map of print items.
     */
    public Map<PrintAttributes.MediaSize, PrintItem> getPrintItems(){
        return printItems;
    }

    /**
     * Adds a print item to the hashmap. Each print item needs a unique media size.
     * @param printItem The print item you want to add to the print item hash map.
     */
    public void addPrintItem(PrintItem printItem) {
        if(printItems == null) {
            printItems = new HashMap();
        }
        printItems.put(printItem.getMediaSize(), printItem);
    }

    /**
     * This method gets a print item using a media size as the key. If the print item does not exist,
     * the default print item will be used.
     * @param mediaSize The media size of the print item you want to retrieve.
     * @return The print item if found, otherwise the default print item.
     */
    public PrintItem getPrintItem(PrintAttributes.MediaSize mediaSize){
        if(printItems == null){
            PrintItem newPrintItem = defaultPrintItem;
            newPrintItem.mediaSize = mediaSize;
            return newPrintItem;
        }

        PrintItem printItem = printItems.get(mediaSize);

        if(printItem == null) {
            //get it for other orientation
            printItem = printItems.get(new PrintAttributes.MediaSize(mediaSize.getId(), mediaSize.getLabel(context.getPackageManager()),mediaSize.getHeightMils(), mediaSize.getWidthMils()));
            if (printItem == null)
                return defaultPrintItem;
            else
                return printItem;

        }

        return printItem;
    }

    /**
     * This method is used to set the default dialog options that appear when the Android print dialog
     * shows up.
     * @see <a href="https://developer.android.com/reference/android/print/PrintAttributes.html">PrintAttributes</a>
     * @param printAttributes Android {@link PrintAttributes}.
     */
    public void setPrintDialogOptions(PrintAttributes printAttributes){
        this.printDialogOptions = printAttributes;
    }

    /**
     * Get the print dialog options.
     * @return Android {@link PrintAttributes}.
     * @see <a href="https://developer.android.com/reference/android/print/PrintAttributes.html">PrintAttributes</a>
     */
    public PrintAttributes getPrintDialogOptions() {
        return printDialogOptions;
    }

    public String getPreviewPaperSize() {
        return previewPaperSize;
    }

    public void setPreviewPaperSize(String previewPaperSize) {
        this.previewPaperSize = previewPaperSize;
    }

    //Parcelable methods
    protected PrintJobData(Parcel in) {
        jobName = in.readString();
        printDialogOptions = (PrintAttributes) in.readValue(PrintAttributes.class.getClassLoader());
//        printItems = (HashMap) in.readParcelable(HashMap.class.getClassLoader());
        int size = in.readInt();
        printItems = new HashMap<>();
        for(int i = 0; i < size; i++){
            printItems.put(new PrintAttributes.MediaSize(in.readString(), "android", in.readInt(), in.readInt()), (PrintItem) in.readValue(PrintItem.class.getClassLoader()));
        }
    }

    public boolean containsPDFItem(){
        if(getDefaultPrintItem() instanceof PDFPrintItem){
            return true;
        }

        if(printItems == null){
            return false;
        }

        for(PrintItem printItem : printItems.values()){
            if(printItem instanceof PDFPrintItem){
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobName);
        dest.writeValue(printDialogOptions);

        dest.writeInt(printItems.size());
        for (PrintAttributes.MediaSize mediaSize: printItems.keySet()) {
            dest.writeString(mediaSize.getId());
            dest.writeInt(mediaSize.getWidthMils());
            dest.writeInt(mediaSize.getHeightMils());
            dest.writeValue(printItems.get(mediaSize));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PrintJobData> CREATOR = new Parcelable.Creator<PrintJobData>() {
        @Override
        public PrintJobData createFromParcel(Parcel in) {
            return new PrintJobData(in);
        }

        @Override
        public PrintJobData[] newArray(int size) {
            return new PrintJobData[size];
        }
    };

}
