package edu.ncat.susman.ais;

import edu.ncat.susman.server.DetectionWorker;
import edu.ncat.susman.server.DetectorLifespanWorker;
import edu.ncat.susman.dataset.Sample;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DetectorSet {

    private HashMap<String, Detector> detectors;
    private LinkedBlockingQueue<Sample> sampleQueue;
    private int rValue;

    private ExecutorService sampleAnalysisThread;
    private ExecutorService regenerationThread;

    public DetectorSet() {
        this.detectors = new HashMap<>();
        this.sampleQueue = new LinkedBlockingQueue<>();
        sampleAnalysisThread = Executors.newSingleThreadExecutor();
        sampleAnalysisThread.execute(new DetectionWorker(this));
        regenerationThread = Executors.newSingleThreadExecutor();

    }

    public void startLifespanEvaluation() {
        regenerationThread.execute(new DetectorLifespanWorker(this));
    }

    public synchronized void addDetector (Detector detector) {
        detectors.put(detector.getId(), detector);
    }

    public synchronized void removeDetector(int index) {
        detectors.remove(index);
    }

    public synchronized void addNewSample(Sample s) {
        this.sampleQueue.add(s);
        //predict();
    }

    public HashMap<String, Detector> getDetectors() {
        return this.detectors;
    }


    public int getrValue() {
        return rValue;
    }

    public void setrValue(int rValue) {
        this.rValue = rValue;
    }

    public void predict() {

        sampleAnalysisThread.execute(new DetectionWorker(this));
    }

    public boolean hasSample() {
        return !sampleQueue.isEmpty();
    }

    public LinkedBlockingQueue getQueue () {
        return this.sampleQueue;
    }
}
