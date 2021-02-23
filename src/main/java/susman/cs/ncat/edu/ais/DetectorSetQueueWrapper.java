package susman.cs.ncat.edu.ais;

import susman.cs.ncat.edu.dataset.Sample;

import java.util.ArrayList;
import java.util.List;

public class DetectorSetQueueWrapper {
    private List<Sample> queue;

    public DetectorSetQueueWrapper () {
        queue = new ArrayList();
    }

    public synchronized void add(Sample sample) {
        this.queue.add(sample);
    }

    public synchronized Sample pop() {
        return this.queue.remove(0);
    }
}
