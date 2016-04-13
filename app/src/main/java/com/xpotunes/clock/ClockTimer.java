package com.xpotunes.clock;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

@EBean(scope = EBean.Scope.Singleton)
public class ClockTimer {

    private final int mDelayToUpdate;
    private boolean mAlive;

    public ClockTimer() {
        mAlive = false;
        mDelayToUpdate = 1000;
    }

    @Background
    public void start() {
        if (!mAlive) {
            mAlive = true;

            while (mAlive) {
                try {
                    Thread.sleep(mDelayToUpdate);

                    EventBus.getDefault().post(new ClockTimerEvent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        mAlive = false;
    }
}
