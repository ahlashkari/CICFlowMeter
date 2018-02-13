package cic.cs.unb.ca.flow.ui;

import cic.cs.unb.ca.LRUCache;
import cic.cs.unb.ca.guava.Event.FlowChartEvent;
import cic.cs.unb.ca.guava.GuavaMgr;
import cic.cs.unb.ca.jnetpcap.FlowFeature;
import cic.cs.unb.ca.weka.WekaFactory;
import cic.cs.unb.ca.weka.WekaXMeans;
import com.google.common.collect.Multimap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.common.CsvPickerPane;
import swing.common.SwingUtils;
import weka.core.Attribute;
import weka.core.Instance;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static cic.cs.unb.ca.jnetpcap.FlowFeature.prot;
import static cic.cs.unb.ca.jnetpcap.FlowFeature.src_ip;

/**
 * Created by yzhang29 on 03/01/18.
 */
public class FlowVisualPane extends JDesktopPane implements CsvPickerPane.CsvSelect{
    protected static final Logger logger = LoggerFactory.getLogger(FlowVisualPane.class);

    private CsvPickerPane pickerPane;
//    private JPanel visualPane;
//    private JPanel chartPane;
    private WekaXMeans wekaXMeans;
    private JProgressBar progressBar;
    //private LRUCache<String,UupVisualization> flowVisualCache;

    public FlowVisualPane() {

        init();

        setLayout(new BorderLayout(0, 3));
        //setBorder(Constants.LINEBORDER);

        pickerPane = new CsvPickerPane(this);
        pickerPane.setFilter("Flow");
        pickerPane.setSelectListener(this);

        add(pickerPane, BorderLayout.NORTH);
        add(new FlowChartPane(),BorderLayout.CENTER);
    }

    public FlowVisualPane(File file) {
        this();

        visualProtocol(file);

    }

    private void init() {
        progressBar = new JProgressBar();
        progressBar.setBorderPainted(true);
        progressBar.setIndeterminate(true);

        //flowVisualCache = new LRUCache<>(20);
//        chartPane = initChartPane();
    }

//    private JPanel initVisualPane() {
////        JPanel pane = new JPanel();
////        pane.setLayout(new BorderLayout(0, 0));
////
////        JScrollPane scrollPane = new JScrollPane(chartPane);
////
////        pane.add(scrollPane, BorderLayout.CENTER);
////
////        visualPane = pane;
////        return pane;
//    }

