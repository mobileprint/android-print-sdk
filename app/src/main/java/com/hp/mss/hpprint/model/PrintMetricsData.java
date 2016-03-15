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

import android.os.Parcel;
import android.os.Parcelable;

import com.hp.mss.hpprint.util.PrintUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the class that encapsulates all the printing related data HP is collecting.
 * This data is available to your app if the calling activity implements
 * {@link com.hp.mss.hpprint.util.PrintUtil.PrintMetricsListener PrintUtil.PrintMetricsListener} interface.
 *
 */
public class PrintMetricsData implements Parcelable {

    public static final String CONTENT_TYPE_DOCUMENT = "Document";
    public static final String CONTENT_TYPE_PHOTO = "Photo";
    public static final String CONTENT_TYPE_UNKNOWN = "Unknown";
    private static final String DATA_NOT_AVAILABLE = "No data available";
    private static final String BLACK_AND_WHITE_FILTER = "black_and_white_filter";
    private static final String COPIES = "copies";
    private static final String PAPER_SIZE = "paper_size";
    private static final String PAPER_TYPE = "paper_type";
    private static final String PRINT_PLUGIN_TECH = "print_plugin_tech";
    private static final String PRINTER_ID = "printer_id";
    private static final String PRINTER_LOCATION = "printer_location";
    private static final String PRINTER_MODEL = "printer_model";
    private static final String PRINTER_NAME = "printer_name";
    private static final String PRINT_RESULT = "print_result";
    private static final String PREVIEW_PAPER_SIZE = "preview_paper_size";
    private static final String CONTENT_TYPE = "content_type";
    private static final String CONTENT_WIDTH_PIXELS = "content_width_pixels";
    private static final String CONTENT_HEIGHT_PIXELS = "content_height_pixels";
    private static final String NUM_OF_PLUGINS_INSTALLED = "num_of_plugins_installed";
    private static final String NUM_OF_PLUGINS_ENABLED = "num_of_plugins_enabled";
    private static final String CUSTOM_DATA = "custom_data";


    public static final String PRINT_RESULT_FAILED = "Fail";
    public static final String PRINT_RESULT_SUCCESS = "Success";
    public static final String PRINT_RESULT_CANCEL = "Cancel";


    public String blackAndWhiteFilter;
    public String numberOfCopy;
    public String paperSize;
    public String paperType;
    public String previewPaperSize;
    public String printPluginTech;
    public String printerID;
    public String printerLocation;
    public String printerModel;
    public String printerName;
    public String printResult;
    public String contentType;
    public String contentWidthPixels;
    public String contentHeightPixels;
    public String numOfPluginsInstalled;
    public String numOfPluginsEnabled;
    public String customData;


    public PrintMetricsData() {
        this.printerID = DATA_NOT_AVAILABLE;
        this.printerLocation = DATA_NOT_AVAILABLE;
        this.printerModel = DATA_NOT_AVAILABLE;
        this.printerName = DATA_NOT_AVAILABLE;
        if(PrintUtil.customData.isEmpty())
            this.customData = "N/A";
        else
            this.customData = PrintUtil.customData.toString();
    }

    public PrintMetricsData(Parcel in) {
        blackAndWhiteFilter = in.readString();
        numberOfCopy = in.readString();
        paperSize = in.readString();
        paperType = in.readString();
        previewPaperSize = in.readString();
        printPluginTech = in.readString();
        printerID = in.readString();
        printerLocation = in.readString();
        printerModel = in.readString();
        printerName = in.readString();
        printResult = in.readString();
        contentType = in.readString();
        contentWidthPixels = in.readString();
        contentHeightPixels = in.readString();
        numOfPluginsInstalled = in.readString();
        numOfPluginsEnabled = in.readString();
        customData = in.readString();
    }

    public PrintMetricsData(Map<String, String> map) {
        this.blackAndWhiteFilter = map.get(BLACK_AND_WHITE_FILTER);
        this.numberOfCopy = map.get(COPIES);
        this.paperSize = map.get(PAPER_SIZE);
        this.paperType = map.get(PAPER_TYPE);
        this.previewPaperSize = map.get(PREVIEW_PAPER_SIZE);
        this.printPluginTech = map.get(PRINT_PLUGIN_TECH);
        this.printerID = map.get(PRINTER_ID);
        this.printerLocation = map.get(PRINTER_LOCATION);
        this.printerModel = map.get(PRINTER_MODEL);
        this.printerName = map.get(PRINTER_NAME);
        this.printResult = map.get(PRINT_RESULT);
        this.contentType = map.get(CONTENT_TYPE);
        this.contentWidthPixels = map.get(CONTENT_WIDTH_PIXELS);
        this.contentHeightPixels = map.get(CONTENT_HEIGHT_PIXELS);
        this.numOfPluginsInstalled = map.get(NUM_OF_PLUGINS_INSTALLED);
        this.numOfPluginsEnabled = map.get(NUM_OF_PLUGINS_ENABLED);
        this.customData = map.get(CUSTOM_DATA);
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        if (blackAndWhiteFilter != null) map.put(BLACK_AND_WHITE_FILTER, this.blackAndWhiteFilter);
        if (numberOfCopy != null) map.put(COPIES, this.numberOfCopy);
        if (paperSize != null) map.put(PAPER_SIZE, this.paperSize);
        if (paperType != null) map.put(PAPER_TYPE, this.paperType);
        if (previewPaperSize != null) map.put(PREVIEW_PAPER_SIZE, this.previewPaperSize);
        if (printPluginTech != null) map.put(PRINT_PLUGIN_TECH, this.printPluginTech);
        if (printerID != null) map.put(PRINTER_ID, this.printerID);
        if (printerLocation != null) map.put(PRINTER_LOCATION, this.printerLocation);
        if (printerModel != null) map.put(PRINTER_MODEL, this.printerModel);
        if (printerName != null) map.put(PRINTER_NAME, this.printerName);
        if (printResult != null) map.put(PRINT_RESULT, this.printResult);
        if (contentType != null) map.put(CONTENT_TYPE, this.contentType);
        if (contentWidthPixels != null) map.put(CONTENT_WIDTH_PIXELS, this.contentWidthPixels);
        if (contentHeightPixels != null) map.put(CONTENT_HEIGHT_PIXELS, this.contentHeightPixels);
        if (numOfPluginsInstalled != null) map.put(NUM_OF_PLUGINS_INSTALLED, this.numOfPluginsInstalled);
        if (numOfPluginsEnabled != null) map.put(NUM_OF_PLUGINS_ENABLED, this.numOfPluginsEnabled);
        if (customData != null) map.put(CUSTOM_DATA, this.customData);

        return map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(blackAndWhiteFilter);
        out.writeString(numberOfCopy);
        out.writeString(paperSize);
        out.writeString(paperType);
        out.writeString(previewPaperSize);
        out.writeString(printPluginTech);
        out.writeString(printerID);
        out.writeString(printerLocation);
        out.writeString(printerModel);
        out.writeString(printerName);
        out.writeString(printResult);
        out.writeString(contentType);
        out.writeString(contentWidthPixels);
        out.writeString(contentHeightPixels);
        out.writeString(numOfPluginsInstalled);
        out.writeString(numOfPluginsEnabled);
        out.writeString(customData);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PrintMetricsData createFromParcel(Parcel in) {
            return new PrintMetricsData(in);
        }

        public PrintMetricsData[] newArray(int size) {
            return new PrintMetricsData[size];
        }
    };

}

