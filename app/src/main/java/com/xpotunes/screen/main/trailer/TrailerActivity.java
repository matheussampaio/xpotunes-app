package com.xpotunes.screen.main.trailer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xpotunes.R;
import com.xpotunes.clock.ClockTimer;
import com.xpotunes.clock.ClockTimerEvent;
import com.xpotunes.music.XPOMusicPlayer;
import com.xpotunes.music.event.MusicEndEvent;
import com.xpotunes.music.event.MusicStartEvent;
import com.xpotunes.pojo.Music;
import com.xpotunes.rest.RESTful;
import com.xpotunes.screen.main.game.GameActivity_;
import com.xpotunes.screen.settings.SettingsActivity_;
import com.xpotunes.utils.Logger;
import com.xpotunes.utils.XPOTunesSharedPref_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.res.StringArrayRes;
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

    @StringArrayRes(R.array.genre_entry_values)
    String[] mGenreValues;

    @ViewById(R.id.playButton)
    Button mPlayButton;

    @ViewById(R.id.stopButton)
    Button mStopButton;

    @ViewById(R.id.skipButton)
    Button mSkipButton;

    @ViewById(R.id.wholeMusicButton)
    Button mWholeMusicButton;

    @ViewById(R.id.pauseButton)
    Button mPauseButton;

    @ViewById(R.id.musicTitleTextView)
    TextView mMusicTitle;

    @ViewById(R.id.musicSubTitleTextView)
    TextView mMusicSubTitle;

    @ViewById(R.id.settingsButton)
    ImageView mSettingsButton;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    @ViewById(R.id.genreSpinner)
    Spinner mGenreSpinner;

    @Bean
    XPOMusicPlayer mXPOMusicPlayer;

    @Bean
    ClockTimer mClockTimer;

    private boolean mPlayWholeMusic = false;
    private boolean mLoading = false;

    @AfterViews
    public void afterViews() {
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CFCFCF"), android.graphics.PorterDuff.Mode.SRC_ATOP);
    }

    @ItemSelect(R.id.genreSpinner)
    public void myListItemSelected(boolean selected, int position) {
        ((TextView) mGenreSpinner.getChildAt(0)).setTextColor(Color.parseColor("#FF5252"));
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
        mPlayWholeMusic = false;

        mPlayButton.setVisibility(View.INVISIBLE);
        mGenreSpinner.setVisibility(View.INVISIBLE);

        fetchRandomMusic();

        mSkipButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.VISIBLE);
        mWholeMusicButton.setVisibility(View.VISIBLE);
    }

    @Click(R.id.skipButton)
    void onClickSkipButton() {
        mPlayWholeMusic = false;

        fetchRandomMusic();
    }

    @Click(R.id.stopButton)
    void onClickStopButton() {
        mPlayWholeMusic = false;

        mStopButton.setVisibility(View.INVISIBLE);
        mSkipButton.setVisibility(View.INVISIBLE);
        mWholeMusicButton.setVisibility(View.INVISIBLE);
        mPauseButton.setVisibility(View.INVISIBLE);

        mXPOMusicPlayer.pause();

        mClockTimer.stop();

        mPlayButton.setVisibility(View.VISIBLE);
        mGenreSpinner.setVisibility(View.VISIBLE);

        loaded();
    }

    @Click(R.id.wholeMusicButton)
    void onClickWholeMusicButton() {
        mPlayWholeMusic = true;

        mWholeMusicButton.setVisibility(View.INVISIBLE);
        mPauseButton.setVisibility(View.VISIBLE);

        Music music = mXPOMusicPlayer.getMusic();

        loading();
        playMusic(music);
    }

    @Click(R.id.pauseButton)
    void onClickPauseButton() {
        onClickStopButton();
    }

    @Click(R.id.settingsButton)
    void onClickSettingsButton() {
        SettingsActivity_.intent(this).start();
    }

    @UiThread
    void loading() {
        mLoading = true;

        mProgressBar.setVisibility(View.VISIBLE);

        mSkipButton.setEnabled(false);
        mPlayButton.setEnabled(false);
        mWholeMusicButton.setEnabled(false);
        mPauseButton.setEnabled(false);
    }

    @UiThread
    void loaded() {
        mLoading = false;

        mProgressBar.setVisibility(View.INVISIBLE);

        mSkipButton.setEnabled(true);
        mPlayButton.setEnabled(true);
        mWholeMusicButton.setEnabled(true);
        mPauseButton.setEnabled(true);
    }

    @Subscribe
    void onMusicStartEvent(MusicStartEvent event) {
        loaded();
    }

    @Subscribe
    void onMusicEndEvent(MusicEndEvent event) {
        if (mPlayWholeMusic = true) {
            mPauseButton.setVisibility(View.INVISIBLE);
            mWholeMusicButton.setVisibility(View.VISIBLE);

            RESTful.addView(mXPOMusicPlayer.getMusic().getId());
        }

        mPlayWholeMusic = false;

        fetchRandomMusic();
    }

    @Subscribe
    @Background
    public void onClockTimerEvent(ClockTimerEvent event) {
        if (!mLoading && !mPlayWholeMusic && mXPOMusicPlayer.isPlaying() && mXPOMusicPlayer.getCurrentPosition() >= mXPOMusicPlayer.getMusic().getEnd()) {
            fetchRandomMusic();
        }
    }

    @UiThread
    public void updateDescription(Music music) {
        mMusicTitle.setText(music.getTitle());
        mMusicSubTitle.setText(String.format("%s - %s", music.getAlbum(), music.getArtist()));
    }

    private void fetchRandomMusic() {
        loading();

        String selectedGenre = mGenreValues[mGenreSpinner.getSelectedItemPosition()];

        Call<List<Music>> randomMusic = RESTful.getInstance().getRandomMusic(selectedGenre);

        randomMusic.enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(Call<List<Music>> call, Response<List<Music>> response) {
                if (response.body().size() > 0) {
                    Music music = response.body().get(0);

                    if (!mPlayWholeMusic) {
                        RESTful.addTrailerView(music.getId());
                    }

                    playMusic(music);
                } else {
                    nothingToPlay();
                }
            }

            @Override
            public void onFailure(Call<List<Music>> call, Throwable t) {
                Logger.e("Error fetching random music");
            }
        });
    }

    private void playMusic(Music music) {
        mClockTimer.start();

        updateDescription(music);

        mXPOMusicPlayer.setMusic(music).pause().prepare();

        if (!mPlayWholeMusic) {
            mXPOMusicPlayer.seekTo(music.getStart());
        }

        mXPOMusicPlayer.play();
    }

    private void nothingToPlay() {
        onClickStopButton();

        Toast.makeText(this, "We don't have any music on this genre.", Toast.LENGTH_LONG).show();
    }

    private void register() {
        Logger.d("MainActivity.register");
        EventBus.getDefault().register(this);
    }

    private void unregister() {
        Logger.d("MainActivity.unregister");
        EventBus.getDefault().unregister(this);
    }


}
