package com.hp.mss.droid.lib.hpprint.adapter;


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

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Copyright 2015 Hewlett-Packard, Co.
 */

public class PhotoPrintDocumentAdapter extends PrintDocumentAdapter {

    Context context;
    private int pageHeight;
    private int pageWidth;
    private PrintedPdfDocument myPdfDocument;
    public Bitmap thePhoto;
    public int totalPages;

    public PhotoPrintDocumentAdapter(Context context,Bitmap bitmap) {
        this.context = context;
        totalPages = 1;
        thePhoto = bitmap;
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

        if ( cancellationSignal.isCanceled() ) {
            callback.onLayoutCancelled();
            return;
        }

        if ( totalPages > 0 ) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("Print elk.jpeg")
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

        PdfDocument.Page page = myPdfDocument.startPage(0);
        System.out.println("onWrite::page " + page);

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

    private boolean pageInRange(PageRange[] pageRanges, int page) {

        for (int i=0; i<pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page) {

        Canvas canvas = page.getCanvas();
        float scale = 72/1000;
        int w = (int)( pageWidth * scale);
        int h = (int)( pageHeight * scale);

        try {
            if (w > h)
                canvas.drawBitmap(thePhoto, null, new Rect(0, 0, h, h), null);
            else
                canvas.drawBitmap(thePhoto, null, new Rect(0, 0, w, w), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
