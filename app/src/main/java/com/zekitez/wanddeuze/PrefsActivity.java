package com.zekitez.wanddeuze;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;
import androidx.appcompat.app.AppCompatActivity;

public class PrefsActivity extends AppCompatActivity {

    private final String TAG = "PrefsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_activity);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.preferences);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (findViewById(R.id.idFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.idFrameLayout, new PrefsFragment()).commit();
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
}
