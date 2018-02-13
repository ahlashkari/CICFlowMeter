package cic.cs.unb.ca.jnetpcap;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapClosedException;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JHeaderPool;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.application.Html;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.lan.SLL;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.sigtran.Sctp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.vpn.L2TP;
import org.jnetpcap.protocol.wan.PPP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketReader {

	private static final Logger logger = LoggerFactory.getLogger(PacketReader.class);
	private IdGenerator  generator = new IdGenerator();
	private Pcap pcapReader;
	
	private long firstPacket;
	private long lastPacket;
	
	private Tcp  tcp;
	private Udp  udp;
	private Ip4  ipv4;
	private Ip6  ipv6;
	private L2TP l2tp;
	private PcapHeader hdr;
	private JBuffer buf;
	
	private boolean readIP6;
	private boolean readIP4;
	
	public PacketReader(String filename) {
		super();	
		this.readIP4 = true;
		this.readIP6 = false;		
		this.config(filename);
	}
	
	public PacketReader(String filename, boolean readip4, boolean readip6) {
		super();	
		this.readIP4 = readip4;
		this.readIP6 = readip6;
		this.config(filename);
	}	
	
	private void config(String filename){
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		pcapReader = Pcap.openOffline(filename, errbuf);
		
		this.firstPacket = 0L;
		this.lastPacket = 0L;

		if (pcapReader == null) {
			logger.error("Error while opening file for capture: "+errbuf.toString());
			System.exit(-1);
		}else{
			this.tcp = new Tcp();
			this.udp = new Udp();
			this.ipv4 = new Ip4();
			this.ipv6 = new Ip6();
			this.l2tp = new L2TP();
			hdr = new PcapHeader(JMemory.POINTER);
			buf = new JBuffer(JMemory.POINTER);		
		}		
	}
	
	public BasicPacketInfo nextPacket(){
		 PcapPacket      packet;
		 BasicPacketInfo packetInfo = null;
		 try{
			 if(pcapReader.nextEx(hdr,buf) == Pcap.NEXT_EX_OK){
				 packet = new PcapPacket(hdr, buf);
				 packet.scan(Ethernet.ID);				 
				 
				 if(this.readIP4){					 
					 packetInfo = getIpv4Info(packet);
					 if (packetInfo == null && this.readIP6){
					 	packetInfo = getIpv6Info(packet);				 	
					 }					 
				 }else if(this.readIP6){
					 packetInfo = getIpv6Info(packet);
					 if (packetInfo == null && this.readIP4){
					 	packetInfo = getIpv4Info(packet);
					 }
				 }
				 
				 if (packetInfo == null){
					 packetInfo = getVPNInfo(packet);
				 }					 

			 }else{
				 throw new PcapClosedException();
			 }
		 }catch(PcapClosedException e){
			 logger.info("We have read All packets!");
			 throw e;
		 }catch(Exception ex){
			 ex.printStackTrace();
		 }
		 return packetInfo;
	}
	
	private BasicPacketInfo getIpv4Info(PcapPacket packet){
		BasicPacketInfo packetInfo = null;		
		try {
						
			if (packet.hasHeader(ipv4)){
				packetInfo = new BasicPacketInfo(this.generator);
				packetInfo.setSrc(this.ipv4.source());
				packetInfo.setDst(this.ipv4.destination());
				//packetInfo.setTimeStamp(packet.getCaptureHeader().timestampInMillis());
				packetInfo.setTimeStamp(packet.getCaptureHeader().timestampInMicros());
				
				if(this.firstPacket == 0L)
					this.firstPacket = packet.getCaptureHeader().timestampInMillis();
				this.lastPacket = packet.getCaptureHeader().timestampInMillis();

				if(packet.hasHeader(this.tcp)){
					packetInfo.setTCPWindow(tcp.window());
					packetInfo.setSrcPort(tcp.source());
					packetInfo.setDstPort(tcp.destination());
					packetInfo.setProtocol(6);
					packetInfo.setFlagFIN(tcp.flags_FIN());
					packetInfo.setFlagPSH(tcp.flags_PSH());
					packetInfo.setFlagURG(tcp.flags_URG());
					packetInfo.setFlagSYN(tcp.flags_SYN());
					packetInfo.setFlagACK(tcp.flags_ACK());
					packetInfo.setFlagECE(tcp.flags_ECE());
					packetInfo.setFlagCWR(tcp.flags_CWR());
					packetInfo.setFlagRST(tcp.flags_RST());
					packetInfo.setPayloadBytes(tcp.getPayloadLength());
					packetInfo.setHeaderBytes(tcp.getHeaderLength());
				}else if(packet.hasHeader(this.udp)){
					packetInfo.setSrcPort(udp.source());
					packetInfo.setDstPort(udp.destination());
					packetInfo.setPayloadBytes(udp.getPayloadLength());
					packetInfo.setHeaderBytes(udp.getHeaderLength());
					packetInfo.setProtocol(17);			
				}else {
					/*logger.debug("other packet Ethernet -> {}"+  packet.hasHeader(new Ethernet()));
					logger.debug("other packet Html     -> {}"+  packet.hasHeader(new Html()));
					logger.debug("other packet Http     -> {}"+  packet.hasHeader(new Http()));
					logger.debug("other packet SLL      -> {}"+  packet.hasHeader(new SLL()));
					logger.debug("other packet L2TP     -> {}"+  packet.hasHeader(new L2TP()));
					logger.debug("other packet Sctp     -> {}"+  packet.hasHeader(new Sctp()));
					logger.debug("other packet PPP      -> {}"+  packet.hasHeader(new PPP()));*/

					int headerCount = packet.getHeaderCount();
					for(int i=0;i<headerCount;i++) {
						JHeader header = JHeaderPool.getDefault().getHeader(i);
						//JHeader hh = packet.getHeaderByIndex(i, header);
						logger.debug("getIpv4Info: {} --description: {} ",header.getName(),header.getDescription());
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			packet.scan(ipv4.getId());
			String errormsg = "";
			errormsg+=e.getMessage()+"\n";
			//errormsg+=packet.getHeader(new Ip4())+"\n";
			errormsg+="********************************************************************************"+"\n";
			errormsg+=packet.toHexdump()+"\n";
			
			//System.out.println(errormsg);
			logger.error(errormsg);
			//System.exit(-1);
			return null;
		}
		
		return packetInfo;
	}
	
	private BasicPacketInfo getIpv6Info(PcapPacket packet){
		BasicPacketInfo packetInfo = null;
		try{
			if(packet.hasHeader(ipv6)){
				packetInfo = new BasicPacketInfo(this.generator);
				packetInfo.setSrc(this.ipv6.source());
				packetInfo.setDst(this.ipv6.destination());
				packetInfo.setTimeStamp(packet.getCaptureHeader().timestampInMillis());			
				
				if(packet.hasHeader(this.tcp)){						
					packetInfo.setSrcPort(tcp.source());
					packetInfo.setDstPort(tcp.destination());
					packetInfo.setPayloadBytes(tcp.getPayloadLength());
					packetInfo.setHeaderBytes(tcp.getHeaderLength());
					packetInfo.setProtocol(6);
				}else if(packet.hasHeader(this.udp)){
					packetInfo.setSrcPort(udp.source());
					packetInfo.setDstPort(udp.destination());
					packetInfo.setPayloadBytes(udp.getPayloadLength());
					packetInfo.setHeaderBytes(tcp.getHeaderLength());
					packetInfo.setProtocol(17);								
				}		
			}
		}catch(Exception e){
			e.printStackTrace();
			packet.scan(ipv6.getId());
			String errormsg = "";
			errormsg+=e.getMessage()+"\n";
			//errormsg+=packet.getHeader(new Ip6())+"\n";
			errormsg+="********************************************************************************"+"\n";
			errormsg+=packet.toHexdump()+"\n";
			
		//	System.out.println(errormsg);
			logger.error(errormsg);
			//System.exit(-1);
			return null;			
		}
				
		return packetInfo;
	}
	
	private BasicPacketInfo getVPNInfo(PcapPacket packet){		
		BasicPacketInfo packetInfo = null;		
		try {
			packet.scan(L2TP.ID);
			
			if (packet.hasHeader(l2tp)){				
		    	if(this.readIP4){		
		    		packet.scan(ipv4.getId());
		    		packetInfo = getIpv4Info(packet);
		    		if (packetInfo == null && this.readIP6){
		    			packet.scan(ipv6.getId());
		    			packetInfo = getIpv6Info(packet);				 	
		    		}					 
		    	}else if(this.readIP6){
		    		packet.scan(ipv6.getId());
		    		packetInfo = getIpv6Info(packet);
		    		if (packetInfo == null && this.readIP4){
		    			packet.scan(ipv4.getId());
		    			packetInfo = getIpv4Info(packet);
		    		}
		    	}				

			}
		} catch (Exception e) {
			e.printStackTrace();
			packet.scan(l2tp.getId());
			String errormsg = "";
			errormsg+=e.getMessage()+"\n";
			//errormsg+=packet.getHeader(new L2TP())+"\n";
			errormsg+="********************************************************************************"+"\n";
			errormsg+=packet.toHexdump()+"\n";
			
			//System.out.println(errormsg);
			logger.error(errormsg);
			//System.exit(-1);
			return null;
		}
		
		return packetInfo;
	}	

	public long getFirstPacket() {
		return firstPacket;
	}

	public void setFirstPacket(long firstPacket) {
		this.firstPacket = firstPacket;
	}

	public long getLastPacket() {
		return lastPacket;
	}

	public void setLastPacket(long lastPacket) {
		this.lastPacket = lastPacket;
	}	

	/*
	 * So far,The value of the field BasicPacketInfo.id is not used
	 * It doesn't matter just using a static IdGenerator for realtime PcapPacket reading
	 */
	private static IdGenerator idGen = new IdGenerator();
	public static BasicPacketInfo getBasicPacketInfo(PcapPacket packet,boolean readIP4, boolean readIP6) {
		BasicPacketInfo packetInfo = null;
		
		
		Protocol protocol = new Protocol();
		
		if(readIP4){					 
			packetInfo = getIpv4Info(packet,protocol);
			if (packetInfo == null && readIP6){
				packetInfo = getIpv6Info(packet,protocol);				 	
			}					 
		}else if(readIP6){
			packetInfo = getIpv6Info(packet,protocol);
			if (packetInfo == null && readIP4){
				packetInfo = getIpv4Info(packet,protocol);
			}
		}
		
		if (packetInfo == null){
			packetInfo = getVPNInfo(packet,protocol,readIP4,readIP6);
		}
		
		return packetInfo;
	}

	private static BasicPacketInfo getVPNInfo(PcapPacket packet,Protocol protocol,boolean readIP4, boolean readIP6) {		
		BasicPacketInfo packetInfo = null;
		try {
			packet.scan(L2TP.ID);
			
			if (packet.hasHeader(protocol.l2tp)){				
		    	if(readIP4){		
		    		packet.scan(protocol.ipv4.getId());
		    		packetInfo = getIpv4Info(packet,protocol);
		    		if (packetInfo == null && readIP6){
		    			packet.scan(protocol.ipv6.getId());
		    			packetInfo = getIpv6Info(packet,protocol);				 	
		    		}					 
		    	}else if(readIP6){
		    		packet.scan(protocol.ipv6.getId());
		    		packetInfo = getIpv6Info(packet,protocol);
		    		if (packetInfo == null && readIP4){
		    			packet.scan(protocol.ipv4.getId());
		    			packetInfo = getIpv4Info(packet,protocol);
		    		}
		    	}				

			}
		} catch (Exception e) {
			/*
			 * BufferUnderflowException while decoding header
			 * havn't fixed, so do not e.printStackTrace() 
			 */
			//e.printStackTrace();
			/*packet.scan(protocol.l2tp.getId());
			String errormsg = "";
			errormsg+=e.getMessage()+"\n";
			//errormsg+=packet.getHeader(new L2TP())+"\n";
			errormsg+="********************************************************************************"+"\n";
			errormsg+=packet.toHexdump()+"\n";
			logger.error(errormsg);*/
			return null;
		}
		
		return packetInfo;
	}

	private static BasicPacketInfo getIpv6Info(PcapPacket packet,Protocol protocol) {
		BasicPacketInfo packetInfo = null;
		try{
			if(packet.hasHeader(protocol.ipv6)){
				packetInfo = new BasicPacketInfo(idGen);
				packetInfo.setSrc(protocol.ipv6.source());
				packetInfo.setDst(protocol.ipv6.destination());
				packetInfo.setTimeStamp(packet.getCaptureHeader().timestampInMillis());			
				
				if(packet.hasHeader(protocol.tcp)){						
					packetInfo.setSrcPort(protocol.tcp.source());
					packetInfo.setDstPort(protocol.tcp.destination());
					packetInfo.setPayloadBytes(protocol.tcp.getPayloadLength());
					packetInfo.setHeaderBytes(protocol.tcp.getHeaderLength());
					packetInfo.setProtocol(6);
				}else if(packet.hasHeader(protocol.udp)){
					packetInfo.setSrcPort(protocol.udp.source());
					packetInfo.setDstPort(protocol.udp.destination());
					packetInfo.setPayloadBytes(protocol.udp.getPayloadLength());
					packetInfo.setHeaderBytes(protocol.tcp.getHeaderLength());
					packetInfo.setProtocol(17);								
				}		
			}
		}catch(Exception e){
			/*
			 * BufferUnderflowException while decoding header
			 * havn't fixed, so do not e.printStackTrace()
			 */
			//e.printStackTrace();
			/*packet.scan(protocol.ipv6.getId());
			String errormsg = "";
			errormsg+=e.getMessage()+"\n";
			//errormsg+=packet.getHeader(new Ip6())+"\n";
			errormsg+="********************************************************************************"+"\n";
			errormsg+=packet.toHexdump()+"\n";
			logger.error(errormsg);
			//System.exit(-1);*/
			return null;			
		}
				
		return packetInfo;
	}

	private static BasicPacketInfo getIpv4Info(PcapPacket packet,Protocol protocol) {
		BasicPacketInfo packetInfo = null;		
		try {
						
			if (packet.hasHeader(protocol.ipv4)){
				packetInfo = new BasicPacketInfo(idGen);
				packetInfo.setSrc(protocol.ipv4.source());
				packetInfo.setDst(protocol.ipv4.destination());
				//packetInfo.setTimeStamp(packet.getCaptureHeader().timestampInMillis());
				packetInfo.setTimeStamp(packet.getCaptureHeader().timestampInMicros());
				
				/*if(this.firstPacket == 0L)
					this.firstPacket = packet.getCaptureHeader().timestampInMillis();
				this.lastPacket = packet.getCaptureHeader().timestampInMillis();*/

				if(packet.hasHeader(protocol.tcp)){
					packetInfo.setTCPWindow(protocol.tcp.window());
					packetInfo.setSrcPort(protocol.tcp.source());
					packetInfo.setDstPort(protocol.tcp.destination());
					packetInfo.setProtocol(6);
					packetInfo.setFlagFIN(protocol.tcp.flags_FIN());
					packetInfo.setFlagPSH(protocol.tcp.flags_PSH());
					packetInfo.setFlagURG(protocol.tcp.flags_URG());
					packetInfo.setFlagSYN(protocol.tcp.flags_SYN());
					packetInfo.setFlagACK(protocol.tcp.flags_ACK());
					packetInfo.setFlagECE(protocol.tcp.flags_ECE());
					packetInfo.setFlagCWR(protocol.tcp.flags_CWR());
					packetInfo.setFlagRST(protocol.tcp.flags_RST());
					packetInfo.setPayloadBytes(protocol.tcp.getPayloadLength());
					packetInfo.setHeaderBytes(protocol.tcp.getHeaderLength());
				}else if(packet.hasHeader(protocol.udp)){
					packetInfo.setSrcPort(protocol.udp.source());
					packetInfo.setDstPort(protocol.udp.destination());
					packetInfo.setPayloadBytes(protocol.udp.getPayloadLength());
					packetInfo.setHeaderBytes(protocol.udp.getHeaderLength());
					packetInfo.setProtocol(17);			
				} else {
					int headerCount = packet.getHeaderCount();
					for(int i=0;i<headerCount;i++) {
						JHeader header = JHeaderPool.getDefault().getHeader(i);
						//JHeader hh = packet.getHeaderByIndex(i, header);
						logger.debug("getIpv4Info: {} --description: {} ",header.getName(),header.getDescription());
					}
				}
			}
		} catch (Exception e) {
			/*
			 * BufferUnderflowException while decoding header
			 * havn't fixed, so do not e.printStackTrace()
			 */
			//e.printStackTrace();
			/*packet.scan(protocol.ipv4.getId());
			String errormsg = "";
			errormsg+=e.getMessage()+"\n";
			//errormsg+=packet.getHeader(new Ip4())+"\n";
			errormsg+="********************************************************************************"+"\n";
			errormsg+=packet.toHexdump()+"\n";
			logger.error(errormsg);
			return null;*/
		}
		
		return packetInfo;
	}
}
