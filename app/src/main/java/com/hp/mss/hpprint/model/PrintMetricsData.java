package com.hp.mss.hpprint.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintJob;
import android.util.Log;

import com.hp.mss.hpprint.util.PrintUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by panini on 5/13/15.
 */
public class PrintMetricsData implements Parcelable {


    public static final String DATA_NOT_AVAILABLE = "No data available";
    public static final String CONTENT_TYPE_DOCUMENT = "Document";
    public static final String CONTENT_TYPE_PHOTO = "Photo";
    public static final String CONTENT_TYPE_UNKNOWN = "Unknown";

    PrintJob printJob;
    PrintUtil.OnPrintDataCollectedListener collectedListener;

    public String blackAndWhiteFilter;
    public String numberOfCopy;
    public String paperSize;
    public String paperType;
    public String printPluginTech;
    public String printerID;
    public String printerLocation;
    public String printerModel;
    public String printerName;

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
    }

    public PrintMetricsData(Map<String, String> map) {
        this.blackAndWhiteFilter = map.get("black_and_white_filter");
        this.numberOfCopy = map.get("copies");
        this.paperSize = map.get("paper_size");
        this.paperType = map.get("paper_type");
        this.printPluginTech = map.get("print_plugin_tech");
        this.printerID = map.get("printer_id");
        this.printerLocation = map.get("printer_location");
        this.printerModel = map.get("printer_model");
        this.printerName = map.get("printer_name");
    }

    public Map<String,String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("black_and_white_filter", this.blackAndWhiteFilter);
        map.put("copies", this.numberOfCopy);
        map.put("paper_size", this.paperSize);
        map.put("paper_type", this.paperType);
        map.put("print_plugin_tech", this.printPluginTech);
        map.put("printer_id", this.printerID);
        map.put("printer_location", this.printerLocation);
        map.put("printer_model", this.printerModel);
        map.put("printer_name", this.printerName);

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

