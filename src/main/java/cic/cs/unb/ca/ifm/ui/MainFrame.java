package cic.cs.unb.ca.ifm.ui;

import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.flow.ui.FlowMonitorPane;
import cic.cs.unb.ca.flow.ui.FlowOfflinePane;
import cic.cs.unb.ca.flow.ui.FlowVisualPane;
import cic.cs.unb.ca.guava.Event.FlowVisualEvent;
import cic.cs.unb.ca.guava.GuavaMgr;
import com.google.common.eventbus.Subscribe;
import swing.common.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = 7419600803861028585L;

	private FlowOfflinePane offLinePane;
	private FlowMonitorPane monitorPane;
	private FlowVisualPane visualPane;
	
	
	public MainFrame() throws HeadlessException {
		super("CICFlowMeter");
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().getInsets().set(5, 5, 5, 5);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("CIC_Logo.png")));
		
		setMinimumSize(new Dimension(700,500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                dispose();
                System.gc();
            }
        });

		initMenu();
		
		offLinePane = new FlowOfflinePane();
        monitorPane = new FlowMonitorPane();
        visualPane = new FlowVisualPane();
        getContentPane().add(monitorPane,BorderLayout.CENTER);

        GuavaMgr.getInstance().getEventBus().register(this);

		setVisible(true);
	}
	
	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(arg0 -> {
            FlowMgr.getInstance().destroy();
            System.exit(EXIT_ON_CLOSE);
        });
		mnFile.add(itemExit);
		
		JMenu mnNetwork = new JMenu("NetWork");
		menuBar.add(mnNetwork);
		
		JMenuItem itemOffline = new JMenuItem("Offline");
		itemOffline.addActionListener(e -> SwingUtils.setBorderLayoutPane(getContentPane(),offLinePane,BorderLayout.CENTER));
		mnNetwork.add(itemOffline);
		
		JMenuItem itemRealtime = new JMenuItem("Realtime");
		itemRealtime.addActionListener(e -> SwingUtils.setBorderLayoutPane(getContentPane(),monitorPane,BorderLayout.CENTER));
		mnNetwork.add(itemRealtime);

        /*JMenu mnAction = new JMenu("Action");
        menuBar.add(mnAction);

        JMenuItem itemVisual = new JMenuItem("Visual");
        itemVisual.addActionListener(actionEvent -> SwingUtils.setBorderLayoutPane(getContentPane(),visualPane,BorderLayout.CENTER));
        mnAction.add(itemVisual);*/

        JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(e -> AboutDialog.show(MainFrame.this));
		mnHelp.add(itemAbout);
	}

    @Subscribe
    public void listenGuava(FlowVisualEvent evt) {
        System.out.println("file: " + evt.getCsv_file().getPath());

        if (visualPane == null) {
            visualPane = new FlowVisualPane(evt.getCsv_file());
        } else {
            visualPane.visualFile(evt.getCsv_file());
        }

        SwingUtils.setBorderLayoutPane(getContentPane(), visualPane, BorderLayout.CENTER);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        GuavaMgr.getInstance().getEventBus().unregister(this);
    }
}
