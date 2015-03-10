//
// Hewlett-Packard Company
// All rights reserved.
//
// This file, its contents, concepts, methods, behavior, and operation
// (collectively the "Software") are protected by trade secret, patent,
// and copyright laws. The use of the Software is governed by a license
// agreement. Disclosure of the Software to third parties, in any form,
// in whole or in part, is expressly prohibited except as authorized by
// the license agreement.
//

package com.hp.mss.droid.lib.hpprint.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hp.mss.droid.lib.hpprint.R;

public class PrintPluginInstaller {

    public interface OnInstallPluginListener {
        public void printPluginInstallationSkipped();
        public void printPluginInstallationSelected();
        public void printPluginEnableSkipped();
    }
    private OnInstallPluginListener installPluginListener;

    private Activity activity = null;

    private final String SHOW_PLUGIN_INSTALL_MESSAGE_KEY = "com.hp.mss.droid.lib.hpprint.ShowPluginInstallMessage";
    private final String SHOW_PLUGIN_ENABLE_MESSAGE_KEY = "com.hp.mss.droid.lib.hpprint.ShowPluginEnableMessage";
    public static final int ENABLE_PRINT_PLUGIN = 10;


    public PrintPluginInstaller(Activity activity, OnInstallPluginListener listener) {
        installPluginListener = listener;
        this.activity = activity;
    }


    public void install() {
        String header = activity.getString(R.string.hp_print_plugin_alert_header);
        String message = activity.getString(R.string.install_hp_print_plugin_msg);
        showInstallMessageDialog(activity, header, message);
    }


    public void enable() {
        String header = activity.getString(R.string.hp_print_plugin_alert_header);
        String message = activity.getString(R.string.enable_hp_print_plugin_msg);
        showEnableMessageDialog(activity, header, message);
    }

    private void showInstallMessageDialog(final Activity activity, String header, String message) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        boolean showPluginInstallMessage = preferences.getBoolean(SHOW_PLUGIN_INSTALL_MESSAGE_KEY, true);
        if (!showPluginInstallMessage) {
            if (installPluginListener != null) {
                installPluginListener.printPluginInstallationSkipped();
            }
            return;
        }

        View checkBoxView = View.inflate(activity, R.layout.checkbox, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
                preferences.edit().putBoolean(SHOW_PLUGIN_INSTALL_MESSAGE_KEY, false).commit();
            }
        });
        checkBox.setText("Do not show again.");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(header)
                .setView(checkBoxView)
                .setCancelable(false)
                .setPositiveButton("Install", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dispatchInstallPluginIntent(activity, PrintUtil.HP_PRINT_PLUGIN_PACKAGE_NAME);
                        if (installPluginListener != null)
                            installPluginListener.printPluginInstallationSelected();
                    }
                })
                .setNeutralButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (installPluginListener != null)
                            installPluginListener.printPluginInstallationSkipped();
                    }
                })
                .create()
                .show();

    }


    private void showEnableMessageDialog (final Activity activity, String title, String message) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        boolean showPluginEnableMessage = preferences.getBoolean(SHOW_PLUGIN_ENABLE_MESSAGE_KEY, true);
        if (!showPluginEnableMessage) {
            if (installPluginListener != null) {
                installPluginListener.printPluginEnableSkipped();
            }
            return;
        }

        View checkBoxView = View.inflate(activity, R.layout.checkbox, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
                preferences.edit().putBoolean(SHOW_PLUGIN_ENABLE_MESSAGE_KEY, false).commit();
            }
        });
        checkBox.setText("Do not show again.");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(title)
                .setView(checkBoxView)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivityForResult(new Intent(Settings.ACTION_PRINT_SETTINGS), ENABLE_PRINT_PLUGIN);
                    }
                })
                .setNeutralButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (installPluginListener != null)
                            installPluginListener.printPluginEnableSkipped();
                    }
                })
                .create()
                .show();
    }

    public void dispatchInstallPluginIntent(Activity activity, String packageName) {

        String url = null;
        if (PrintUtil.checkGooglePlayStoreStatus(activity) != PrintUtil.PackageStatus.NOT_INSTALLED) {
            url = "market://details?id=" + packageName;
        } else {
            url = "https://play.google.com/store/apps/details?id=" + packageName;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
