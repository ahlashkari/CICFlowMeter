package cic.cs.unb.ca.flow.ui;

import cic.cs.unb.ca.jnetpcap.FlowFeature;
import cic.cs.unb.ca.weka.WekaFactory;
import cic.cs.unb.ca.weka.WekaXMeans;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.math.NumberUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Attribute;
import weka.core.Instance;

import javax.swing.*;

public class FlowChartWorkerFactory {

    protected static final Logger logger = LoggerFactory.getLogger(FlowChartWorkerFactory.class);

    public static final int PIE_CHART = 1;
    public static final int BAR_CHART = 2;

    public static abstract class FlowChartSwingWorker<T, V> extends SwingWorker<T, V> {
        protected FlowFileInfo flowFileInfo;
        protected FlowFeature feature;
        protected int chartType;

        protected String title = "undefined";


        public FlowFileInfo getFlowFileInfo() {
            return flowFileInfo;
        }

        public FlowFeature getFeature() {
            return feature;
        }

        public int getChartType() {
            return chartType;
        }

        public String getChartTitle() {
            return title;
        }
    }

    public static class BuildProtocolChartWorker extends FlowChartSwingWorker<JFreeChart, String> {
        WekaXMeans xMeans;


        public BuildProtocolChartWorker(FlowFileInfo info, int type) {
            flowFileInfo = info;
            feature = FlowFeature.prot;
            chartType = type;
            xMeans = flowFileInfo.getxMeans();
        }

        @Override
        protected JFreeChart doInBackground() {

            if (xMeans ==null) {
                throw new IllegalArgumentException("xMeans should not be null");
            }
            JFreeChart chart;

            title = "Flows By " + feature.getName();

            Attribute attribute = WekaFactory.feature2attr(feature.getName(),feature.isNumeric());
            Multimap<String, Instance> protocol_multimap = xMeans.getMultiMap(attribute);
            switch(chartType){
                case PIE_CHART:
                    DefaultPieDataset pieDataset = new DefaultPieDataset();

                    for (String key : protocol_multimap.keySet()) {
                        pieDataset.setValue(FlowFeature.featureValue2String(feature,key),protocol_multimap.get(key).size());
                    }

                    chart = ChartFactory.createPieChart(
                            title,  // chart title
                            pieDataset,        // data
                            true,           // include legend
                            true,
                            false);

                    break;
                case BAR_CHART:

                    DefaultCategoryDataset barDataSet = new DefaultCategoryDataset();

                    for (String key : protocol_multimap.keySet()) {
                        barDataSet.setValue(protocol_multimap.get(key).size(),key,feature.getAbbr());
                    }


                    chart = ChartFactory.createBarChart(title,
                            "",
                            "num",
                            barDataSet, PlotOrientation.HORIZONTAL,
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

    public static class BuildIPChartWorker extends FlowChartSwingWorker<JFreeChart, String> {

        WekaXMeans xMeans;

        public BuildIPChartWorker(FlowFileInfo info, FlowFeature f, int type) {
            flowFileInfo = info;
            feature = f;
            chartType = type;
            xMeans = flowFileInfo.getxMeans();
        }

        @Override
        protected JFreeChart doInBackground() {
            if (xMeans == null || feature ==null) {
                throw new IllegalArgumentException("xMeans or feature should not be null");
            }

            JFreeChart chart;
            title = "Flows By " + feature.getName();
            Attribute attribute = WekaFactory.feature2attr(feature.getName(),feature.isNumeric());
            Multimap<String, Instance> feature_value_map = xMeans.getMultiMap(attribute);

            switch(chartType){
                case PIE_CHART:
                    DefaultPieDataset pieDataset = new DefaultPieDataset();

                    for (String key : feature_value_map.keySet()) {
                        pieDataset.setValue(key,feature_value_map.get(key).size());
                    }

                    chart = ChartFactory.createPieChart(
                            title,  // chart title
                            pieDataset,        // data
                            true,           // include legend
                            true,
                            false);

                    break;
                case BAR_CHART:

                    DefaultCategoryDataset barDataSet = new DefaultCategoryDataset();

                    for (String key : feature_value_map.keySet()) {
                        double value = feature_value_map.get(key).size();
                        String rowKey = feature.getAbbr();
                        String colKey = key;
                        barDataSet.setValue(value,rowKey,colKey);
                    }

                    chart = ChartFactory.createBarChart(title,
                            null,
                            "Count",
                            barDataSet,
                            PlotOrientation.HORIZONTAL,
                            false,
                            true,
                            false);

                    break;
                default:
                    return null;
            }
            return chart;
        }

    }

    public static class BuildPortChartWorker extends FlowChartSwingWorker<JFreeChart, String> {
        WekaXMeans xMeans;

        public BuildPortChartWorker(FlowFileInfo info, FlowFeature f, int type) {
            flowFileInfo = info;
            feature = f;
            chartType = type;
            xMeans = flowFileInfo.getxMeans();
        }

        @Override
        protected JFreeChart doInBackground() {
            if (xMeans == null || feature ==null) {
                throw new IllegalArgumentException("xMeans or feature should not be null");
            }
            JFreeChart chart;
            title = "Flows By " + feature.getName();
            Attribute attribute = WekaFactory.feature2attr(feature.getName(),feature.isNumeric());
            Multimap<String, Instance> port_multimap = xMeans.getMultiMap(attribute);
            switch(chartType){
                case PIE_CHART:
                    DefaultPieDataset pieDataSet = new DefaultPieDataset();

                    for (String key : port_multimap.keySet()) {
                        Integer port = NumberUtils.createNumber(key).intValue();
                        pieDataSet.setValue(port,port_multimap.get(key).size());
                    }

                    chart = ChartFactory.createPieChart(
                            title,  // chart title
                            pieDataSet,        // data
                            true,           // include legend
                            true,
                            false);

                    break;
                case BAR_CHART:
                    DefaultCategoryDataset barDataSet = new DefaultCategoryDataset();

                    for (String key : port_multimap.keySet()) {
                        double value = port_multimap.get(key).size();
                        String rowKey = feature.getAbbr();
                        Integer colKey = NumberUtils.createNumber(key).intValue();
                        barDataSet.setValue(value,rowKey,colKey);
                    }

                    chart = ChartFactory.createBarChart(title,
                            "",
                            "Count",
                            barDataSet,PlotOrientation.HORIZONTAL,
                            false,
                            true,
                            false);

                    break;
                default:
                    return null;
            }
            return chart;
        }
    }
}
