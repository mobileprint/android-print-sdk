package com.hp.mss.hpprint.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Parcel;
import android.os.Parcelable;
import android.print.PrintAttributes;

import com.hp.mss.hpprint.util.ImageLoaderUtil;

/**
 * Created by panini on 6/30/15.
 */
public class ImagePrintItem extends PrintItem {
    private static final float PdfDocumentScale = .50f;
    public static final int PAPER_DIMENS_4000 = 4000;
    public static final int PAPER_DIMENS_6000 = 6000;
    public static final int PAPER_DIMENS_7000 = 7000;
    public static final int PAPER_DIMENS_5000 = 5000;

    public static PrintItem.ScaleType DEFAULT_SCALE_TYPE = PrintItem.ScaleType.CENTER;

    public ImagePrintItem(PrintAttributes.MediaSize mediaSize, ScaleType scaleType, String uri) {
        super(mediaSize, scaleType, uri);
    }

    public ImagePrintItem(PrintAttributes.MediaSize mediaSize, String uri) {
        super(mediaSize, DEFAULT_SCALE_TYPE, uri);
    }


    public ImagePrintItem(String uri) {
        super(uri);
    }

    @Override
    public Bitmap getPrintableBitmap(){
        return ImageLoaderUtil.getImageBitmap(uri);
    }

    @Override
    public PdfDocument getPrintablePDF(){
        return null;
    }

    public void drawPage(PdfDocument.Page page) {
        Bitmap bitmap = getPrintableBitmap();
        Canvas canvas = page.getCanvas();
        switch (scaleType) {
            default:
            case CENTER:
                drawCenterCrop(canvas,bitmap);
                break;
            case CENTER_CROP:
                drawCenterCrop(canvas,bitmap);
                break;
            case CENTER_INSIDE:
                //TODO: Not required for current use case
                break;
            case FIT_XY:
                canvas.drawBitmap(bitmap, null, canvas.getClipBounds(), null);
                break;
        }
    }

    private void drawCenterCrop(Canvas canvas, Bitmap bitmap) {

        float photoWidth = bitmap.getWidth();
        float photoHeight = bitmap.getHeight();
        int pageHeight = mediaSize.getHeightMils();
        int pageWidth = mediaSize.getHeightMils();

        float scale = PAPER_DIMENS_4000 / photoWidth * PdfDocumentScale;

        if ((pageHeight == PAPER_DIMENS_6000 && pageWidth == PAPER_DIMENS_4000) || (pageHeight == PAPER_DIMENS_7000 && pageWidth == PAPER_DIMENS_5000)) {
            scale = canvas.getWidth() / photoWidth;
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final float left = canvas.getWidth() / 2 - photoWidth / 2;
        final float right = left + photoWidth;
        final float top;

        if (pageWidth == PAPER_DIMENS_4000) {
            top = 0;
        } else {
            top = canvas.getHeight() / 2 - photoHeight / 2;
        }

        final float bottom = top + photoHeight;

        canvas.drawBitmap(bitmap, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }

// incase we want to use assets again
//    private static class ImageAsset implements Asset{
//        String filename;
//        ImageAsset(String name) {
//            filename = name;
//        }
//
//        @Override
//        public Bitmap getImageBitmap(Context context){
//            return ImageLoaderUtil.getImageBitmap(context, filename);
//        }
//
//    }

    //Parcelable methods
    protected ImagePrintItem(Parcel in) {
        mediaSize = new PrintAttributes.MediaSize(in.readString(), "android", in.readInt(), in.readInt());
        scaleType = (ScaleType) in.readValue(ScaleType.class.getClassLoader());
        uri = in.readString();
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
        dest.writeString(uri);
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
