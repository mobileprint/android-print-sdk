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
import com.hp.mss.hpprint.model.PrintPlugin;

public class PrintPluginAdapter extends BaseAdapter {
    private static final String TOOLBAR_TITLE_FONTFACE = "fonts/HPSimplified_Rg.ttf";

    Context context;
    PrintPlugin[] plugins;

    private LayoutInflater inflater;

    /**
     *
     * @param context calling activity
     * @param plugins plugin list that is managed by this adapter
     */
    public PrintPluginAdapter(Context context, PrintPlugin[] plugins) {
        this.context = context;
        this.plugins = plugins;

        this.inflater = LayoutInflater.from(context);
    }

    /**
     *
     * @return counter of the items
     */
    @Override
    public int getCount() {
        return plugins.length;
    }

    /**
     *
     * @param i item index
     * @return  the object at index i
     */
    @Override
    public Object getItem(int i) {
        return plugins[i];
    }

    /**
     *
     * @param position item index
     * @return item id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @param i item position
     * @param view the view to reused
     * @param parent the parent that this view eventually be attached to
     * @return a new view that will be displayed in the list
     */
    @Override
    public View getView(int i, View view, ViewGroup parent) {

        View row;

        row = inflater.inflate(R.layout.item_print_plugin_service, null);

        TextView nameView, makerView, statusImageView;
        ImageView iconImageView;

        nameView = (TextView) row.findViewById(R.id.plugin_name);
        nameView.setText( ((PrintPlugin)plugins[i]).getName() );

        makerView = (TextView) row.findViewById(R.id.plugin_maker_name);
        makerView.setText( ((PrintPlugin)plugins[i]).getMaker() );

        iconImageView = (ImageView) row.findViewById(R.id.print_service_plugin_id);
        iconImageView.setBackgroundResource( ((PrintPlugin)plugins[i]).getIcon() );

        statusImageView = (TextView) row.findViewById(R.id.plugin_state);
        statusImageView.setBackgroundResource(((PrintPlugin) plugins[i]).getNextActionIcon());

        if( ((PrintPlugin) plugins[i]).getStatus().equals(PrintPlugin.PluginStatus.DISABLED) )
            statusImageView.setText(R.string.disabled);
        else if ( ((PrintPlugin) plugins[i]).getStatus().equals(PrintPlugin.PluginStatus.READY) )
            statusImageView.setText(R.string.enabled);

        return row;
    }

}
