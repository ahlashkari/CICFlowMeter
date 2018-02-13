package cic.cs.unb.ca.guava.Event;

import org.jfree.chart.ChartPanel;

public class FlowChartEvent {

    ChartPanel chartPanel;

    public FlowChartEvent(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
