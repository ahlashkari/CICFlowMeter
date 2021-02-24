package susman.cs.ncat.edu.dataset;

import java.util.ArrayList;
import java.util.List;

public class Sample {
    private float[] features;

    public Sample () {
        features = new float[DataSet.getInstance().NUMBER_OF_FEATURES];
    }

    public Sample (float[] features) {
        this.features = features;
    }

    public float[] getFeatures () {
        return this.features;
    }

    public void setFeatures(float[] features) {
        this.features = features;
    }

    public void setSingleFeature(int index, float feature) {
        this.features[index] = feature;
    }

    public float getSingleFeature(int index) {
        return this.features[index];
    }

    @Override
    public String toString () {
        String str = "";

        for (float f : features) {
            str += f + ",";
        }

        return str;
    }
}
