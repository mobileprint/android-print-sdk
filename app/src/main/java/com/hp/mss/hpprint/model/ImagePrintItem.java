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
import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintAttributes;
import android.util.Log;

import com.hp.mss.hpprint.model.asset.Asset;
import com.hp.mss.hpprint.model.asset.ImageAsset;

import java.io.File;

/**
 * Once you create an image asset, you need to associate it with a PrintItem object.
 * You will need to provide a ScaleType and the ImageAsset itself.
 */
public class ImagePrintItem extends PrintItem {

    public static PrintItem.ScaleType DEFAULT_SCALE_TYPE = PrintItem.ScaleType.FIT;

    /**
     * Create an ImagePrintItem with defined media size, scale type, and asset.
     * @param mediaSize The media size you want the asset to be used for.
     * @param margins The margins you want to give the printable asset.
     * @param scaleType The scale type used to layout the asset on the media.
     * @param asset The asset itself.
     */
    public ImagePrintItem(PrintAttributes.MediaSize mediaSize, PrintAttributes.Margins margins, ScaleType scaleType, ImageAsset asset) {
        super(mediaSize, margins, scaleType, asset);
    }

    /**
     * Create an ImagePrintItem without a defined margin. Margin will be set to zero.
     * @param mediaSize The media size you want the asset to be used for.
     * @param scaleType The scale type used to layout the asset on the media.
     * @param asset The asset itself.
     */
    public ImagePrintItem(PrintAttributes.MediaSize mediaSize, ScaleType scaleType, ImageAsset asset) {
        this(mediaSize, new PrintAttributes.Margins(0, 0, 0, 0), scaleType, asset);
    }

    /**
     * Create an ImagePrintItem without a defined scale type. Fit to page will be used.
     * @param mediaSize The media size you want the asset to be used for.
     * @param margins The margins you want to give the printable asset.
     * @param asset The asset itself.
     */
    public ImagePrintItem(PrintAttributes.MediaSize mediaSize, PrintAttributes.Margins margins, ImageAsset asset) {
        this(mediaSize, margins, DEFAULT_SCALE_TYPE, asset);
    }

    /**
     * Create an ImagePrintItem without defining a media size. This is useful when defining the default
     * print item.
     * @param margins The margins you want to give the printable asset.
     * @param scaleType The scale type used to layout the asset on the media.
     * @param asset The asset itself.
     */
    public ImagePrintItem(PrintAttributes.Margins margins, ScaleType scaleType, ImageAsset asset) {
        this(null,margins, scaleType, asset);
    }

    /**
     * Create an ImagePrintItem without defining margin and media size. Margin will be set to zero. This is useful when defining the default
     * print item.
     * @param scaleType The scale type used to layout the asset on the media.
     * @param asset The asset itself.
     */
    public ImagePrintItem(ScaleType scaleType, ImageAsset asset) {
        this(null, new PrintAttributes.Margins(0, 0, 0, 0), scaleType, asset);
    }

    /**
     * Create an ImagePrintItem without a defined media size and scale type. Fit to page will be used.
     * @param margins The margins you want to give the printable asset.
     * @param asset The asset itself.
     */
    public ImagePrintItem(PrintAttributes.Margins margins, ImageAsset asset) {
        this(null, margins, DEFAULT_SCALE_TYPE, asset);
    }

    /**
     * Create an ImagePrintItem without a defined margin and scale type. Margin will be set to zero. Fit to page will be used.
     * @param mediaSize The media size you want the asset to be used for.
     * @param asset The asset itself.
     */
    public ImagePrintItem(PrintAttributes.MediaSize mediaSize, ImageAsset asset) {
        this(mediaSize, new PrintAttributes.Margins(0, 0, 0, 0), DEFAULT_SCALE_TYPE, asset);
    }

    /**
     * Create an ImagePrintItem with only the asset. This will use the default scale type, a null
     * media size, and no margins.
     * @param asset The asset itself.
     */
    public ImagePrintItem(ImageAsset asset) {
        this(null, new PrintAttributes.Margins(0, 0, 0, 0), DEFAULT_SCALE_TYPE, asset);
    }

    public void drawPage(Canvas canvas, float ppi, RectF pageBounds) {
        Bitmap bitmap = getPrintableBitmap();
        switch (scaleType) {
            default:
            case CENTER:
                drawCenter(canvas, bitmap, ppi, pageBounds);
                break;
            case CENTER_TOP:
                drawCenterTop(canvas, bitmap, ppi, pageBounds);
                break;
            case CROP:
                drawCrop(canvas, bitmap, pageBounds);
                break;
            case FIT:
                drawFit(canvas, bitmap, ppi, pageBounds);
                break;
            case CENTER_TOP_LEFT:
                drawCenterTopLeft(canvas, bitmap, ppi, pageBounds);
        }
    }

