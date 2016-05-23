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

package com.hp.mss.hpprint.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.hp.mss.hpprint.BuildConfig;
import com.hp.mss.hpprint.util.PrintUtil;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This is the class that encapsulates all the basic application data HP is collecting.
 * It is used inside HP print SDK, you should not create this yourself.
 */
public class ApplicationMetricsData {
    private static final String DO_NOT_ENCRYPT_DEVICE_ID = "Device id is not encrypted";
    private static final String APP_SPECIFIC_DEVICE_ID = "Device id is encrypted & app specific";
    private static final String VENDOR_SPECIFIC_DEVICE_ID = "Device id is encrypted & vendor specific";

    private static final String TAG = "ApplicationMetricsData";

    protected static final String OS_TYPE = "Android";
    protected static final String PRODUCT_NAME = "HP Snapshots";
    protected static final String ACTION_PRINT = "Android Print";
    protected static final String NO = "NO";
    protected static final String APP_TYPE = "Partner";

    protected static final String DEVICE_ID_LABEL = "device_id";
    protected static final String DEVICE_TYPE_LABEL = "device_type";
    protected static final String OFF_RAMP_LABEL = "off_ramp";
    protected static final String OS_TYPE_LABEL = "os_type";
    protected static final String OS_VERSION_LABEL = "os_version";
    protected static final String PRODUCT_NAME_LABEL = "product_name";
    protected static final String VERSION_LABEL = "version";
    protected static final String WIFI_SSID_LABEL = "wifi_ssid";
    protected static final String UNKNOWN_SSID = "<unknown ssid>";
    protected static final String NO_WIFI = "NO-WIFI";
    protected static final String APP_TYPE_LABEL = "app_type";
    protected static final String PRODUCT_ID_LABEL = "product_id";
    protected static final String PRINT_LIBRARY_VERSION_LABEL = "print_library_version";
    protected static final String LANGUAGE_CODE_LABEL = "language_code";
    protected static final String COUNTRY_CODE_LABEL = "country_code";
    protected static final String TIMEZONE_DESCRIPTION = "timezone_description";
    protected static final String TIMEZONE_OFFSET_SECONDS = "timezone_offset_seconds";


    protected String deviceId;
    protected String deviceType;
    protected String offRamp;
    protected String osType;
    protected String osVersion;
    protected String productName;
    protected String version;
    protected String wifiSsid;
    protected String appType;
    protected String productId;
    protected String printLibraryVersion;
    protected String languageCode;
    protected String countryCode;
    protected String timezoneDescription;
    protected String timezoneOffsetSeconds;


    public ApplicationMetricsData(final Context context) {
        boolean isDebuggable =  ( 0 != ( context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );

        this.productId = context.getPackageName();
        this.productName = getAppLable(context);
        this.printLibraryVersion = BuildConfig.VERSION_NAME;

        if (PrintUtil.doNotEncryptDeviceId) {
            this.deviceId = getDeviceId(context);
            if(isDebuggable)
                Log.d(TAG, DO_NOT_ENCRYPT_DEVICE_ID);
        } else if (PrintUtil.uniqueDeviceIdPerApp) {
            this.deviceId = getAppSpecificDeviceID(context);
            if(isDebuggable)
                Log.d(TAG, APP_SPECIFIC_DEVICE_ID);
        } else {
            this.deviceId = getVendorSpecificDeviceID(context);
            if(isDebuggable)
                Log.d(TAG, VENDOR_SPECIFIC_DEVICE_ID);
        }

        this.deviceType = Build.MODEL;
//        this.manufacturer = Build.MANUFACTURER;
        this.osType = OS_TYPE;
        this.osVersion = Build.VERSION.RELEASE;

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            this.version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            this.version = "<N/A>";
        }
        offRamp = ACTION_PRINT;

        try {
            this.wifiSsid = md5(getWifiSsid(context));
        } catch (SecurityException se) {
            this.wifiSsid = "NO PERMISSION";
        }
        this.appType = APP_TYPE;

        this.languageCode = Locale.getDefault().getISO3Language();
        this.countryCode = Locale.getDefault().getISO3Country();
        this.timezoneDescription = TimeZone.getDefault().getDisplayName();
        this.timezoneOffsetSeconds = String.valueOf(TimeZone.getDefault().getRawOffset()/1000);

    }

    public String getAppLable(Context pContext) {
        PackageManager lPackageManager = pContext.getPackageManager();
        ApplicationInfo lApplicationInfo = null;
        try {
            lApplicationInfo = lPackageManager.getApplicationInfo(pContext.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        return (String) (lApplicationInfo != null ? lPackageManager.getApplicationLabel(lApplicationInfo) : "Unknown");
    }

    public HashMap<String, String> toMap() {

        HashMap<String, String>  map = new HashMap<String, String>();

        if (this.deviceId != null) map.put(DEVICE_ID_LABEL, this.deviceId);
        if (this.deviceType != null) map.put(DEVICE_TYPE_LABEL, this.deviceType);
        if (this.offRamp != null) map.put(OFF_RAMP_LABEL, this.offRamp);
        if (this.osType != null) map.put(OS_TYPE_LABEL, this.osType);
        if (this.osVersion != null) map.put(OS_VERSION_LABEL, this.osVersion);
        if (this.productName != null) map.put(PRODUCT_NAME_LABEL, this.productName);
        if (this.version != null) map.put(VERSION_LABEL, this.version);
        if (this.wifiSsid != null) map.put(WIFI_SSID_LABEL, this.wifiSsid);
        if (this.appType != null) map.put(APP_TYPE_LABEL, this.appType);
        if (this.productId != null) map.put(PRODUCT_ID_LABEL, this.productId);
        if (this.printLibraryVersion != null) map.put(PRINT_LIBRARY_VERSION_LABEL, this.printLibraryVersion);
        if (this.languageCode != null) map.put(LANGUAGE_CODE_LABEL, this.languageCode);
        if (this.countryCode != null) map.put(COUNTRY_CODE_LABEL, this.countryCode);
        if (this.timezoneDescription != null) map.put(TIMEZONE_DESCRIPTION, this.timezoneDescription);
        if (this.timezoneOffsetSeconds != null) map.put(TIMEZONE_OFFSET_SECONDS, this.timezoneOffsetSeconds);

        return map;
    }

    public HashMap<String, String> toEventOnlyMap() {
        HashMap<String, String>  map = toMap();

        map.remove(OFF_RAMP_LABEL);
        map.remove(APP_TYPE);
        map.remove(WIFI_SSID_LABEL);

        return map;
    }

    public static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(s.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            return String.format("%032x", number).toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getWifiSsid(Context context) {

        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String ssid = wifiInfo.getSSID().replace("\"", "");
        if (UNKNOWN_SSID.equals(ssid)) {
            ssid = NO_WIFI;
        }
        return ssid;
    }

    private String getDeviceId(Context context) {

        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getVendorSpecificDeviceID(Context context) {

        String vendorName = "";
        String[] vendorNames = null;

        if (this.productId != null)
            vendorNames = this.productId.split("[.]");

        if (vendorNames != null && vendorNames.length >= 2)
            vendorName = this.productId.split("[.]")[0] + "." + this.productId.split("[.]")[1];

        return md5(vendorName + getDeviceId(context));

    }

    private String getAppSpecificDeviceID(Context context) {

        return md5(this.productId + getDeviceId(context));

    }

}
