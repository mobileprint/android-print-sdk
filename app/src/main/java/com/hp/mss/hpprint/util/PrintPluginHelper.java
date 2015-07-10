package com.hp.mss.hpprint.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.hp.mss.hpprint.R;

/**
 * Created by panini on 7/6/15.
 */
public class PrintPluginHelper {

    public interface PluginHelperListener {
        public void printPluginHelperSkippedByPreference();
        public void printPluginHelperSkipped();
        public void printPluginHelperSelected();
        public void printPluginHelperCanceled();
    }
    private PluginHelperListener pluginHelperListener;

    private static final String SHOW_PLUGIN_HELPER_KEY = "com.hp.mss.hpprint.ShowPluginHelper";

    public static void showPluginHelper(final Activity activity,final PluginHelperListener pluginHelperListener) {
        String header = activity.getString(R.string.hp_print_helper_header);
        String message = activity.getString(R.string.install_print_plugin_msg);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        boolean showPluginInstallMessage = preferences.getBoolean(SHOW_PLUGIN_HELPER_KEY, true);
        if (!showPluginInstallMessage) {
            if (pluginHelperListener != null) {
                pluginHelperListener.printPluginHelperSkippedByPreference();
            }
            return;
        }
        View checkBoxView = View.inflate(activity, R.layout.checkbox, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setText("Do not show again.");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog pluginDialog = builder.setMessage(message)
                .setTitle(header)
                .setView(checkBoxView)
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (pluginHelperListener != null)
                            pluginHelperListener.printPluginHelperCanceled();
                    }
                })
                .setPositiveButton("Get a Print Service", null)
                .setNeutralButton("I have one", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkBox.isChecked()) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
                            preferences.edit().putBoolean(SHOW_PLUGIN_HELPER_KEY, false).commit();
                        }
                        if (pluginHelperListener != null)
                            pluginHelperListener.printPluginHelperSkipped();
                    }
                })
                .create();
//        This is to prevent the dialog from closing when user goes to play store
        pluginDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = pluginDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkBox.isChecked()) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
                            preferences.edit().putBoolean(SHOW_PLUGIN_HELPER_KEY, false).commit();
                        }
                        if (pluginHelperListener != null)
                            pluginHelperListener.printPluginHelperSelected();
                    }
                });

            }
        });
        pluginDialog.show();

    }


}
