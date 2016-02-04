package com.hp.mss.hpprint.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.adapter.PrintPluginAdapter;
import com.hp.mss.hpprint.model.PrintPlugin;
import com.hp.mss.hpprint.util.PrintPluginStatusHelper;
import com.hp.mss.hpprint.util.PrintUtil;

import java.util.Collection;

public class PrintPluginManagerActivity extends AppCompatActivity {
    private static final String TAG = "PRINT_PLUGIN_MANGER_ACTIVITY";

    private Toolbar topToolBar;
    private ListView pluginListView;
    private PrintPluginStatusHelper printPluginStatusHelper;
    private PrintPluginAdapter printPluginAdapter;
    private BroadcastReceiver receiver;
    Button printBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity thisActivity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_plugin_manger);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if  (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.x);
            actionBar.setElevation(2);
        }


        pluginListView = (ListView) findViewById(R.id.plugin_manager_list_view);
        printPluginStatusHelper = PrintPluginStatusHelper.getInstance(this);
        printPluginAdapter = new PrintPluginAdapter(this, getprintPluginList());
        pluginListView.setAdapter(printPluginAdapter);

        pluginListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PrintPlugin plugin = (PrintPlugin) printPluginAdapter.getItem(i);
                if(printPluginStatusHelper !=null && printPluginStatusHelper.showBeforeDownloadDialog(plugin) ) {
                    displayDownloadTipsDialog(plugin);
                } else if ( plugin.getStatus() == PrintPlugin.PluginStatus.DISABLED) {
                    startActivity(new Intent(Settings.ACTION_PRINT_SETTINGS));
                }
            }
        });

        // Continue to print action
        printBtn = (Button) findViewById(R.id.print_btn);
        printBtn.setText(readyToPrint() ? R.string.continue_to_print : R.string.skip);
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String viewText = ((Button) view).getText().toString();
                if (viewText.equals(getResources().getString(R.string.continue_to_print))) {
                    PrintUtil.readyToPrint(thisActivity);
                } else {
                    finish();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        printPluginStatusHelper.updateAllPrintPluginStatus();
        printPluginAdapter = new PrintPluginAdapter(this, getprintPluginList());
        pluginListView.setAdapter(printPluginAdapter);

        printBtn.setText(readyToPrint() ? R.string.continue_to_print : R.string.skip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_print_plugin_manager, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_help) {
            Intent intent = new Intent(this, PrintServicePluginInformation.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private  PrintPlugin[] getprintPluginList() {
        if(printPluginStatusHelper == null)
            return null;
        return printPluginStatusHelper.getPluginsSortedByStatus();
    }

    private boolean readyToPrint() {
        return printPluginStatusHelper.readyToPrint();
    }

    private void displayDownloadTipsDialog(PrintPlugin printPlugin) {
        final PrintPlugin plugin = printPlugin;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_before_download_tips);


        Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                plugin.goToPlayStoreForPlugin();
            }
        });

        Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
