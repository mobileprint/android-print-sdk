package com.hp.mss.hpprint.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Parcel;
import android.print.PrintAttributes;

import com.hp.mss.hpprint.model.asset.Asset;
import com.hp.mss.hpprint.model.asset.PDFAsset;

public class PDFPrintItem extends PrintItem{

    PDFPrintItem() {
        super();
    }

    public PDFPrintItem(PrintAttributes.MediaSize mediaSize, ScaleType scaleType, PDFAsset asset) {
        super(mediaSize, scaleType, asset);
    }

    public PDFPrintItem(PrintAttributes.MediaSize mediaSize, PDFAsset asset) {
        this(mediaSize, DEFAULT_SCALE_TYPE, asset);
    }

    public PDFPrintItem(ScaleType scaleType, PDFAsset asset) {
        this(null, scaleType, asset);
    }
    @Override
    public PrintAttributes.MediaSize getMediaSize() {
        return super.getMediaSize();
    }

    @Override
    public ScaleType getScaleType() {
        return super.getScaleType();
    }

    @Override
    public Asset getAsset() {
        return super.getAsset();
    }

    @Override
    public Bitmap getPrintableBitmap() {
        return super.getPrintableBitmap();
    }

    @Override
    public void drawPage(Canvas canvas, float dpi, RectF pageBounds) {

    }

    @Override
    protected void cleanup(Context context){
        return;
    }

    protected PDFPrintItem(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}
