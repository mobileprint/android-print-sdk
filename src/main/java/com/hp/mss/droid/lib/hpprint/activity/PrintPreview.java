package com.hp.mss.droid.lib.hpprint.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.hp.mss.droid.lib.hpprint.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Copyright 2015 Hewlett-Packard, Co.
 */

public class PrintPreview extends Activity{

    String photoFileName = null;
    Bitmap photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        String photoFileName = (String) getIntent().getExtras().get("photoFileUri");
        photo = getImageBitmap(this, photoFileName);
    }


    private void performPrint() {

//        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
//        String jobName = this.getString(R.string.app_name);
//        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(this, photo);
//        printManager.print(jobName, adapter, null );

        PrintHelper printHelper = new PrintHelper(this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        printHelper.printBitmap("Print Photo", photo);

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
            performPrint();
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
        if ( photoFileName != null ) {
            File photoFile = new File(photoFileName);
            if ( photoFile.exists() )
                photoFile.deleteOnExit();
        }
    }


}
