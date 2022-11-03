package edu.ncat.susman.dataset;


import cic.cs.unb.ca.jnetpcap.PcapIfWrapper;
import edu.ncat.susman.jnetpcap.worker.TrafficFlowWorker;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Constructor with interface name
// Begin listening on the interface
// When a flow is generated, send to the datasetqueuewrapper
public class DataSet {
    protected static final Logger logger = LoggerFactory.getLogger(DataSet.class);

    private static DataSet Instance = new DataSet();

    private String ifName;

    private TrafficFlowWorker mWorker;

    private List<PcapIfWrapper> pcapiflist;
    private int ifSelected = -1;

    public DataSet init () {
        Normalizer.getInstance().init();
        loadPcapIfs();
        return Instance;
    }

    public void close() {
        mWorker.close();
        mWorker.interrupt();
    }

    public static DataSet getInstance() {
        return Instance;
    }

    public String getIfName() {
        return ifName;
    }

    public void setIfName(String ifName) {
        this.ifName = ifName;
    }

    private void loadPcapIfs() {

        StringBuilder errbuf = new StringBuilder();

        // Create a list to hold all network interfaces
        List<PcapIf> ifs = new ArrayList<>();

        // Using findAllDevs passing the list above
        // The list is populated by the network interfaces
        // If the result is not Pcap.OK
        // Throw an exception
        if (Pcap.findAllDevs(ifs, errbuf) != Pcap.OK) {
            logger.error("Error occured: " + errbuf.toString());
            System.exit(-1);
        }

        // Add the list of network interfaces to a PcapIfWrapper
        pcapiflist = PcapIfWrapper.fromPcapIf(ifs);

        System.out.println("Select the network interface to monitor");
        // Add each element from the interfaces to the list
        for (int i = 0; i < pcapiflist.size(); i++) {
            System.out.println(i + " - " + pcapiflist.get(i).toString());
        }
        Scanner scan = new Scanner(System.in);
        int selection = scan.nextInt();

        if (selection < 0 || selection >= pcapiflist.size()) {
            logger.error("None valid interface");
            System.exit(-1);
        }

        setIfName(pcapiflist.get(selection).name());
        startTrafficFlow();

    }

    private void startTrafficFlow() {

        // Return if mWorker is curently working
        if (mWorker != null && mWorker.isAlive()) {
            return;
        }

        // Create a new TrafficFlowWorker passing the name of the selected interface
        mWorker = new TrafficFlowWorker(ifName);

        // Begin executing the TrafficFlowWorker
        mWorker.start();
    }

    public TrafficFlowWorker getmWorker() {
        return this.mWorker;
    }


}
