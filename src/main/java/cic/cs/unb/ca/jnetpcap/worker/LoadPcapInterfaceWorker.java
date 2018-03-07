package cic.cs.unb.ca.jnetpcap.worker;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoadPcapInterfaceWorker extends SwingWorker<List<PcapIf>,String>{

	public static final Logger logger = LoggerFactory.getLogger(LoadPcapInterfaceWorker.class);
	
	public LoadPcapInterfaceWorker() {
		super();
	}

	@Override
	protected List<PcapIf> doInBackground() throws Exception {
		
		StringBuilder errbuf = new StringBuilder();
		List<PcapIf> ifs = new ArrayList<>();
		if(Pcap.findAllDevs(ifs, errbuf)!=Pcap.OK) {
			logger.error("Error occured: " + errbuf.toString());
			throw new Exception(errbuf.toString());
		}
		return ifs;
	}

	@Override
	protected void done() {
		super.done();
	}
}
