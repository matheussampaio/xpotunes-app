package com.xpotunes.screen.settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.xpotunes.R;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceScreen;

import java.util.Locale;

@PreferenceScreen(R.xml.preferences)
@EFragment
public class SettingsFragment extends PreferenceFragment {

    public static String PREF_NAME = "XPOTunesSharedPref";

    @PreferenceByKey(R.string.prefPlayingTimeKey)
    EditTextPreference mPreferencePlayingTime;

    @PreferenceByKey(R.string.prefRandomTimeKey)
    SwitchPreference mPreferenceRandomTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(PREF_NAME);
    }

    @AfterPreferences
    void initPrefs() {
        mPreferencePlayingTime.setEnabled(!mPreferenceRandomTime.isChecked());
    }

    @PreferenceChange(R.string.prefRandomTimeKey)
    void preferenceChangeRandomTime(boolean newValue) {
        mPreferencePlayingTime.setEnabled(!newValue);
    }

    @PreferenceChange(R.string.prefLanguageKey)
    void preferenceChangeLanguage(String language) {
        Locale locale;

        if (language.equals("pt-rBR")) {
            locale = new Locale("pt", "BR");
        } else {
            locale = new Locale(language);
        }

        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.locale = locale;

        getActivity().getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Toast.makeText(getActivity(), R.string.restart_app_language, Toast.LENGTH_LONG).show();
    }

}