package cic.cs.unb.ca.ifm;

import java.io.File;
import java.io.FilenameFilter;

import cic.cs.unb.ca.jnetpcap.FlowFeature;
import org.jnetpcap.PcapClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cic.cs.unb.ca.jnetpcap.BasicPacketInfo;
import cic.cs.unb.ca.jnetpcap.FlowGenerator;
import cic.cs.unb.ca.jnetpcap.PacketReader;


public class CICFlowMeter {

	public static final Logger logger = LoggerFactory.getLogger(CICFlowMeter.class);
	public static void main(String[] args) {
		
		PacketReader    packetReader;
		BasicPacketInfo basicPacket = null;
		FlowGenerator   flowGen; //15000 useconds = 15ms
		
		boolean readIP6 = false;
		boolean readIP4 = true;
		
		int     totalFlows = 0;
		
		String rootPath = System.getProperty("user.dir");
		String pcapPath;
		String outpath;
		
		/* Select path for reading all .pcap files */
		if(args.length<1 || args[0]==null) {
			pcapPath = rootPath+"/data/in/";
		}else {
			pcapPath = args[0];
		}
		
		/* Select path for writing all .csv files */
		if(args.length<2 || args[1]==null) {
			outpath = rootPath+"/data/out/";
		}else {
			outpath = args[1];
		}
		
		//String[] files = new File(pcapPath).list();
		
		
		String[] pcapfiles = new File(pcapPath).list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith("pcap"));
			}});
		
		if(pcapfiles==null||pcapfiles.length==0) {
			logger.info("Sorry,no pcap files can be found under: {}",pcapPath);
			return;
		}
		
		logger.info("");
		logger.info("CICFlowMeterV2 found: {} Files.",pcapfiles.length);
	
		for(String file:pcapfiles){
			flowGen = new FlowGenerator(true,120000000L, 5000000L);
			packetReader = new PacketReader(pcapPath+file,readIP4,readIP6);
			logger.info("");
			logger.info("");
			logger.info("Working on... {} ", file);
	
			int nValid=0;
			int nTotal=0;
			int nDiscarded = 0;
			
			long start = System.currentTimeMillis();
						
		    while(true){
				try{
					basicPacket = packetReader.nextPacket();					
					nTotal++;					
					if(basicPacket!=null){
						flowGen.addPacket(basicPacket);
						nValid++;
					}else{
						nDiscarded++;
					}
				}catch(PcapClosedException e){
					break;
				}
			}		
			
			long end = System.currentTimeMillis();
			logger.info("Done! in {} seconds",((end-start)/1000));
			logger.info("\t Total packets: {}",nTotal);
			logger.info("\t Valid packets: {}",nValid);
			logger.info("\t Ignored packets:{} {} ", nDiscarded,(nTotal-nValid) );
			logger.info("PCAP duration {} seconds",((packetReader.getLastPacket()-packetReader.getFirstPacket())/1000));
			logger.info("----------------------------------------------------------------------------");
			totalFlows+=flowGen.dumpLabeledFlowBasedFeatures(outpath, file+"_ISCX.csv", FlowFeature.getHeader());
			//flowGen.dumpIPAddresses(outpath, file+"_IP-Addresses.csv");
			//flowGen.dumpTimeBasedFeatures(outpath, file+".csv");
			
		}
		logger.info("\n\n----------------------------------------------------------------------------\n TOTAL FLOWS GENERATED: {}",totalFlows);
		logger.info("----------------------------------------------------------------------------\n");
//		try {
//			Files.write(Paths.get("src/main/resources/executionLog.log"),flowGen.dumpFlows().getBytes());
//		} catch (IOException e) {			
//			e.printStackTrace();
//		}
	}
}
