package susman.cs.ncat.edu.dataset;

import java.util.ArrayList;
import java.util.List;

public class DataSetQueueWrapper {
    private List<Sample> queue;

    public DataSetQueueWrapper () {
        queue = new ArrayList();
    }

    public synchronized void add(Sample s) {
        this.queue.add(s);
    }

    public synchronized Sample pop() {
        return this.queue.remove(0);
    }
}
