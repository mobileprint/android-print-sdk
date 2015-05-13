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

public class SnapShotsMediaPrompt {

    public interface SnapShotsPromptListener {
        public void SnapShotsPromptOk();
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
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
        checkBox.setButtonDrawable(id);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                preferences.edit().putBoolean(SHOW_SNAP_SHOTS_MESSAGE_KEY, !isChecked).commit();
            }
        });
        checkBox.setText(R.string.snap_shots_prompt_do_not_show);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(header)
                .setView(checkBoxView)
                .setCancelable(true)
                .setPositiveButton(R.string.snap_shots_prompt_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (promptListener != null)
                            promptListener.SnapShotsPromptOk();
                    }
                })
                .create()
                .show();

    }
}
