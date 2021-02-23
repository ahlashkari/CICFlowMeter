package susman.cs.ncat.edu.ais;

import susman.cs.ncat.edu.dataset.Sample;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Detector {
    private List<Range> ranges;
    private byte type;
    private Timestamp creation;

    public Detector (String line) {
        ranges = new ArrayList<>();

        String[] splits = line.split(",");

        for (int i = 0; i < splits.length; i += 2) {
            float min = Float.parseFloat(splits[i]);
            float max = Float.parseFloat(splits[i+1]);

            ranges.add(new Range(min, max));

        }
    }

    public List<Range> getRanges () {
        return this.ranges;
    }

    public byte getType() {
        return this.type;
    }

    public void setType (byte type) {
        this.type = type;
    }

    /**
     * Return true if the detector matches the sample
     * @param s
     * @param rValue
     * @return
     */
    public boolean classify (Sample s, int rValue) {

        int matches = 0;

        for (int i = 0; i < s.getFeatures().size(); i++) {
            if (this.ranges.get(i).between(s.getFeatures().get(i))) {
                matches += 1;
            }
        }

        if (matches >= rValue) {
            return true;
        }
        else {
            return false;
        }
    }

    public Timestamp getCreation() {
        return creation;
    }

    public void setCreation(Timestamp creation) {
        this.creation = creation;
    }
}
