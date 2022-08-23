package com.zekitez.wanddeuze;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class PrefsFragment extends PreferenceFragmentCompat {

    private final String TAG = "PrefsFragment";

    private final PrefsLanguageListener callback;

    private PreferenceCategory prefCatLoginAccount;
    private EditTextPreference editTxtPrefUserName;
    private EditTextPreference editTxtPrefPassword;

    private PreferenceCategory prefCatCharger;
    private EditTextPreference editTxtPrefChargerId;
    private EditTextPreference editTxtPrefConnectionTimeOut;
    private CheckBoxPreference editTxtPrefDisplayNbrChargerStats;

    private PreferenceCategory prefCatSettings;
    private ListPreference listPrefLanguage;
    private CheckBoxPreference checkBoxPreferenceLogToFile;

    private Context context;
    public PrefsFragment(PrefsLanguageListener callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager prefManager = getPreferenceManager();
        
        addPreferencesFromResource(R.xml.preferences);

        prefCatLoginAccount = prefManager.findPreference(getString(R.string.key_loginAccount));
        editTxtPrefUserName = prefManager.findPreference(getString(R.string.key_username));
        editTxtPrefPassword = prefManager.findPreference(getString(R.string.key_password));
        assert editTxtPrefPassword != null;
        // Input only numbers. Cannot be set in preferences.xml so do it here !
        editTxtPrefPassword.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD ));

        prefCatCharger = prefManager.findPreference(getString(R.string.key_charger));
        editTxtPrefChargerId = prefManager.findPreference(getString(R.string.key_chargerId));
        assert editTxtPrefChargerId != null;
        // Input only numbers. Cannot be set in preferences.xml so do it here !
        editTxtPrefChargerId.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER ));
        editTxtPrefConnectionTimeOut = prefManager.findPreference(getString(R.string.key_connectionTimeOut));
        assert editTxtPrefConnectionTimeOut != null;
        // Input only numbers. Cannot be set in preferences.xml so do it here !
        editTxtPrefConnectionTimeOut.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER ));
        editTxtPrefDisplayNbrChargerStats = prefManager.findPreference(getString(R.string.key_displayNbrChargerStats));
        
        prefCatSettings = prefManager.findPreference(getString(R.string.key_settings));
        checkBoxPreferenceLogToFile = prefManager.findPreference(getString(R.string.key_logToFile));
        listPrefLanguage = prefManager.findPreference(getString(R.string.key_displayLanguage));
        assert listPrefLanguage != null;
        // When the language changes update the all text fields.

        listPrefLanguage.setOnPreferenceChangeListener((preference, newVal) -> {
            LogThis.d(TAG, "onPreferenceChange language: " + newVal.toString());
            Configuration config = new Configuration(requireContext().getResources().getConfiguration());
            config.setLocale(new Locale(newVal.toString()));
            context = requireContext().createConfigurationContext(config);

            callback.languageChangedListener(context);  // Update the actionbar title using a callback.

            // String result = getContext().createConfigurationContext(config).getText(R.string.language).toString();
            prefCatLoginAccount.setTitle(context.getText(R.string.login_account));
            editTxtPrefUserName.setTitle(context.getText(R.string.username));
            editTxtPrefPassword.setTitle(context.getText(R.string.password));

            prefCatCharger.setTitle(context.getText(R.string.charger));
            editTxtPrefChargerId.setTitle(context.getText(R.string.chargerid));
            editTxtPrefConnectionTimeOut.setTitle(context.getText(R.string.connecttimeout));
            editTxtPrefDisplayNbrChargerStats.setTitle(context.getText(R.string.nbrReceivedChargerStatus));

            prefCatSettings.setTitle(context.getText(R.string.settings));
            listPrefLanguage.setTitle(context.getText(R.string.language));
            checkBoxPreferenceLogToFile.setTitle(context.getText(R.string.log_to_file));

            return true;
        });
    }

}
