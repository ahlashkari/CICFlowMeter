package edu.ncat.susman.server.writer;

public class SampleWrapper {

    private String flowDump;
    private boolean collected;

    public SampleWrapper (boolean collected, String flowDump) {
        this.setFlowDump(flowDump);
        this.setCollected(collected);
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }


    public String getFlowDump() {
        return flowDump;
    }

    public void setFlowDump(String flowDump) {
        this.flowDump = flowDump;
    }
}
