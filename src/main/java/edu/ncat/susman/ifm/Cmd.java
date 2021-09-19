package edu.ncat.susman.ifm;

import edu.ncat.susman.ais.AIS;
import edu.ncat.susman.client.BCPServer;
import edu.ncat.susman.dataset.DataSet;

public class Cmd {
    public static void init () {
        AIS.getInstance().init();
        DataSet.getInstance().init();
        BCPServer.getInstance().init();
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
