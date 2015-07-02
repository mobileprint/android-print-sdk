package com.hp.mss.hpprint.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintAttributes;

import com.hp.mss.hpprint.util.ImageLoaderUtil;

/**
 * Created by panini on 6/30/15.
 */
//Clients are able to override printitems, to have their own layouting methods.
public abstract class PrintItem implements Parcelable{

    PrintAttributes.MediaSize mediaSize;
    ScaleType scaleType;
    String uri;
    public static PrintItem.ScaleType DEFAULT_SCALE_TYPE = null;
    public enum ScaleType {
        CENTER,
        CENTER_CROP,
        CENTER_INSIDE,
        FIT_CENTER,
        FIT_END,
        FIT_START,
        FIT_XY,
        MATRIX
    }

    PrintItem () {
    }

    PrintItem (PrintAttributes.MediaSize mediaSize, ScaleType scaleType,String uri) {
        this.scaleType = scaleType;
        this.mediaSize = mediaSize;
        this.uri = uri;
    }

    PrintItem (String uri) {
        this.uri = uri;
    }

    public PrintAttributes.MediaSize getMediaSize(){
        return mediaSize;
    }

    public ScaleType getScaleType(){
        return scaleType;
    }

    public String getUri(){
        return uri;
    }



    public abstract Bitmap getPrintableBitmap();

    public abstract PdfDocument getPrintablePDF();

    public abstract void drawPage(PdfDocument.Page page);


    //Parcelable methods
    protected PrintItem(Parcel in) {
        mediaSize = new PrintAttributes.MediaSize(in.readString(), "android", in.readInt(), in.readInt());
        scaleType = (ScaleType) in.readValue(ScaleType.class.getClassLoader());
        uri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeValue(mediaSize);
        dest.writeString(mediaSize.getId());
        dest.writeInt(mediaSize.getWidthMils());
        dest.writeInt(mediaSize.getHeightMils());

        dest.writeValue(scaleType);
        dest.writeString(uri);
    }
}
