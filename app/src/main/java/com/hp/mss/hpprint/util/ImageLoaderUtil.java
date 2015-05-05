package com.hp.mss.hpprint.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageLoaderUtil {
    public static Bitmap getImage(Context context, String imageSize) {
        Bitmap photo = null;
        ContextWrapper cw = new ContextWrapper(context);

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f = new File(directory, imageSize + ".jpg");
            photo = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return photo;
    }
}
