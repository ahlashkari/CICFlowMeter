package edu.ncat.susman.jnetpcap.worker;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowFeature;
import cic.cs.unb.ca.jnetpcap.FlowGenerator;
import cic.cs.unb.ca.jnetpcap.PacketReader;
import cic.cs.unb.ca.jnetpcap.worker.FlowGenListener;
import org.apache.commons.lang3.StringUtils;
import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory.Type;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrafficFlowWorker extends Thread implements FlowGenListener {

	public static final Logger logger = LoggerFactory.getLogger(TrafficFlowWorker.class);
	private String device;

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
        insertFlow(flow);
	}

	/** Writes flow to file
	 *  and writes flow to JTable
	 * @param flow
	 */
	private void insertFlow(BasicFlow flow) {
				// Using the csvWriterThread, insert a new csv row
		sampleWriterThread.execute(new InsertSample(flow));

	}

	@Override
	public void run() {

		// FlowGenerated class analyzes data and creates a flow
		FlowGenerator   flowGen = new FlowGenerator(true,120000000L, 5000000L);

		// Add this object to the FlowGenerator
		flowGen.addFlowListener(this);

		// Truncate packets
		int snaplen = 64 * 1024;//2048; // Truncate packet at this size

		// Set promiscuous mode to true
		int promiscous = Pcap.MODE_PROMISCUOUS;

		// Set the timeout value
		int timeout = 60 * 1000; // In milliseconds
		StringBuilder errbuf = new StringBuilder();

		// Begin listening
		Pcap pcap = Pcap.openLive(device, snaplen, promiscous, timeout, errbuf);
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
				pcap.breakloop();
				logger.debug("break Packet loop");
			}
		};

		//FlowMgr.getInstance().setListenFlag(true);
		logger.info("Pcap is listening...");
		//firePropertyChange("progress","open successfully","listening: "+device);
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
}
