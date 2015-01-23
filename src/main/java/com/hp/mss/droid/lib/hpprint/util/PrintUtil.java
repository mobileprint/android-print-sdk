package com.hp.mss.droid.lib.hpprint.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;

import com.hp.mss.droid.lib.hpprint.activity.PrintPreview;
import com.hp.mss.droid.lib.hpprint.adapter.PhotoPrintDocumentAdapter;
import com.hp.mss.droid.lib.hpprint.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Copyright 2015 Hewlett-Packard, Co.
 */

public class PrintUtil {
    private static String HP_PRINT_PLUGIN_PACKAGE_NAME = "com.hp.android.printservice";
    private static String MOPRIA_PRINT_PLUGIN_PACKAGE_NAME = "org.mopria.printplugin";
    private static String GOOGLE_STORE_PACKAGE_NAME = "com.android.vending";
    private static String INSTALL_HP_PRINT_PLUGIN_MSG = "To print best quality photos, " +
                                                        "please install HP Print Plugin if you have HP printer(s)." +
                                                        "  Otherwise install Mopria Print Plugin. " +
                                                        "Please make sure plugin service is turned on after the installation";


    public static void launchPrint(Activity activity, Bitmap bitmap){

//        Bitmap photo = getImageBitmap(activity, bitmap);

        if ( checkAndInstallHPPrintPlugin(activity) ) {

            //if running on Lollipop or after, use the default print capability
            // that shows its own preview
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                print(activity, bitmap);

            } else { // since earlier than Lollipop, show our own preview screen

//                Intent intent = new Intent(activity, PrintPreview.class);
//                intent.putExtra("photoFileUri", photoFileUri);
//                activity.startActivity(intent);
            }
        }
    }


    public static boolean checkAndInstallHPPrintPlugin(Activity activity) {

        boolean isInstalled = false;

        if ( isPackageInstalled(activity, HP_PRINT_PLUGIN_PACKAGE_NAME) ||
                isPackageInstalled(activity, MOPRIA_PRINT_PLUGIN_PACKAGE_NAME))
        {

            isInstalled = true;

        } else {

            dispatchInstallMsgDialog(activity, INSTALL_HP_PRINT_PLUGIN_MSG);

        }

        return isInstalled;
    }

    public static boolean isPackageInstalled(Activity activity, String packageName) {

        boolean isInstalled = false;

        PackageManager packageManager = activity.getPackageManager();

        try {

            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            isInstalled = true;

        } catch (PackageManager.NameNotFoundException e) { }

        return isInstalled;
    }


    public static void dispatchInstallPluginIntent(Activity activity, String packageName) {

        String url;

        if ( isPackageInstalled(activity, GOOGLE_STORE_PACKAGE_NAME) ) {

            url = "market://details?id=" + packageName;

        } else {

            url = "https://play.google.com/store/apps/details?id=" + packageName;

        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }


    private static void dispatchInstallMsgDialog(final Activity activity, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
               .setCancelable(false)
               .setPositiveButton("Install HP Print Plugin", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dispatchInstallPluginIntent(activity, HP_PRINT_PLUGIN_PACKAGE_NAME);
                    }
               })
               .setNegativeButton("Install Mopria Print Plugin", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dispatchInstallPluginIntent(activity, MOPRIA_PRINT_PLUGIN_PACKAGE_NAME);
                   }
               })
               .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               })
        .create()
        .show();

    }


    private static void print(Activity activity, Bitmap photo){
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        String jobName = activity.getString(R.string.app_name);
        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(activity, photo);
        printManager.print(jobName, adapter, null );
    }

    public static Bitmap getImageBitmap(Activity activity, Uri photoFileUri) {
        Bitmap b = null;

        try {
            File file = new File(photoFileUri.getPath());

            InputStream inputStream = new FileInputStream(file);
            b = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }
}
