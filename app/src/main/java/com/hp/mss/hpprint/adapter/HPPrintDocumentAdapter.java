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

package com.hp.mss.hpprint.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class is our customized PrintDocumentAdapter. It's intended to be used within the HP Print SDK.
 * You should not need to create this yourself.
 */
public class HPPrintDocumentAdapter extends PrintDocumentAdapter {
    private static final float PdfDocumentScale = .50f;
    private Context context;
    private int pageHeight;
    private int pageWidth;
    private PrintedPdfDocument myPdfDocument;
    private Bitmap thePhoto;
    private int totalPages;
    private boolean is4x5media;
    private PrintJobData printJobData;
    private PrintItem printItem;

    public HPPrintDocumentAdapter(Context context, PrintJobData printJobData, boolean is4x5media) {
        this.context = context;
        totalPages = 1;
        this.printJobData = printJobData;
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

        printItem = (printJobData.getPrintItem(newAttributes.getMediaSize()));

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
        Canvas canvas = page.getCanvas();

        printItem.drawPage(canvas, 1000 * PdfDocumentScale, new RectF(0, 0, canvas.getWidth(), canvas.getHeight()));
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
        if (thePhoto != null) {
            thePhoto.recycle();
            thePhoto = null;
        }
    }
}
