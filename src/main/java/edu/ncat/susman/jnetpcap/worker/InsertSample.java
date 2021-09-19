package edu.ncat.susman.jnetpcap.worker;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowFeature;
import edu.ncat.susman.ais.AIS;
import edu.ncat.susman.dataset.Sample;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.ncat.susman.dataset.Normalizer;

import java.util.ArrayList;
import java.util.List;

public class InsertSample implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(InsertSample.class);
    private BasicFlow flow;

    public InsertSample(BasicFlow flow) {
        this.flow = flow;
    }

    @Override
    public void run() {
        insert();
    }

    public void insert() {
        java.util.List<String> flowStringList = new ArrayList<>();
        List<String[]> flowDataList = new ArrayList<>();

        // Get the list of features from the flow comma separated
        String flowDump = this.flow.dumpFlowBasedFeaturesEx();

        //System.out.println("Flow Dump: " + flowDump);

        // Add the flow's features to the flowString List
        //flowStringList.add(flowDump);

        // Add the flow's individual features to the flowDataList
        String[] dataList = StringUtils.split(flowDump, ",");
        //flowDataList.add(features);

		/*String str = "";
		for (String f: dataList) {
			str += f + ",";
		}
		str += "\n";
		logger.info(str);*/

        //write flows to csv file
        String header  = FlowFeature.getHeader();
        // String path = FlowMgr.getInstance().getSavePath();
        // String filename = LocalDate.now().toString() + FlowMgr.FLOW_SUFFIX;
        if (dataList == null || dataList.length <= 0) {
            throw new IllegalArgumentException("No features to write");
        }

        // Send to python socket
        // Create Sample with normalized values
        // Add Sample to DetectorSetQueues
        Sample sample = new Sample(Normalizer.getInstance().normalize(dataList));
        sample.setSrcIP(flow.getSrcIP());
        sample.setDstIP(flow.getDstIP());
        sample.setSrcPort(String.valueOf(flow.getSrcPort()));
        sample.setDstPort(String.valueOf(flow.getDstPort()));
        sample.setProtocol(String.valueOf(flow.getProtocol()));

        AIS.getInstance().addSample(sample);

    }
}
