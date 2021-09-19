package edu.ncat.susman.dataset;

import edu.ncat.susman.Parameters;

public class Sample {
    private String dstIP;
    private String srcIP;
    private String dstPort;
    private String srcPort;
    private String protocol;
    private float[] features;

    public Sample () {
        features = new float[Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES];
    }

    public Sample (float[] features) {
        this.features = features;
    }

    public float[] getFeatures () {
        return this.features;
    }

    public void setFeatures(float[] features) {
        this.features = features;
    }

    public void setSingleFeature(int index, float feature) {
        this.features[index] = feature;
    }

    public float getSingleFeature(int index) {
        return this.features[index];
    }

    @Override
    public String toString () {
        String str = "";

        for (float f : features) {
            str += f + ",";
        }

        return str;
    }

    public String getDstIP() {
        return dstIP;
    }

    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getDstPort() {
        return dstPort;
    }

    public void setDstPort(String dstPort) {
        this.dstPort = dstPort;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
