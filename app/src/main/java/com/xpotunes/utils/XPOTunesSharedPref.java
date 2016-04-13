package com.xpotunes.utils;

import com.xpotunes.R;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.UNIQUE)
public interface XPOTunesSharedPref {

    @DefaultBoolean(value = true, keyRes = R.string.prefRandomTimeKey)
    boolean randomTime();

    @DefaultInt(value = 15, keyRes = R.string.prefMinimumTimeKey)
    int minimumTime();

    @DefaultInt(value = 30, keyRes = R.string.prefMaximumTimeKey)
    int maximumTime();

    @DefaultInt(value = 30, keyRes = R.string.prefPlayingTimeKey)
    int playingTime();

    @DefaultInt(value = 15, keyRes = R.string.prefIntermittentTimeKey)
    int intermittentTime();

    @DefaultBoolean(true)
    boolean alertStop();

    @DefaultBoolean(false)
    boolean alertStart();

    @DefaultBoolean(value = true, keyRes = R.string.prefLocalMusicKey)
    boolean localMusic();

    @DefaultString("en")
    String language();
}
