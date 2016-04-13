package com.xpotunes.screen.main.trailer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xpotunes.R;
import com.xpotunes.clock.ClockTimer;
import com.xpotunes.clock.ClockTimerEvent;
import com.xpotunes.music.XPOMusicPlayer;
import com.xpotunes.music.event.MusicStartedEvent;
import com.xpotunes.pojo.Music;
import com.xpotunes.rest.RESTful;
import com.xpotunes.screen.main.game.GameActivity_;
import com.xpotunes.screen.settings.SettingsActivity_;
import com.xpotunes.utils.Logger;
import com.xpotunes.utils.XPOTunesSharedPref_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@EActivity(R.layout.activity_trailer)
public class TrailerActivity extends AppCompatActivity {

    @Pref
    XPOTunesSharedPref_ mXPOXpoTunesSharedPref;

    @ViewById(R.id.playButton)
    Button mPlayButton;

    @ViewById(R.id.stopButton)
    Button mStopButton;

    @ViewById(R.id.skipButton)
    Button mSkipButton;

    @ViewById(R.id.wholeMusicButton)
    Button mWholeMusicButton;

    @ViewById(R.id.musicTitleTextView)
    TextView mMusicTitle;

    @ViewById(R.id.musicSubTitleTextView)
    TextView mMusicSubTitle;

    @ViewById(R.id.settingsButton)
    ImageView mSettingsButton;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    @Bean
    XPOMusicPlayer mXPOMusicPlayer;

    @Bean
    ClockTimer mClockTimer;

    @AfterViews
    public void afterViews() {
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CFCFCF"), android.graphics.PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void onStart() {
        super.onStart();

        register();

        boolean localMusic = mXPOXpoTunesSharedPref.localMusic().getOr(false);

        if (localMusic) {
            Intent intent = new Intent(this, GameActivity_.class);
            startActivity(intent);
//            finish();
        }
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

        mSkipButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.VISIBLE);
        mWholeMusicButton.setVisibility(View.VISIBLE);
    }

    @Click(R.id.skipButton)
    void onClickSkipButton() {
        fetchRandomMusic();
    }

    @Click(R.id.stopButton)
    void onClickStopButton() {
        mStopButton.setVisibility(View.INVISIBLE);
        mSkipButton.setVisibility(View.INVISIBLE);
        mWholeMusicButton.setVisibility(View.INVISIBLE);

        mXPOMusicPlayer.pause();
        mClockTimer.stop();

        mPlayButton.setVisibility(View.VISIBLE);
    }

    @Click(R.id.settingsButton)
    void onClickSettingsButton() {
        SettingsActivity_.intent(this).start();
    }

    @UiThread
    void loading() {
        mProgressBar.setVisibility(View.VISIBLE);

        mSkipButton.setEnabled(false);
        mStopButton.setEnabled(false);
        mPlayButton.setEnabled(false);
        mWholeMusicButton.setEnabled(false);
    }

    @UiThread
    @Subscribe
    void onMusicStartedEvent(MusicStartedEvent event) {
        mProgressBar.setVisibility(View.INVISIBLE);

        mSkipButton.setEnabled(true);
        mStopButton.setEnabled(true);
        mPlayButton.setEnabled(true);
        mWholeMusicButton.setEnabled(true);
    }

    @Subscribe
    public void onClockTimerEvent(ClockTimerEvent event) {
        System.out.println("getDuration() = " + mXPOMusicPlayer.getDuration());

        if (mXPOMusicPlayer.getDuration() >= 10) {

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

    @UiThread
    public void updateDescription(Music music) {
        mMusicTitle.setText(music.getTitle());
        mMusicSubTitle.setText(String.format("%s - %s", music.getAlbum(), music.getArtist()));
    }

    void fetchRandomMusic() {
        loading();

        Call<List<Music>> randomMusic = RESTful.getInstance().getRandomMusic();

        randomMusic.enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(Call<List<Music>> call, Response<List<Music>> response) {
                Music music = response.body().get(0);

                updateDescription(music);

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
