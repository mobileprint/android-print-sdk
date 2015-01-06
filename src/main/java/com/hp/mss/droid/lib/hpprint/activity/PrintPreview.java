package com.hp.mss.droid.lib.hpprint.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hp.mss.droid.lib.hpprint.R;
import com.hp.mss.droid.lib.hpprint.adapter.PhotoPrintDocumentAdapter;

/**
 * Copyright 2015 Hewlett-Packard, Co.
 */

public class PrintPreview extends Activity {

    Bitmap photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        photo = (Bitmap) getIntent().getExtras().get("bitmap");
        setupPrintButton();
    }

    private void setupPrintButton() {
        Button printButton = (Button)findViewById(R.id.button_print);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPrint();
            }
        });
    }

    private void performPrint() {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = this.getString(R.string.app_name);
        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(this, photo);
        printManager.print(jobName, adapter, null );
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
