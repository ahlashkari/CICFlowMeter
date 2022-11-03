package cic.cs.unb.ca.jnetpcap.worker;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowGenerator;
import cic.cs.unb.ca.jnetpcap.PacketReader;
import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JMemory.Type;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

public class TrafficFlowWorker extends SwingWorker<String,String> implements FlowGenListener{

	public static final Logger logger = LoggerFactory.getLogger(TrafficFlowWorker.class);
    public static final String PROPERTY_FLOW = "flow";
	private String device;


    public TrafficFlowWorker(String device) {
		super();
		this.device = device;
	}

	@Override
	protected String doInBackground() {
		
		FlowGenerator   flowGen = new FlowGenerator(true,5*1000L, 1*1000L);
		flowGen.addFlowListener(this);
		int snaplen = 64 * 1024;//2048; // Truncate packet at this size
		int promiscous = Pcap.MODE_PROMISCUOUS;
		int timeout = 5 * 1000; // In milliseconds
		StringBuilder errbuf = new StringBuilder();
		Pcap pcap = Pcap.openLive(device, snaplen, promiscous, timeout, errbuf);
		if (pcap == null) {
			logger.info("open {} fail -> {}",device,errbuf.toString());
			return String.format("open %s fail ->",device)+errbuf.toString();
		}

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

            PcapPacket permanent = new PcapPacket(Type.POINTER);
            packet.transferStateAndDataTo(permanent);

            flowGen.addPacket(PacketReader.getBasicPacketInfo(permanent, true, false));
            if(isCancelled()) {
                pcap.breakloop();
                logger.debug("break Packet loop");
            }
        };

        //FlowMgr.getInstance().setListenFlag(true);
        logger.info("Pcap is listening...");
        firePropertyChange("progress","open successfully","listening: "+device);
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

        return str;
	}

	@Override
	protected void process(List<String> chunks) {
		super.process(chunks);
	}

	@Override
	protected void done() {
		super.done();
	}

	@Override
	public synchronized void onFlowGenerated(BasicFlow flow) {
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
    	firePropertyChange(PROPERTY_FLOW,null,flow);
	}
}
