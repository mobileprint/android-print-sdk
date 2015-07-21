package com.hp.mss.hpprint.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintAttributes;

import com.hp.mss.hpprint.model.asset.Asset;

//Clients are able to override printitems, to have their own layouting methods.
public abstract class PrintItem implements Parcelable{

    PrintAttributes.MediaSize mediaSize;
    ScaleType scaleType;
    Asset asset;

    public static PrintItem.ScaleType DEFAULT_SCALE_TYPE = null;

    /**
     * This defines how the print item will be laid out on the paper.
     */
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

    /**
     * Get the media size of the print item.
     * @return The MediaSize attribute of the print item.
     * @see <a href="https://developer.android.com/reference/android/print/PrintAttributes.MediaSize.html">MediaSize</a>
     */
    public PrintAttributes.MediaSize getMediaSize(){
        return mediaSize;
    }

    /**
     * Get the scale type.
     * @return The print item's scale type.
     */
    public ScaleType getScaleType(){
        return scaleType;
    }

    /**
     * Get the asset of the print item.
     * @return The print item asset.
     */
    public Asset getAsset(){
        return asset;
    }

    /**
     * Get's the bitmap from the asset.
     * @return The bitmap
     */
    public Bitmap getPrintableBitmap(){
        return asset.getPrintableBitmap();
    }

    /**
     * Draw the printItem onto a canvas.
     * @param canvas The canvas to be drawn on
     * @param dpi The dpi of the canvas
     * @param pageBounds The bounds of the page on the canvas. (Canvas can be bigger than the page we
     *                   want to draw on.
     */
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
