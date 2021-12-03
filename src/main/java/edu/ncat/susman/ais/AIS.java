package edu.ncat.susman.ais;

import edu.ncat.susman.dataset.Sample;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import edu.ncat.susman.Parameters;

// Class to hold multiple detector sets
// Read detector sets from file and init detector set objects
public class AIS {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(AIS.class);

    // Single static instance to be used throughout the project
    private static AIS Instance = new AIS();

    // List of detector set (for each malicious type)
    private HashMap<Integer, DetectorSet> detectorSets;

    // Constructor
    private AIS () {
        super();
    }

    // Returns the static instance
    public static AIS getInstance() {
        return Instance;
    }

    // Initialize the static instance
    public AIS init () {
        readDetectorSet();
        return Instance;
    }

    // Read the detector set files from a directory
    public void readDetectorSet() {

        File detectorSet = new File(Parameters.DETECTOR_DIRECTORY);

        if (!detectorSet.exists()) {
                logger.error(Parameters.DETECTOR_DIRECTORY + " does not exist");
                System.exit(-1);
        }
        readSet(detectorSet);
    }

    // Read a single detector set file
    // Create a Detector Set
    // Add the detectors from the file to the detector set
    public void readSet (File file) {
        detectorSets = new HashMap<>();

        try {
            Scanner input = new Scanner(file);
            String line = input.nextLine();
            int numDetectorsPerSet = Integer.parseInt(line);

            while (input.hasNextLine()) {
                line = input.nextLine();
                DetectorSet ds = new DetectorSet();
                ds.setrValue(Integer.parseInt(line));

                int type = -1;
                for (int i = 0; i < numDetectorsPerSet; i++) {
                    line = input.nextLine();

                    Detector detector = new Detector(line);

                    if (type < 0) {
                        type = detector.getType();
                    }

                    ds.addDetector(detector);
                }
                detectorSets.put(type, ds);
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (Map.Entry<Integer, DetectorSet> entry : detectorSets.entrySet()) {
            entry.getValue().startLifespanEvaluation();
        }
    }

    // Replicate a sample to be classified by all the Detector Sets
    public synchronized void addSample(Sample sample) {
        for (Map.Entry<Integer, DetectorSet> entry : detectorSets.entrySet()) {
            entry.getValue().addNewSample(sample);
        }
    }

    public synchronized HashMap<String, Detector> getDetectors() {
        HashMap<String, Detector> rtdetectors = new HashMap();
        for (Map.Entry<Integer, DetectorSet> entry : detectorSets.entrySet()) {
            rtdetectors.putAll(entry.getValue().getDetectors());
        }

        return rtdetectors;
    }

}
