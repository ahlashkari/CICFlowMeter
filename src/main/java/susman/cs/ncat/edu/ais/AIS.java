package susman.cs.ncat.edu.ais;

import cic.cs.unb.ca.Sys;
import cic.cs.unb.ca.flow.ui.FlowMonitorPane;
import org.slf4j.LoggerFactory;
import susman.cs.ncat.edu.dataset.Sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class AIS {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(AIS.class);

    private static AIS Instance = new AIS();

    // Class to hold multiple detector sets
    // Read detector sets from file and init detector set objects

    private List<DetectorSet> detectorSetList;

    private AIS () {
        super();
    }

    public static AIS getInstance() {
        return Instance;
    }

    public AIS init (String dir) {
        detectorSetList = new ArrayList<>();

        readDetectorSets(dir);

        return Instance;
    }

    public void readDetectorSets(String dir) {
            File detectorSetDir = new File(dir);

            if (!detectorSetDir.exists()) {
                logger.error(dir + " does not exist");
                System.exit(-1);
            }

            File[] detectorSetDirListing = detectorSetDir.listFiles();

            for (File f : detectorSetDirListing) {
                readSet(f);
            }

    }

    public void readSet (File file) {
        DetectorSet detectorSet = new DetectorSet();
        try {
            Scanner input = new Scanner(file);

            while (input.hasNextLine()) {
                String line = input.nextLine();

                Detector detector = new Detector (line);

                detectorSet.addDetector(detector);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.detectorSetList.add(detectorSet);
    }

    public void addSample(Sample sample) {
        for (DetectorSet ds : detectorSetList) {
            ds.addToQueue(sample);
        }
    }
}