    private JPanel initChartPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout(0, 0));


        return pane;
    }

    public void visualFile(File file) {
        visualProtocol(file);
    }

    /*private void updateVisualization(final UupVisualization uupVisual) {
        SwingUtils.setBorderLayoutPane(this,visualPane,BorderLayout.CENTER);
        if (uupVisual != null) {
            SwingUtilities.invokeLater(() -> {
                visualPane.setTopChart(uupVisual.clusterChart);
                visualPane.setBottomChart(uupVisual.xyChart);
            });
        }
    }*/

    @Override
    public void onSelected(File file) {


        /*UupVisualization uupVisual = flowVisualCache.get(file.getPath());

        if (uupVisual != null) {
            //updateVisualization(uupVisual);
        } else {
            ClusterWorker worker = new ClusterWorker(file, ClusterWorker.FLOW_CSV);
            worker.addPropertyChangeListener(event -> {
                if ("state".equals(event.getPropertyName())) {
                    ClusterWorker task = (ClusterWorker) event.getSource();
                    switch (task.getState()) {
                        case STARTED:
                            break;
                        case DONE:
                            try {
                                wekaXMeans = task.get();
                                BuildChartWorker chartWorker = new FlowVisualPane.BuildChartWorker(wekaXMeans, file);
                                chartWorker.execute();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            });
            worker.execute();
            SwingUtils.setBorderLayoutPane(FlowVisualPane.this,progressBar,BorderLayout.CENTER);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            pickerPane.setPickerEnabled(false);
        }*/
        visualProtocol(file);
    }

    /*private class BuildChartWorker extends SwingWorker<UupVisualization, Integer> {

        private File csv;
        private WekaXMeans xMeans;

        public BuildChartWorker(WekaXMeans xMeans, File csv) {
            this.csv = csv;
            this.xMeans = xMeans;
        }

        *//*@Override
        protected UupVisualization doInBackground() throws Exception {

            UupVisualization ret = new UupVisualization();
            ret.csvFile = csv;
            ret.instanceNum = xMeans.getDRDataset().numInstances();
            ret.clusterChart = UupChartFactory.buildClusterChart(xMeans, "Flow Chart");
            ret.xyChart = buildXYChart(xMeans);

            return ret;
        }*//*

        @Override
        protected void done() {
            super.done();
            try {
                UupVisualization uupVisual = get();

                flowVisualCache.put(uupVisual.csvFile.getPath(),uupVisual);

                //updateVisualization(uupVisual);

                setCursor(Cursor.getDefaultCursor());
                pickerPane.setPickerEnabled(true);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

        private JFreeChart buildXYChart(WekaXMeans wekaXMeans) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<FlowFeature> lengthFeatures = FlowFeature.getLengthFeature();
            for (FlowFeature feature : lengthFeatures) {
                Attribute attribute = WekaFactory.feature2attr(feature.getName(),feature.isNumeric());
                dataset.addValue(wekaXMeans.getMean(attribute), "avg of length" , feature.getAbbr());
            }


            JFreeChart lineChart = ChartFactory.createLineChart(
                    "Average of length",
                    "Feature","Value",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,true,false);

            return lineChart;
        }
    }*/

    private class BuildFlowChartWorker extends SwingWorker<JFreeChart,String> {

        public static final int PIE_CHART = 1;
        public static final int BAR_CHART = 2;

        File csv;
        FlowFeature feature;
        int chartType;

        public BuildFlowChartWorker(File csv, FlowFeature feature,int chartType) {
            this.csv = csv;
            this.feature = feature;
            this.chartType = chartType;
        }

        @Override
        protected JFreeChart doInBackground() {

            if (csv == null || feature ==null) {
                throw new IllegalArgumentException("csv or feature should not be null");
            }


            JFreeChart chart;

            WekaXMeans xMeans = new WekaXMeans(WekaFactory.loadFlowCsv(csv));

            switch(chartType){
                case PIE_CHART:
                    Attribute attribute = WekaFactory.feature2attr(feature.getName(),feature.isNumeric());
                    Multimap<String, Instance> multimap = xMeans.getMultiMap(attribute);
                    DefaultPieDataset pieDataset = new DefaultPieDataset();

                    for (String key : multimap.keySet()) {
                        logger.info("{}: {}",key,multimap.get(key).size());
                        pieDataset.setValue(key,multimap.get(key).size());
                    }

                    chart = ChartFactory.createPieChart(
                            feature.getName(),  // chart title
                            pieDataset,        // data
                            true,           // include legend
                            true,
                            false);

                    break;
                case BAR_CHART:
                    Attribute attr = WekaFactory.feature2attr(src_ip.getName(),src_ip.isNumeric());
                    Multimap<String, Instance> IPmultimap = xMeans.getMultiMap(attr);

                    DefaultCategoryDataset barDataSet = new DefaultCategoryDataset();

                    for (String key : IPmultimap.keySet()) {
                        //logger.info("IP {}: {}",key,IPmultimap.get(key).size());
                        barDataSet.setValue(IPmultimap.get(key).size(),key,"src ip");
                    }

                    chart = ChartFactory.createBarChart("chart",
                                                "IP",
                                                "num",
                                                barDataSet,PlotOrientation.HORIZONTAL,
                                                true,
                                                true,
                                                false);

                    break;
                default:
                    return null;
            }

            return chart;

        }
    }

    private void visualProtocol(File file) {
        BuildFlowChartWorker protocol_worker = new BuildFlowChartWorker(file,prot,BuildFlowChartWorker.PIE_CHART);
        protocol_worker.addPropertyChangeListener(event -> WorkerPropertyChange(event));
        protocol_worker.execute();

        BuildFlowChartWorker srcip_worker = new BuildFlowChartWorker(file,src_ip,BuildFlowChartWorker.BAR_CHART);
        srcip_worker.addPropertyChangeListener(event -> WorkerPropertyChange(event));
        srcip_worker.execute();
    }

    private void WorkerPropertyChange(PropertyChangeEvent event){
        BuildFlowChartWorker task = (BuildFlowChartWorker) event.getSource();
        if("progress".equals(event.getPropertyName())){

        }else if ("state".equals(event.getPropertyName())){
            switch (task.getState()) {
                case STARTED:
                    SwingUtils.setBorderLayoutPane(this,progressBar,BorderLayout.SOUTH);
                break;
                case DONE:
                    try {
                        JFreeChart chart = task.get();
                        GuavaMgr.getInstance().getEventBus().post(new FlowChartEvent(new ChartPanel(chart)));
                        SwingUtils.setBorderLayoutPane(this,null,BorderLayout.SOUTH);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                break;
            }
        }
    }
}
