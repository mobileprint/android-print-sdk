package com.hp.mss.hpprint.util;

import android.content.Context;
import android.graphics.Typeface;

import com.hp.mss.hpprint.R;

public class FontUtil {
    private static Typeface defaultFont;

    public static Typeface getDefaultFont(Context context) {
        if (defaultFont == null) {
            final String defaultFontFile = context.getResources().getString(R.string.lib_font);
            defaultFont = Typeface.createFromAsset(context.getAssets(), defaultFontFile);
        }
        return defaultFont;
    }
}
