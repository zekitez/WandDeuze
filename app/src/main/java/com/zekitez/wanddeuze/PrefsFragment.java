package com.zekitez.wanddeuze;

import android.os.Bundle;
import android.text.InputType;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class PrefsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        // Input only numbers. Cannot be set in preferences.xml so do it here !
        EditTextPreference editTextPreference = getPreferenceManager().findPreference(getString(R.string.key_chargerId));
        assert editTextPreference != null;
        editTextPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER ));

        editTextPreference = getPreferenceManager().findPreference(getString(R.string.key_connectionTimeOut));
        assert editTextPreference != null;
        editTextPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER ));

        editTextPreference = getPreferenceManager().findPreference(getString(R.string.key_password));
        assert editTextPreference != null;
        editTextPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD ));

    }

}
