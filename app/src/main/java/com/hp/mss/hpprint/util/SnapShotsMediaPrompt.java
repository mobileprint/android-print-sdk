/*
 * Hewlett-Packard Company
 * All rights reserved.
 *
 * This file, its contents, concepts, methods, behavior, and operation
 * (collectively the "Software") are protected by trade secret, patent,
 * and copyright laws. The use of the Software is governed by a license
 * agreement. Disclosure of the Software to third parties, in any form,
 * in whole or in part, is expressly prohibited except as authorized by
 * the license agreement.
 */

package com.hp.mss.hpprint.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hp.mss.hpprint.R;

/**
 * This informs the user about 4x5 media and the steps necessary to print.
 */
public class SnapShotsMediaPrompt {

    /**
     * This allows us to track user interaction with the prompt.
     */
    public interface SnapShotsPromptListener {
        public void SnapShotsPromptOk();
        public void SnapShotsPromptCancel();
    }

    private static final String SHOW_SNAP_SHOTS_MESSAGE_KEY = "com.hp.mss.hpprint.ShowSnapShotsMessage";

    public static void displaySnapShotsPrompt(final Context context, final SnapShotsPromptListener promptListener) {
        String header = context.getString(R.string.snap_shots_prompt_header);
        String message = context.getString(R.string.snap_shots_prompt_message);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        boolean showSnapShotsPrompt = preferences.getBoolean(SHOW_SNAP_SHOTS_MESSAGE_KEY, true);
        if (!showSnapShotsPrompt) {
            if (promptListener != null) {
                promptListener.SnapShotsPromptOk();
            }
            return;
        }

        View checkBoxView = View.inflate(context, R.layout.checkbox, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        if (Build.VERSION.SDK_INT <  Build.VERSION_CODES.LOLLIPOP) {
            int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
            checkBox.setButtonDrawable(id);
        }
        checkBox.setText(R.string.snap_shots_prompt_do_not_show);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(header)
                .setView(checkBoxView)
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (promptListener != null)
                            promptListener.SnapShotsPromptCancel();
                    }
                })
                .setPositiveButton(R.string.snap_shots_prompt_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkBox.isChecked()) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                            preferences.edit().putBoolean(SHOW_SNAP_SHOTS_MESSAGE_KEY, false).commit();
                        }
                        if (promptListener != null)
                            promptListener.SnapShotsPromptOk();
                    }
                })
                .create()
                .show();

    }
}
