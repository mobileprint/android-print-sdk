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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.hp.mss.hpprint.util.ImageLoaderUtil;

import java.util.UUID;

/**
 *
 * One of the first objects you need to create in order to use our print workflow is the ImageAsset
 * object. You will need at least one but you can create as many as needed.
 * <p>
 * The ImageAsset represents an image. It contains the uri, measurements, and the unit type.
 * <p>
 * When created, give it to a {@link com.hp.mss.hpprint.model.PrintItem}
 */
public class ImageAsset implements Asset, Parcelable {

    /**
     * This enum is used to tell the PrintJob how to read the size of the imageAsset.
     * Currently, we only support measurements in inches.
     */
    public enum MeasurementUnits {
        INCHES
//        PIXELS
    }
    float width, height;
    MeasurementUnits measurementUnits;
    String uri;

    /**
     * Constructor used to create the image asset. Provide the width and height in inches.
     * TODO: Allow pixel measurements of the height and width.
     * @param uri The location of the asset.
     * @param measurementUnits The measurement unit of the width and height.
     * @param width The width of the asset.
     * @param height The height of the asset.
     */
    public ImageAsset(String uri, MeasurementUnits measurementUnits, float width, float height) {
        this.width = width;
        this.height = height;
        this.measurementUnits = measurementUnits;
        this.uri = uri;
    }

    /**
     * Constructor used to create an ImageAsset with a resource.
     * @param context The context.
     * @param resourceId The resource id.
     * @param measurementUnits The measurement unit of the width and height.
     * @param width The width of the asset.
     * @param height The height of the asset.
     */
    public ImageAsset(Context context, int resourceId, MeasurementUnits measurementUnits, float width, float height) {
        this(ImageLoaderUtil.saveImageFromDrawable(context, resourceId, Integer.toString(resourceId)),
                measurementUnits,
                width,
                height);
    }

    /**
     * Constructor used to create an ImageAsset with an asset located in the "assets" directory.
     * @param context The context.
     * @param assetURI The location of the asset within the assets directory.
     * @param measurementUnits The measurement unit of the width and height.
     * @param width The width of the asset.
     * @param height The height of the asset.
     */
    public ImageAsset(Context context, String assetURI, MeasurementUnits measurementUnits, float width, float height) {
        this(ImageLoaderUtil.saveImageFromAssetDir(context, assetURI),
                measurementUnits,
                width,
                height);
    }

    /**
     * Constructor used to create an ImageAsset with a bitmap.
     * @param context The context.
     * @param bitmap The bitmap you want to use to create the ImageAsset.
     * @param measurementUnits The measurement unit of the width and height.
     * @param width The width of the asset.
     * @param height The height of the asset.
     */
    public ImageAsset(Context context, Bitmap bitmap, MeasurementUnits measurementUnits, float width, float height){
        this(ImageLoaderUtil.savePrintableImage(context, bitmap, UUID.randomUUID().toString()),
                measurementUnits,
                width,
                height);
    }

    /**
     * Gets the bitmap of the image asset.
     * @return The bitmap.
     */
    @Override
    public Bitmap getPrintableBitmap(){
        return ImageLoaderUtil.getImageBitmap(uri);
    }

    /**
     * Gets the uri of the image asset.
     * @return String uri.
     */
    @Override
    public String getAssetUri(){
        return uri;
    }

    /**
     * Get the width of the image in inches.
     * @return The width
     */
    public float widthInInches(){
        return width;
    }

    /**
     * Get the height of the image in inches.
     * @return The height
     */
    public float heightInInches(){

        return height;
    }

    @Override
    public int getAssetWidth(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(getAssetUri(), options);
        return options.outWidth;
    }

    @Override
    public int getAssetHeight(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(getAssetUri(), options);
        return options.outWidth;
    }

    @Override
    public String getContentType(){
        return "image";
    }

    //Parcelable
    protected ImageAsset(Parcel in) {
        width = in.readFloat();
        height = in.readFloat();
        measurementUnits = (MeasurementUnits) in.readValue(MeasurementUnits.class.getClassLoader());
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(width);
        dest.writeFloat(height);
        dest.writeValue(measurementUnits);
        dest.writeString(uri);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageAsset> CREATOR = new Parcelable.Creator<ImageAsset>() {
        @Override
        public ImageAsset createFromParcel(Parcel in) {
            return new ImageAsset(in);
        }

        @Override
        public ImageAsset[] newArray(int size) {
            return new ImageAsset[size];
        }
    };
}