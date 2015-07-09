package com.hp.mss.hpprint.model;

import android.content.Context;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintAttributes;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by panini on 6/30/15.
 */
public class PrintJob implements Parcelable{
    String jobName;

    Map<PrintAttributes.MediaSize, PrintItem> printItems;

    PrintItem defaultPrintItem;

    PrintAttributes printDialogOptions;

    Context context;

    public enum ItemType {
        IMAGE,
        PDF,
        HTML
    }

    //TODO: validate defaultAssetUri is consistent with itemType
    public PrintJob(Context context, PrintItem defaultPrintItem) {
        setDefaultPrintItem(defaultPrintItem);
        if(defaultPrintItem == null) {
            throw new NullPointerException("defaultPrintItem is required to be set.");
        }
        this.context = context;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public PrintItem getDefaultPrintItem() {
        return defaultPrintItem;
    }

    public void setDefaultPrintItem(PrintItem defaultPrintItem) {
        this.defaultPrintItem = defaultPrintItem;
    }
    public int numPrintItems(){
        return printItems.size();
    }
    public Map<PrintAttributes.MediaSize, PrintItem> getPrintItems(){
        return printItems;
    }
    public void addPrintItem(PrintItem printItem) {
        if(printItems == null) {
            printItems = new HashMap();
        }
        printItems.put(printItem.getMediaSize(), printItem);
    }

    public PrintItem getPrintItem(PrintAttributes.MediaSize mediaSize){
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

    public void setPrintDialogOptions(PrintAttributes printAttributes){
        this.printDialogOptions = printAttributes;
    }

    public PrintAttributes getPrintDialogOptions() {
        return printDialogOptions;
    }

    //Parcelable methods
    protected PrintJob(Parcel in) {
        jobName = in.readString();
        printDialogOptions = (PrintAttributes) in.readValue(PrintAttributes.class.getClassLoader());
//        printItems = (HashMap) in.readParcelable(HashMap.class.getClassLoader());
        int size = in.readInt();
        printItems = new HashMap<>();
        for(int i = 0; i < size; i++){
            printItems.put(new PrintAttributes.MediaSize(in.readString(), "android", in.readInt(), in.readInt()), (PrintItem) in.readValue(PrintItem.class.getClassLoader()));
        }

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
    public static final Parcelable.Creator<PrintJob> CREATOR = new Parcelable.Creator<PrintJob>() {
        @Override
        public PrintJob createFromParcel(Parcel in) {
            return new PrintJob(in);
        }

        @Override
        public PrintJob[] newArray(int size) {
            return new PrintJob[size];
        }
    };

}
