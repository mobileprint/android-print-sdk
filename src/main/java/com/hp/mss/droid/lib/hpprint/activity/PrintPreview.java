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

package com.hp.mss.droid.lib.hpprint.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.hp.mss.droid.lib.hpprint.R;
import com.hp.mss.droid.lib.hpprint.util.PrintUtil;
import com.hp.mss.droid.lib.hpprint.view.PagePreviewView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class PrintPreview extends Activity {

    private static final int DEFAULT_WIDTH = 5;
    private static final int DEFAULT_HEIGHT = 7;

    private int previewWidth;
    private int previewHeight;

    private boolean landscapePhoto;

    static String HP_ANDROID_MOBILE_SITE = "http://www8.hp.com/us/en/ads/mobility/overview.html?jumpid=va_r11400_eprint";
    String photoFileName = null;
    Bitmap photo = null;
    private PagePreviewView previewView;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        String photoFileName = (String) getIntent().getExtras().get(PrintUtil.PHOTO_FILE_URI);
        int dpi = (int) getIntent().getExtras().get(PrintUtil.DPI);
        photo = getImageBitmap(this, photoFileName);
        photo.setDensity(dpi);

        Spinner size_spinner = (Spinner) findViewById(R.id.paper_size_spinner);
        setSizeSpinnerListener(size_spinner);

        Spinner type_spinner = (Spinner) findViewById(R.id.paper_type_spinner);

        previewView = (PagePreviewView) findViewById(R.id.preview_image_view);

        landscapePhoto = photo.getWidth() > photo.getHeight();

        setPreviewViewLayoutProperties();

        TextView linkTextView = (TextView) findViewById(R.id.ic_printing_support_link);
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mobileSiteIntent = new Intent(Intent.ACTION_VIEW);
                mobileSiteIntent.setData(Uri.parse(HP_ANDROID_MOBILE_SITE));
                startActivity(mobileSiteIntent);
            }
        });
    }



    private void setPreviewViewLayoutProperties() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        if (outMetrics.widthPixels <= outMetrics.heightPixels) { //screen in portrait mode
            previewWidth = outMetrics.widthPixels;
            previewHeight = (int) (outMetrics.widthPixels * 4 / 5f);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) previewView.getLayoutParams();
            params.width = previewWidth;
            params.height = previewHeight;
            previewView.setLayoutParams(params);
        } else { //screen in landscape mode
            previewWidth = (int) (outMetrics.widthPixels / 2f);
            previewHeight = outMetrics.heightPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) previewView.getLayoutParams();
            params.width = previewWidth;
            params.height = previewHeight;
            previewView.setLayoutParams(params);
        }

        previewView.setOrientation(landscapePhoto);
        previewView.setScaleType(scaleType);
        previewView.setPhoto(new BitmapDrawable(getResources(), photo));
        //

        Point photoSize = (landscapePhoto)? new Point(DEFAULT_HEIGHT, DEFAULT_WIDTH) :new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        previewView.setPhotoSize(photoSize);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_print) {
            PrintUtil.performPrint(this, photo, scaleType);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Bitmap getImageBitmap(Activity activity, String photoFileName) {
        Bitmap b = null;

        try {
            File file = new File(photoFileName);

            InputStream inputStream = new FileInputStream(file);
            b = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (photoFileName != null) {
            File photoFile = new File(photoFileName);
            if (photoFile.exists())
                photoFile.deleteOnExit();
        }
    }



    public void setSizeSpinnerListener(Spinner sizeSpinner) {
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String paperSize = (String) parent.getItemAtPosition(position);

                String[] sizeArray = paperSize.split(" x ");

                if (sizeArray.length == 2) {
                    float width = landscapePhoto ? Float.parseFloat(sizeArray[1].trim()) : Float.parseFloat(sizeArray[0].trim());
                    float height = landscapePhoto ? Float.parseFloat(sizeArray[0].trim()) : Float.parseFloat(sizeArray[1].trim());

                    previewView.setPageSize(width, height);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setPreviewViewLayoutProperties();
    }

}
