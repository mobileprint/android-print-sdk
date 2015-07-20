package com.hp.mss.hpprint.model.asset;

import android.graphics.Bitmap;

/**
 * Interface for subclassing different types of assets (Image, PDF, HTML...)
 */
public interface Asset{
    /**
     * This method is used to get the bitmap from the asset.
     * @return The bitmap.
     */
    Bitmap getPrintableBitmap();

    /**
     * This returns the uri of the asset.
     * @return The URI location of the asset.
     */
    String getAssetUri();
}