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

package com.hp.mss.hpprint.util;

import android.content.Context;
import android.graphics.Typeface;

import com.hp.mss.hpprint.R;

/**
 * This is used to load the HP Simplified font.
 */
public class FontUtil {
    private static Typeface defaultFont;

    public static Typeface getDefaultFont(Context context) {
        if (defaultFont == null) {
            final String defaultFontFile = context.getResources().getString(R.string.lib_font);

            if(defaultFontFile.equals("")){
                defaultFont = Typeface.DEFAULT;
            }else{
                defaultFont = Typeface.createFromAsset(context.getAssets(), defaultFontFile);
            }
        }
        return defaultFont;
    }
}
