package susman.cs.ncat.edu.ais;

import susman.cs.ncat.edu.ais.worker.DetectorSetWorker;
import susman.cs.ncat.edu.dataset.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetectorSet {

    private List<Detector> detectors;
    private List<Sample> newSampleQueue;
    private int rValue;

    private ExecutorService newSampleThread;

    public DetectorSet() {
        this.detectors = new ArrayList<>();
        this.newSampleQueue = new ArrayList();
        newSampleThread = Executors.newSingleThreadExecutor();
    }

    public void addDetector (Detector detector) {
        detectors.add(detector);
    }

    public void addNewSample(Sample s) {
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
        newSampleThread.execute(new DetectorSetWorker(this));
    }
}
