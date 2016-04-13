package com.xpotunes.music.event;

import com.xpotunes.music.MusicGame;

import org.joda.time.Duration;

public class GameDurationPassedEvent extends GameEvent {

    public GameDurationPassedEvent(MusicGame musicGame) {
        super(musicGame);
    }

    public Duration getDurationPassed() {
        return getMusicGame().getDurationPassed();
    }

}
