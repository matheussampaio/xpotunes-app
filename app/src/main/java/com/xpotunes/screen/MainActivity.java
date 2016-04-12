package com.xpotunes.screen;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.xpotunes.R;
import com.xpotunes.clock.ClockTimer;
import com.xpotunes.clock.ClockTimerEvent;
import com.xpotunes.music.XPOMusicPlayer;
import com.xpotunes.pojo.Music;
import com.xpotunes.rest.RESTful;
import com.xpotunes.utils.Logger;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.playButton)
    Button mPlayButton;

    @ViewById(R.id.stopButton)
    Button mStopButton;

    @Bean
    XPOMusicPlayer mXPOMusicPlayer;

    @Bean
    ClockTimer mClockTimer;

    @Override
    protected void onStart() {
        super.onStart();

        register();
    }

    @Override
    protected void onStop() {
        unregister();

        super.onStop();
    }

    @Click(R.id.playButton)
    void onClickPlayButton() {
        mPlayButton.setVisibility(View.INVISIBLE);

        mClockTimer.start();
        fetchRandomMusic();

        mStopButton.setVisibility(View.VISIBLE);
    }

    @Click(R.id.stopButton)
    void onClickStopButton() {
        mStopButton.setVisibility(View.INVISIBLE);

        mXPOMusicPlayer.pause();
        mClockTimer.stop();

        mPlayButton.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onClockTimerEvent(ClockTimerEvent event) {
        if (mXPOMusicPlayer.getDuration() >= 30) {
            fetchRandomMusic();
        }
    }

    void fetchRandomMusic() {
        Call<List<Music>> randomMusic = RESTful.getInstance().getRandomMusic();

        randomMusic.enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(Call<List<Music>> call, Response<List<Music>> response) {
                Music music = response.body().get(0);

                mXPOMusicPlayer
                    .setMusic(music)
                    .pause()
                    .prepare()
                    .play();
            }

            @Override
            public void onFailure(Call<List<Music>> call, Throwable t) {
                Logger.e("Error fetching random music");
            }
        });
    }

    public void register() {
        Logger.d("MainActivity.register");
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        Logger.d("MainActivity.unregister");
        EventBus.getDefault().unregister(this);
    }

}
