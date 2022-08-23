package com.zekitez.wanddeuze;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PrefsActivity extends AppCompatActivity  implements PrefsLanguageListener{

    private final String TAG = "PrefsActivity";
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_activity);
        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(R.string.preferences);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (findViewById(R.id.idFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.idFrameLayout, new PrefsFragment(this) ).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == android.R.id.home ) {
            LogThis.d(TAG, "HOME " + android.R.id.home);
            finish();
        }
        return (super.onOptionsItemSelected(item));
	}

    @Override
    public void languageChangedListener(Context context) {
            runOnUiThread(() -> actionBar.setTitle(context.getText(R.string.preferences)));
    }
}

