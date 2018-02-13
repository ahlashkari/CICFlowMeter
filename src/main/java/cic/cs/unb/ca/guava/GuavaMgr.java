package cic.cs.unb.ca.guava;

import com.google.common.eventbus.EventBus;

public class GuavaMgr {

    private static GuavaMgr Instance = new GuavaMgr();

    private EventBus mEventBus;


    public GuavaMgr() {
    }

    public static GuavaMgr getInstance() {
        return Instance;
    }

    public void init(){
        mEventBus = new EventBus("CICFlowMeter");
    }

    public EventBus getEventBus() {
        return mEventBus;
    }
}
