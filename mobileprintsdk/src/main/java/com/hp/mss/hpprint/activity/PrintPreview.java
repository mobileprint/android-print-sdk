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

package com.hp.mss.hpprint.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.util.PrintUtil;
import com.hp.mss.hpprint.util.SnapShotsMediaPrompt;
import com.hp.mss.hpprint.view.PagePreviewView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The intent of this class is to allow KitKat device users to preview the print out prior to sending
 * the print job to the printer. This class is only used within the HP Print SDK (You should not
 * create this activity by yourself). In the future, we may allow UI customization
 * within this activity.
 */
public class PrintPreview extends AppCompatActivity {
    HashMap<String,PrintAttributes.MediaSize> spinnerMap = new HashMap<>();
    private PagePreviewView previewView;

    private float paperWidth;
    private float paperHeight;
    private boolean disablePrintButton = false;

    PrintJobData printJobData;
    String spinnerSelectedText;

    private static final PrintAttributes.MediaSize[] defaultMediaSizes = {
        PrintAttributes.MediaSize.NA_INDEX_4X6,
        PrintUtil.mediaSize5x7,
        PrintAttributes.MediaSize.NA_LETTER
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if  (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }
        setContentView(R.layout.activity_print_preview);

        printJobData = PrintUtil.getPrintJobData();

        initializeSpinnerData();

        previewView = (PagePreviewView) findViewById(R.id.preview_image_view);

        final FloatingActionButton button = (FloatingActionButton) findViewById(R.id.print_preview_print_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!disablePrintButton) {
                    disablePrintButton = true;
                    onPrintClicked(v);
                }
            }
        });

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }

    private void initializeSpinnerData(){
        AppCompatSpinner sizeSpinner = (AppCompatSpinner) findViewById(R.id.paper_size_spinner);
        List<String> spinnerList = new ArrayList<String>();

        // add 4x5 as needed
        String text = "";
        if (PrintUtil.is4x5media) {
            text = (String) getText(R.string.preview_spinner_4x5);
            spinnerMap.put(text, PrintAttributes.MediaSize.NA_INDEX_4X6);
            spinnerList.add(text);
        }

        // add default media size
        for (int i = 0; i < defaultMediaSizes.length; i++) {
            text = getSpinnerText(defaultMediaSizes[i]);
            spinnerMap.put(text, defaultMediaSizes[i]);
            spinnerList.add(text);
        }

        // add media size in print items
        if(printJobData.getPrintItems() != null)
            for (PrintAttributes.MediaSize mediaSize: printJobData.getPrintItems().keySet()) {
                text = getSpinnerText(mediaSize);
                if (!spinnerList.contains(text)) {
                    spinnerMap.put(text, mediaSize);
                    spinnerList.add(text);
                }
            }

        // add media size from default print item if it does exist
        if (printJobData.getDefaultPrintItem() != null && printJobData.getDefaultPrintItem().getMediaSize() != null) {
            PrintAttributes.MediaSize mediaSize = printJobData.getDefaultPrintItem().getMediaSize();
            text = getSpinnerText(mediaSize);
            if (!spinnerList.contains(text)) {
                spinnerMap.put(text, mediaSize);
                spinnerList.add(text);
            }
        }

        String[] spinnerArray = spinnerList.toArray(new String[spinnerList.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(adapter);

        if(printJobData.getPrintDialogOptions() != null) {
            PrintAttributes.MediaSize mediaSize = printJobData.getPrintDialogOptions().getMediaSize();
            text = getSpinnerText(mediaSize);
            sizeSpinner.setSelection(adapter.getPosition(text));
        }
        setSizeSpinnerListener(sizeSpinner);
    }

    private String getSpinnerText(PrintAttributes.MediaSize mediaSize){
        String spinnerText = mediaSize.getLabel(getPackageManager());

        if (mediaSize == PrintAttributes.MediaSize.NA_INDEX_4X6) {
            spinnerText = (String) getText(R.string.preview_spinner_4x6);
        } else if (mediaSize == PrintUtil.mediaSize5x7) {
            spinnerText = (String) getText(R.string.preview_spinner_5x7);
        } else if (mediaSize == PrintAttributes.MediaSize.NA_LETTER) {
            spinnerText = (String) getText(R.string.preview_spinner_letter);
        }
        return  spinnerText;

//        Note: Below is the no localized spinner text
//        String widthText = fmt(mediaSize.getWidthMils() / 1000f);
//        String heightText = fmt(mediaSize.getHeightMils() / 1000f);
//        return String.format("%s x %s", widthText, heightText);
    }



    private static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_print_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        invalidateOptionsMenu();
        //noinspection SimplifiableIfStatement
        if (id == R.id.print_preview_menu_print_help) {
            printHelpClicked();
        } else if (id == R.id.print_preview_menu_print_service_plugins) {
            printServicePluginClicked();
        } else if (id == android.R.id.home) {
            super.onBackPressed();
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewView.setPhoto(null);
        //todo: delete printjob when done
//        if (photoFileName != null) {
//            File photoFile = new File(photoFileName);
//            if (photoFile.exists())
//                photoFile.deleteOnExit();
//        }
    }

    public void onPrintClicked(View view) {
        if (paperWidth == 4 && paperHeight == 5) {
            SnapShotsMediaPrompt.SnapShotsPromptListener snapShotsPromptListener =
                    new SnapShotsMediaPrompt.SnapShotsPromptListener() {
                        @Override
                        public void SnapShotsPromptOk() {
                            doPrint();
                        }

                        public void SnapShotsPromptCancel() {
                            disablePrintButton = false;
                        }
                    };
            SnapShotsMediaPrompt.displaySnapShotsPrompt(this, snapShotsPromptListener);
        } else {
            doPrint();
        }
    }

    public void printHelpClicked() {
        Intent printHelpIntent = new Intent(this, PrintHelp.class);
        startActivity(printHelpIntent);
    }

    public void printServicePluginClicked() {
        Intent pluginStatusIntent = new Intent(this, PrintPluginManagerActivity.class);
        startActivity(pluginStatusIntent);
    }

    public void setSizeSpinnerListener(Spinner sizeSpinner) {
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedText = (String) parent.getItemAtPosition(position);

                PrintItem printItem = printJobData.getPrintItem(spinnerMap.get(spinnerSelectedText));
                if (printItem != null && printItem.getMediaSize() != null) {
                    if (spinnerSelectedText == getText(R.string.preview_spinner_4x5)) {
                        paperWidth = 4;
                        paperHeight = 5;
                    } else {
                        paperWidth = printItem.getMediaSize().getWidthMils() / 1000f;
                        paperHeight = printItem.getMediaSize().getHeightMils() / 1000f;
                    }
                } else {
                    printItem = printJobData.getDefaultPrintItem();
                    PrintAttributes.MediaSize mediaSize = spinnerMap.get(spinnerSelectedText);
                    if (spinnerSelectedText == getText(R.string.preview_spinner_4x5)) {
                        paperWidth = 4;
                        paperHeight = 5;
                    } else {
                        paperWidth = mediaSize.getWidthMils() / 1000f;
                        paperHeight = mediaSize.getHeightMils() / 1000f;
                    }
                }

                previewView.setPageSize(paperWidth, paperHeight);
                new PagePreviewView.ImageLoaderTask(PrintPreview.this).execute(new PagePreviewView.LoaderParams(printItem, previewView));

                // 'is4x5media' boolean was added to be used with adapter which is already removed from the project.
                // Currently 'is4x5media' boolean is used only for building of paper size spinner list.
                // Following row contains deprecated logic and causes issues in paper size list.
                // PrintUtil.is4x5media = paperHeight == 5 && paperWidth == 4;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void doPrint() {

        PrintAttributes printAttributes = printJobData.getPrintDialogOptions();
        if(printAttributes == null){
            printAttributes = new PrintAttributes.Builder()
                    .setMediaSize(spinnerMap.get(spinnerSelectedText))
                    .build();
        } else {
             printAttributes = new PrintAttributes.Builder()
                    .setColorMode(printJobData.getPrintDialogOptions().getColorMode())
                    .setMediaSize(spinnerMap.get(spinnerSelectedText))
                    .setMinMargins(printJobData.getPrintDialogOptions().getMinMargins())
                    .setResolution(printJobData.getPrintDialogOptions().getResolution())
                    .build();
        }

        printJobData.setPrintDialogOptions(printAttributes);
        printJobData.setPreviewPaperSize(spinnerSelectedText);
        PrintUtil.setPrintJobData(printJobData);

        PrintUtil.createPrintJob(this);

        invalidateOptionsMenu();
    }

    public void returnPrintDataToPreviousActivity(PrintMetricsData data) {
        Intent callingIntent = new Intent();
        callingIntent.putExtra(PrintMetricsData.class.toString(), data);
        setResult(RESULT_OK, callingIntent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        disablePrintButton = false;
        initializeSpinnerData();
    }

}
