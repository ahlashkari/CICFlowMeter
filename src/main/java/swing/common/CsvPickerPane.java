package swing.common;

//import cic.cs.unb.ca.uup.ui.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public class CsvPickerPane extends JPanel{
    protected static final Logger logger = LoggerFactory.getLogger(CsvPickerPane.class);

    private JFileChooser fileChooser;
    private TextFileFilter fileChooserFilter;
    private JComboBox<CsvFileWrapper> cmbCSVFile;
    private Vector<CsvFileWrapper> cmbCSVFileEle;
    private JButton btnBrowse;
    private JButton btnOK;
    private CsvSelect selectListener;

    public CsvPickerPane(Container parent) {

        initComponent();

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        //setBorder(Constants.LINEBORDER);

        Box baseBox = Box.createVerticalBox();

        Box cmbBox = Box.createHorizontalBox();

        cmbBox.add(cmbCSVFile);
        cmbBox.add(Box.createHorizontalStrut(20));
        cmbBox.add(btnBrowse);
        cmbBox.add(Box.createHorizontalStrut(20));
        cmbBox.add(btnOK);
        cmbBox.add(Box.createHorizontalStrut(10));

        baseBox.add(cmbBox);

        add(baseBox);
    }

    private void initComponent(){
        fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        fileChooserFilter = new TextFileFilter("CSV File (*.csv)",new String[]{"csv"});
        fileChooser.setFileFilter(fileChooserFilter);
        cmbCSVFileEle = new Vector<>();
        cmbCSVFile = new  JComboBox<>(cmbCSVFileEle);
        btnBrowse = new JButton("Browse");
        btnBrowse.addActionListener(mActionListener);
        btnOK = new JButton("OK");
        btnOK.addActionListener(mActionListener);
    }

    public void setPickerEnabled(boolean b){
        btnOK.setEnabled(b);
    }

    public void setFilter(CharSequence... searchCharSequences) {
        fileChooserFilter = new TextFileFilter("CSV File (*.csv)",new String[]{"csv"},searchCharSequences);
        fileChooser.setFileFilter(fileChooserFilter);
    }

    public void setSelectListener(CsvSelect listener) {
        selectListener = listener;
    }

    private ActionListener mActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Browse":
                    int action = fileChooser.showOpenDialog(CsvPickerPane.this);
                    if (action == JFileChooser.APPROVE_OPTION) {
                        File f = fileChooser.getSelectedFile();

                        File csvFilePath;

                        if(f.isDirectory()) {
                            csvFilePath = f;
                        }else {
                            csvFilePath = f.getParentFile();
                        }

                        cmbCSVFileEle.clear();
                        cmbCSVFileEle.addAll(CsvFileWrapper.loadCSVFile(csvFilePath,fileChooserFilter.getFileNameFilter()));

                        cmbCSVFile.setSelectedIndex(0);
                        for(int i=0;i<cmbCSVFileEle.size();i++) {
                            CsvFileWrapper csvF = cmbCSVFileEle.get(i);

                            if (csvF.getFile().getPath().equals(f.getPath())) {
                                cmbCSVFile.setSelectedIndex(i);
                                break;
                            }
                        }

                    }
                    break;
                case "OK":

                    CsvFileWrapper csvfile = (CsvFileWrapper) cmbCSVFile.getSelectedItem();

                    if (csvfile != null && selectListener != null) {
                        selectListener.onSelected(csvfile.getFile());
                    }
                    break;
            }
        }
    };

    public interface CsvSelect{
        void onSelected(File file);
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("CsvPickerPane");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CsvPickerPane demopane = new CsvPickerPane(frame);
        frame.getContentPane().add(demopane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
    }


}
