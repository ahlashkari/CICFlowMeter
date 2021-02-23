package susman.cs.ncat.edu.ais;

import susman.cs.ncat.edu.dataset.Sample;

import java.util.ArrayList;
import java.util.List;

public class DetectorSet {

    private List<Detector> detectors;
    private DetectorSetQueueWrapper queue;

    public DetectorSet() {
        this.detectors = new ArrayList<>();
        this.queue = new DetectorSetQueueWrapper();
    }

    public void addDetector (Detector detector) {
        detectors.add(detector);
    }

    public void addToQueue(Sample s) {
        this.queue.add(s);
    }
}
