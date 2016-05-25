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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PDFPrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.model.asset.PDFAsset;
import com.hp.mss.hpprint.util.PrintUtil;
import com.hp.mss.printsdksample.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by panini on 2/25/16.
 */
public class TabFragmentPrintLayout extends Fragment implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, PrintUtil.PrintMetricsListener {
    public static String CONTENT_TYPE_PDF = "PDF";
    public static String CONTENT_TYPE_IMAGE = "Image";
    public static String MIME_TYPE_PDF = "application/pdf";
    public static String MIME_TYPE_IMAGE = "image/*";
    public static String MIME_TYPE_IMAGE_PREFIX = "image/";
    public static String TAG = "TabFragmentPrintLayout";

    String contentType;
    PrintItem.ScaleType scaleType;
    PrintAttributes.Margins margins;
    boolean showMetricsDialog;
    boolean showCustomData;
    PrintJobData printJobData;
    EditText tagText;
    EditText valueText;

    static final int PICKFILE_RESULT_CODE = 1;
    private Uri userPickedUri;

    //Example for creating a custom media size in android.
    PrintAttributes.MediaSize mediaSize5x7;
    RelativeLayout filePickerLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.tab_fragment_print_layout, container, false);
        filePickerLayout = (RelativeLayout) inflatedView.findViewById(R.id.filePickerLayout);

        RadioGroup layoutRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.layoutRadioGroup);
        layoutRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(layoutRadioGroup, layoutRadioGroup.getCheckedRadioButtonId());

        RadioGroup layoutMarginRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.layoutMarginRadioGroup);
        layoutMarginRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(layoutMarginRadioGroup, layoutMarginRadioGroup.getCheckedRadioButtonId());

        SwitchCompat metricsSwitch = (SwitchCompat) inflatedView.findViewById(R.id.metricsRadioGroup);
        metricsSwitch.setOnCheckedChangeListener(this);
        onCheckedChanged(metricsSwitch, metricsSwitch.isChecked());

        RadioGroup contentRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.contentRadioGroup);
        contentRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(contentRadioGroup, contentRadioGroup.getCheckedRadioButtonId());

        RadioGroup deviceIdRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.deviceIdRadioGroup);
        deviceIdRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(deviceIdRadioGroup, deviceIdRadioGroup.getCheckedRadioButtonId());

        tagText = (EditText) inflatedView.findViewById(R.id.tagEditText);
        valueText = (EditText) inflatedView.findViewById(R.id.valueEditText);
        LinearLayout customData = (LinearLayout) inflatedView.findViewById(R.id.customData);
        showCustomData = customData.getVisibility() == View.VISIBLE;


        FloatingActionButton printButton = (FloatingActionButton) inflatedView.findViewById(R.id.printBtn);
        printButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                continueButtonClicked(v);
            }
        });

        Button buttonPick = (Button) inflatedView.findViewById(R.id.buttonPick);
        buttonPick.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(getContentMimeType());
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICKFILE_RESULT_CODE);
            }});



        mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "5 x 7", 5000, 7000);

        return inflatedView;
    }

    private String getContentMimeType() {
        String mimeType = MIME_TYPE_IMAGE;

        if (contentType == CONTENT_TYPE_PDF)
            mimeType =  MIME_TYPE_PDF;

        return mimeType;
    }

    public void continueButtonClicked(View v) {
        createPrintJobData();
        PrintUtil.setPrintJobData(printJobData);
        createCustomData();
//        PrintUtil.sendPrintMetrics = showMetricsDialog;
        PrintUtil.print(getActivity());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.metricsRadioGroup:
                showMetricsDialog = isChecked;
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
                contentType = CONTENT_TYPE_PDF;
//                filePickerLayout.setVisibility(View.GONE);
                break;
            case R.id.contentImage:
                contentType = CONTENT_TYPE_IMAGE;
//                filePickerLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.notEncrypted:
                PrintUtil.doNotEncryptDeviceId = true;
                break;
            case R.id.uniquePerApp:
                PrintUtil.doNotEncryptDeviceId = false;
                PrintUtil.uniqueDeviceIdPerApp = true;
                break;
            case R.id.uniquePerVendor:
                PrintUtil.doNotEncryptDeviceId = false;
                PrintUtil.uniqueDeviceIdPerApp = false;
                break;
            default:
                showMetricsDialog = true;
                scaleType = PrintItem.ScaleType.CENTER;
                contentType = "Image";
                margins = new PrintAttributes.Margins(0, 0, 0, 0);
        }
    }

    private void createPrintJobData() {
        if( userPickedUri !=null && getMimeType(userPickedUri).startsWith(MIME_TYPE_IMAGE_PREFIX) && contentType == CONTENT_TYPE_IMAGE)
            createUserSelectedImageJobData();
        else if( userPickedUri !=null  && getMimeType(userPickedUri).equals(MIME_TYPE_PDF) && contentType == CONTENT_TYPE_PDF  )
            createUserSelectedPDFJobData();
        else
            createDefaultPrintJobData();

        //Giving the print job a name.
        printJobData.setJobName("Example");

        //Optionally include print attributes.
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .build();
        printJobData.setPrintDialogOptions(printAttributes);

    }
    private void createDefaultPrintJobData() {

        if(contentType.equals("Image")) {
            //Create image assets from the saved files.
            ImageAsset imageAsset4x5 = new ImageAsset(getActivity(), R.drawable.t4x5, ImageAsset.MeasurementUnits.INCHES, 4, 5);
            ImageAsset imageAsset4x6 = new ImageAsset(getActivity(), R.drawable.t4x6, ImageAsset.MeasurementUnits.INCHES, 4, 6);
            ImageAsset imageAsset5x7 = new ImageAsset(getActivity(), R.drawable.t5x7, ImageAsset.MeasurementUnits.INCHES, 5, 7);
            ImageAsset assetdirectory = new ImageAsset(getActivity(), "t8.5x11.png", ImageAsset.MeasurementUnits.INCHES, 8.5f, 11f);


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

    }

    private void createUserSelectedImageJobData() {
        Bitmap userPickedBitmap;

        try {
            userPickedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), userPickedUri);
            int width = userPickedBitmap.getWidth();
            int height = userPickedBitmap.getHeight();

            // if user picked bitmap is too big, just reduce the size, so it will not chock the print plugin
            if (width * height > 5000) {
                width = width / 2;
                height = height / 2;
                userPickedBitmap = Bitmap.createScaledBitmap(userPickedBitmap, width, height, true);
            }

            DisplayMetrics mDisplayMetric = getActivity().getResources().getDisplayMetrics();
            float widthInches =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, width, mDisplayMetric);
            float heightInches =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, height, mDisplayMetric);

            ImageAsset imageAsset = new ImageAsset(getActivity(),
                    userPickedBitmap,
                    ImageAsset.MeasurementUnits.INCHES,
                    widthInches, heightInches);

            PrintItem printItem4x6 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6,margins, scaleType, imageAsset);
            PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER,margins, scaleType, imageAsset);
            PrintItem printItem5x7 = new ImagePrintItem(mediaSize5x7,margins, scaleType, imageAsset);

            printJobData = new PrintJobData(getActivity(), printItem4x6);
            printJobData.addPrintItem(printItem85x11);
            printJobData.addPrintItem(printItem5x7);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createUserSelectedPDFJobData() {
        File file = new File(userPickedUri.toString());
        try {
//            FileInputStream input = new FileInputStream(file);
            InputStream input=getActivity().getContentResolver().openInputStream(userPickedUri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "No File", e);
        }
        Bitmap userPickedBitmap;

//        try {
//        userPickedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), userPickedUri);
//        int width = userPickedBitmap.getWidth();
//        int height = userPickedBitmap.getHeight();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

                  PDFAsset pdfAsset = new PDFAsset(userPickedUri, false);

            PrintItem printItem4x6 = new PDFPrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, margins, scaleType, pdfAsset);
            PrintItem printItem5x7 = new PDFPrintItem(mediaSize5x7, margins, scaleType, pdfAsset);
            PrintItem printItemLetter = new PDFPrintItem(PrintAttributes.MediaSize.NA_LETTER, margins, scaleType, pdfAsset);

            printJobData = new PrintJobData(getActivity(), printItem4x6);

            printJobData.addPrintItem(printItemLetter);
            printJobData.addPrintItem(printItem5x7);

    }


    private void createCustomData() {
        PrintUtil.customData.clear();
        if (showCustomData)
            PrintUtil.customData.put(tagText.getText(), valueText.getText());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    userPickedUri = data.getData();
                    showFileInfo(userPickedUri);
                }
                break;

        }
    }

    private String getMimeType(Uri uri) {
        Uri returnUri = uri;
        return getActivity().getContentResolver().getType(returnUri);
    }

    private void showFileInfo(Uri uri) {
        Cursor returnCursor =
                getActivity().getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        Toast.makeText(getActivity(),
                "File " + returnCursor.getString(nameIndex) + "(" + Long.toString(returnCursor.getLong(sizeIndex)) + "0",
                Toast.LENGTH_LONG).show();

    }
}
