package cic.cs.unb.ca.ifm;

import cic.cs.unb.ca.Sys;
import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.BasicPacketInfo;
import cic.cs.unb.ca.jnetpcap.FlowFeature;
import cic.cs.unb.ca.jnetpcap.FlowGenerator;
import cic.cs.unb.ca.jnetpcap.PacketReader;
import cic.cs.unb.ca.jnetpcap.PcapIfWrapper;
import cic.cs.unb.ca.jnetpcap.worker.FlowGenListener;
import cic.cs.unb.ca.jnetpcap.worker.InsertCsvRow;
import cic.cs.unb.ca.jnetpcap.worker.LoadPcapInterfaceWorker;
import cic.cs.unb.ca.jnetpcap.worker.TrafficFlowWorker;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
import org.jnetpcap.PcapClosedException;
import org.jnetpcap.PcapIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.common.SwingUtils;

public class Cmd2 {
  public static final Logger logger = LoggerFactory.getLogger(Cmd.class);
  
  private static final String DividingLine = "-------------------------------------------------------------------------------";
  
  private static boolean loadDone = false;
  
  private static boolean captureDone = false;
  
  private static ExecutorService csvWriterThread;
  
  private static DefaultListModel<PcapIfWrapper> listModel;
  
  public static void main(String[] args) {
    String pcapPath, outPath;
    File in, out;
    long flowTimeout = 120000000L;
    long activityTimeout = 5000000L;
    String rootPath = System.getProperty("user.dir");
    if (args.length < 1) {
      showUsage();
      return;
    } 
    switch (args[0]) {
      case "-l":
        logger.info("\nLoad the list of network interfaces.\n");
        try {
          SwingUtilities.invokeAndWait(Cmd2::loadPcapIfs);
        } catch (InterruptedException|java.lang.reflect.InvocationTargetException e) {
          logger.error(e.getMessage());
        } 
        return;
      case "-i":
        logger.info("\nCapture packets from the given interface in realtime.\n");
        try {
          initRealtimeCapture();
          SwingUtilities.invokeAndWait(() -> startTrafficFlow(args[1]));
        } catch (InterruptedException|java.lang.reflect.InvocationTargetException e) {
          logger.error(e.getMessage());
        } 
        while (!captureDone)
          pauseMainThread(60000L); 
        return;
      case "-r":
        logger.info("\nLoad interfaces and select the interface to capture.\n");
        try {
          initRealtimeCapture();
          SwingUtilities.invokeAndWait(Cmd2::loadPcapIfs);
        } catch (InterruptedException|java.lang.reflect.InvocationTargetException e) {
          logger.error(e.getMessage());
        } 
        while (!loadDone)
          pauseMainThread(1000L); 
        try {
          Scanner sc = new Scanner(System.in);
          System.out.print("\nChoose index of the interface to capture: ");
          int input = sc.nextInt();
          SwingUtilities.invokeAndWait(() -> startTrafficFlow(input));
        } catch (InterruptedException|java.lang.reflect.InvocationTargetException e) {
          logger.error(e.getMessage());
        } 
        while (!captureDone)
          pauseMainThread(60000L); 
        return;
      case "-o":
        logger.info("\nOffline pcap analysis.\n");
        if (args.length < 3) {
          showUsage();
          return;
        } 
        pcapPath = args[1];
        outPath = args[2];
        in = new File(pcapPath);
        if (!in.exists()) {
          logger.info("The pcap file or folder does not exist! -> {}", pcapPath);
          return;
        } 
        out = new File(outPath);
        if (out.isFile()) {
          logger.info("The out folder does not exist! -> {}", outPath);
          return;
        } 
        logger.info("You select: {}", pcapPath);
        logger.info("Out folder: {}", outPath);
        if (in.isDirectory()) {
          readPcapDir(in, outPath, flowTimeout, activityTimeout);
        } else if (!SwingUtils.isPcapFile(in)) {
          logger.info("Please select pcap file!");
        } else {
          logger.info("CICFlowMeter received 1 pcap file");
          readPcapFile(in.getPath(), outPath, flowTimeout, activityTimeout);
        } 
        return;
    } 
    showUsage();
  }
  
  private static void showUsage() {
    logger.info("\n\nUsage: sh cfm command [ \n-l load the list of interfaces \n-r load list of interfaces and next select the one to capture \n-i [name_of_interface] capture realtime packet from name_of_interface \n-o [input path/file] [output path] offline pcap analysis\n ]\n");
  }
  
  private static void initRealtimeCapture() {
    FlowMgr.getInstance().init();
    csvWriterThread = Executors.newSingleThreadExecutor();
  }
  
