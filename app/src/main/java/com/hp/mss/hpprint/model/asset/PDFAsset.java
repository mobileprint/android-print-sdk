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

package com.hp.mss.hpprint.model.asset;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PDFAsset implements Asset, Parcelable {

    String uri;
    Boolean fromAsset;

    public PDFAsset(String uri) {
        this.uri = uri;
        this.fromAsset = false;
    }

    public PDFAsset(String uri, Boolean fromAsset) {
        this.uri = uri;
        this.fromAsset = fromAsset;
    }

    @Override
    public Bitmap getPrintableBitmap() {
        return null;
    }

    @Override
    public String getAssetUri() {
        return uri;
    }

    @Override
    public int getAssetWidth() {
        return 0;
    }

    @Override
    public int getAssetHeight() {
        return 0;
    }

    @Override
    public String getContentType() {
        return "pdf";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public InputStream getInputStream(Context context) {
        InputStream input = null;
        try {
            if (fromAsset) {
                // get InputStream from AssetManager
                if (context != null) {
                    input = context.getAssets().open(this.uri);

                    if (input == null) {
                        Log.e("PDFAsset", "Unable to open asset: " + this.uri);
                    }
                } else {
                    Log.e("PDFAsset", "Error opening file. Context was null.");
                }
            } else {
                File file = new File(this.uri);
                input = new FileInputStream(file);
                if (input == null) {
                    Log.e("PDFAsset", "Unable to open file: " + this.uri);
                }
            }
        } catch (IOException e) {
            Log.e("PDFAsset", "Error opening file: " + this.uri);
            e.printStackTrace();
        }

        return input;
    }
}
