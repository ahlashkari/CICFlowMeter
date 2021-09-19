package edu.ncat.susman.ais;

import edu.ncat.susman.client.DetectionWorker;
import edu.ncat.susman.client.DetectorLifespanWorker;
import edu.ncat.susman.dataset.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetectorSet {

    private HashMap<String, Detector> detectors;
    private List<Sample> sampleQueue;
    private int rValue;

    private ExecutorService sampleAnalysisThread;
    private ExecutorService regenerationThread;

    public DetectorSet() {
        this.detectors = new HashMap<>();
        this.sampleQueue = new ArrayList<>();
        sampleAnalysisThread = Executors.newSingleThreadExecutor();
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
        predict();
    }

    public HashMap<String, Detector> getDetectors() {
        return this.detectors;
    }

    public synchronized Sample pop() {
        if (sampleQueue.size() > 0)
            return sampleQueue.remove(0);
        else
            return null;
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
}
