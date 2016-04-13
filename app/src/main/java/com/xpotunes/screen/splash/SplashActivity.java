package com.xpotunes.screen.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.xpotunes.screen.main.MainActivity_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity_.class);
        startActivity(intent);
        finish();
    }
}