# Intro
The CICFlowMeter is an open source tool that generates Biflows from pcap files, and extracts features from these flows.

CICFlowMeter is a network traffic flow generator available from here . It can be used to generate bidirectional flows, where the first packet determines the forward (source to destination) and backward (destination to source) directions, hence the statistical time-related features can be calculated separately in the forward and backward directions. Additional functionalities include, selecting features from the list of existing features, adding new features, and controlling the duration of flow timeout.

NOTE: TCP flows are usually terminated upon connection teardown (by FIN packet) while UDP flows are terminated by a flow timeout. The flow timeout value can be assigned arbitrarily by the individual scheme e.g., 600 seconds for both TCP and UDP.

For citation in your works and also understanding CICFlowMeter (formerly ISCXFlowMeter) completely, you can find below published paper:
Gerard Drapper Gil, Arash Habibi Lashkari, Mohammad Mamun, Ali A. Ghorbani, "Characterization of Encrypted and VPN Traffic Using Time-Related Features", In Proceedings of the 2nd International Conference on Information Systems Security and Privacy(ICISSP 2016) , pages 407-414, Rome , Italy


----------------------------------------

# Installation and executing:

Extract CICFlowMeterV3.zip

___Note: The only prerequisite is that "libpcap" library or WinPcap on windows systems, be pre-installed___


For Linux

> $ sudo apt-get install libpcap-dev


