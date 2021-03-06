package susman.cs.ncat.edu.ais;

import susman.cs.ncat.edu.ais.worker.DetectionWorker;
import susman.cs.ncat.edu.ais.worker.DetectorLifespanWorker;
import susman.cs.ncat.edu.dataset.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetectorSet {

    private List<Detector> detectors;
    private List<Sample> newSampleQueue;
    private String type;
    private int rValue;

    private ExecutorService newSampleThread;
    private ExecutorService regenerationThread;

    public DetectorSet() {
        this.detectors = new ArrayList<>();
        this.newSampleQueue = new ArrayList();
        newSampleThread = Executors.newSingleThreadExecutor();
        regenerationThread = Executors.newSingleThreadExecutor();

    }

    public void startLifespanEvaluation() {
        regenerationThread.execute(new DetectorLifespanWorker(this));
    }

    public synchronized void addDetector (Detector detector) {
        detectors.add(detector);
    }

    public synchronized void removeDetector(int index) {
        detectors.remove(index);
    }

    public synchronized void addNewSample(Sample s) {
        this.newSampleQueue.add(s);
        predict();
    }

    public List<Detector> getDetectors() {
        return this.detectors;
    }

    public synchronized Sample pop() {
        if (newSampleQueue.size() > 0)
            return newSampleQueue.remove(0);
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

        newSampleThread.execute(new DetectionWorker(this));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
