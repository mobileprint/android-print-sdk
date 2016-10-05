package com.hp.mss.hpprint.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.adapter.PrintPluginAdapter;
import com.hp.mss.hpprint.model.PrintPlugin;
import com.hp.mss.hpprint.util.EventMetricsCollector;
import com.hp.mss.hpprint.util.PrintPluginStatusHelper;
import com.hp.mss.hpprint.util.PrintUtil;

public class PrintPluginManagerActivity extends AppCompatActivity {
    private static final String TAG = "PRINT_PLUGIN_MANGER_ACTIVITY";

    private Toolbar topToolBar;
    private ListView pluginListView;
    private PrintPluginStatusHelper printPluginStatusHelper;
    private PrintPluginAdapter printPluginAdapter;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    protected static boolean isVisible = false;
    protected static boolean newPackageInstalled = false;
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
                if(plugin.getStatus().equals(PrintPlugin.PluginStatus.READY)) {
                    startActivity(new Intent(Settings.ACTION_PRINT_SETTINGS));
                } else if(printPluginStatusHelper != null && printPluginStatusHelper.showBeforeEnableDialog(plugin) ) {
                    displayEnableTipsDialog();
                } else if ( printPluginStatusHelper.goToGoogleStore(plugin)) {
                    plugin.goToPlayStoreForPlugin();
                    EventMetricsCollector.postMetricsToHPServer(
                            thisActivity,
                            EventMetricsCollector.PrintFlowEventTypes.SENT_TO_GOOGLE_PLAY_STORE);
                }
            }
        });

        // Continue to print action
        printBtn = (Button) findViewById(R.id.print_btn);
        View listDivider = findViewById(R.id.list_divider);
        if (!PrintUtil.hasPrintJob()) {
            printBtn.setVisibility(View.GONE);
            listDivider.setVisibility(View.GONE);
        } else {
            printBtn.setVisibility(View.VISIBLE);
            listDivider.setVisibility(View.VISIBLE);
            printBtn.setText(readyToPrint() ? R.string.continue_to_print : R.string.skip);
            printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PrintUtil.readyToPrint(thisActivity);
                }
            });
        }

        // Receiver for broadcast message
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String packageName = intent.getData().getEncodedSchemeSpecificPart();
                if( isAPluginInstalled(packageName) ) {
                    if(isVisible) {
                        newPluginInstalledHandler();
                    } else {
                        newPackageInstalled = true;
                    }
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        EventMetricsCollector.postMetricsToHPServer(
                this,
                EventMetricsCollector.PrintFlowEventTypes.OPENED_PLUGIN_HELPER);
        newPluginInstalledHandler();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        isVisible = false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
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
        return printPluginStatusHelper.getSortedPlugins();
    }

    private boolean readyToPrint() {
        return printPluginStatusHelper.readyToPrint();
    }

    private void displayEnableTipsDialog() {

        final Activity thisActivity = this;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_before_enable_tips);


        Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_PRINT_SETTINGS));
                dialog.dismiss();
                EventMetricsCollector.postMetricsToHPServer(
                        thisActivity,
                        EventMetricsCollector.PrintFlowEventTypes.SENT_TO_PRINT_SETTING);
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

    private boolean isAPluginInstalled(String packageName) {
        boolean isInstalled = false;

        for(int i = 0; i < PrintPluginStatusHelper.packageNames.length; i++) {
            if( packageName.equals(PrintPluginStatusHelper.packageNames[i]) ) {
                    isInstalled = true;
            }

        }
        return isInstalled;
    }

    private void newPluginInstalledHandler() {

        printPluginStatusHelper.updateAllPrintPluginStatus();
        PrintPlugin[] sortedList = getprintPluginList();
        printPluginAdapter = new PrintPluginAdapter(this, sortedList);
        pluginListView.setAdapter(printPluginAdapter);

        printBtn.setText(readyToPrint() ? R.string.continue_to_print : R.string.skip);
        if(newPackageInstalled) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                displayEnableTipsDialog();
            }
            newPackageInstalled = false;
        }

    }
}
