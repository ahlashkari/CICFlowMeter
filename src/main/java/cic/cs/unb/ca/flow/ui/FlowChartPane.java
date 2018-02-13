package cic.cs.unb.ca.flow.ui;

import cic.cs.unb.ca.guava.Event.FlowChartEvent;
import cic.cs.unb.ca.guava.GuavaMgr;
import com.google.common.eventbus.Subscribe;
import org.jfree.chart.ChartPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class FlowChartPane extends JPanel{
    protected static final Logger logger = LoggerFactory.getLogger(FlowChartPane.class);

    private JPanel chartPane;
    private Box curBox;

    public FlowChartPane() {

        init();
        setLayout(new BorderLayout(0,0));
//        JPanel pane = new JPanel();
//        pane.setLayout(new BorderLayout(0, 0));
//        pane.setPreferredSize(new Dimension(600,600));

        JScrollPane jScrollPane = new JScrollPane(initBoxPane());
        jScrollPane.setPreferredSize(getPreferredSize());

        add(jScrollPane, BorderLayout.CENTER);
    }

    private void init() {
        GuavaMgr.getInstance().getEventBus().register(this);

    }

    private JPanel initBoxPane() {
        chartPane = new JPanel();

        chartPane.setLayout(new BoxLayout(chartPane,BoxLayout.Y_AXIS));

        return chartPane;
    }

    @Subscribe
    public void addChart(FlowChartEvent event) {
        ChartPanel chart = event.getChartPanel();

        if (curBox == null || curBox.getComponentCount()>=2) {
            curBox = Box.createHorizontalBox();
        }
        logger.info("addChart {}", curBox.getComponentCount());

        if (chart != null) {
            curBox.add(chart);
            chartPane.add(curBox);

            chartPane.repaint();
            chartPane.revalidate();
        }

    }
}