    private void drawCenter(Canvas canvas, Bitmap bitmap, float ppi, RectF pageBounds) {
        float scale = 1;
        float marginLeft =  (float)margins.getLeftMils()/1000*ppi;
        float marginRight =  (float)margins.getRightMils()/1000*ppi;
        float marginTop =  (float)margins.getTopMils()/1000*ppi;
        float marginBottom =  (float)margins.getBottomMils()/1000*ppi;

        float pageWidth = pageBounds.width() - (marginLeft + marginRight);
        float pageHeight = pageBounds.height() - (marginTop + marginBottom);

        float photoWidth = ((ImageAsset)asset).widthInInches() * ppi;
        float photoHeight = ((ImageAsset)asset).heightInInches() * ppi;

        if(pageWidth/photoWidth < 1 || pageHeight/photoHeight < 1){
            if (pageWidth/photoWidth < pageHeight/photoHeight) {
                scale = pageWidth/photoWidth;
            } else {
                scale = pageHeight/photoHeight;
            }
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final float left = pageBounds.left + (pageWidth - photoWidth)/2 + marginLeft;
        final float right = left + photoWidth;
        final float top = pageBounds.top + (pageHeight - photoHeight)/2 + marginTop;
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }

    private void drawCenterTop (Canvas canvas, Bitmap bitmap, float ppi, RectF pageBounds) {
        float scale = 1;
        float marginLeft =  (float)margins.getLeftMils()/1000*ppi;
        float marginRight =  (float)margins.getRightMils()/1000*ppi;
        float marginTop =  (float)margins.getTopMils()/1000*ppi;
        float marginBottom =  (float)margins.getBottomMils()/1000*ppi;

        float pageWidth = pageBounds.width() - (marginLeft + marginRight);
        float pageHeight = pageBounds.height() - (marginTop + marginBottom);

        float photoWidth = ((ImageAsset)asset).widthInInches() * ppi;
        float photoHeight = ((ImageAsset)asset).heightInInches() * ppi;

        if(pageWidth/photoWidth < 1 || pageHeight/photoHeight < 1){
            if (pageWidth/photoWidth < pageHeight/photoHeight) {
                scale = pageWidth/photoWidth;
            } else {
                scale = pageHeight/photoHeight;
            }
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final float left = pageBounds.left + (pageWidth - photoWidth)/2 + marginLeft;
        final float right = left + photoWidth;
        final float top = pageBounds.top + marginTop;
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }

    private void drawCenterTopLeft(Canvas canvas, Bitmap bitmap, float ppi, RectF pageBounds) {
        float scale = 1;
        float marginLeft =  (float)margins.getLeftMils()/1000*ppi;
        float marginRight =  (float)margins.getRightMils()/1000*ppi;
        float marginTop =  (float)margins.getTopMils()/1000*ppi;
        float marginBottom =  (float)margins.getBottomMils()/1000*ppi;

        float pageWidth = pageBounds.width() - (marginLeft + marginRight);
        float pageHeight = pageBounds.height() - (marginTop + marginBottom);

        float photoWidth = ((ImageAsset)asset).widthInInches() * ppi;
        float photoHeight = ((ImageAsset)asset).heightInInches() * ppi;

        if(pageWidth/photoWidth < 1 || pageHeight/photoHeight < 1){
            if (pageWidth/photoWidth < pageHeight/photoHeight) {
                scale = pageWidth/photoWidth;
            } else {
                scale = pageHeight/photoHeight;
            }
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final float left = pageBounds.left + marginLeft;
        final float right = left + photoWidth;
        final float top = pageBounds.top + marginTop;
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }

    private void drawCrop(Canvas canvas, Bitmap bitmap, RectF pageBounds) {
        //Currently ignores margin
        //TODO: need to accept Gravity types for cropping (Center, East, West, etc..) - currently assumes center
        //TODO: Make this work for margins - Requires scaling then crop - this might cause memory issues creating 3 different bitmaps
        float scale;

        float pageWidth = pageBounds.width();
        float pageHeight = pageBounds.height();

        float photoWidth = bitmap.getWidth();
        float photoHeight = bitmap.getHeight();

        if(pageWidth/photoWidth > pageHeight/photoHeight){
            scale = pageWidth/photoWidth;
        } else {
            scale = pageHeight/photoHeight;
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final float left = pageBounds.left + (pageWidth - photoWidth)/2 ;
        final float right = left + photoWidth;
        final float top = pageBounds.top + (pageHeight - photoHeight)/2;
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }

    private void drawFit(Canvas canvas, Bitmap bitmap, float ppi,  RectF pageBounds) {
        //TODO: need to accept Gravity types for cropping (Center, East, West, etc..) - currently assumes North Gravity
        float scale;
        float marginLeft =  (float)margins.getLeftMils()/1000*ppi;
        float marginRight =  (float)margins.getRightMils()/1000*ppi;
        float marginTop =  (float)margins.getTopMils()/1000*ppi;
        float marginBottom =  (float)margins.getBottomMils()/1000*ppi;

        float pageWidth = pageBounds.width() - (marginLeft + marginRight);
        float pageHeight = pageBounds.height() - (marginTop + marginBottom);

        float photoWidth = bitmap.getWidth();
        float photoHeight = bitmap.getHeight();

        if(pageWidth/photoWidth > pageHeight/photoHeight){
            scale = pageHeight/photoHeight;
        } else {
            scale = pageWidth/photoWidth;
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final float left = pageBounds.left + (pageWidth - photoWidth)/2 + marginLeft;
        final float right = left + photoWidth;
        final float top = pageBounds.top + marginTop;
        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }


    @Override
    protected void cleanup(Context context){
        try {
            final File f = new File(asset.getAssetUri());
            f.delete();
        }catch(Exception e){
            Log.e("ImagePrintItem", "File already deleted");
        }
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
