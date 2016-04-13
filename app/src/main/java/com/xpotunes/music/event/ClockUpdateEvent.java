package com.xpotunes.music.event;

import org.joda.time.Duration;

public class ClockUpdateEvent {

    private Duration mDuration;

    public ClockUpdateEvent(Duration duration) {
        mDuration = duration;
    }

}