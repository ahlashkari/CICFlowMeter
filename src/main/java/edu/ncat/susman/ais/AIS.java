package edu.ncat.susman.ais;

import edu.ncat.susman.dataset.Sample;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import edu.ncat.susman.Parameters;

// Class to hold multiple detector sets
// Read detector sets from file and init detector set objects
public class AIS {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(AIS.class);

    // Single static instance to be used throughout the project
    private static AIS Instance = new AIS();

    // List of detector set (for each malicious type)
    private DetectorSet detectorSet;

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
        this.detectorSet = new DetectorSet();
        try {
            Scanner input = new Scanner(file);

            String line = input.nextLine();

            detectorSet.setrValue(Integer.parseInt(line));

            while (input.hasNextLine()) {
                line = input.nextLine();

                Detector detector = new Detector (line);

                detectorSet.addDetector(detector);
            }

            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        detectorSet.startLifespanEvaluation();
    }

    // Replicate a sample to be classified by all the Detector Sets
    public synchronized void addSample(Sample sample) {
        detectorSet.addNewSample(sample);
    }

    public DetectorSet getDetectorSet () {
        return this.detectorSet;
    }
}
