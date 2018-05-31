package cic.cs.unb.ca.flow.ui;

import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.FlowFeature;
import cic.cs.unb.ca.jnetpcap.worker.ReadPcapFileWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cic.cs.unb.ca.jnetpcap.worker.InsertCsvRow;
import swing.common.PcapFileFilter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlowOfflinePane extends JPanel{
    protected static final Logger logger = LoggerFactory.getLogger(FlowOfflinePane.class);
    private static final Border PADDING = BorderFactory.createEmptyBorder(10,5,10,5);
    private JFileChooser fileChooser;
    private PcapFileFilter pcapChooserFilter;
    private JTextArea textArea;
    private JButton btnClr;
    private JComboBox<File> cmbInput;
    private JComboBox<File> cmbOutput;
    private Vector<File> cmbInputEle;
    private Vector<File> cmbOutputEle;

    private JComboBox<Long> param1;
    private JComboBox<Long> param2;
    private Vector<Long> param1Ele;
    private Vector<Long> param2Ele;

    private Box progressBox;
    private JProgressBar fileProgress;
    private JProgressBar fileCntProgress;

    private ExecutorService csvWriterThread;

    public FlowOfflinePane() {

        init();

        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(initOutPane(), BorderLayout.CENTER);
        add(initCtrlPane(), BorderLayout.SOUTH);
    }

    private void init(){
        fileChooser = new JFileChooser(new File("."));
        pcapChooserFilter = new PcapFileFilter();
        fileChooser.setFileFilter(pcapChooserFilter);
        csvWriterThread = Executors.newSingleThreadExecutor();

    }

    public void destroy() {
        csvWriterThread.shutdown();
    }

    private JPanel initOutPane(){
        JPanel jPanel = new JPanel(new BorderLayout(5, 5));

        JScrollPane scrollPane = new JScrollPane();
        textArea = new JTextArea();
        textArea.setRows(36);
        textArea.setToolTipText("message");
        scrollPane.setViewportView(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x555555)));

        /*JPanel msgSettingPane = new JPanel();
        msgSettingPane.setLayout(new BoxLayout(msgSettingPane, BoxLayout.X_AXIS));

        btnClr = new JButton("Clear");
        msgSettingPane.add(Box.createHorizontalGlue());
        msgSettingPane.add(btnClr);

        btnClr.addActionListener(actionEvent -> textArea.setText(""));*/

        jPanel.add(scrollPane, BorderLayout.CENTER);
        jPanel.add(initOutStatusPane(), BorderLayout.SOUTH);

        return jPanel;
    }

    private JPanel initOutStatusPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));

        progressBox = Box.createVerticalBox();
        fileProgress = new JProgressBar();
        fileCntProgress = new JProgressBar();
        fileProgress.setBorderPainted(true);
        fileProgress.setStringPainted(true);
        fileCntProgress.setBorderPainted(true);
        fileCntProgress.setStringPainted(true);
        progressBox.add(fileProgress);
        progressBox.add(fileCntProgress);

        btnClr = new JButton("Clear");
        int height = fileProgress.getPreferredSize().height + fileCntProgress.getPreferredSize().height;
        Dimension d = new Dimension(80,height);
        btnClr.setPreferredSize(d);
        btnClr.setMaximumSize(d);
        btnClr.setMinimumSize(d);

        btnClr.addActionListener(actionEvent -> textArea.setText(""));

        progressBox.setVisible(false);

        pane.add(btnClr);
        pane.add(Box.createHorizontalStrut(18));
        pane.add(progressBox);

        return pane;
    }

    private JPanel initCtrlPane(){
        JPanel jPanel = new JPanel(new BorderLayout(5, 5));

        JPanel optPane = new JPanel();
        optPane.setLayout(new BoxLayout(optPane,BoxLayout.Y_AXIS));

        optPane.add(initFilePane());
        optPane.add(initSettingPane());
        optPane.add(initActionPane());

        jPanel.add(optPane, BorderLayout.CENTER);

        jPanel.setBorder(BorderFactory.createLineBorder(new Color(0x555555)));

        return jPanel;
    }

    private JPanel initFilePane(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        jPanel.setBorder(PADDING);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 0);


        JLabel lblInputDir = new JLabel("Pcap dir:");
        JButton btnInputBrowse = new JButton("Browse");
        cmbInputEle = new Vector<>();
        cmbInput = new JComboBox<>(cmbInputEle);
        cmbInput.setEditable(true);
        btnInputBrowse.addActionListener(actionEvent -> {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setFileFilter(pcapChooserFilter);
            int action = fileChooser.showOpenDialog(FlowOfflinePane.this);
            if (action == JFileChooser.APPROVE_OPTION) {
                File inputFile = fileChooser.getSelectedFile();
                logger.debug("offline select input {}", inputFile.getPath());
                setComboBox(cmbInput, cmbInputEle, inputFile);
            }
        });

        JLabel lblOutputDir = new JLabel("Output dir:");
        JButton btnOutputBrowse = new JButton("Browse");
        cmbOutputEle = new Vector<>();
        cmbOutput = new JComboBox<>(cmbOutputEle);
        cmbOutput.setEditable(true);
        btnOutputBrowse.addActionListener(actionEvent -> {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.removeChoosableFileFilter(pcapChooserFilter);
            int action = fileChooser.showOpenDialog(FlowOfflinePane.this);
            if (action == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                logger.debug("offline select output {}", outputFile.getPath());
                setComboBox(cmbOutput, cmbOutputEle, outputFile);
            }
        });

        //first row
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        gc.weighty = 0.1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        //gc.insets = new Insets(10, 5, 10, 5);
        jPanel.add(lblInputDir, gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 0.1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        //gc.insets = new Insets(0, 10, 0, 0);
        gc.insets.left = gc.insets.right = 10;
        jPanel.add(cmbInput, gc);

        gc.gridx = 2;
        gc.gridy = 0;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        //gc.insets = new Insets(10, 5, 10, 0);
        jPanel.add(btnInputBrowse, gc);

        //second row
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.weighty = 0.1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        //gc.insets = new Insets(10, 5, 10, 5);
        jPanel.add(lblOutputDir, gc);

        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        //gc.insets = new Insets(0, 10, 0, 0);
        gc.insets.left = gc.insets.right = 10;
        jPanel.add(cmbOutput, gc);

        gc.gridx = 2;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        //gc.insets = new Insets(10, 5, 10, 0);
        jPanel.add(btnOutputBrowse, gc);


        return jPanel;
    }

    private JPanel initSettingPane(){

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.X_AXIS));
        jPanel.setBorder(PADDING);

        JLabel lbl1 = new JLabel("Flow TimeOut:");
        param1Ele = new Vector<>();
        param1Ele.add(120000000L);
        param1 = new JComboBox<>(param1Ele);
        param1.setEditable(true);

        JLabel lbl2 = new JLabel("Activity Timeout:");
        param2Ele = new Vector<>();
        param2Ele.add(5000000L);
        param2 = new JComboBox<>(param2Ele);
        param2.setEditable(true);

        jPanel.add(lbl1);
        jPanel.add(param1);
        jPanel.add(Box.createHorizontalGlue());
        jPanel.add(lbl2);
        jPanel.add(param2);

        return jPanel;
    }

    private JPanel initActionPane() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.X_AXIS));
        jPanel.setBorder(PADDING);

        JButton btnOK = new JButton("OK");
        Dimension d = new Dimension(80,36);
        btnOK.setPreferredSize(d);
        btnOK.setMaximumSize(d);
        btnOK.setMinimumSize(d);
        jPanel.add(Box.createHorizontalGlue());
        jPanel.add(btnOK);
        jPanel.add(Box.createHorizontalGlue());
        btnOK.addActionListener(actionEvent -> startReadPcap());

        return jPanel;
    }

    private void setComboBox(JComboBox<File> combo, Vector<File> comboEle, File ele) {

        if (comboEle.contains(ele)) {
            combo.setSelectedItem(ele);
        } else {
            comboEle.addElement(ele);
            combo.setSelectedItem(comboEle.lastElement());
        }
    }

    private void updateOut(String str) {
        textArea.append(str);
        textArea.append(System.lineSeparator());
    }

    private long getComboParameter(JComboBox<Long> param,Vector<Long> paramEle) throws ClassCastException,NumberFormatException{
        long ret;
        int index = param.getSelectedIndex();
        String input;

        if (index < 0) {

            Object o = param.getEditor().getItem();
            if (o instanceof Long) {
                ret = (long) o;
            } else {
                input = (String) param.getEditor().getItem();
                ret = Long.valueOf(input);
            }
            paramEle.add(ret);

        } else {
            ret = paramEle.get(index);
        }
        return ret;
    }

    private void startReadPcap(){
        final File in;
        int cmbInIndex = cmbInput.getSelectedIndex();
        if (cmbInIndex < 0) {
            in = new File((String) cmbInput.getEditor().getItem());
        }else{
            in = cmbInputEle.get(cmbInIndex);
        }

        final File out;
        int cmbOutIndex = cmbOutput.getSelectedIndex();
        if (cmbOutIndex < 0) {
            out = new File((String) cmbOutput.getEditor().getItem());
        }else{
            out = cmbOutputEle.get(cmbOutIndex);
        }

        updateOut("You select: " + in.toString());
        updateOut("Out folder: " + out.toString());
        updateOut("-------------------------------");

        long flowTimeout;
        long activityTimeout;
        try {
            flowTimeout = getComboParameter(param1, param1Ele);
            activityTimeout = getComboParameter(param2, param2Ele);

            Map<String, Long> flowCnt = new HashMap<>();

            ReadPcapFileWorker worker = new ReadPcapFileWorker(in, out.getPath(), flowTimeout, activityTimeout);
            worker.addPropertyChangeListener(evt -> {
                ReadPcapFileWorker task = (ReadPcapFileWorker) evt.getSource();
                if ("progress".equals(evt.getPropertyName())) {
                    //logger.info("progress -> {}", evt.getNewValue());
                    List<String> chunks = (List<String>) evt.getNewValue();
                    if (chunks != null) {
                        SwingUtilities.invokeLater(() -> {
                            for (String str : chunks) {
                                updateOut(str);
                            }
                        });
                    }
                } else if ("state".equals(evt.getPropertyName())) {
                    switch (task.getState()) {
                        case STARTED:
                            progressBox.setVisible(true);
                            break;
                        case DONE:
                            progressBox.setVisible(false);
                            flowCnt.clear();
                            break;
                    }
                } else if (ReadPcapFileWorker.PROPERTY_FILE_CNT.equalsIgnoreCase(evt.getPropertyName())) {

                    int max = (int) evt.getOldValue();
                    int cur = (int) evt.getNewValue()+1;

                    fileCntProgress.setIndeterminate(false);
                    fileCntProgress.setMaximum(max);
                    fileCntProgress.setValue(cur);

                } else if (ReadPcapFileWorker.PROPERTY_CUR_FILE.equalsIgnoreCase(evt.getPropertyName())) {
                    fileProgress.setIndeterminate(true);
                    String curFile = (String) evt.getNewValue();
                    fileProgress.setString(curFile);
                    flowCnt.put(curFile, 0L);
                } else if (ReadPcapFileWorker.PROPERTY_FLOW.equalsIgnoreCase(evt.getPropertyName())) {

                    String fileName = (String) evt.getOldValue();
                    BasicFlow flow = (BasicFlow) evt.getNewValue();

                    flowCnt.put(fileName, flowCnt.get(fileName) + 1);

                    String msg = String.format("%d flows on Reading %s",flowCnt.get(fileName),fileName);
                    fileProgress.setString(msg);

                    //write flows to csv file
                    String header  = FlowFeature.getHeader();
                    csvWriterThread.execute(new InsertCsvRow(header, flow.dumpFlowBasedFeaturesEx(), out.getPath(), fileName+FlowMgr.FLOW_SUFFIX));
                }
            });
            worker.execute();
        } catch(ClassCastException | NumberFormatException e){
            logger.info("startRead: {}",e.getMessage());
            JOptionPane.showMessageDialog(FlowOfflinePane.this, "The parameter is not a number,please check and try again.", "Parameter error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
