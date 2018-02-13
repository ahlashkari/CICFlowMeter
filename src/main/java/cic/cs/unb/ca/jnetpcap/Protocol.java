package cic.cs.unb.ca.jnetpcap;

import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.vpn.L2TP;

public class Protocol {

	public Tcp  tcp;
	public Udp  udp;
	public Ip4  ipv4;
	public Ip6  ipv6;
	public L2TP l2tp;
	public Protocol() {
		super();
		tcp = new Tcp();
		udp = new Udp();
		ipv4 = new Ip4();
		ipv6 = new Ip6();
		l2tp = new L2TP();
	}
	
	
}
