package cic.cs.unb.ca.flow.ui;

import cic.cs.unb.ca.weka.WekaFactory;
import cic.cs.unb.ca.weka.WekaXMeans;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.common.CsvPickerPane;
import swing.common.SwingUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import static cic.cs.unb.ca.jnetpcap.FlowFeature.*;

/**
 * Created by yzhang29 on 03/01/18.
 */
public class FlowVisualPane extends JDesktopPane implements CsvPickerPane.CsvSelect{
    protected static final Logger logger = LoggerFactory.getLogger(FlowVisualPane.class);

    private CsvPickerPane pickerPane;
    private FlowChartPane flowChartPane;
    private JProgressBar progressBar;


    private JTree graphTree;
    private Multimap<FlowFileInfo,FlowChartInfo> treeNodeData;

    public FlowVisualPane() {

        init();

        setLayout(new BorderLayout(0, 3));
        //setBorder(Constants.LINEBORDER);

        pickerPane = new CsvPickerPane(this);
        pickerPane.setFilter("Flow");
        pickerPane.setSelectListener(this);

        flowChartPane = new FlowChartPane();

        add(pickerPane, BorderLayout.NORTH);
        add(flowChartPane,BorderLayout.CENTER);
        add(initOptionPane(), BorderLayout.WEST);
    }

    public FlowVisualPane(File file) {
        this();

        visualFile(file);

    }

    private void init() {
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        treeNodeData = ArrayListMultimap.create();
    }

    public void visualFile(File file) {
        logger.info("visualFile {}",file.getPath());

        if (isFlowFileInfoExist(file)) {
            return;
        } else {
            flowChartPane.removeChart();
            final CreateXMeansWorker xMeansWorker = new CreateXMeansWorker(file);
            SwingUtils.setBorderLayoutPane(FlowVisualPane.this,progressBar,BorderLayout.SOUTH);
            xMeansWorker.execute();
        }
    }

    @Override
    public void onSelected(File file) {
        visualFile(file);
    }


    private JPanel initOptionPane() {
        JPanel pane = new JPanel(new BorderLayout());


        pane.add(initGraphTreePane(), BorderLayout.CENTER);
        pane.add(initGraphButtonPane(), BorderLayout.SOUTH);

        return pane;
    }


    private JScrollPane initGraphTreePane() {
        graphTree = new JTree(createTree());
        JScrollPane treeView = new JScrollPane(graphTree);

        return treeView;
    }

    private JPanel initGraphButtonPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane,BoxLayout.Y_AXIS));

        Box sizeBox = Box.createHorizontalBox();
        JLabel wlbl = new JLabel("Width: ");
        JSpinner widthSpinner;
        JSpinner heightSpinner;
        JLabel hlbl = new JLabel("Height: ");
        SpinnerNumberModel  widthSpinnerModel;
        SpinnerNumberModel  heightSpinnerModel;
        widthSpinnerModel = new SpinnerNumberModel(300,ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH,ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH,12);
        heightSpinnerModel = new SpinnerNumberModel(200,ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT,ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT,12);
        widthSpinner = new JSpinner(widthSpinnerModel);
        heightSpinner = new JSpinner(heightSpinnerModel);
        widthSpinner.setPreferredSize(heightSpinner.getPreferredSize());


        sizeBox.add(Box.createHorizontalStrut(16));
        sizeBox.add(wlbl);
        sizeBox.add(widthSpinner);
        sizeBox.add(Box.createHorizontalStrut(16));
        sizeBox.add(hlbl);
        sizeBox.add(heightSpinner);
        sizeBox.add(Box.createHorizontalStrut(16));
        sizeBox.setVisible(false);


        Dimension btnDim = new Dimension(116, 64);

        JButton zoomIn = new JButton("Zoom In");
        zoomIn.setPreferredSize(btnDim);
        zoomIn.addActionListener(actionEvent -> flowChartPane.zoomIn());

        JButton zoomOut = new JButton("Zoom Out");
        zoomOut.setPreferredSize(btnDim);
        zoomOut.addActionListener(actionEvent -> flowChartPane.zoomOut());


        JButton reset_size = new JButton("Reset size");
        reset_size.setPreferredSize(btnDim);
        reset_size.setMinimumSize(btnDim);
        reset_size.addActionListener(actionEvent -> flowChartPane.resetSize());

        JButton reset_scale = new JButton("Reset scale");
        reset_scale.setPreferredSize(btnDim);
        reset_scale.setMinimumSize(btnDim);
        reset_scale.addActionListener(actionEvent -> flowChartPane.resetScale());


        Box zoomBox = Box.createHorizontalBox();
        zoomBox.add(Box.createHorizontalStrut(16));
        zoomBox.add(zoomIn);
        zoomBox.add(Box.createHorizontalGlue());
        zoomBox.add(zoomOut);
        zoomBox.add(Box.createHorizontalStrut(16));


        Box resetBox = Box.createHorizontalBox();
        resetBox.add(Box.createHorizontalStrut(16));
        resetBox.add(reset_scale);
        resetBox.add(Box.createHorizontalGlue());
        resetBox.add(reset_size);
        resetBox.add(Box.createHorizontalStrut(16));

        pane.add(sizeBox);
        pane.add(Box.createVerticalStrut(24));
        pane.add(zoomBox);
        pane.add(Box.createVerticalStrut(24));
        pane.add(resetBox);
        pane.add(Box.createVerticalStrut(16));

        return pane;
    }

    private DefaultMutableTreeNode createTree() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Flow Chart");
