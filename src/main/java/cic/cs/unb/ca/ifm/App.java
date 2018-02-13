package cic.cs.unb.ca.ifm;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.guava.GuavaMgr;
import cic.cs.unb.ca.ifm.ui.MainFrame;

public class App {
	
	public static void init() {
		FlowMgr.getInstance().init();
		GuavaMgr.getInstance().init();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    
	    
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			/*UIManager.getFont("Label.font");
			UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");*/
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		EventQueue.invokeLater(() -> {
            try {
                init();
                new MainFrame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
	}
}
