package com.hp.mss.hpprint.model.asset;

import android.graphics.Bitmap;

public interface Asset{
    Bitmap getPrintableBitmap();
    String getAssetUri();
}