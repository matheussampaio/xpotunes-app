package com.xpotunes.music.event;

import com.xpotunes.music.MusicGame;

public class GameMusicNameEvent extends GameEvent {

    private final String mMusicName;

    public GameMusicNameEvent(MusicGame musicGame, String musicName) {
        super(musicGame);

        mMusicName = musicName;
    }

    public String getMusicName() {
        return mMusicName;
    }
}