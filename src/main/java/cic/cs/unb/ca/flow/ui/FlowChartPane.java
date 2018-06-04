package cic.cs.unb.ca.flow.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class FlowChartPane extends JPanel{
    protected static final Logger logger = LoggerFactory.getLogger(FlowChartPane.class);

    private JPanel chartPane;
    private List<ChartContainer> ccList = new ArrayList<>();
    private ChartContainer focusCC = null;


    public FlowChartPane() {

        init();
        setLayout(new BorderLayout(0,0));
        setOpaque(true);
        JScrollPane jScrollPane = new JScrollPane(initBoxPane());
        jScrollPane.setPreferredSize(getPreferredSize());
        jScrollPane.setOpaque(true);

        add(jScrollPane, BorderLayout.CENTER);
    }

    private void init() {
    }

    private JPanel initBoxPane() {
        chartPane = new JPanel();

        chartPane.setLayout(new BoxLayout(chartPane,BoxLayout.Y_AXIS));
        chartPane.setOpaque(true);

        return chartPane;
    }

    public void addChartContainer(ChartContainer cc) {
        if (cc == null) {
            return;
        }

        cc.addMouseListener(mChartContainerMouseListener);

        ccList.add(cc);

        chartPane.add(cc);
        chartPane.repaint();
        chartPane.revalidate();
    }

    public void removeChart(){
        chartPane.removeAll();
        chartPane.revalidate();
        chartPane.repaint();
    }


    public void zoomIn() {
        if (focusCC != null) {
            focusCC.zoomIn();
        }
    }

    public void zoomOut() {
        if (focusCC != null) {
            focusCC.zoomOut();
        }
    }

    public void resetSize() {
        for (ChartContainer chartContainer : ccList) {
            chartContainer.resetSize();
        }
    }

    public void resetScale() {
        for (ChartContainer chartContainer : ccList) {
            chartContainer.resetScale();
        }
    }

    private MouseAdapter mChartContainerMouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            super.mouseClicked(mouseEvent);
            ChartContainer cc = (ChartContainer) mouseEvent.getSource();
            for (ChartContainer chartContainer : ccList) {
                chartContainer.setFocus(false);
            }
            cc.setFocus(true);
            focusCC = cc;
        }
    };

}
