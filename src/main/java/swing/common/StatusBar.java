package swing.common;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;

import javax.swing.Box;
import javax.swing.BoxLayout;

public class StatusBar extends JPanel{

	private static final long serialVersionUID = 4367500760345312984L;

	private JLabel lblLeftText;
	private JLabel lblRightTxt;
	
	public StatusBar() {
		super();
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		Box baseBox = Box.createHorizontalBox();
				
		lblLeftText = new JLabel("");
		lblRightTxt = new JLabel("");
		baseBox.add(lblLeftText);
		baseBox.add(Box.createHorizontalGlue());
		baseBox.add(lblRightTxt);
		
		add(baseBox);
	}
	
	public StatusBar(String left,String right) {
		this();
		setLeftTxt(left);
		setRightTxt(right);
	}
	
	public void setLeftTxt(String str) {
		lblLeftText.setText(str);
	}
	
	public void setRightTxt(String str) {
		lblRightTxt.setText(str);
	}
	
	public void setRightTxt(int count) {
		lblRightTxt.setText(String.valueOf(count));
	}
	
	private static void createAndShowGUI() {
        JFrame frame = new JFrame("CICStatusBar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        StatusBar pane = new StatusBar();
        pane.setLeftTxt("I am Left");
        pane.setRightTxt("I am Right");
        frame.getContentPane().add(pane);
 
        frame.pack();
        frame.setVisible(true);
	}
	
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}
