package susman.cs.ncat.edu.ais;

import cic.cs.unb.ca.Sys;
import org.slf4j.LoggerFactory;
import susman.cs.ncat.edu.dataset.Sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Class to hold multiple detector sets
// Read detector sets from file and init detector set objects
public class AIS {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(AIS.class);
    public final String DNN_IP_ADDR = "localhost";
    public final int DNN_PORT = 2021;
    public final int REGEN_PORT = 2022;
    public final int IMMATURE_INCORRECT_MATCHES_THRESHOLD = 10;
    public final String DETECTOR_DIRECTORY = System.getProperty("user.dir") + Sys.FILE_SEP + "detectors";

    // Single static instance to be used throughout the project
    private static AIS Instance = new AIS();

    // List of detector set (for each malicious type)
    private List<DetectorSet> detectorSetList;

    // Construcotr
    private AIS () {
        super();
    }

    // Returns the static instance
    public static AIS getInstance() {
        return Instance;
    }

    // Initialize the static instance
    public AIS init () {
        detectorSetList = new ArrayList();

        readDetectorSets();

        return Instance;
    }

    // Read the detector set files from a directory
    public void readDetectorSets() {

        File detectorSetDir = new File(DETECTOR_DIRECTORY);

        if (!detectorSetDir.exists()) {
                logger.error(DETECTOR_DIRECTORY + " does not exist");
                System.exit(-1);
        }

        File[] detectorSetDirListing = detectorSetDir.listFiles();

        assert detectorSetDirListing != null;
        for (File f : detectorSetDirListing) {
            readSet(f);
        }

    }

    // Read a single detector set file
    // Create a Detector Set
    // Add the detectors from the file to the detector set
    public void readSet (File file) {
        DetectorSet detectorSet = new DetectorSet();
        detectorSet.setType(file.getName().split("\\.")[0]);
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
        this.detectorSetList.add(detectorSet);
    }

    // Replicate a sample to be classified by all the Detector Sets
    public synchronized void addSample(Sample sample) {
        for (DetectorSet ds : detectorSetList) {
            ds.addNewSample(sample);
        }
    }

    public List<DetectorSet> getDetectorSetList () {
        return this.detectorSetList;
    }
}
