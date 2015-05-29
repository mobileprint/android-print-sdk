package com.hp.mss.hpprint.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by panini on 5/13/15.
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

    public static final String PRINT_RESULT_FAILED = "Fail";
    public static final String PRINT_RESULT_SUCCESS = "Success";
    public static final String PRINT_RESULT_CANCEL = "Cancel";


    public String blackAndWhiteFilter;
    public String numberOfCopy;
    public String paperSize;
    public String paperType;
    public String printPluginTech;
    public String printerID;
    public String printerLocation;
    public String printerModel;
    public String printerName;
    public String printResult;

    public PrintMetricsData() {
        this.printerID = DATA_NOT_AVAILABLE;
        this.printerLocation = DATA_NOT_AVAILABLE;
        this.printerModel = DATA_NOT_AVAILABLE;
        this.printerName = DATA_NOT_AVAILABLE;
    }

    public PrintMetricsData(Parcel in) {
        blackAndWhiteFilter = in.readString();
        numberOfCopy = in.readString();
        paperSize = in.readString();
        paperType = in.readString();
        printPluginTech = in.readString();
        printerID = in.readString();
        printerLocation = in.readString();
        printerModel = in.readString();
        printerName = in.readString();
        printResult = in.readString();
    }

    public PrintMetricsData(Map<String, String> map) {
        this.blackAndWhiteFilter = map.get(BLACK_AND_WHITE_FILTER);
        this.numberOfCopy = map.get(COPIES);
        this.paperSize = map.get(PAPER_SIZE);
        this.paperType = map.get(PAPER_TYPE);
        this.printPluginTech = map.get(PRINT_PLUGIN_TECH);
        this.printerID = map.get(PRINTER_ID);
        this.printerLocation = map.get(PRINTER_LOCATION);
        this.printerModel = map.get(PRINTER_MODEL);
        this.printerName = map.get(PRINTER_NAME);
        this.printResult = map.get(PRINT_RESULT);
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        if (blackAndWhiteFilter != null) map.put(BLACK_AND_WHITE_FILTER, this.blackAndWhiteFilter);
        if (numberOfCopy != null) map.put(COPIES, this.numberOfCopy);
        if (paperSize != null) map.put(PAPER_SIZE, this.paperSize);
        if (paperType != null) map.put(PAPER_TYPE, this.paperType);
        if (printPluginTech != null) map.put(PRINT_PLUGIN_TECH, this.printPluginTech);
        if (printerID != null) map.put(PRINTER_ID, this.printerID);
        if (printerLocation != null) map.put(PRINTER_LOCATION, this.printerLocation);
        if (printerModel != null) map.put(PRINTER_MODEL, this.printerModel);
        if (printerName != null) map.put(PRINTER_NAME, this.printerName);
        if (printResult != null) map.put(PRINT_RESULT, this.printResult);

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
        out.writeString(printPluginTech);
        out.writeString(printerID);
        out.writeString(printerLocation);
        out.writeString(printerModel);
        out.writeString(printerName);
        out.writeString(printResult);

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

