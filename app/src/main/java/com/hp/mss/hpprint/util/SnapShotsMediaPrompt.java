package com.hp.mss.hpprint.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private SnapShotsPromptListener promptListener;

    private Activity activity = null;

    private final String SHOW_SNAP_SHOTS_MESSAGE_KEY = "com.hp.mss.hpprint.ShowSnapShotsMessage";

    public SnapShotsMediaPrompt(Activity activity) {
        promptListener = (SnapShotsPromptListener)activity;
        this.activity = activity;
    }

    public void displaySnapShotsPrompt() {
        String header = activity.getString(R.string.snap_shots_prompt_header);
        String message = activity.getString(R.string.snap_shots_prompt_message_kitkat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            message = activity.getString(R.string.snap_shots_prompt_message_lollipop);
        }
        showSnapShotsPrompt(activity, header, message);
    }

    private void showSnapShotsPrompt(final Activity activity, String header, String message) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        boolean showSnapShotsPrompt = preferences.getBoolean(SHOW_SNAP_SHOTS_MESSAGE_KEY, true);
        if (!showSnapShotsPrompt) {
            if (promptListener != null) {
                promptListener.SnapShotsPromptOk();
            }
            return;
        }

        View checkBoxView = View.inflate(activity, R.layout.checkbox, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
                preferences.edit().putBoolean(SHOW_SNAP_SHOTS_MESSAGE_KEY, false).commit();
            }
        });
        checkBox.setText("Do not show again.");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(header)
                .setView(checkBoxView)
                .setCancelable(true)
                .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
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
