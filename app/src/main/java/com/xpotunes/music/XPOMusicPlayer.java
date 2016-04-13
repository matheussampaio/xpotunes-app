package com.xpotunes.music;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.xpotunes.music.event.MusicEndEvent;
import com.xpotunes.music.event.MusicStartEvent;
import com.xpotunes.pojo.Music;
import com.xpotunes.rest.RESTful;
import com.xpotunes.utils.Utils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.Duration;
import org.joda.time.Instant;

@EBean(scope = EBean.Scope.Singleton)
public class XPOMusicPlayer {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 10;

    private Instant mBeginInstant;
    private ExoPlayer mExoPlayer;
    private Music mMusic;

    @RootContext
    Context mContext;

    private boolean mPlaying;

    public XPOMusicPlayer() {
        mExoPlayer = ExoPlayer.Factory.newInstance(1);

        mExoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                System.out.println("playWhenReady = [" + playWhenReady + "], playbackState = [" + playbackState + "]");

                if (playbackState == 4) {
                    EventBus.getDefault().post(new MusicStartEvent());
                } else if (playbackState == 5) {
                    EventBus.getDefault().post(new MusicEndEvent());
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {
                System.out.println("XPOMusicPlayer.onPlayWhenReadyCommitted");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                System.out.println("XPOMusicPlayer.onPlayerError");
            }
        });
    }

    public XPOMusicPlayer prepare() {
        String url = String.format("%saudiodata/%s/stream", RESTful.URL_BASE, mMusic.getFile());

        Uri uri = Uri.parse(url);
        String userAgent = Utils.getUserAgent(mContext, "XPOTunes");

        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        DataSource dataSource = new DefaultUriDataSource(mContext, null, userAgent);

        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
                sampleSource, MediaCodecSelector.DEFAULT);

        mExoPlayer.prepare(audioRenderer);

        return this;
    }

    public XPOMusicPlayer setMusic(Music music) {
        mMusic = music;

        return this;
    }

    public XPOMusicPlayer pause() {
        mPlaying = false;
        mExoPlayer.seekTo(0);
        mExoPlayer.stop();

        return this;
    }

    public XPOMusicPlayer play() {
        mPlaying = true;

        mBeginInstant = new Instant();
        mExoPlayer.setPlayWhenReady(true);

        return this;
    }

    public int getDuration() {
        return (int) new Duration(mBeginInstant, new Instant()).getStandardSeconds();
    }

    public Music getMusic() {
        return mMusic;
    }

    public XPOMusicPlayer seekTo(long millis) {
        mExoPlayer.seekTo(millis);

        return this;
    }

    public boolean isPlaying() {
        return mPlaying;
    }
}
