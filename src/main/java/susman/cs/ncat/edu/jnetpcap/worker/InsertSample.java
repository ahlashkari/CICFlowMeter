package susman.cs.ncat.edu.jnetpcap.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import susman.cs.ncat.edu.ais.AIS;
import susman.cs.ncat.edu.dataset.DataSet;
import susman.cs.ncat.edu.dataset.Normalizer;
import susman.cs.ncat.edu.dataset.Sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.net.Socket;

public class InsertSample implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(InsertSample.class);
    private String header;
    private List<String> rows;
    private String[] features;

    public InsertSample(String header, String[] features) {
        this.header = header;
        this.features = features;
    }

    @Override
    public void run() {
        insert(header,features);
    }

    public static void insert(String header,String[]  dataList) {
        if (dataList == null || dataList.length <= 0) {
            throw new IllegalArgumentException("No features to write");
        }

        // Send to python socket
        // Create Sample with normalized values
        // Add Sample to DetectorSetQueues
        Sample sample = new Sample(Normalizer.getInstance().normalize(dataList));


        AIS.getInstance().addSample(sample);

    }
}
