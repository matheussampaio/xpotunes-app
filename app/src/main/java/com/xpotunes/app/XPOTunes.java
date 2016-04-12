package com.xpotunes.app;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import org.androidannotations.annotations.EApplication;

@EApplication
public class XPOTunes extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
    }
}
