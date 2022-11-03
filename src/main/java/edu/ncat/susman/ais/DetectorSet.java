package edu.ncat.susman.ais;

import edu.ncat.susman.server.DetectionWorker;
import edu.ncat.susman.server.DetectorLifespanWorker;
import edu.ncat.susman.dataset.Sample;
import edu.ncat.susman.server.writer.SampleWriter;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DetectorSet {

    private HashMap<String, Detector> detectors;
    private LinkedBlockingQueue<Sample> sampleQueue;

    private DetectionWorker sampleAnalysisThread;
    private DetectorLifespanWorker regenerationThread;

    public DetectorSet(SampleWriter sampleWriter) {
        this.detectors = new HashMap<>();
        this.sampleQueue = new LinkedBlockingQueue<>();
        setSampleAnalysisThread(new DetectionWorker(this, sampleWriter));
        getSampleAnalysisThread().start();
        setRegenerationThread(new DetectorLifespanWorker(this));

    }

    public void startLifespanEvaluation() {
        getRegenerationThread().start();
    }

    public synchronized void addDetector (Detector detector) {
        detectors.put(detector.getId(), detector);
    }

    public synchronized void removeDetector(String id) {
        detectors.remove(id);
    }

    public synchronized void addNewSample(Sample s) {
        try {
            this.sampleQueue.put(s);
        } catch (InterruptedException ex) {

        }
    }

    public HashMap<String, Detector> getDetectors() {
        return this.detectors;
    }

    public boolean hasSample() {
        return !sampleQueue.isEmpty();
    }

    public LinkedBlockingQueue getQueue () {
        return this.sampleQueue;
    }

    public DetectionWorker getSampleAnalysisThread() {
        return sampleAnalysisThread;
    }

    public void setSampleAnalysisThread(DetectionWorker sampleAnalysisThread) {
        this.sampleAnalysisThread = sampleAnalysisThread;
    }

    public DetectorLifespanWorker getRegenerationThread() {
        return regenerationThread;
    }

    public void setRegenerationThread(DetectorLifespanWorker regenerationThread) {
        this.regenerationThread = regenerationThread;
    }

    public void close () {
        sampleAnalysisThread.close();
        //regenerationThread.interrupt();

    }
}
