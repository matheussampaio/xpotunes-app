package com.xpotunes.screen.main.game;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.xpotunes.R;
import com.xpotunes.music.MusicGame;
import com.xpotunes.music.event.GameDurationPassedEvent;
import com.xpotunes.music.event.GameMusicNameEvent;
import com.xpotunes.music.event.GamePauseEvent;
import com.xpotunes.music.event.GamePlayEvent;
import com.xpotunes.music.event.GameStartEvent;
import com.xpotunes.music.event.GameStopEvent;
import com.xpotunes.screen.main.trailer.TrailerActivity_;
import com.xpotunes.screen.settings.SettingsActivity_;
import com.xpotunes.utils.Logger;
import com.xpotunes.utils.XPOTunesSharedPref_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

@EActivity(R.layout.activity_game)
@OptionsMenu(R.menu.settings)
public class GameActivity extends AppCompatActivity {

    private static final int SELECT_MUSIC_RESULT = 10;

    @Pref
    XPOTunesSharedPref_ mXPOXpoTunesSharedPref;

    @ViewById(R.id.btnToggleGame)
    ImageButton mBtnToggleGame;

    @ViewById(R.id.btnSkipMusic)
    CircleButton mBtnSkipMusic;

    @ViewById(R.id.txtTimePassed)
    TextView mTxtTimePassed;

    @ViewById(R.id.txtSongName)
    TextView mTxtSongName;

    @ViewById(R.id.btnSelectMusic)
    Button mBtnSelectMusic;

    @StringRes(R.string.start_game)
    String mStrStartGame;

    @StringRes(R.string.stop_game)
    String mStrStopGame;

    @Bean
    MusicGame mMusicGame;

    private ArrayList<Uri> mMusicUri;

    @AfterViews
    void afterViews() {
        mMusicUri = new ArrayList<>();

        mBtnToggleGame.setImageResource(R.drawable.play_button);

        mBtnSkipMusic.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_fast_forward)
            .color(Color.parseColor("#E8EAF6"))
            .sizeDp(24));

        if (mMusicUri != null && mMusicUri.size() > 0) {
            mBtnToggleGame.setEnabled(true);
        } else {
            mBtnToggleGame.setEnabled(false);
        }
    }

    @OptionsItem(R.id.settings)
    void menuSettings() {
        SettingsActivity_.intent(this).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mMusicGame.setContext(getApplicationContext());

        boolean localMusic = mXPOXpoTunesSharedPref.localMusic().getOr(false);

        if (!localMusic) {
            Intent intent = new Intent(this, TrailerActivity_.class);
            startActivity(intent);
//            finish();
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void startGame() {
        mMusicGame.start(mMusicUri);
    }

    private void stopGame() {
        mMusicGame.stop();
    }

    /**
     * Called when GameStopEvent
     *
     * @param event GameStopEvent
     */
    @Subscribe
    public void onGameStopEvent(GameStopEvent event) {
        Logger.d("GameStopEvent catched");

        mTxtTimePassed.setVisibility(View.INVISIBLE);

        mBtnToggleGame.setImageResource(R.drawable.play_button);

        mBtnSelectMusic.setEnabled(true);

        mBtnSkipMusic.setVisibility(View.INVISIBLE);
        mTxtSongName.setVisibility(View.INVISIBLE);

        mBtnSelectMusic.setVisibility(View.VISIBLE);
    }

    /**
     * Called when GameStartEvent
     *
     * @param event GameStartEvent
     */
    @Subscribe
    public void onGameStartEvent(GameStartEvent event) {
        Logger.d("GameStartEvent catched");

        mTxtTimePassed.setText("0s");
        mTxtTimePassed.setVisibility(View.VISIBLE);
        mBtnSelectMusic.setVisibility(View.INVISIBLE);

        mBtnToggleGame.setImageResource(R.drawable.pause_button);

        mBtnSelectMusic.setEnabled(false);

        if (mMusicUri.size() > 1) {
            mBtnSkipMusic.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onGameMusicNameEvent(GameMusicNameEvent event) {
        Logger.d("MainActivity.onEvent");

        updateTxtSongTitle(event.getMusicName());
    }

    /**
     * Called when GamePauseEvent
     *
     * @param event GamePauseEvent
     */
    @Subscribe
    public void onGamePauseEvent(GamePauseEvent event) {
        Logger.d("GamePauseEvent catched");

        updatePauseTime(event.getMusicGame().getSeconds());
    }

    /**
     * Called when GamePlayEvent
     *
     * @param event GamePlayEvent
     */
    @Subscribe
    public void onGamePlayEvent(GamePlayEvent event) {
        Logger.d("GamePlayEvent catched");

        updatePlayTime(event.getMusicGame().getSeconds());
    }

    /**
     * Callend when GameDurationPassedEvent
     *
     * @param event GameDurationPassedEvent
     */
    @Subscribe
    public void onGameDurationPassedEvent(GameDurationPassedEvent event) {
        if (event.getMusicGame().isPlaying()) {
            updatePlayTime(event.getMusicGame().getSeconds());
        } else {
            updatePauseTime(event.getMusicGame().getSeconds());
        }
    }

    private void updatePlayTime(int seconds) {
        updateTxtPassedTime("Seconds played: " + seconds + "s");
    }

    private void updatePauseTime(int seconds) {
        updateTxtPassedTime("Seconds to start: " + seconds + "s");
    }

    @UiThread
    public void updateTxtPassedTime(String strDuration) {
        mTxtTimePassed.setText(strDuration);
    }

    @UiThread
    public void updateTxtSongTitle(String songName) {
        mTxtSongName.setText(songName);
        mTxtSongName.setVisibility(View.VISIBLE);
    }

    @Click(R.id.btnToggleGame)
    void getPlayMusicListener() {
        if (mMusicGame.isStopped()) {
            startGame();
        } else {
            stopGame();
        }
    }

    @Click(R.id.btnSelectMusic)
    void selectMusic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, SELECT_MUSIC_RESULT);
    }

    @Click(R.id.btnSkipMusic)
    void skipMusic() {
        mMusicGame.pause();
        mMusicGame.play();
    }

    @OnActivityResult(SELECT_MUSIC_RESULT)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mMusicUri.clear();

            ClipData clipData = data.getClipData();

            // If user select more then one music
            if (clipData != null) {

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mMusicUri.add(uri);
                }

                // If user select just one music file
            } else {
                mMusicUri.add(data.getData());
            }

            if (mMusicUri.size() > 0) {
                mBtnToggleGame.setEnabled(true);
            } else {
                mBtnToggleGame.setEnabled(false);
            }

        }
    }

}
