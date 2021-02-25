package susman.cs.ncat.edu.ifm;

import cic.cs.unb.ca.flow.FlowMgr;
import susman.cs.ncat.edu.ais.AIS;
import susman.cs.ncat.edu.dataset.DataSet;

public class Cmd {
    public static void init () {
        AIS.getInstance().init();
        DataSet.getInstance().init();

    }

    public static void main (String[] args) {
        init();
        System.out.println("Done");
        // Data Set Processing Thread
        // Pop off queue a flow
        // Normalize the flow into a sample
        // Replicate the sample to each detector set queue

        // Create threads for each detector set
        // Each thread has a queue of samples

        // Create a thread socket to listen for incoming connections from the DNN or human
        // Incoming packets should contain detector id and true/false for validated/invalidated

        // Collect the network interfaces
        // Print the network interfaces
        // Ask the user which interface to run
        // Based on the user selection, run the listen on that interface in the background

        // Create thread to wait for flows to be generated (listener)
        // If a flow is generated, send it to the data set processing queue
    }
}
