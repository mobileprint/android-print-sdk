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
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintJob;
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
import com.hp.mss.hpprint.model.PDFPrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.model.asset.PDFAsset;
import com.hp.mss.hpprint.util.ImageLoaderUtil;
import com.hp.mss.hpprint.util.PrintUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener, PrintUtil.PrintMetricsListener {

    String contentType;
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

        RadioGroup contentRadioGroup = (RadioGroup) findViewById(R.id.contentRadioGroup);
        contentRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(contentRadioGroup, contentRadioGroup.getCheckedRadioButtonId());

        RadioGroup deviceIdRadioGroup = (RadioGroup) findViewById(R.id.deviceIdRadioGroup);
        contentRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(contentRadioGroup, contentRadioGroup.getCheckedRadioButtonId());
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
            case R.id.contentPDF:
                contentType = "PDF";
                break;
            case R.id.contentImage:
                contentType = "Image";
                break;
            case R.id.udipaTrue:
                PrintUtil.uniqueDeviceIdPerApp = true;
                break;
            case R.id.udipaFalse:
                PrintUtil.uniqueDeviceIdPerApp = false;
                break;
            default:
                showMetricsDialog = true;
                scaleType = PrintItem.ScaleType.CENTER;
                contentType = "Image";
        }
    }

    public void buttonClicked(View v) {

        PrintJobData printJobData;

        //Example for creating a custom media size in android.
        PrintAttributes.MediaSize mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "android", 5000, 7000);

        if(contentType.equals("Image")) {
            //Create image assets from the saved files.
            ImageAsset imageAsset4x5 = new ImageAsset(this, R.drawable.t4x5, ImageAsset.MeasurementUnits.INCHES, 4, 5);
            ImageAsset imageAsset4x6 = new ImageAsset(this, R.drawable.t4x6, ImageAsset.MeasurementUnits.INCHES, 4, 6);
            ImageAsset imageAsset5x7 = new ImageAsset(this, R.drawable.t5x7, ImageAsset.MeasurementUnits.INCHES, 5, 7);
            ImageAsset assetdirectory = new ImageAsset(this, "t8.5x11.png", ImageAsset.MeasurementUnits.INCHES, 8.5f, 11f);

            //Alternatively, you can use a bitmap by doing the following.
            // ImageAsset bitmapAsset = new ImageAsset(this, bitmap, ImageAsset.MeasurementUnits.INCHES, 4,5);



            //Create printitems from the assets. These define what asset is to be used for each media size.
            PrintItem printItem4x6 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, scaleType, imageAsset4x6);
            PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER, scaleType, assetdirectory);
            PrintItem printItem5x7 = new ImagePrintItem(mediaSize5x7, scaleType, imageAsset5x7);
            PrintItem printItem5x8 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_5X8, scaleType, imageAsset4x5);

            //Create the printJobData with the default print item
            PrintItem printItemDefault = new ImagePrintItem(scaleType, imageAsset4x5);
            printJobData = new PrintJobData(this, printItemDefault);

            //Lastly, add all the printitems to the print job data.
            printJobData.addPrintItem(printItem4x6);
            printJobData.addPrintItem(printItem85x11);
            printJobData.addPrintItem(printItem5x7);
            printJobData.addPrintItem(printItem5x8);


        } else {
            PDFAsset pdf4x6 = new PDFAsset("4x6.pdf");
            PDFAsset pdf5x7 = new PDFAsset("5x7.pdf");
            PDFAsset pdfletter = new PDFAsset("8.5x11.pdf", true);

//            PDFAsset pdfletter = null;
//            try {
//                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath();
//                File file = new File(path + "/HP_PRINT_ITEM_ORIGINAL_3.pdf");
//                String s = file.getCanonicalPath();
//                pdfletter = new PDFAsset(s);
//            } catch (IOException e) {
//                Log.e("MainActivity", "Unable to create path string.");
//            }

            PrintItem printItem4x6 = new PDFPrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, scaleType, pdf4x6);
            PrintItem printItem5x7 = new PDFPrintItem(mediaSize5x7, scaleType, pdf5x7);
            PrintItem printItemLetter = new PDFPrintItem(PrintAttributes.MediaSize.NA_LETTER, scaleType, pdfletter);

            printJobData = new PrintJobData(this, printItem4x6);

            printJobData.addPrintItem(printItemLetter);
            printJobData.addPrintItem(printItem5x7);
        }

        //Giving the print job a name.
        printJobData.setJobName("Example");

        //Optionally include print attributes.
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
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
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
}
