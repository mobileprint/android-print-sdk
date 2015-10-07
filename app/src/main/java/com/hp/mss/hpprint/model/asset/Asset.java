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

    /**
     * Get the asset width.
     */
    int getAssetWidth();

    /**
     * Get the asset height.
     */
    int getAssetHeight();

    /**
     * Get content type string.
     */
    String getContentType();
}