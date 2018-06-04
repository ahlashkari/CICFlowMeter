package cic.cs.unb.ca.flow.ui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class ChartContainer extends JPanel{
    protected static final Logger logger = LoggerFactory.getLogger(ChartContainer.class);

    private static Dimension maxDim;
    private static Dimension minDim;

    private static double zoomPercentage;

    private static Color boxDefaultColor;
    private static Color focusColor;

    Box parentBox;
    Box chartBox;

    ChartPanel chartPane;


    static{
        maxDim = new Dimension(ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH*4, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT*4);
        minDim = new Dimension(ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT);
        zoomPercentage = 0.1;

        boxDefaultColor = Box.createHorizontalBox().getBackground();
        focusColor = UIManager.getColor("Tree.selectionBackground");

    }


    public ChartContainer(JFreeChart chart) {
        setLayout(new BorderLayout(0,0));

        chartPane = new ChartPanel(chart);

        chartPane.setMaximumSize(maxDim);
        chartPane.setMaximumDrawWidth(maxDim.width);
        chartPane.setMaximumDrawHeight(maxDim.height);
        chartPane.setMouseWheelEnabled(true);
        chartPane.setMouseZoomable(true);
        chartPane.setFillZoomRectangle(true);

        add(initChartBox(chartPane), BorderLayout.CENTER);
    }

    private Box initChartBox(ChartPanel chart) {

        parentBox = Box.createVerticalBox();

        chartBox = Box.createHorizontalBox();
        //Dimension d = new Dimension(500, 500);

        chartBox.setPreferredSize(minDim);
        chartBox.setMinimumSize(minDim);
        chartBox.setMaximumSize(minDim);

        //chartBox.add(Box.createHorizontalGlue());
        chartBox.add(chart);
        //chartBox.add(Box.createHorizontalGlue());


        parentBox.add(chartBox);
        parentBox.add(Box.createVerticalStrut(4));
        parentBox.add(new JSeparator(SwingConstants.HORIZONTAL));
        parentBox.add(Box.createVerticalStrut(4));

        return parentBox;
    }

    private JPanel init_btnPane(ChartPanel chart) {
        JPanel pane = new JPanel();

        pane.setLayout(new BoxLayout(pane,BoxLayout.Y_AXIS));


        JButton zoomIn = new JButton("Zoom In");
        JButton zoomOut = new JButton("Zoom Out");

        zoomIn.addActionListener(actionEvent -> {
            int w = getWidth();
            int h = getHeight();
            Dimension d = new Dimension(w + 10, h + 10);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
            repaint();
            revalidate();
        });

        zoomOut.addActionListener(actionEvent -> chart.restoreAutoBounds());


        pane.add(Box.createVerticalGlue());
        pane.add(zoomIn);
        pane.add(Box.createVerticalGlue());
        pane.add(zoomOut);
        pane.add(Box.createVerticalGlue());

        return pane;
    }

    public void setFocus(boolean focus) {

        parentBox.setOpaque(true);
        if (focus) {
            parentBox.setBackground(focusColor);
        } else {
            parentBox.setBackground(boxDefaultColor);
        }

    }

    public void zoomIn() {

        Dimension d = chartBox.getSize();

        double w = d.width + d.width * zoomPercentage;
        double h = (w * d.height)/d.width;
        d.setSize(w, h);
        d = clipDim(d);

        chartBox.setPreferredSize(d);
        chartBox.setMinimumSize(d);
        chartBox.setMaximumSize(d);

        parentBox.setMaximumSize(d);

        chartBox.repaint();
        chartBox.revalidate();
    }

    public void zoomOut() {
        Dimension d = chartBox.getSize();

        double w = d.width - d.width * zoomPercentage;
        double h = (w * d.height)/d.width;
        d.setSize(w, h);

        chartBox.setPreferredSize(d);
        chartBox.setMinimumSize(d);
        chartBox.setMaximumSize(d);

        chartBox.repaint();
        chartBox.revalidate();
    }

    public void resetSize() {
        chartBox.setPreferredSize(minDim);
        chartBox.setMinimumSize(minDim);
        chartBox.setMaximumSize(minDim);
        chartBox.repaint();
        chartBox.revalidate();
    }

    public void resetScale() {
        chartPane.restoreAutoBounds();
    }

    private Dimension clipDim(Dimension dimension) {

        if (dimension == null) {
            return null;
        }

        if (dimension.width < minDim.width || dimension.height < minDim.height) {
            return minDim;
        } else if (dimension.width > maxDim.width || dimension.height > maxDim.height) {
            return maxDim;
        } else {
            return dimension;
        }
    }
}
