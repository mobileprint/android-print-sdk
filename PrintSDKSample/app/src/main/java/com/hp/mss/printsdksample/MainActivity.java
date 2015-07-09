package com.hp.mss.printsdksample;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.hp.mss.hpprint.adapter.HPPrintDocumentAdapter;
import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJob;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.util.ImageLoaderUtil;
import com.hp.mss.hpprint.util.PrintUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    String filename4x5l;
    String filename4x5p;

    String filename4x6;
    String filename5x7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity activity = this;

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.scaleTypeArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // make sure it is not called anymore
                linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                filename4x5l = generateBitmap((ImageView) findViewById(R.id.templateimage4x5l), "4x5l");
                filename4x5p = generateBitmap((ImageView) findViewById(R.id.templateimage4x5p), "4x5p");
                filename4x6 = generateBitmap((ImageView) findViewById(R.id.templateimage4x6), "4x6");
                filename5x7 = generateBitmap((ImageView) findViewById(R.id.templateimage5x7), "5x7");

            }
        });


    }

    PrintItem.ScaleType scaleType;

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String scaleTypeString = (String) parent.getItemAtPosition(pos);
        if (scaleTypeString.equals("Center")) {
            scaleType = PrintItem.ScaleType.CENTER;
        } else if (scaleTypeString.equals("Fit")) {
            scaleType = PrintItem.ScaleType.FIT;
        } else if (scaleTypeString.equals("Crop")) {
            scaleType = PrintItem.ScaleType.CROP;
        } else if (scaleTypeString.equals("Center Top-Left")) {
            scaleType = PrintItem.ScaleType.CENTER_TOP_LEFT;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void buttonClicked(View v) {
        Button button = (Button) v;

        String buttonText = ((Button) v).getText().toString();

        //Create the image assets
        ImageAsset imageAsset4x5l = new ImageAsset(filename4x5l, ImageAsset.MeasurementUnits.INCHES, 5, 4);
        ImageAsset imageAsset4x5p = new ImageAsset(filename4x5p, ImageAsset.MeasurementUnits.INCHES, 4, 5);

        ImageAsset imageAsset4x6 = new ImageAsset(filename4x6, ImageAsset.MeasurementUnits.INCHES, 4, 6);
        ImageAsset imageAsset5x7 = new ImageAsset(filename5x7, ImageAsset.MeasurementUnits.INCHES, 5, 7);

        //this is the minimum constructor to create an image print item
        PrintItem printItemDefault = new ImagePrintItem(scaleType, imageAsset5x7);
        PrintJob printJob = new PrintJob(this, printItemDefault);

        printJob.setJobName("Example");
        PrintAttributes.MediaSize mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "android", 5000, 7000);

        PrintItem printItem4x5 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, scaleType, imageAsset4x5p);
        PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER, scaleType, imageAsset4x5p);
        PrintItem printItem85x11l = new ImagePrintItem(new PrintAttributes.MediaSize(printItem85x11.getMediaSize().getId(), "android", printItem85x11.getMediaSize().getHeightMils(), printItem85x11.getMediaSize().getWidthMils()), scaleType, imageAsset4x5l);

        PrintItem printItem5x7 = new ImagePrintItem(mediaSize5x7, scaleType, imageAsset5x7);

        printJob.addPrintItem(printItem4x5);
        printJob.addPrintItem(printItem85x11);
        printJob.addPrintItem(printItem85x11l);
        printJob.addPrintItem(printItem5x7);

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(printItem85x11.getMediaSize())
                .build();
        printJob.setPrintDialogOptions(printAttributes);

        PrintUtil.setPrintJob(printJob);
        PrintUtil.print(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public String generateBitmap(ImageView imageView, String prefix) {

        imageView.buildDrawingCache();
        Bitmap printableBitmap = imageView.getDrawingCache();
        return savePrintableImage(printableBitmap, prefix);
    }

    private String savePrintableImage(Bitmap photo, String prefix) {

        String imageURI = null;

        FileOutputStream out;
        try {
            File imageFile = createImageFile(getApplicationContext(), prefix);
            if (imageFile != null) {
                imageURI = imageFile.getAbsolutePath();
                out = new FileOutputStream(imageURI);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.i("Filename", imageURI + "");
        return imageURI;

    }

    public static File createImageFile(Context context, String imageSize) throws IOException {

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File path = new File(directory, imageSize + ".jpg");

        return path;
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
}
