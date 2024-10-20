package cic.cs.unb.ca.ifm;

import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.jnetpcap.*;
import cic.cs.unb.ca.jnetpcap.worker.FlowGenListener;
import cic.cs.unb.ca.jnetpcap.worker.TrafficFlowWorker;
import org.apache.commons.io.FilenameUtils;
import org.jnetpcap.PcapClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cic.cs.unb.ca.jnetpcap.worker.InsertCsvRow;
import swing.common.SwingUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static cic.cs.unb.ca.Sys.FILE_SEP;

public class RealCmd {

    public static final Logger logger = LoggerFactory.getLogger(Cmd.class);
    private static final String DividingLine = "-------------------------------------------------------------------------------";
    private static String[] animationChars = new String[]{"|", "/", "-", "\\"};

    public static void main(String[] args) {

        long flowTimeout = 120000000L;
        long activityTimeout = 5000000L;
        String outPath;

        // Checking if the interface argument is provided
        if (args.length < 1) {
            logger.info("Please select a network interface!");
            return;
        }
        
        String networkInterface = args[0];  // Network interface name
        logger.info("Selected interface: {}", networkInterface);

        // Select output path for CSV
        if (args.length < 2) {
            logger.info("Please select output folder!");
            return;
        }
        outPath = args[1];
        File out = new File(outPath);
        if (out == null || out.isFile()) {
            logger.info("The output folder does not exist! -> {}", outPath);
            return;
        }

        logger.info("Output folder: {}", outPath);

        // Start the TrafficFlowWorker on the selected network interface
        listenOnNetworkInterface(networkInterface, outPath, flowTimeout, activityTimeout);
    }

    private static void listenOnNetworkInterface(String networkInterface, String outPath, long flowTimeout, long activityTimeout) {
        // Start capturing traffic on the provided network interface
        TrafficFlowWorker worker = new TrafficFlowWorker(networkInterface);

        // Attach a listener to handle flows and save them
        worker.addPropertyChangeListener(event -> {
            if (TrafficFlowWorker.PROPERTY_FLOW.equalsIgnoreCase(event.getPropertyName())) {
                BasicFlow flow = (BasicFlow) event.getNewValue();
                String flowDump = flow.dumpFlowBasedFeaturesEx();
                List<String> flowStringList = new ArrayList<>();
                flowStringList.add(flowDump);
                InsertCsvRow.insert(FlowFeature.getHeader(), flowStringList, outPath, networkInterface + FlowMgr.FLOW_SUFFIX);
        
                
                logger.info("Flow: {}", flowDump);
            }
        });

        try {
            worker.execute(); // Start the worker 
            worker.get();     
        } catch (Exception e) {
            logger.error("Error during network interface capture: ", e);
        }

        logger.info("Network capture on {} completed.", networkInterface);
    }

}
