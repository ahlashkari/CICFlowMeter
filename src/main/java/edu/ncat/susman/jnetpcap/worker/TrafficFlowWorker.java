package edu.ncat.susman.jnetpcap.worker;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowFeature;
import cic.cs.unb.ca.jnetpcap.FlowGenerator;
import cic.cs.unb.ca.jnetpcap.PacketReader;
import cic.cs.unb.ca.jnetpcap.worker.FlowGenListener;
import edu.ncat.susman.Parameters;
import edu.ncat.susman.ais.AIS;
import edu.ncat.susman.dataset.Normalizer;
import edu.ncat.susman.dataset.Sample;
import org.apache.commons.lang3.StringUtils;
import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory.Type;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.LocalDate;

public class TrafficFlowWorker extends Thread implements FlowGenListener {

	public static final Logger logger = LoggerFactory.getLogger(TrafficFlowWorker.class);
	public static BufferedWriter writer;
	private static boolean exists;
	private String device;
	private Pcap pcap;


	private ExecutorService sampleWriterThread;


    public TrafficFlowWorker(String device) {
		super();
		init();
		this.device = device;



	}

	private void init() {
		sampleWriterThread = Executors.newSingleThreadExecutor();
	}

	// When a flow is generated, fire a property change to an event listener
	@Override
	public void onFlowGenerated(BasicFlow flow) {
		if (flow.getSrcIP().equals("192.168.1.49") || flow.getDstIP().equals("192.168.1.49")
				|| flow.getSrcIP().equals("192.168.1.59") || flow.getDstIP().equals("192.168.1.59")
				|| flow.getSrcIP().equals("8.6.0.1") || flow.getDstIP().equals("8.6.0.1")
				|| flow.getSrcIP().equals("192.168.1.4") || flow.getDstIP().equals("192.168.1.4")
				|| flow.getSrcIP().equals("192.168.1.170") || flow.getDstIP().equals("192.168.1.170")
				|| flow.getSrcIP().equals("192.168.1.155") || flow.getDstIP().equals("192.168.1.155")
				|| flow.getSrcIP().equals("192.168.1.115") || flow.getDstIP().equals("192.168.1.115")
				|| flow.getSrcIP().equals("192.168.1.229") || flow.getDstIP().equals("192.168.1.229")
				|| flow.getSrcIP().equals("192.168.1.1") || flow.getDstIP().equals("192.168.1.1")
				|| flow.getSrcIP().equals("8.8.8.8") || flow.getDstIP().equals("8.8.8.8")
				|| flow.getSrcIP().equals("8.8.4.4") || flow.getDstIP().equals("8.8.4.4")
				|| flow.getProtocol() == 0) {
			return;
		}
        insertFlow(flow);
	}

	/** Writes flow to file
	 *  and writes flow to JTable
	 * @param flow
	 */
	private void insertFlow(BasicFlow flow) {
		java.util.List<String> flowStringList = new ArrayList<>();
		List<String[]> flowDataList = new ArrayList<>();

		// Get the list of features from the flow comma separated
		String flowDump = flow.dumpFlowBasedFeaturesEx();

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

		// writeCSV(header, flowDump);

		// Send to python socket
		// Create Sample with normalized values
		// Add Sample to DetectorSetQueues
		Sample sample = new Sample(Normalizer.getInstance().normalize(dataList));
		sample.setSrcIP(flow.getSrcIP());
		sample.setDstIP(flow.getDstIP());
		sample.setSrcPort(String.valueOf(flow.getSrcPort()));
		sample.setDstPort(String.valueOf(flow.getDstPort()));
		sample.setProtocol(String.valueOf(flow.getProtocol()));
		sample.setHeader(header);
		sample.setFlowDump(flowDump);

		AIS.getInstance().addSample(sample);

	}

	@Override
	public void run() {

		// FlowGenerated class analyzes data and creates a flow
		FlowGenerator   flowGen = new FlowGenerator(true,5*1000L, 1*1000L);

		// Add this object to the FlowGenerator
		flowGen.addFlowListener(this);

		// Truncate packets
		int snaplen = 64 * 1024;//2048; // Truncate packet at this size

		// Set promiscuous mode to true
		int promiscous = Pcap.MODE_PROMISCUOUS;

		// Set the timeout value
		int timeout = 5 * 1000; // In milliseconds
		StringBuilder errbuf = new StringBuilder();

		// Begin listening
		pcap = Pcap.openLive(device, snaplen, promiscous, timeout, errbuf);
		if (pcap == null) {
			logger.info("open {} fail -> {}",device,errbuf.toString());
			//return String.format("open %s fail ->",device)+errbuf.toString();
		}

		// Create event handler for when a packet is received
		PcapPacketHandler<String> jpacketHandler = (packet, user) -> {

			/*
			 * BufferUnderflowException while decoding header
			 * that is because:
			 * 1.PCAP library is not multi-threaded
			 * 2.jNetPcap library is not multi-threaded
			 * 3.Care must be taken how packets or the data they referenced is used in multi-threaded environment
			 *
			 * typical rule:
			 * make new packet objects and perform deep copies of the data in PCAP buffers they point to
			 *
			 * but it seems not work
			 */

			// Getting a packet from pcap
			PcapPacket permanent = new PcapPacket(Type.POINTER);
			packet.transferStateAndDataTo(permanent);

			// Add packet to FlowGenerator for analysis
			flowGen.addPacket(PacketReader.getBasicPacketInfo(permanent, true, false));
			if(isInterrupted()) {
				assert pcap != null;
				pcap.breakloop();
				logger.debug("break Packet loop");
			}
		};

		//FlowMgr.getInstance().setListenFlag(true);
		logger.info("Pcap is listening...");
		//firePropertyChange("progress","open successfully","listening: "+device);
		assert pcap != null;
		int ret = pcap.loop(Pcap.DISPATCH_BUFFER_FULL, jpacketHandler, device);

		String str;
		switch (ret) {
			case 0:
				str = "listening: " + device + " finished";
				break;
			case -1:
				str = "listening: " + device + " error";
				break;
			case -2:
				str = "stop listening: " + device;
				break;
			default:
				str = String.valueOf(ret);
		}

		logger.info(str);
	}

	public void close() {
		pcap.close();
	}
}
