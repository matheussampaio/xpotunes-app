package com.xpotunes.music.event;

import com.xpotunes.music.MusicGame;

public class GameStartEvent extends GameEvent {

    public GameStartEvent(MusicGame musicGame) {
        super(musicGame);
    }
}
