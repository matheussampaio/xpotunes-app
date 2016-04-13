package com.xpotunes.screen.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xpotunes.R;
import com.xpotunes.screen.main.game.GameActivity_;
import com.xpotunes.screen.main.trailer.TrailerActivity_;
import com.xpotunes.utils.XPOTunesSharedPref_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Pref
    XPOTunesSharedPref_ mXPOXpoTunesSharedPref;

    @Override
    protected void onStart() {
        super.onStart();

        decide();
    }

    public void decide() {
        boolean localMusic = mXPOXpoTunesSharedPref.localMusic().getOr(false);
        Log.d("XPO", "localMusic = " + localMusic);

        Intent intent;

        if (localMusic) {
            intent = new Intent(this, GameActivity_.class);
        } else {
            intent = new Intent(this, TrailerActivity_.class);
        }

        startActivity(intent);
        finish();
    }
}
