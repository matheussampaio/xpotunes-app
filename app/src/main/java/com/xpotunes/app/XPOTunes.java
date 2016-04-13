package com.xpotunes.app;

import android.app.Application;
import android.content.res.Configuration;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.xpotunes.utils.XPOTunesSharedPref_;

import net.danlew.android.joda.JodaTimeAndroid;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Locale;

@EApplication
public class XPOTunes extends Application {

    @Pref
    XPOTunesSharedPref_ mXPOTunesSharedPref;

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);

        Iconify.with(new FontAwesomeModule());
    }

    private void initLocale() {
        String language = mXPOTunesSharedPref.language().getOr("en");

        Locale locale;

        if (language.equals("pt-rBR")) {
            locale = new Locale("pt", "BR");
        } else {
            locale = new Locale(language);
        }

        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.locale = locale;

        getApplicationContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}
