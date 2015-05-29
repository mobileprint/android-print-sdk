//
// Hewlett-Packard Company
// All rights reserved.
//
// This file, its contents, concepts, methods, behavior, and operation
// (collectively the "Software") are protected by trade secret, patent,
// and copyright laws. The use of the Software is governed by a license
// agreement. Disclosure of the Software to third parties, in any form,
// in whole or in part, is expressly prohibited except as authorized by
// the license agreement.
//

package com.hp.mss.hpprint.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.widget.ImageView;

import com.hp.mss.hpprint.util.ImageLoaderUtil;
import com.hp.mss.hpprint.util.PrintUtil;

import java.io.FileOutputStream;
import java.io.IOException;


public class MultiplePhotoPrintDocumentAdapter extends PrintDocumentAdapter {
    private static final float PdfDocumentScale = .5f;
    private static final String DEFAULT_IMAGE_SIZE = PrintUtil.IMAGE_SIZE_4x5;
    public static final int PAPER_DIMENS_4000 = 4000;
    public static final int PAPER_DIMENS_6000 = 6000;
    public static final int PAPER_DIMENS_7000 = 7000;
    public static final int PAPER_DIMENS_5000 = 5000;
    private Context context;
    private int pageHeight;
    private int pageWidth;
    private PrintedPdfDocument myPdfDocument;
    private Bitmap thePhoto;
    private int totalPages;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;
    private boolean is4x5media;

    public MultiplePhotoPrintDocumentAdapter(Context context, ImageView.ScaleType scaleType, boolean is4x5media) {
        this.context = context;
        totalPages = 1;
        this.scaleType = scaleType;
        this.is4x5media = is4x5media;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle metadata) {

        newAttributes = new PrintAttributes.Builder()
                .setResolution(newAttributes.getResolution())
                .setMediaSize(newAttributes.getMediaSize())
                .setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0))
                .build();

        myPdfDocument = new PrintedPdfDocument(context, newAttributes);

        pageHeight = newAttributes.getMediaSize().getHeightMils();
        pageWidth = newAttributes.getMediaSize().getWidthMils();

        if (thePhoto != null) {
            thePhoto.recycle();
        }
        if (pageHeight == PAPER_DIMENS_7000 && pageWidth == PAPER_DIMENS_5000) {
            thePhoto = ImageLoaderUtil.getImageWithSize(context, PrintUtil.IMAGE_SIZE_5x7);
        } else {
            thePhoto = ImageLoaderUtil.getImageWithSize(context, PrintUtil.IMAGE_SIZE_4x5);
        }

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalPages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_card")
                    .setContentType((PrintDocumentInfo.CONTENT_TYPE_PHOTO))
                    .setPageCount(totalPages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);

        } else {
            callback.onLayoutFailed("Page count is zero");
        }
    }

    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {

        final float scaledWidth = pageWidth * PdfDocumentScale;
        final float scaledHeight = pageHeight * PdfDocumentScale;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int) scaledWidth, (int) scaledHeight, 0).create();

        PdfDocument.Page page = myPdfDocument.startPage(pageInfo);

        //check for cancellation
        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            myPdfDocument.close();
            myPdfDocument = null;
            return;
        }

        drawPage(page);
        myPdfDocument.finishPage(page);

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));

        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pageRanges);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        thePhoto.recycle();
        thePhoto = null;
    }

    //This method needs corresponding one for pagepreviewview to make the result print same as the preview.
    private void drawPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();
        switch (scaleType) {
            default:
            case CENTER:
                //TODO: Not required for current use case
                break;
            case CENTER_CROP:
                drawCenterCrop(canvas);
                break;
            case CENTER_INSIDE:
                //TODO: Not required for current use case
                break;
            case FIT_XY:
                canvas.drawBitmap(thePhoto, null, canvas.getClipBounds(), null);
                break;
        }
    }

    private void drawCenterCrop(Canvas canvas) {

        float photoWidth = thePhoto.getWidth();
        float photoHeight = thePhoto.getHeight();

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

        canvas.drawBitmap(thePhoto, null, new Rect((int) left, (int) top, (int) right, (int) bottom), null);
    }


}
