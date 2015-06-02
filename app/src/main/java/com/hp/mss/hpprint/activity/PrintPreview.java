//
// Hewlett-Packard Company
// All rights reserved.
//
// This file, its contents, concepts, methods, behavior, and operation
// (collectively the "Software") are protected by trade secret, patent,
// and copyright laws. The use of the Software is governed by a license
// agreement. Disclosure of the Software to third parties, in any form,
// in whole or in part, is expressly prohibited except as authorized by
// the license agreement.
//

package com.hp.mss.hpprint.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.util.FontUtil;
import com.hp.mss.hpprint.util.GAUtil;
import com.hp.mss.hpprint.util.ImageLoaderUtil;
import com.hp.mss.hpprint.util.PrintUtil;
import com.hp.mss.hpprint.util.SnapShotsMediaPrompt;
import com.hp.mss.hpprint.view.PagePreviewView;

import java.io.File;

public class PrintPreview extends AppCompatActivity {
    private static final String HP_ANDROID_MOBILE_SITE = "http://www8.hp.com/us/en/ads/mobility/overview.html?jumpid=va_r11400_eprint";

    public static final String PHOTO_FILE_URI = "photoFileUri";
    public static final String PRINT_JOB_NAME = "printJobName";
    public static final String SCALE_TYPE = "scaleMode";
    public static final String MULTIPLE_MEDIA_TYPES = "multiMediaTypes";

    private boolean landscapePhoto;

    private String photoFileName = null;
    private String printJobName = null;
    private float paperWidth;
    private float paperHeight;

    private PagePreviewView previewView;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;
    private boolean multiFile;
    private boolean disableMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_print_preview);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String photoFileName = getIntent().getExtras().getString(PHOTO_FILE_URI);
        printJobName = getIntent().getExtras().getString(PRINT_JOB_NAME);
        scaleType = (ImageView.ScaleType) getIntent().getExtras().get(SCALE_TYPE);

        Spinner sizeSpinner = (Spinner) findViewById(R.id.paper_size_spinner);
        setSizeSpinnerListener(sizeSpinner);

        previewView = (PagePreviewView) findViewById(R.id.preview_image_view);
        multiFile = getIntent().getExtras().getBoolean(MULTIPLE_MEDIA_TYPES);

        Rect photoBounds = ImageLoaderUtil.getImageSize(photoFileName);
        landscapePhoto = photoBounds.width() > photoBounds.height();

        setPreviewViewLayoutProperties();

        TextView linkTextView = (TextView) findViewById(R.id.ic_printing_support_link);
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAboutLinkClicked(view);
            }
        });

        ((TextView) findViewById(R.id.paper_size_title)).setTypeface(FontUtil.getDefaultFont(this));
        ((TextView) findViewById(R.id.print_preview_support_title)).setTypeface(FontUtil.getDefaultFont(this));
        ((TextView) findViewById(R.id.ic_printing_support_link)).setTypeface(FontUtil.getDefaultFont(this));


    }


    private void setPreviewViewLayoutProperties() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        if (outMetrics.widthPixels <= outMetrics.heightPixels) { //screen in portrait mode
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) previewView.getLayoutParams();
            int size = outMetrics.widthPixels;
            params.width = size;
            params.height = size;
            previewView.setLayoutParams(params);
        } else { //screen in landscape mode
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) previewView.getLayoutParams();
            params.width = (int) (outMetrics.widthPixels / 2f);
            params.height = outMetrics.heightPixels;
            previewView.setLayoutParams(params);
        }

        previewView.setOrientation(landscapePhoto);
        previewView.setScaleType(scaleType);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_print_preview, menu);
        menu.findItem(R.id.action_print).setEnabled(!disableMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        disableMenu = true;
        invalidateOptionsMenu();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_print) {
            if (paperWidth == 4 && paperHeight == 5) {
                SnapShotsMediaPrompt.SnapShotsPromptListener snapShotsPromptListener =
                        new SnapShotsMediaPrompt.SnapShotsPromptListener() {
                            @Override
                            public void SnapShotsPromptOk() {
                                doPrint();
                            }
                            public void SnapShotsPromptCancel() {
                                disableMenu = false;
                                invalidateOptionsMenu();
                            }
                        };
                SnapShotsMediaPrompt.displaySnapShotsPrompt(this, snapShotsPromptListener);
            } else {
                doPrint();
            }
            return true;
        } else if (id == android.R.id.home) {
            super.onBackPressed();
            disableMenu = false;
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewView.setPhoto(null);
        if (photoFileName != null) {
            File photoFile = new File(photoFileName);
            if (photoFile.exists())
                photoFile.deleteOnExit();
        }
    }

    public void onAboutLinkClicked(View view) {
        Intent mobileSiteIntent = new Intent(Intent.ACTION_VIEW);
        mobileSiteIntent.setData(Uri.parse(HP_ANDROID_MOBILE_SITE));
        startActivity(mobileSiteIntent);
    }

    public void setSizeSpinnerListener(Spinner sizeSpinner) {
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String paperSize = (String) parent.getItemAtPosition(position);

                String[] sizeArray = paperSize.split(" x ");

                if (sizeArray.length == 2) {
                    paperWidth = landscapePhoto ? Float.parseFloat(sizeArray[1].trim()) : Float.parseFloat(sizeArray[0].trim());
                    paperHeight = landscapePhoto ? Float.parseFloat(sizeArray[0].trim()) : Float.parseFloat(sizeArray[1].trim());


                    previewView.setPageSize(paperWidth, paperHeight);
                    new PagePreviewView.ImageLoaderTask(PrintPreview.this).execute(new PagePreviewView.LoaderParams((int) paperHeight, multiFile, photoFileName, previewView));

                    PrintUtil.is4x5media = paperHeight == 5 && paperWidth == 4;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void doPrint() {
        PrintUtil.OnPrintDataCollectedListener printDataCollectedListener =
                new PrintUtil.OnPrintDataCollectedListener() {
                    @Override
                    public void postPrintData(PrintMetricsData data) {
                        if (data.printResult.equals(PrintMetricsData.PRINT_RESULT_SUCCESS)) {
                            returnPrintDataToPreviousActivity(data);
                        } else {
                            GAUtil.sendEvent(GAUtil.EVENT_CATEGORY_FULFILLMENT, GAUtil.EVENT_ACTION_PRINT, data.printResult);
                        }
                    }
                };
        if (multiFile) {
            PrintUtil.printMultipleMediaTypesWithoutPreview(this, scaleType, printJobName, printDataCollectedListener, paperWidth, paperHeight);
        } else {
            PrintUtil.printWithoutPreview(this, previewView.getPhoto(), scaleType, printJobName, printDataCollectedListener, paperWidth, paperHeight);
        }
        disableMenu = false;
        invalidateOptionsMenu();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setPreviewViewLayoutProperties();
    }

    public void returnPrintDataToPreviousActivity(PrintMetricsData data) {
        Intent callingIntent = new Intent();
        callingIntent.putExtra(PrintMetricsData.class.toString(), data);
        setResult(RESULT_OK, callingIntent);
        finish();
    }

}
