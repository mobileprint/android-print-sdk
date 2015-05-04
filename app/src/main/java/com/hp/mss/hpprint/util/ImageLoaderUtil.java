package com.hp.mss.hpprint.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageLoaderUtil {
    public static Bitmap getImage(Context context, String imageSize) {
        Bitmap photo = null;
        ContextWrapper cw = new ContextWrapper(context);

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f=new File(directory, imageSize + ".jpg");
            photo = BitmapFactory.decodeStream(new FileInputStream(f));
//            photo = writeSize(photo, imageSize);

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return photo;
    }

    private static Bitmap writeSize(Bitmap bitmap, String imageSize){

        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(12);

        canvas.drawBitmap(mutableBitmap,0,0,paint);
        canvas.drawText(imageSize, 10, 10, paint);
        return mutableBitmap;
    }
}
