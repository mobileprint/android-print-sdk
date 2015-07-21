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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintAttributes;

import com.hp.mss.hpprint.adapter.HPPrintDocumentAdapter;
import com.hp.mss.hpprint.model.asset.Asset;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.util.ImageLoaderUtil;

/**
 * Once you create an image asset, you need to associate it with a PrintItem object.
 * You will need to provide a ScaleType and the ImageAsset itself.
 */
public class ImagePrintItem extends PrintItem {

    public static PrintItem.ScaleType DEFAULT_SCALE_TYPE = PrintItem.ScaleType.FIT;

    /**
     * Create an ImagePrintItem with defined media size, scale type, and asset.
     * @param mediaSize The media size you want the asset to be used for.
     * @param scaleType The scale type used to layout the asset on the media.
     * @param asset The asset itself.
     */
    public ImagePrintItem(PrintAttributes.MediaSize mediaSize, ScaleType scaleType, ImageAsset asset) {
        super(mediaSize, scaleType, asset);
    }

    /**
     * Create an ImagePrintItem without a defined scale type. Fit to page will be used.
     * @param mediaSize The media size you want the asset to be used for.
     * @param asset The asset itself.
     */
    public ImagePrintItem(PrintAttributes.MediaSize mediaSize, ImageAsset asset) {
        this(mediaSize, DEFAULT_SCALE_TYPE, asset);
    }

    /**
     * Create an ImagePrintItem without defining a media size. This is useful when defining the default
     * print item.
     * @param scaleType The scale type used to layout the asset on the media.
     * @param asset The asset itself.
     */
    public ImagePrintItem(ScaleType scaleType, ImageAsset asset) {
        this(null, scaleType, asset);
    }

    /**
     * Create an ImagePrintItem with only the asset. This will use the default scale type and a null
     * media size.
     * @param asset The asset itself.
     */
    public ImagePrintItem(ImageAsset asset) {
        this(null, DEFAULT_SCALE_TYPE, asset);
    }

    public void drawPage(Canvas canvas, float dpi, RectF pageBounds) {
        Bitmap bitmap = getPrintableBitmap();
        switch (scaleType) {
            default:
            case CENTER:
                drawCenter(canvas, bitmap, dpi, pageBounds);
                break;
            case CROP:
                drawCrop(canvas, bitmap, pageBounds);
                break;
            case FIT:
                drawFit(canvas, bitmap, pageBounds);
                break;
            case CENTER_TOP_LEFT:
                drawCenterTopLeft(canvas, bitmap, dpi, pageBounds);
        }
    }
    private void drawCenter(Canvas canvas, Bitmap bitmap, float dpi, RectF pageBounds) {

        float photoWidth = bitmap.getWidth();
        float photoHeight = bitmap.getHeight();

        float assetWidthInInches = ((ImageAsset)asset).widthInInches();
        float assetHeightInInches = ((ImageAsset)asset).heightInInches();

        float widthScale = dpi / (photoWidth/assetWidthInInches);
        float heightScale = dpi /(photoHeight/assetHeightInInches);

        photoWidth *= widthScale;
        photoHeight *= heightScale;

        final float left = pageBounds.left + (pageBounds.width() / 2) - (photoWidth / 2);
        final float right = left + photoWidth;
        final float top = pageBounds.top + (pageBounds.height() / 2) - (photoHeight / 2);
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }
    private void drawCenterTopLeft(Canvas canvas, Bitmap bitmap, float dpi, RectF pageBounds) {

        float photoWidth = bitmap.getWidth();
        float photoHeight = bitmap.getHeight();

        float assetWidthInInches = ((ImageAsset)asset).widthInInches();
        float assetHeightInInches = ((ImageAsset)asset).heightInInches();

        float widthScale = dpi / (photoWidth/assetWidthInInches);
        float heightScale = dpi /(photoHeight/assetHeightInInches);

        photoWidth *= widthScale;
        photoHeight *= heightScale;

        final float left = pageBounds.left;
        final float right = left + photoWidth;
        final float top = pageBounds.top;
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }

    private void drawCrop(Canvas canvas, Bitmap bitmap, RectF pageBounds) {

        float photoWidth = bitmap.getWidth();
        float photoHeight = bitmap.getHeight();
        float scale;

        if(pageBounds.width()/photoWidth > pageBounds.height()/photoHeight){
            scale = pageBounds.width()/photoWidth;
        } else {
            scale = pageBounds.height()/photoHeight;
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final float left = pageBounds.left + pageBounds.width() / 2 - photoWidth / 2;
        final float right = left + photoWidth;
        final float top = pageBounds.top + (pageBounds.height() / 2) - photoHeight / 2;
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }

    private void drawFit(Canvas canvas, Bitmap bitmap, RectF pageBounds) {
        float photoWidth = bitmap.getWidth();
        float photoHeight = bitmap.getHeight();
        float scale;

        if(pageBounds.width()/photoWidth > pageBounds.height()/photoHeight){
            scale = pageBounds.height()/photoHeight;
        } else {
            scale = pageBounds.width()/photoWidth;
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final float left = pageBounds.left + pageBounds.width() / 2 - photoWidth / 2;
        final float right = left + photoWidth;
        final float top = pageBounds.top + pageBounds.height() / 2 - photoHeight / 2;
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }

    //Parcelable methods
    protected ImagePrintItem(Parcel in) {
        mediaSize = new PrintAttributes.MediaSize(in.readString(), "android", in.readInt(), in.readInt());
        scaleType = (ScaleType) in.readValue(ScaleType.class.getClassLoader());
        asset = (Asset) in.readValue(Asset.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mediaSize.getId());
        dest.writeInt(mediaSize.getWidthMils());
        dest.writeInt(mediaSize.getHeightMils());
        dest.writeValue(scaleType);
        dest.writeValue(asset);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImagePrintItem> CREATOR = new Parcelable.Creator<ImagePrintItem>() {
        @Override
        public ImagePrintItem createFromParcel(Parcel in) {
            return new ImagePrintItem(in);
        }

        @Override
        public ImagePrintItem[] newArray(int size) {
            return new ImagePrintItem[size];
        }
    };
}
