package edu.ncat.susman.ais;

import edu.ncat.susman.dataset.Sample;
import edu.ncat.susman.server.writer.SampleWriter;
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
    private DetectorSet detectorSet;

    private SampleWriter sampleWriter;

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
        sampleWriter = new SampleWriter();
        sampleWriter.start();
        readDetectorSet();
        return Instance;
    }

    public void close() {
        detectorSet.close();
        sampleWriter.interrupt();
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
        detectorSet = new DetectorSet(sampleWriter);

        try {
            Scanner input = new Scanner(file);
            String line = input.nextLine();
            int numDetectorsPerSet = Integer.parseInt(line);

            while (input.hasNextLine()) {
                line = input.nextLine();

                int rValue = Integer.parseInt(line);
                int type = -1;
                for (int i = 0; i < numDetectorsPerSet; i++) {
                    line = input.nextLine();

                    Detector detector = new Detector(line);

                    if (type < 0) {
                        type = detector.getType();
                    }

                    detector.setrValue(rValue);

                    detectorSet.addDetector(detector);
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Replicate a sample to be classified by all the Detector Sets
    public synchronized void addSample(Sample sample) {
        detectorSet.addNewSample(sample);
    }

    public synchronized HashMap<String, Detector> getDetectors() {
        return detectorSet.getDetectors();
    }

}
