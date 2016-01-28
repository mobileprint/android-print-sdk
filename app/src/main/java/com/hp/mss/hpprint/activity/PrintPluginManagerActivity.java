package com.hp.mss.hpprint.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.adapter.PrintPluginAdapter;

public class PrintPluginManagerActivity extends AppCompatActivity {
    private static final String TAG = "PRINT_PLUGIN_MANGER_ACTIVITY";

    // customize your toolbar here
    private static final int TOOLBAR_BACKGROUND_COLOR = R.color.PluginToolbarBKColor;
    private static final int TOOLBAR__TITLE_TEXT_COLOR = R.color.HPFontColorWhite;

    public static int[] plugin_icons = new int[]{
            R.drawable.hp,
            R.drawable.canon,
            R.drawable.epson,
            R.drawable.brother,
            R.drawable.mopria,
            R.drawable.other
    };

    public static int[] plugin_status = new int[]{
            R.drawable.downloading_arrow,
            R.drawable.downloading_arrow,
            R.drawable.downloading_arrow,
            R.drawable.downloading_arrow,
            R.drawable.downloading_arrow,
            R.drawable.downloading_arrow
    };

    public String[] pluginNames;
    public String[] pluginMakers;

    private Toolbar topToolBar;
    private ListView pluginListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_plugin_manger);

        Resources res = getResources();

        // Toolbar setup
        topToolBar = (Toolbar) findViewById(R.id.plugin_manager_toolbar);
        topToolBar.setTitleTextColor(res.getColor(TOOLBAR__TITLE_TEXT_COLOR));
        topToolBar.setBackgroundColor(res.getColor(R.color.PluginToolbarBKColor));

        setSupportActionBar(topToolBar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(res.getString(R.string.plugin_manager_title));

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.x);

        // plugin list view
        pluginNames = getResources().getStringArray(R.array.plugin_names);
        pluginMakers = getResources().getStringArray(R.array.plugin_makers);

        ListView pluginListView = (ListView) findViewById(R.id.plugin_manager_list_view);
        pluginListView.setAdapter(new PrintPluginAdapter(this, plugin_icons, pluginNames, pluginMakers, plugin_status));
        pluginListView.smoothScrollByOffset(1);
        pluginListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "item " + i + "clicked", Toast.LENGTH_LONG).show();
            }
        });

        // Continue to print action
        Button printBtn = (Button) findViewById(R.id.print_btn);
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "button clicked", Toast.LENGTH_LONG).show();
            }
        });

    }


}
