package com.xpotunes.music.async;

import android.os.AsyncTask;

import com.xpotunes.music.event.ClockUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.Duration;
import org.joda.time.Instant;

public class ClockAsyncTask extends AsyncTask<Void, Void, Void> {

    private final int mDelayToUpdate;
    private boolean mAlive;

    public ClockAsyncTask() {
        this(1000);
    }

    public ClockAsyncTask(int delayToUpdate) {
        mAlive = true;
        mDelayToUpdate = delayToUpdate;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Instant mStartInstance = new Instant();

        while (mAlive) {
            try {
                Thread.sleep(mDelayToUpdate);

                Duration duration = new Duration(mStartInstance, new Instant());

                EventBus.getDefault().post(new ClockUpdateEvent(duration));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void stop() {
        mAlive = false;
    }
}
