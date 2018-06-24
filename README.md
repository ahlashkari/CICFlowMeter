# Intro
The CICFlowMeter is an open source tool that generates Biflows from pcap files, and extracts features from these flows.

CICFlowMeter is a network traffic flow generator available from here . It can be used to generate bidirectional flows, where the first packet determines the forward (source to destination) and backward (destination to source) directions, hence the statistical time-related features can be calculated separately in the forward and backward directions. Additional functionalities include, selecting features from the list of existing features, adding new features, and controlling the duration of flow timeout.

NOTE: TCP flows are usually terminated upon connection teardown (by FIN packet) while UDP flows are terminated by a flow timeout. The flow timeout value can be assigned arbitrarily by the individual scheme e.g., 600 seconds for both TCP and UDP.

For citation in your works and also understanding CICFlowMeter (formerly ISCXFlowMeter) completely, you can find below published papers:

Arash Habibi Lashkari, Gerard Draper-Gil, Mohammad Saiful Islam Mamun and Ali A. Ghorbani, "Characterization of Tor Traffic Using Time Based Features", In the proceeding of the 3rd International Conference on Information System Security and Privacy, SCITEPRESS, Porto, Portugal, 2017

Gerard Drapper Gil, Arash Habibi Lashkari, Mohammad Mamun, Ali A. Ghorbani, "Characterization of Encrypted and VPN Traffic Using Time-Related Features", In Proceedings of the 2nd International Conference on Information Systems Security and Privacy(ICISSP 2016) , pages 407-414, Rome , Italy


Contact us at A.Habibi.L@unb.ca if there are any problems.



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

# Development
## Install jnetpcap local repo

for linux, sudo is a prerequisite
```
//linux :at the pathtoproject/jnetpcap/linux/jnetpcap-1.4.r1425
//windows: at the pathtoproject/jnetpcap/win/jnetpcap-1.4.r1425
mvn install:install-file -Dfile=jnetpcap.jar -DgroupId=org.jnetpcap -DartifactId=jnetpcap -Dversion=1.4.1 -Dpackaging=jar
```

## Run
### IntelliJ IDEA
open a Terminal in the IDE
```
//linux:
$ sudo bash
$ gradle execute

//windows:
$ gradlew execute
```
### Eclipse

Run eclipse with sudo
```
1. Right click App.java -> Run As -> Run Configurations -> Arguments -> VM arguments:
-Djava.library.path="pathtoproject/jnetpcap/linux/jnetpcap-1.4.r1425"  -> Run

2. Right click App.java -> Run As -> Java Application

```

## Make package

### IntelliJ IDEA
open a Terminal in the IDE
```
//linux:
$ gradle distZip
http://www.scitepress.org/DigitalLibrary/PublicationsDetail.aspx?ID=g4gLnPa/2OM=&t=1
//window
$ gradlew distZip
```
the zip file will be in the pathtoproject/CICFlowMeter/build/distributions

### Eclipse
At the project root
```
mvn package
```
the jar file will be in the pathtoproject/CICFlowMeter/target




----------------------------------------------------------------------
# CICFlowMeter Features

Duration:Duration of the flow in Microsecond

total_fpackets:Total packets in the forward direction

total_bpackets:Total packets in the backward direction

total_fpktl:Total size of packet in forward direction

total_bpktl:Total size of packet in backward direction

min_fpktl:Minimum size of packet in forward direction

min_bpktl:Minimum size of packet in backward direction

max_fpktl:Maximum size of packet in forward direction

max_bpktl:Maximum size of packet in backward direction

mean_fpktl:Mean size of packet in forward direction

mean_bpktl:Mean size of packet in backward direction

std_fpktl:Standard deviation size of packet in forward direction

std_bpktl:Standard deviation size of packet in backward direction

total_fiat:Total time between two packets sent in the forward direction

total_biat:Total time between two packets sent in the backward direction

min_fiat:Minimum time between two packets sent in the forward direction

min_biat:Minimum time between two packets sent in the backward direction

max_fiat:Maximum time between two packets sent in the forward direction

max_biat:Maximum time between two packets sent in the backward direction

mean_fiat:Mean time between two packets sent in the forward direction

mean_biat:Mean time between two packets sent in the backward direction

std_fiat:Standard deviation time between two packets sent in the forward
 direction

std_biat:Standard deviation time between two packets sent in the backward direction

fpsh_cnt:Number of times the PSH flag was set in packets travelling in the forward direction (0 for UDP)

bpsh_cnt:Number of times the PSH flag was set in packets travelling in the backward direction (0 for UDP)

furg_cnt:Number of times the URG flag was set in packets travelling in the forward direction (0 for UDP)

burg_cnt:Number of times the URG flag was set in packets travelling in the backward direction (0 for UDP)

total_fhlen: Total bytes used for headers in the forward direction

total_bhlen: Total bytes used for headers in the forward direction

fPktsPerSecond: Number of forward packets per second

bPktsPerSecond: Number of backward packets per second

flowPktsPerSecond: Number of flow packets per second

flowBytesPerSecond: Number of flow bytes per second

min_flowpktl: Minimum length of a flow

max_flowpktl: Maximum length of a flow

mean_flowpktl: Mean length of a flow

std_flowpktl: Standard deviation length of a flow

min_flowiat: Minimum inter-arrival time of packet

max_flowiat: Maximum inter-arrival time of packet

mean_flowiat: Mean inter-arrival time of packet

std_flowiat: Standard deviation inter-arrival time of packet

flow_fin: Number of packets with FIN

flow_syn: Number of packets with SYN

flow_rst: Number of packets with RST

flow_psh: Number of packets with PUSH

flow_ack: Number of packets with ACK

flow_urg: Number of packets with URG

flow_cwr: Number of packets with CWE

flow_ece: Number of packets with ECE

downUpRatio: Download and upload ratio

avgPacketSize: Average size of packet

fAvgSegmentSize: Average size observed in the forward direction

fAvgBytesPerBulk: Average number of bytes bulk rate in the forward direction

fAvgPacketsPerBulk: Average number of packets bulk rate in the forward
 direction

fAvgBulkRate: Average number of bulk rate in the forward direction

bAvgSegmentSize: Average size observed in the backward direction

bAvgBytesPerBulk: Average number of bytes bulk rate in the backward direction

bAvgPacketsPerBulk: Average number of packets bulk rate in the backward
 direction

bAvgBulkRate: Average number of bulk rate in the backward direction

sflow_fpacket: The average number of packets in a sub flow in the forward direction

sflow_fbytes: The average number of bytes in a sub flow in the forward direction

sflow_bpacket: The average number of packets in a sub flow in the backward direction

sflow_bbytes: The average number of bytes in a sub flow in the backward direction

min_active: Minimum time a flow was active before becoming idle

mean_active: Mean time a flow was active before becoming idle

max_active: Maximum time a flow was active before becoming idle

std_active: Standard deviation time a flow was active before becoming idle

min_idle: Minimum time a flow was idle before becoming active

mean_idle: Mean time a flow was idle before becoming active

max_idle: Maximum time a flow was idle before becoming active

std_idle: Standard deviation time a flow was idle before becoming active

Init_Win_bytes_forward: The total number of bytes sent in initial window in the forward direction

Init_Win_bytes_backward: The total number of bytes sent in initial window in the backward direction

Act_data_pkt_forward: Count of packets with at least 1 byte of TCP data payload in the forward direction

min_seg_size_forward: Minimum segment size observed in the forward direction