For windows
> download [winpcap](<https://www.winpcap.org/install/default.htm>)

## executing
Go to the extracted directory,enter the 'bin' folder

### linux
Open a terminal and run this command
```
//For GUI:
sudo ./CICFlowMeter

//For Command line:
./cfm "inputFolder" "outputFolder"
```
### windows
Lanunch the Comand Prompt and run this command
```
//for GUI:
CICFlowMeter.bat

//for Commond line:
cfm.bat "inputFolder" "outputFolder"
```

## Get started
for offline
```
1.Select the folder that include your PCAP files
2.Select the folder that you would like to save you CSV files
3.Click OK button
```

for realtime
```
1 CLick Load button to find the list of network interfaces
2 Select the interface you would like to monitor
3 Click start button and wait for a while
4 Click stop button to stop the process and save the csv in same applcation folder/data/daily
```

--------------------------------------------------------------

Contact us at A.Habibi.L@unb.ca if there are any problems.


For citation in your works and also understanding CICFlowMeter (formerly ISCXFlowMeter) completely, you can find below published papers:

Arash Habibi Lashkari, Gerard Draper-Gil, Mohammad Saiful Islam Mamun and Ali A. Ghorbani, "Characterization of Tor Traffic Using Time Based Features", In the proceeding of the 3rd International Conference on Information System Security and Privacy, SCITEPRESS, Porto, Portugal, 2017

Gerard Drapper Gil, Arash Habibi Lashkari, Mohammad Mamun, Ali A. Ghorbani, "Characterization of Encrypted and VPN Traffic Using Time-Related Features", In Proceedings of the 2nd International Conference on Information Systems Security and Privacy(ICISSP 2016) , pages 407-414, Rome , Italy


--------------------------------------------------------------
List of extracted features and descriptions:
Feature Name				Description
Flow duration			Duration of the flow in Microsecond
total Fwd Packet		Total packets in the forward direction
total Bwd packets		Total packets in the backward direction
total Length of Fwd Packet	Total size of packet in forward direction
total Length of Bwd Packet	Total size of packet in backward direction
Fwd Packet Length Min 		Minimum size of packet in forward direction
Fwd Packet Length Max 		Maximum size of packet in forward direction
Fwd Packet Length Mean		Mean size of packet in forward direction
Fwd Packet Length Std		Standard deviation size of packet in forward direction
Bwd Packet Length Min		Minimum size of packet in backward direction
Bwd Packet Length Max		Maximum size of packet in backward direction
Bwd Packet Length Mean		Mean size of packet in backward direction
Bwd Packet Length Std		Standard deviation size of packet in backward direction
Flow Byte/s			Number of flow packets per second
Flow Packets/s			Number of flow bytes per second 
Flow IAT Mean			Mean time between two packets sent in the flow
Flow IAT Std			Standard deviation time between two packets sent in the flow
Flow IAT Max			Maximum time between two packets sent in the flow
Flow IAT Min			Minimum time between two packets sent in the flow
Fwd IAT Min			Minimum time between two packets sent in the forward direction
Fwd IAT Max			Maximum time between two packets sent in the forward direction
Fwd IAT Mean			Mean time between two packets sent in the forward direction
Fwd IAT Std			Standard deviation time between two packets sent in the forward direction
Fwd IAT Total   		Total time between two packets sent in the forward direction
Bwd IAT Min			Minimum time between two packets sent in the backward direction
Bwd IAT Max			Maximum time between two packets sent in the backward direction
Bwd IAT Mean			Mean time between two packets sent in the backward direction
Bwd IAT Std			Standard deviation time between two packets sent in the backward direction
Bwd IAT Total			Total time between two packets sent in the backward direction
Fwd PSH flag			Number of times the PSH flag was set in packets travelling in the forward direction (0 for UDP)
Bwd PSH Flag			Number of times the PSH flag was set in packets travelling in the backward direction (0 for UDP)
Fwd URG Flag			Number of times the URG flag was set in packets travelling in the forward direction (0 for UDP)
Bwd URG Flag			Number of times the URG flag was set in packets travelling in the backward direction (0 for UDP)
Fwd Header Length		Total bytes used for headers in the forward direction
Bwd Header Length		Total bytes used for headers in the backward direction
FWD Packets/s			Number of forward packets per second
Bwd Packets/s			Number of backward packets per second
Min Packet Length 		Minimum length of a packet
Max Packet Length 		Maximum length of a packet
Packet Length Mean 		Mean length of a packet
Packet Length Std		Standard deviation length of a packet
Packet Length Variance  	Variance length of a packet
FIN Flag Count 			Number of packets with FIN
SYN Flag Count 			Number of packets with SYN
RST Flag Count 			Number of packets with RST
PSH Flag Count 			Number of packets with PUSH
ACK Flag Count 			Number of packets with ACK
URG Flag Count 			Number of packets with URG
CWR Flag Count 			Number of packets with CWE
ECE Flag Count 			Number of packets with ECE
down/Up Ratio			Download and upload ratio
Average Packet Size 		Average size of packet
Avg Fwd Segment Size 		Average size observed in the forward direction
AVG Bwd Segment Size 		Average number of bytes bulk rate in the backward direction
Fwd Header Length		Length of the forward packet header
Fwd Avg Bytes/Bulk		Average number of bytes bulk rate in the forward direction
Fwd AVG Packet/Bulk 		Average number of packets bulk rate in the forward direction
Fwd AVG Bulk Rate 		Average number of bulk rate in the forward direction
Bwd Avg Bytes/Bulk		Average number of bytes bulk rate in the backward direction
Bwd AVG Packet/Bulk 		Average number of packets bulk rate in the backward direction
Bwd AVG Bulk Rate 		Average number of bulk rate in the backward direction
Subflow Fwd Packets		The average number of packets in a sub flow in the forward direction
Subflow Fwd Bytes		The average number of bytes in a sub flow in the forward direction
Subflow Bwd Packets		The average number of packets in a sub flow in the backward direction
Subflow Bwd Bytes		The average number of bytes in a sub flow in the backward direction
Init_Win_bytes_forward		The total number of bytes sent in initial window in the forward direction
Init_Win_bytes_backward		The total number of bytes sent in initial window in the backward direction
Act_data_pkt_forward		Count of packets with at least 1 byte of TCP data payload in the forward direction
min_seg_size_forward		Minimum segment size observed in the forward direction
Active Min			Minimum time a flow was active before becoming idle
Active Mean			Mean time a flow was active before becoming idle
Active Max			Maximum time a flow was active before becoming idle
Active Std			Standard deviation time a flow was active before becoming idle
Idle Min			Minimum time a flow was idle before becoming active
Idle Mean			Mean time a flow was idle before becoming active
Idle Max			Maximum time a flow was idle before becoming active
Idle Std			Standard deviation time a flow was idle before becoming active

