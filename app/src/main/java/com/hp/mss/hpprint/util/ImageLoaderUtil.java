package com.hp.mss.hpprint.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class ImageLoaderUtil {
    private static final String TAG = "ImageLoaderUtil";
    private static final String IMAGE_DIR = "imageDir";
    private static final String IMAGE_EXT = ".jpg";

    public static Bitmap getImageWithSize(Context context, String imageSize) {
        final Context applicationContext = context.getApplicationContext();
        final File imageDir = applicationContext.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        final File f = new File(imageDir, imageSize + IMAGE_EXT);

        try {
            return getImage(f.getCanonicalPath());
        } catch (IOException e) {
            Log.e(TAG, "Error loading image for loading", e);
            return null;
        }
    }

    public static Bitmap getImage(String imagePath) {
        final int MAX_TRIALS = 3;
        int trial = 0;
        Bitmap bitmap = null;
        do {
            try {
                bitmap =  BitmapFactory.decodeFile(imagePath);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    throw new RuntimeException("OoM");
                }
            }
        } while (trial++ != MAX_TRIALS);
        if (trial == MAX_TRIALS || bitmap == null) {
            throw new RuntimeException("OoM");
        }
        return bitmap;
    }

    public static Bitmap getBitmapWithSize(String imagePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static Rect getImageSize(String imagePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);

        final int imageWidth = options.outWidth;
        final int imageHeight = options.outHeight;

        return new Rect(0, 0, imageWidth, imageHeight);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
