package susman.cs.ncat.edu.dataset;

import java.util.ArrayList;
import java.util.List;

public class Sample {
    private List<Float> features;

    public Sample () {
        features = new ArrayList<>();
    }

    public Sample (ArrayList<Float> features) {
        this.features = features;
    }

    public List<Float> getFeatures () {
        return this.features;
    }

    public void addFeature(float feature) {
        features.add(feature);
    }

    public void setFeature(int index, float feature) {
        this.features.set(index, feature);
    }
}
