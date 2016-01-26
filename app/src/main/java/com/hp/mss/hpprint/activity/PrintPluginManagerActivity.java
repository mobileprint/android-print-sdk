package com.hp.mss.hpprint.activity;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.adapter.PrintPluginAdapter;

public class PrintPluginManagerActivity extends AppCompatActivity {
    private static final String TAG = "PRINT_PLUGIN_MANGER_ACTIVITY";

    // customize your toolbar here
    private static final int TOOLBAR_BACKGROUND_COLOR = R.color.HPFontColorBlue;
    private static final int TOOLBAR__TITLE_TEXT_COLOR = R.color.HPFontColorWhite;
    private static final String TOOLBAR_TITLE_FONTFACE = "fonts/HPSimplified_Rg.ttf";

    public static int[] plugin_icons = new int[]{
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_plugin_manger);

        Resources res = getResources();

        Toolbar topToolBar = (Toolbar) findViewById(R.id.plugin_manager_toolbar);
        topToolBar.setTitleTextColor(res.getColor(TOOLBAR__TITLE_TEXT_COLOR));
        topToolBar.setBackgroundColor(res.getColor(TOOLBAR_BACKGROUND_COLOR));

        Typeface typeface = Typeface.createFromAsset(getAssets(), TOOLBAR_TITLE_FONTFACE);
        setToolbarFontface(topToolBar, typeface, res.getString(R.string.plugin_manager_title));

        setSupportActionBar(topToolBar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(res.getString(R.string.plugin_manager_title));

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.x);

        TextView textView = (TextView) findViewById(R.id.plugin_manager_sub_toolbar);
        textView.setTypeface(typeface);

        pluginNames = getResources().getStringArray(R.array.plugin_names);
        pluginMakers = getResources().getStringArray(R.array.plugin_makers);

        ListView pluginListView = (ListView) findViewById(R.id.plugin_manager_list_view);
        pluginListView.setAdapter(new PrintPluginAdapter(this, plugin_icons, pluginNames, pluginMakers, plugin_status));
        pluginListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "item " + i + "clicked", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setToolbarFontface(Toolbar toolbar, Typeface typeface, CharSequence title) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(title)) {
                    tv.setTypeface(typeface);
//                    tv.setTextSize(TOOLBAR_TITLE_TEXT_SIZE_PX);
                    break;
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }
}
