package com.xpotunes.music;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;

import com.xpotunes.R;
import com.xpotunes.music.async.ClockAsyncTask;
import com.xpotunes.music.event.ClockUpdateEvent;
import com.xpotunes.music.event.GameDurationPassedEvent;
import com.xpotunes.music.event.GameMusicNameEvent;
import com.xpotunes.music.event.GamePauseEvent;
import com.xpotunes.music.event.GamePlayEvent;
import com.xpotunes.music.event.GameStartEvent;
import com.xpotunes.music.event.GameStopEvent;
import com.xpotunes.utils.Logger;
import com.xpotunes.utils.Utils;
import com.xpotunes.utils.XPOTunesSharedPref_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.io.IOException;
import java.util.ArrayList;

@EBean(scope = EBean.Scope.Singleton)
public class MusicGame {

    @Pref
    XPOTunesSharedPref_ mXPOTunesSharedPref;

    private ClockAsyncTask mClockAsyncTask;
    private int mIntermittentTime;
    private int mMaxTime;
    private int mMinTime;
    private Boolean mRandomTime;

    private MusicGameState mState;
    private MediaPlayer mMediaPlayer;
    private Instant mBeginInstant;

    private Context mContext;
    private Boolean mAlertStop;
    private Boolean mAlertStart;
    private int mTurnDuration;
    private ArrayList<Uri> mMusicsUri;

    public MusicGame() {
        mState = MusicGameState.STOP;

        EventBus.getDefault().register(this);
    }

    public void start(ArrayList<Uri> musicsUri) {
        if (isStopped()) {
            Logger.d("Starting Game...");

            mMusicsUri = musicsUri;
            mMediaPlayer = new MediaPlayer();

            mAlertStop = mXPOTunesSharedPref.alertStop().get();
            mAlertStart = mXPOTunesSharedPref.alertStart().get();

            mRandomTime = mXPOTunesSharedPref.randomTime().get();

            if (mRandomTime) {
                mMinTime = mXPOTunesSharedPref.minimumTime().get();
                mMaxTime = mXPOTunesSharedPref.maximumTime().get();
            } else {
                mMinTime = mXPOTunesSharedPref.playingTime().get();
                mMaxTime = mXPOTunesSharedPref.playingTime().get();
            }

            mIntermittentTime = mXPOTunesSharedPref.intermittentTime().get();

            mState = MusicGameState.START;

            System.out.println("mRandomTime = " + mRandomTime);
            System.out.println("mMaxTime = " + mMaxTime);
            System.out.println("mMinTime = " + mMinTime);
            System.out.println("mIntermittentTime = " + mIntermittentTime);

            play();

            mClockAsyncTask = new ClockAsyncTask();
            mClockAsyncTask.execute();

            Logger.d("Game started.");

            EventBus.getDefault().post(new GameStartEvent(this));
        }
    }

    public void stop() {
        if (!isStopped()) {
            Logger.d("Stoping game...");

            pause();

            mState = MusicGameState.STOP;

            mClockAsyncTask.stop();

            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();

            Logger.d("Game stoped.");

            EventBus.getDefault().post(new GameStopEvent(this));
        }
    }

    public void play() {
        if (isStarted() || isPaused()) {
            Logger.d("playing...");

            mBeginInstant = new Instant();
            mState = MusicGameState.PLAY;

            mTurnDuration = Utils.randomInt(mMinTime, mMaxTime);

            int musicIndex = Utils.randomInt(0, mMusicsUri.size() - 1);
            Uri uri = mMusicsUri.get(musicIndex);
            System.out.println("uri = " + uri);

            try {

                findMusicTitle(uri);

                mMediaPlayer.setDataSource(mContext, uri);
                mMediaPlayer.prepare();

                int initTime = getInitTime();

                mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });

                mMediaPlayer.seekTo(initTime);

                mMediaPlayer.setLooping(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Logger.d(String.format("Play for %d seconds", mTurnDuration));

            EventBus.getDefault().post(new GamePlayEvent(this));
        }
    }

    private void findMusicTitle(Uri uri) {
        Cursor tempCursor = mContext.getContentResolver().query(uri,
                null, null, null, null);

        if (tempCursor != null) {
            tempCursor.moveToFirst(); //reset the cursor

            String displayName;
            do {
                displayName = tempCursor.getString(tempCursor.getColumnIndex("_display_name"));

                if (displayName != null) {
                    EventBus.getDefault().post(new GameMusicNameEvent(this, displayName));
                }

            } while (tempCursor.moveToNext());

            tempCursor.close();
        }
    }

    public void pause() {
        if (isPlaying()) {
            Logger.d("pausing...");

            mBeginInstant = new Instant();
            mState = MusicGameState.PAUSE;

            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.reset();

            Logger.d(String.format("Pause. Starting in %d seconds", mIntermittentTime));

            EventBus.getDefault().post(new GamePauseEvent(this));
        }
    }

    @Subscribe
    public void onClockUpdateEvent(ClockUpdateEvent event) {
        long durationInSeconds = getDurationPassed().getStandardSeconds();

        if (isPlaying() && durationInSeconds >= mTurnDuration) {
            if (mAlertStop) {
                playAlert();
            }

            pause();
        } else if (isPaused() && durationInSeconds >= mIntermittentTime) {
            if (mAlertStart) {
                playAlert();
            }

            play();
        } else {
            Logger.d(String.format("Updating... SecondsPassed: %d - RandomTime: %d - IntermittentTime: %d",
                    durationInSeconds, mTurnDuration, mIntermittentTime));

            EventBus.getDefault().post(new GameDurationPassedEvent(this));
        }
    }

    private void playAlert() {
        if (mContext != null) {
            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.stop);
            mediaPlayer.start();
        }
    }

    public MusicGameState getState() {
        return mState;
    }

    public Duration getDurationPassed() {
        return new Duration(mBeginInstant, new Instant());
    }

    public int getSeconds() {
        if (isPlaying()) {
            return (int) getDurationPassed().getStandardSeconds();
        } else {
            return (int) (mIntermittentTime - getDurationPassed().getStandardSeconds());
        }
    }

    public boolean isStopped() {
        return getState() == MusicGameState.STOP;
    }

    public boolean isPlaying() {
        return getState() == MusicGameState.PLAY;
    }

    public boolean isStarted() {
        return getState() == MusicGameState.START;
    }

    public boolean isPaused() {
        return getState() == MusicGameState.PAUSE;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public int getInitTime() {
        int durationInSeconds = mMediaPlayer.getDuration() / 1000;

        if (mTurnDuration >= durationInSeconds) {
            return 0;
        }

        int initTime = Utils.randomInt(0, durationInSeconds - mTurnDuration);

        return initTime * 1000;
    }
}
