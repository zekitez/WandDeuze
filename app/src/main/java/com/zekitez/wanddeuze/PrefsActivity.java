package com.zekitez.wanddeuze;

import android.os.Bundle;
import java.util.Objects;
import androidx.appcompat.app.AppCompatActivity;

public class PrefsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_activity);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.preferences);
        if (findViewById(R.id.idFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.idFrameLayout, new PrefsFragment()).commit();
        }
    }

}
