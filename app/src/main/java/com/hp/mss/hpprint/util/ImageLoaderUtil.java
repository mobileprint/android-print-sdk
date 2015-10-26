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
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is used in the HP Print SDK to load images from URI's.
 */
public class ImageLoaderUtil {
    private static final int MAXFILEAGE = (int) 3600000L;   // Max file age is 1 hour
    private static final String TAG = "ImageLoaderUtil";
    public static final String IMAGE_DIR = "imageDir";
    private static final String IMAGE_EXT = ".jpg";

    private static Context c;
    /**
     * This method gives you the bitmap of the image you tell it to load from the filesystem.
     * @param uri The file location.
     * @return The bitmap of the photo at the location you specify.
     */
    public static Bitmap getImageBitmap(String uri) {
        final File f = new File(uri);
        try {
            return getImage(f.getCanonicalPath());
        } catch (IOException e) {
            Log.e(TAG, "Error loading image for loading", e);
            return null;
        }
    }

    private static Bitmap getImage(String imagePath) {
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

    /**
     * Use this to save the image if the image exists in your assets directory.
     * @param context The activity context.
     * @param filePath Path to the asset in your assets directory.
     * @return The string uri to be used for creating {@link com.hp.mss.hpprint.model.asset.ImageAsset}.
     */
    public static String saveImageFromAssetDir(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return savePrintableImage(context, bitmap, filePath);
    }

    /**
     * Use this to save the image if the image is a drawable.
     * @param context The activity context.
     * @param resourceId Resource ID.
     * @param name Name of the file you want to save as.
     * @return The string uri to be used for creating {@link com.hp.mss.hpprint.model.asset.ImageAsset}.
     */
    public static String saveImageFromDrawable(Context context, int resourceId, String name){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        return savePrintableImage(context, bitmap, name);
    }

    /**
     * Use this to save the image if you already have a bitmap generated to print.
     * @param context The activity context.
     * @param bitmap The bitmap.
     * @param fileName Name of the file you want to save as.
     * @return The string uri to be used for creating {@link com.hp.mss.hpprint.model.asset.ImageAsset}.
     */
    public static String savePrintableImage(Context context, Bitmap bitmap, String fileName) {
        c = context;
        String imageURI = null;

        FileOutputStream out;
        try {
            File imageFile = ImageLoaderUtil.createImageFile(context.getApplicationContext(), fileName);
            if (imageFile != null) {
                imageURI = imageFile.getAbsolutePath();
                out = new FileOutputStream(imageURI);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.i("Filename", imageURI + "");

        return imageURI;


    }

    protected static File createImageFile(Context context, String fileName) throws IOException {

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);

        // Create imageDir
        File path = new File(directory, fileName + IMAGE_EXT);
        path.deleteOnExit();

        return path;
    }

    protected static void cleanUpFileDirectory(){
        if (c == null) // no application context exists
            return;

        ContextWrapper cw = new ContextWrapper(c.getApplicationContext());
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);

        File file[] = directory.listFiles();
        for (int i=0; i < file.length; i++)
        {
            if(file[i].lastModified() + MAXFILEAGE < System.currentTimeMillis()) {
                file[i].delete();
            }
        }
    }
//    public static Bitmap getBitmapWithSize(String imagePath, int reqWidth, int reqHeight) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(imagePath, options);
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(imagePath, options);
//    }
//
//    private static Rect getImageSize(String imagePath) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//
//        BitmapFactory.decodeFile(imagePath, options);
//
//        final int imageWidth = options.outWidth;
//        final int imageHeight = options.outHeight;
//
//        return new Rect(0, 0, imageWidth, imageHeight);
//    }
//
//    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }
}
