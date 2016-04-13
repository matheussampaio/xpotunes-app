package com.xpotunes.music.event;

import com.xpotunes.music.MusicGame;

public class GameEvent {
    private final MusicGame mMusicGame;

    public GameEvent(MusicGame musicGame) {
        mMusicGame = musicGame;
    }

    public MusicGame getMusicGame() {
        return mMusicGame;
    }

}