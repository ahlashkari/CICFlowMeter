package cic.cs.unb.ca.jnetpcap.worker;

import cic.cs.unb.ca.jnetpcap.BasicFlow;

public interface FlowGenListener {
    void onFlowGenerated(BasicFlow flow);
}
