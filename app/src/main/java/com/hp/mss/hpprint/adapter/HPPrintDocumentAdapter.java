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

import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PDFPrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is our customized PrintDocumentAdapter. It's intended to be used within the HP Print SDK.
 * You should not need to create this yourself.
 */
public class HPPrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
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



        if(printItem instanceof ImagePrintItem){
            
            PdfDocument.Page page = myPdfDocument.startPage(0);

            //check for cancellation
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                myPdfDocument.finishPage(page);
                myPdfDocument.close();
                myPdfDocument = null;
                return;
            }
            Canvas canvas = page.getCanvas();

            // units are in points (1/72 of am inch)
            printItem.drawPage(canvas, 72, new RectF(0, 0, canvas.getWidth(), canvas.getHeight()));

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

        } else if (printItem instanceof PDFPrintItem){

            //do other stuff
            InputStream input = null;
            OutputStream output = null;
            try {
                input = context.getAssets().open(printItem.getAsset().getAssetUri());
                output = new FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            }catch (FileNotFoundException ee) {
                //Catch exception
            } catch (Exception e) {
                //Catch exception
            } finally {
                try {
                    input.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
