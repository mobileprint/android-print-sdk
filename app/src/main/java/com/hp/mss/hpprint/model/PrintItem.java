package com.hp.mss.hpprint.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintAttributes;

import com.hp.mss.hpprint.model.asset.Asset;

/**
 * Created by panini on 6/30/15.
 */
//Clients are able to override printitems, to have their own layouting methods.
public abstract class PrintItem implements Parcelable{

    PrintAttributes.MediaSize mediaSize;
    ScaleType scaleType;
    Asset asset;

    public static PrintItem.ScaleType DEFAULT_SCALE_TYPE = null;
    public enum ScaleType {
        CENTER,
        CROP,
        FIT,
        CENTER_TOP_LEFT
//        CENTER_INSIDE,
//        FIT_CENTER,
//        FIT_END,
//        FIT_START,
//        FIT_XY,
//        MATRIX
    }

    PrintItem () {
    }

    PrintItem (PrintAttributes.MediaSize mediaSize, ScaleType scaleType, Asset asset) {
        this.scaleType = scaleType;
        this.mediaSize = mediaSize;
        this.asset = asset;
    }

    public PrintAttributes.MediaSize getMediaSize(){
        return mediaSize;
    }

    public ScaleType getScaleType(){
        return scaleType;
    }

    public Asset getAsset(){
        return asset;
    }

    public Bitmap getPrintableBitmap(){
        return asset.getPrintableBitmap();
    }

    public abstract void drawPage(Canvas canvas, float dpi, RectF pageBounds);

    //Parcelable methods
    protected PrintItem(Parcel in) {
        mediaSize = new PrintAttributes.MediaSize(in.readString(), "android", in.readInt(), in.readInt());
        scaleType = (ScaleType) in.readValue(ScaleType.class.getClassLoader());
        asset = (Asset) in.readValue(Asset.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mediaSize.getId());
        dest.writeInt(mediaSize.getWidthMils());
        dest.writeInt(mediaSize.getHeightMils());

        dest.writeValue(scaleType);
        dest.writeValue(asset);
    }
}
