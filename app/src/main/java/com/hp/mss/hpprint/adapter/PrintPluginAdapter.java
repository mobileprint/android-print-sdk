package com.hp.mss.hpprint.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hp.mss.hpprint.R;

public class PrintPluginAdapter extends BaseAdapter {
    private static final String TOOLBAR_TITLE_FONTFACE = "fonts/HPSimplified_Rg.ttf";

    Context context;

    private String[] names;
    private String[] makers;
    private int[] icons;
    private int[] status;

    private LayoutInflater inflater;

    public PrintPluginAdapter(Context context, int[] iconResources, String[] pluginNames, String[] pluginMakers, int[] status) {
        this.context = context;
        this.icons = iconResources;
        this.names = pluginNames;
        this.makers = pluginMakers;
        this.status = status;

        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        View row;

        row = inflater.inflate(R.layout.item_print_plugin_service, null);

        TextView nameView, makerView;
        ImageView iconImageView, statusImageView;

        nameView = (TextView) row.findViewById(R.id.plugin_name);
        nameView.setText(names[i]);
        nameView.setTextColor(Color.rgb(51,51,51));

        makerView = (TextView) row.findViewById(R.id.plugin_maker_name);
        makerView.setText(makers[i]);
        makerView.setTextColor(Color.rgb(149, 149, 149));


        iconImageView = (ImageView) row.findViewById(R.id.print_service_plugin_id);
        iconImageView.setBackground(context.getDrawable(icons[i]));

        statusImageView = (ImageView) row.findViewById(R.id.plugin_state);
        statusImageView.setBackground(context.getDrawable(status[i]));


        return row;
    }

}
