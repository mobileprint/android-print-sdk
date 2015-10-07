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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class PDFAsset implements Asset, Parcelable {

    String uri;

    public PDFAsset(String uri) {
        this.uri = uri;
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
}
