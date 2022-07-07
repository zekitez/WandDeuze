package com.zekitez.wanddeuze;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.preference.PreferenceManager;

public class AcceptDeclineDialog {

    private final Dialog dialog;
    private final Activity activity;
    private final int layoutResID;

    public AcceptDeclineDialog(Activity activity, int layoutResID, String message) {

        this.activity = activity;
        this.layoutResID = layoutResID;

        dialog = new Dialog(activity, R.style.NoTitleBar);
        dialog.setContentView(layoutResID);
        dialog.setCancelable(false);

        TextView textviewMessage = dialog.findViewById(R.id.textViewMessage);
        textviewMessage.setText(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT));

        Button button = dialog.findViewById(R.id.buttonAccept);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPref(true);
                dialog.dismiss();
            }
        });

        button = dialog.findViewById(R.id.buttonDecline);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPref(false);
                dialog.dismiss();
                activity.finish();
            }
        });

        dialog.show();
    }

    private void setPref(boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        if (layoutResID == R.layout.privacy_policy) {
            editor.putBoolean(activity.getResources().getString(R.string.key_privacyPolycyAccepted), value);
        } else if (layoutResID == R.layout.disclaimer) {
            editor.putBoolean(activity.getResources().getString(R.string.key_disclaimerAccepted), value);
        }
        editor.commit();
    }

}
