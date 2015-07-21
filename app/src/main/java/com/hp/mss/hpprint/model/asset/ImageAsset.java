package com.hp.mss.hpprint.model.asset;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.hp.mss.hpprint.util.ImageLoaderUtil;

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