  private static void pauseMainThread(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    } 
  }
  
  private static void loadPcapIfs() {
    loadDone = false;
    listModel = new DefaultListModel<>();
    LoadPcapInterfaceWorker task = new LoadPcapInterfaceWorker();
    task.addPropertyChangeListener(event -> {
          if ("state".equals(event.getPropertyName())) {
            LoadPcapInterfaceWorker task1 = (LoadPcapInterfaceWorker)event.getSource();
            switch (task1.getState()) {
              case DONE:
                try {
                  List<PcapIf> ifs = (List<PcapIf>)task1.get();
                  List<PcapIfWrapper> pcapiflist = PcapIfWrapper.fromPcapIf(ifs);
                  int i = 0;
                  for (PcapIfWrapper w : pcapiflist) {
                    listModel.addElement(w);
                    System.out.println(i + " : " + w);
                    i++;
                  } 
                  loadDone = true;
                } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
                  logger.error(e.getMessage());
                } 
                break;
            } 
          } 
        });
    task.execute();
  }
  
  private static void startTrafficFlow(int id) {
    try {
      String ifName = ((PcapIfWrapper)listModel.get(id)).name();
      if (ifName != null)
        startTrafficFlow(ifName); 
    } catch (Exception e) {
      logger.error(e.getMessage());
    } 
  }
  
  private static void startTrafficFlow(String name) {
    TrafficFlowWorker task = new TrafficFlowWorker(name);
    task.addPropertyChangeListener(event -> {
          TrafficFlowWorker task1 = (TrafficFlowWorker)event.getSource();
          if ("progress".equals(event.getPropertyName())) {
            logger.info(String.valueOf(event.getNewValue()));
          } else if ("flow".equalsIgnoreCase(event.getPropertyName())) {
            insertFlow((BasicFlow)event.getNewValue());
          } else if ("state".equals(event.getPropertyName())) {
            switch (task1.getState()) {
              case DONE:
                try {
                  captureDone = true;
                  logger.info((String)task.get());
                } catch (CancellationException e) {
                  logger.info("Pcap stop listening");
                } catch (Exception e) {
                  logger.error(e.getMessage());
                } 
                break;
            } 
          } 
        });
    task.execute();
  }
  
  private static void insertFlow(BasicFlow flow) {
    List<String> flowStringList = new ArrayList<>();
    String flowDump = flow.dumpFlowBasedFeaturesEx();
    flowStringList.add(flowDump);
    String header = FlowFeature.getHeader();
    String path = FlowMgr.getInstance().getSavePath();
    String filename = LocalDate.now() + "_Flow.csv";
    csvWriterThread.execute((Runnable)new InsertCsvRow(header, flowStringList, path, filename));
  }
  
  private static void readPcapDir(File inputPath, String outPath, long flowTimeout, long activityTimeout) {
    if (inputPath == null || outPath == null)
      return; 
    File[] pcapFiles = inputPath.listFiles(SwingUtils::isPcapFile);
    int file_cnt = pcapFiles.length;
    System.out.println(String.format("CICFlowMeter found :%d pcap files", new Object[] { Integer.valueOf(file_cnt) }));
    for (int i = 0; i < file_cnt; i++) {
      File file = pcapFiles[i];
      if (!file.isDirectory()) {
        int cur = i + 1;
        System.out.println(String.format("==> %d / %d", new Object[] { Integer.valueOf(cur), Integer.valueOf(file_cnt) }));
        readPcapFile(file.getPath(), outPath, flowTimeout, activityTimeout);
      } 
    } 
    System.out.println("Completed!");
  }
  
  private static void readPcapFile(String inputFile, String outPath, long flowTimeout, long activityTimeout) {
    if (inputFile == null || outPath == null)
      return; 
    String fileName = FilenameUtils.getName(inputFile);
    if (!outPath.endsWith(Sys.FILE_SEP))
      outPath = outPath + Sys.FILE_SEP; 
    File saveFileFullPath = new File(outPath + fileName + "_Flow.csv");
    if (saveFileFullPath.exists() && 
      !saveFileFullPath.delete())
      System.out.println("Save file can not be deleted"); 
    FlowGenerator flowGen = new FlowGenerator(true, flowTimeout, activityTimeout);
    flowGen.addFlowListener(new FlowListener(fileName, outPath));
    boolean readIP6 = false;
    boolean readIP4 = true;
    PacketReader packetReader = new PacketReader(inputFile, readIP4, readIP6);
    System.out.println(String.format("Working on... %s", new Object[] { fileName }));
    int nValid = 0;
    int nTotal = 0;
    int nDiscarded = 0;
    long start = System.currentTimeMillis();
    int i = 0;
    while (true) {
      try {
        BasicPacketInfo basicPacket = packetReader.nextPacket();
        nTotal++;
        if (basicPacket != null) {
          flowGen.addPacket(basicPacket);
          nValid++;
        } else {
          nDiscarded++;
        } 
      } catch (PcapClosedException e) {
        break;
      } 
      i++;
    } 
    flowGen.dumpLabeledCurrentFlow(saveFileFullPath.getPath(), FlowFeature.getHeader());
    long lines = SwingUtils.countLines(saveFileFullPath.getPath());
    System.out.println(String.format("%s is done. total %d flows ", new Object[] { fileName, Long.valueOf(lines) }));
    System.out.println(String.format("Packet stats: Total=%d,Valid=%d,Discarded=%d", new Object[] { Integer.valueOf(nTotal), Integer.valueOf(nValid), Integer.valueOf(nDiscarded) }));
    System.out.println("-------------------------------------------------------------------------------");
  }
  
  static class FlowListener implements FlowGenListener {
    private String fileName;
    
    private String outPath;
    
    private long cnt;
    
    public FlowListener(String fileName, String outPath) {
      this.fileName = fileName;
      this.outPath = outPath;
    }
    
    public void onFlowGenerated(BasicFlow flow) {
      String flowDump = flow.dumpFlowBasedFeaturesEx();
      List<String> flowStringList = new ArrayList<>();
      flowStringList.add(flowDump);
      InsertCsvRow.insert(FlowFeature.getHeader(), flowStringList, this.outPath, this.fileName + "_Flow.csv");
      this.cnt++;
      String console = String.format("%s -> %d flows \r", new Object[] { this.fileName, Long.valueOf(this.cnt) });
      System.out.print(console);
    }
  }
}
