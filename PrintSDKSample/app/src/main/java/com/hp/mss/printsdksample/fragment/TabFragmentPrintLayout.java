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

package com.hp.mss.printsdksample.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PDFPrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.model.asset.PDFAsset;
import com.hp.mss.hpprint.util.PrintUtil;
import com.hp.mss.printsdksample.R;

/**
 * Created by panini on 2/25/16.
 */
public class TabFragmentPrintLayout extends Fragment implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, PrintUtil.PrintMetricsListener {
    String contentType;
    PrintItem.ScaleType scaleType;
    PrintAttributes.Margins margins;
    boolean showMetricsDialog;
    PrintJobData printJobData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.tab_fragment_print_layout, container, false);


        RadioGroup layoutRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.layoutRadioGroup);
        layoutRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(layoutRadioGroup, layoutRadioGroup.getCheckedRadioButtonId());

        RadioGroup layoutMarginRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.layoutMarginRadioGroup);
        layoutMarginRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(layoutMarginRadioGroup, layoutMarginRadioGroup.getCheckedRadioButtonId());

//        SwitchCompat metricsSwitch = (SwitchCompat) inflatedView.findViewById(R.id.metricsRadioGroup);
//        metricsSwitch.setOnCheckedChangeListener(this);
//        onCheckedChanged(metricsSwitch, metricsSwitch.isChecked());

        RadioGroup contentRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.contentRadioGroup);
        contentRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(contentRadioGroup, contentRadioGroup.getCheckedRadioButtonId());

//        SwitchCompat deviceIdSwitch = (SwitchCompat) inflatedView.findViewById(R.id.deviceIdRadioGroup);
//        deviceIdSwitch.setOnCheckedChangeListener(this);
//        onCheckedChanged(deviceIdSwitch, deviceIdSwitch.isChecked());

        FloatingActionButton printButton = (FloatingActionButton) inflatedView.findViewById(R.id.printBtn);
        printButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                continueButtonClicked(v);
            }
        });

        return inflatedView;
    }

    public void continueButtonClicked(View v) {
        createPrintJobData();
        PrintUtil.setPrintJobData(printJobData);
        PrintUtil.sendPrintMetrics = showMetricsDialog;
        PrintUtil.print(getActivity());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
//            case R.id.deviceIdRadioGroup:
//                PrintUtil.uniqueDeviceIdPerApp = isChecked;
//            case R.id.metricsRadioGroup:
//                showMetricsDialog = isChecked;
            default:
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch(checkedId) {
            case R.id.layoutCenterTop:
                scaleType = PrintItem.ScaleType.CENTER_TOP;
                break;
            case R.id.layoutCenter:
                scaleType = PrintItem.ScaleType.CENTER;
                break;
            case R.id.layoutCrop:
                scaleType = PrintItem.ScaleType.CROP;
                break;
            case R.id.layoutFit:
                scaleType = PrintItem.ScaleType.FIT;
                break;
            case R.id.layoutCenterTopLeft:
                scaleType = PrintItem.ScaleType.CENTER_TOP_LEFT;
                break;
            case R.id.layoutWithMargin:
                margins = new PrintAttributes.Margins(500, 500, 500, 500);
                break;
            case R.id.layoutWithTopMargin:
                margins = new PrintAttributes.Margins(0, 500, 0, 0);
                break;
            case R.id.layoutWithoutMargin:
                margins = new PrintAttributes.Margins(0, 0, 0, 0);
                break;
            case R.id.contentPDF:
                contentType = "PDF";
                break;
            case R.id.contentImage:
                contentType = "Image";
                break;
            default:
                showMetricsDialog = true;
                scaleType = PrintItem.ScaleType.CENTER;
                contentType = "Image";
                margins = new PrintAttributes.Margins(0, 0, 0, 0);
        }
    }

    private void createPrintJobData() {
        //Example for creating a custom media size in android.
        PrintAttributes.MediaSize mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "android", 5000, 7000);

        if(contentType.equals("Image")) {
            //Create image assets from the saved files.
            ImageAsset imageAsset4x5 = new ImageAsset(getActivity(), R.drawable.t4x5, ImageAsset.MeasurementUnits.INCHES, 4, 5);
            ImageAsset imageAsset4x6 = new ImageAsset(getActivity(), R.drawable.t4x6, ImageAsset.MeasurementUnits.INCHES, 4, 6);
            ImageAsset imageAsset5x7 = new ImageAsset(getActivity(), R.drawable.t5x7, ImageAsset.MeasurementUnits.INCHES, 5, 7);
            ImageAsset assetdirectory = new ImageAsset(getActivity(), "t8.5x11.png", ImageAsset.MeasurementUnits.INCHES, 8.5f, 11f);

            //Alternatively, you can use a bitmap by doing the following.
            // ImageAsset bitmapAsset = new ImageAsset(this, bitmap, ImageAsset.MeasurementUnits.INCHES, 4,5);

            //Create printitems from the assets. These define what asset is to be used for each media size.
            PrintItem printItem4x6 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6,margins, scaleType, imageAsset4x6);
            PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER,margins, scaleType, assetdirectory);
            PrintItem printItem5x7 = new ImagePrintItem(mediaSize5x7,margins, scaleType, imageAsset5x7);
            PrintItem printItem5x8 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_5X8,margins, scaleType, imageAsset4x5);

            //Create the printJobData with the default print item
            PrintItem printItemDefault = new ImagePrintItem(margins, scaleType, imageAsset4x5);
            printJobData = new PrintJobData(getActivity(), printItemDefault);

            //Lastly, add all the printitems to the print job data.
            printJobData.addPrintItem(printItem4x6);
            printJobData.addPrintItem(printItem85x11);
            printJobData.addPrintItem(printItem5x7);
            printJobData.addPrintItem(printItem5x8);


        } else {
            PDFAsset pdf4x6 = new PDFAsset("4x6.pdf", true);
            PDFAsset pdf5x7 = new PDFAsset("5x7.pdf", true);
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

            PrintItem printItem4x6 = new PDFPrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6,margins, scaleType, pdf4x6);
            PrintItem printItem5x7 = new PDFPrintItem(mediaSize5x7,margins, scaleType, pdf5x7);
            PrintItem printItemLetter = new PDFPrintItem(PrintAttributes.MediaSize.NA_LETTER,margins, scaleType, pdfletter);

            printJobData = new PrintJobData(getActivity(), printItem4x6);

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
    }

    @Override
    public void onPrintMetricsDataPosted(PrintMetricsData printMetricsData) {
        if (showMetricsDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
