package com.xpotunes.music.event;

import com.xpotunes.music.MusicGame;

public class GameStopEvent extends GameEvent {

    public GameStopEvent(MusicGame musicGame) {
        super(musicGame);
    }

}
