package com.xpotunes.screen.main;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xpotunes.R;
import com.xpotunes.clock.ClockTimer;
import com.xpotunes.clock.ClockTimerEvent;
import com.xpotunes.music.XPOMusicPlayer;
import com.xpotunes.pojo.Music;
import com.xpotunes.rest.RESTful;
import com.xpotunes.screen.settings.SettingsActivity_;
import com.xpotunes.utils.Logger;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@OptionsMenu(R.menu.settings)
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

    @OptionsItem(R.id.settings)
    void menuSettings() {
        SettingsActivity_.intent(this).start();
    }

    @Subscribe
    public void onClockTimerEvent(ClockTimerEvent event) {
        if (mXPOMusicPlayer.getDuration() >= 30) {

            Call<Music> musicCall = RESTful.getInstance().addView(mXPOMusicPlayer.getMusic().getId());
            musicCall.enqueue(new Callback<Music>() {
                @Override
                public void onResponse(Call<Music> call, Response<Music> response) {
                    Logger.i("view add: ");
                }

                @Override
                public void onFailure(Call<Music> call, Throwable t) {

                }
            });

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
