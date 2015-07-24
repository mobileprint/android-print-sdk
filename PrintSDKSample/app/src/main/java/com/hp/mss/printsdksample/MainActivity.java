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

package com.hp.mss.printsdksample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.hp.mss.hpprint.activity.PrintPreview;
import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.util.ImageLoaderUtil;
import com.hp.mss.hpprint.util.PrintUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity implements RadioGroup.OnCheckedChangeListener, PrintUtil.PrintMetricsListener {


    PrintItem.ScaleType scaleType;
    boolean showMetricsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup layoutRadioGroup = (RadioGroup) findViewById(R.id.layoutRadioGroup);
        layoutRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(layoutRadioGroup, layoutRadioGroup.getCheckedRadioButtonId());

        RadioGroup metricsRadioGroup = (RadioGroup) findViewById(R.id.metricsRadioGroup);
        metricsRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(metricsRadioGroup, metricsRadioGroup.getCheckedRadioButtonId());
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch(checkedId) {
            case R.id.layoutCenter:
                scaleType = PrintItem.ScaleType.CENTER;
                break;
            case R.id.layoutCorp:
                scaleType = PrintItem.ScaleType.CROP;
                break;
            case R.id.layoutFit:
                scaleType = PrintItem.ScaleType.FIT;
                break;
            case R.id.layoutCenterTopLeft:
                scaleType = PrintItem.ScaleType.CENTER_TOP_LEFT;
                break;
            case R.id.withMetrics:
                showMetricsDialog = true;
                break;
            case R.id.withoutMetrics:
                showMetricsDialog = false;
                break;
            default:
                showMetricsDialog = true;
                scaleType = PrintItem.ScaleType.CENTER;
        }
    }

    public void buttonClicked(View v) {

        //Create image assets from the saved files.
        ImageAsset imageAsset4x5l = new ImageAsset(this, R.drawable.template4x5l, ImageAsset.MeasurementUnits.INCHES, 5, 4);
        ImageAsset imageAsset4x5p = new ImageAsset(this, R.drawable.template4x5p, ImageAsset.MeasurementUnits.INCHES, 4, 5);
        ImageAsset imageAsset4x6 = new ImageAsset(this, R.drawable.template4x6, ImageAsset.MeasurementUnits.INCHES, 4, 6);
        ImageAsset imageAsset5x7 = new ImageAsset(this, R.drawable.template5x7, ImageAsset.MeasurementUnits.INCHES, 5, 7);
        ImageAsset assetdirectory = new ImageAsset(this, "oceanwave.jpeg", ImageAsset.MeasurementUnits.INCHES, 4, 6);

        //Alternatively, you can use a bitmap by doing the following.
        // ImageAsset bitmapAsset = new ImageAsset(this, bitmap, ImageAsset.MeasurementUnits.INCHES, 4,5);

        //Create the printJobData with the default print item
        PrintItem printItemDefault = new ImagePrintItem(scaleType, imageAsset5x7);
        PrintJobData printJobData = new PrintJobData(this, printItemDefault);

        //Giving the print job a name.
        printJobData.setJobName("Example");

        //Example for creating a custom media size in android.
        PrintAttributes.MediaSize mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "android", 5000, 7000);

        //Create printitems from the assets. These define what asset is to be used for each media size.
        PrintItem printItem4x6 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, scaleType, imageAsset4x6);
        PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER, scaleType, imageAsset4x5p);
        PrintItem printItem85x11l = new ImagePrintItem(new PrintAttributes.MediaSize(printItem85x11.getMediaSize().getId(), "android", printItem85x11.getMediaSize().getHeightMils(), printItem85x11.getMediaSize().getWidthMils()), scaleType, imageAsset4x5l);
        PrintItem printItem5x7 = new ImagePrintItem(mediaSize5x7, scaleType, imageAsset5x7);
        PrintItem printItem5x8 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_5X8, scaleType, assetdirectory);

        //Lastly, add all the printitems to the print job data.
        printJobData.addPrintItem(printItem4x6);
        printJobData.addPrintItem(printItem85x11);
        printJobData.addPrintItem(printItem85x11l);
        printJobData.addPrintItem(printItem5x7);
        printJobData.addPrintItem(printItem5x8);

        //Optionally include print attributes.
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_INDEX_4X6)
                .build();
        printJobData.setPrintDialogOptions(printAttributes);

        //Set the printJobData to the PrintUtil and call print.
        PrintUtil.setPrintJobData(printJobData);
        PrintUtil.print(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrintMetricsDataPosted(PrintMetricsData printMetricsData) {
        if (showMetricsDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(printMetricsData.toMap().toString());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }
    }
}
