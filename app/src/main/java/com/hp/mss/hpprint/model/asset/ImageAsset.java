package com.hp.mss.hpprint.model.asset;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.hp.mss.hpprint.util.ImageLoaderUtil;

public class ImageAsset implements Asset, Parcelable {

    public enum MeasurementUnits {
        INCHES,
        PIXELS
    }
    float width, height;
    MeasurementUnits measurementUnits;
    String uri;

    public ImageAsset(String uri, MeasurementUnits measurementUnits, float width, float height) {
        this.width = width;
        this.height = height;
        this.measurementUnits = measurementUnits;
        this.uri = uri;
    }

    @Override
    public Bitmap getPrintableBitmap(){
        return ImageLoaderUtil.getImageBitmap(uri);
    }

    @Override
    public String getAssetUri(){
        return uri;
    }

    public float widthInInches(){

        return width;
    }

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