//        DefaultMutableTreeNode branch1 = new DefaultMutableTreeNode("File Name");
//        DefaultMutableTreeNode graph1 = new DefaultMutableTreeNode(new FlowChartInfo("Flows By Protocol"));
//        DefaultMutableTreeNode graph2 = new DefaultMutableTreeNode(new FlowChartInfo("Flows By Src IP"));
//        DefaultMutableTreeNode graph3 = new DefaultMutableTreeNode(new FlowChartInfo("Flows By Dst IP"));
//        DefaultMutableTreeNode graph4 = new DefaultMutableTreeNode(new FlowChartInfo("Flows By Src Port"));
//        DefaultMutableTreeNode graph5 = new DefaultMutableTreeNode(new FlowChartInfo("Flows By Dst Port"));
//        branch1.add(graph1);
//        branch1.add(graph2);
//        branch1.add(graph3);
//        branch1.add(graph4);
//        branch1.add(graph5);


        //top.add(branch1);

        return top;
    }

    private void addChart2Tree(FlowFileInfo flowFileInfo, FlowChartInfo flowChartInfo) {
        DefaultTreeModel model = (DefaultTreeModel) graphTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        DefaultMutableTreeNode fileInfoNode=null;
        for(int i=0;i<root.getChildCount();i++) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) root.getChildAt(i);

            FlowFileInfo fileInfoInNode = (FlowFileInfo) treeNode.getUserObject();

            if (fileInfoInNode == flowFileInfo) {
                logger.debug("tree node -> {} exist",flowFileInfo.getFilepath().getPath());
                fileInfoNode = treeNode;
                break;
            }
        }

        if (fileInfoNode == null) {
            fileInfoNode = new DefaultMutableTreeNode(flowFileInfo);
        }

        fileInfoNode.add(new DefaultMutableTreeNode(flowChartInfo));
        root.add(fileInfoNode);
        model.reload();

        treeNodeData.put(flowFileInfo, flowChartInfo);
    }

    private boolean isFlowFileInfoExist(File file) {

        if (file == null) {
            return false;
        }

        for (FlowFileInfo info : treeNodeData.keySet()) {
            if (info.getFilepath().getPath().equalsIgnoreCase(file.getPath())) {
                return true;
            }
        }
        return false;

    }

    private class CreateXMeansWorker extends SwingWorker<FlowFileInfo, String> {

        File csv;

        CreateXMeansWorker(File csv) {

            this.csv = csv;
        }

        @Override
        protected FlowFileInfo doInBackground() {

            if (csv == null) {
                throw new IllegalArgumentException("csv cannot be null");
            }


            WekaXMeans xMeans = new WekaXMeans(WekaFactory.loadFlowCsv(csv));

            FlowFileInfo flowFileInfo = new FlowFileInfo(csv, xMeans);

            return flowFileInfo;
        }

        @Override
        protected void done() {
            super.done();
            try {
                FlowFileInfo flowFileInfo = get();
                buildChart(flowFileInfo);
                SwingUtils.setBorderLayoutPane(FlowVisualPane.this,null,BorderLayout.SOUTH);
            } catch (InterruptedException | ExecutionException e) {
                logger.debug(e.getMessage());
            }
        }
    }

    public void buildChart(FlowFileInfo info) {
        logger.info("buildChart");

        FlowChartWorkerFactory.BuildProtocolChartWorker protocol_worker = new FlowChartWorkerFactory.BuildProtocolChartWorker(info,FlowChartWorkerFactory.PIE_CHART);
        protocol_worker.addPropertyChangeListener(event -> {
            //logger.info("build Protocol chart");
            ChartWorkerPropertyChange(event, protocol_worker);
        });
        protocol_worker.execute();

        FlowChartWorkerFactory.BuildIPChartWorker sip_worker = new FlowChartWorkerFactory.BuildIPChartWorker(info,src_ip,FlowChartWorkerFactory.BAR_CHART);
        sip_worker.addPropertyChangeListener(event -> {
            //logger.info("build src ip chart");
            ChartWorkerPropertyChange(event, sip_worker);
        });
        sip_worker.execute();

        FlowChartWorkerFactory.BuildIPChartWorker dip_worker = new FlowChartWorkerFactory.BuildIPChartWorker(info,dst_ip,FlowChartWorkerFactory.BAR_CHART);
        dip_worker.addPropertyChangeListener(event -> {
            //logger.info("build dst ip chart");
            ChartWorkerPropertyChange(event, dip_worker);
        });
        dip_worker.execute();

        FlowChartWorkerFactory.BuildPortChartWorker spt_worker = new FlowChartWorkerFactory.BuildPortChartWorker(info, src_port, FlowChartWorkerFactory.BAR_CHART);
        spt_worker.addPropertyChangeListener(event -> {
            //logger.info("build src port chart");
            ChartWorkerPropertyChange(event, spt_worker);
        });
        spt_worker.execute();

        FlowChartWorkerFactory.BuildPortChartWorker dpt_worker = new FlowChartWorkerFactory.BuildPortChartWorker(info, dst_pot, FlowChartWorkerFactory.BAR_CHART);
        dpt_worker.addPropertyChangeListener(event -> {
            //logger.info("build dst port chart");
            ChartWorkerPropertyChange(event, dpt_worker);
        });
        dpt_worker.execute();
    }

    private void ChartWorkerPropertyChange(PropertyChangeEvent event, FlowChartWorkerFactory.FlowChartSwingWorker<JFreeChart, String> task) {

        if ("state".equalsIgnoreCase(event.getPropertyName())) {

            SwingWorker.StateValue  sv = (SwingWorker.StateValue) event.getNewValue();

            switch (sv) {
                case STARTED:
                    SwingUtils.setBorderLayoutPane(FlowVisualPane.this,progressBar,BorderLayout.SOUTH);
                    break;
                case DONE:
                    try {
                        JFreeChart chart = task.get();


                        FlowFileInfo fileInfo = task.getFlowFileInfo();

                        ChartContainer cc = new ChartContainer(chart);

                        FlowChartInfo chartInfo = new FlowChartInfo(task.getChartTitle(),cc);

                        flowChartPane.addChartContainer(cc);
                        addChart2Tree(fileInfo, chartInfo);

                        SwingUtils.setBorderLayoutPane(FlowVisualPane.this,null,BorderLayout.SOUTH);
                    } catch (InterruptedException | ExecutionException e) {
                        logger.debug(e.getMessage());
                    }
                    break;
            }
        }
    }

}
