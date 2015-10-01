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

import com.hp.mss.hpprint.BuildConfig;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * This is the class that encapsulates all the basic application data HP is collecting.
 * It is used inside HP print SDK, you should not create this yourself.
 */
public class ApplicationMetricsData {

    private static final String TAG = "ClientMetricsData";

    private static final String OS_TYPE = "Android";
    private static final String PRODUCT_NAME = "HP Snapshots";
    private static final String ACTION_PRINT = "Android Print";
    private static final String NO = "NO";
    private static final String APP_TYPE = "Partner";

    private static final String DEVICE_BRAND_LABEL = "device_brand";
    private static final String DEVICE_ID_LABEL = "device_id";
    private static final String DEVICE_TYPE_LABEL = "device_type";
    private static final String MANUFACTURER_LABEL = "manufacturer";
    private static final String OFF_RAMP_LABEL = "off_ramp";
    private static final String OS_TYPE_LABEL = "os_type";
    private static final String OS_VERSION_LABEL = "os_version";
    private static final String PRODUCT_NAME_LABEL = "product_name";
    private static final String VERSION_LABEL = "version";
    private static final String WIFI_SSID_LABEL = "wifi_ssid";
    private static final String UNKNOWN_SSID = "<unknown ssid>";
    private static final String NO_WIFI = "NO-WIFI";
    private  static final String APP_TYPE_LABEL = "app_type";
    private  static final String PRODUCT_ID_LABEL = "product_id";
    private  static final String PRINT_LIBRARY_VERSION_LABEL = "print_library_version";

    private String deviceBrand;
    private String deviceId;
    private String deviceType;
    private String manufacturer;
    private String offRamp;
    private String osType;
    private String osVersion;
    private String productName;
    private String version;
    private String wifiSsid;
    private String appType;
    private String productId;
    private String printLibraryVersion;

    public ApplicationMetricsData(final Context context) {

//        this.deviceBrand = Build.BRAND;
        this.deviceId = getDeviceId(context);
        this.deviceType = Build.MODEL;
//        this.manufacturer = Build.MANUFACTURER;
        this.osType = OS_TYPE;
        this.osVersion = Build.VERSION.RELEASE;

        this.productName = getAppLable(context);
        this.productId = context.getPackageName();
        this.printLibraryVersion = BuildConfig.VERSION_NAME;

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
//        this.appType = APP_TYPE;
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

        if (this.deviceBrand != null) map.put(DEVICE_BRAND_LABEL, this.deviceBrand);
        if (this.deviceId != null) map.put(DEVICE_ID_LABEL, this.deviceId);
        if (this.deviceType != null) map.put(DEVICE_TYPE_LABEL, this.deviceType);
        if (this.manufacturer != null) map.put(MANUFACTURER_LABEL, this.manufacturer);
        if (this.offRamp != null) map.put(OFF_RAMP_LABEL, this.offRamp);
        if (this.osType != null) map.put(OS_TYPE_LABEL, this.osType);
        if (this.osVersion != null) map.put(OS_VERSION_LABEL, this.osVersion);
        if (this.productName != null) map.put(PRODUCT_NAME_LABEL, this.productName);
        if (this.version != null) map.put(VERSION_LABEL, this.version);
        if (this.wifiSsid != null) map.put(WIFI_SSID_LABEL, this.wifiSsid);
        if (this.appType != null) map.put(APP_TYPE_LABEL, this.appType);
        if (this.productId != null) map.put(PRODUCT_ID_LABEL, this.productId);
        if (this.printLibraryVersion != null) map.put(PRINT_LIBRARY_VERSION_LABEL, this.printLibraryVersion);
        return map;
    }

    public static String md5(String s) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(), 0, s.length());
            String hash = new BigInteger(1, digest.digest()).toString(16);
            return hash.toUpperCase();
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

}
