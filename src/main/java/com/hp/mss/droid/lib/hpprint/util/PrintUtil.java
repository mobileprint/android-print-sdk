package com.hp.mss.droid.lib.hpprint.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;

import com.hp.mss.droid.lib.hpprint.R;
import com.hp.mss.droid.lib.hpprint.activity.PrintPreview;
import com.hp.mss.droid.lib.hpprint.adapter.PhotoPrintDocumentAdapter;

/**
 * Copyright 2015 Hewlett-Packard, Co.
 */

public class PrintUtil {

    public static void launchPrint(Activity activity, Bitmap photo){
        //if running on Lollipop or after, use the default print capability
        // that shows its own preview
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //if running on Lollipop or after
            print(activity, photo);
        }
        else { // since earlier than Lollipop, show our own preview screen
            Intent intent = new Intent(activity, PrintPreview.class);
            intent.putExtra("bitmap", photo);
            activity.startActivity(intent);
        }
    }


    private static void print(Activity activity, Bitmap photo){
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        String jobName = activity.getString(R.string.app_name);
        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(activity, photo);
        printManager.print(jobName, adapter, null );
    }
}
