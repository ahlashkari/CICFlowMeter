package swing.common;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class InsertTableRow implements Runnable  {

    private DefaultTableModel defaultTableModel;
    private List<String[]> rowList;
    private JLabel label;

    public InsertTableRow(DefaultTableModel defaultTableModel, List<String[]> rowList, JLabel label) {
        this.defaultTableModel = defaultTableModel;
        this.rowList = rowList;
        this.label = label;
    }

    @Override
    public void run() {
        //logger.info("insert table thead:{} name--{}",Thread.currentThread().getId(),Thread.currentThread().getName());
        for(String[] row:rowList) {
            defaultTableModel.insertRow(0, row);
        }

        if(label!=null){
            label.setText(String.valueOf(defaultTableModel.getRowCount()));
        }
    }
}
