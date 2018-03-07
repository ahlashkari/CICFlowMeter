package cic.cs.unb.ca.guava.Event;

import java.io.File;

public class FlowVisualEvent {

    private File csv_file;

    public FlowVisualEvent(File csv_file) {
        this.csv_file = csv_file;
    }

    public File getCsv_file() {
        return csv_file;
    }
}
