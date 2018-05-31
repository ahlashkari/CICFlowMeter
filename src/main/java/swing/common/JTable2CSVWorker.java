package swing.common;

import cic.cs.unb.ca.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JTable2CSVWorker extends SwingWorker<String,String> {
    protected static final Logger logger = LoggerFactory.getLogger(JTable2CSVWorker.class);

    private JTable table;
    private File file;

    public JTable2CSVWorker(JTable table, File file) {

        if (table == null || file == null) {
            throw new IllegalArgumentException("table or file should not be null!!");
        }

        if (file.isDirectory()) {
            throw new IllegalArgumentException(file.toString()+" is NOT a file!!!");
        }

        this.table = table;
        this.file = file;
    }

    @Override
    protected String doInBackground() {
        FileWriter csv = null;
        try {
            if (file.exists()) {
                if (!file.delete()) {
                    System.out.println("File can not be deleted");
                }
            }
            if (file.createNewFile()) {

                csv = new FileWriter(file);
            }

            StringBuilder tableHeader = new StringBuilder();
            TableModel model = table.getModel();
            for (int j = 0; j < model.getColumnCount(); j++) {
                tableHeader.append(model.getColumnName(j)).append(",");
            }
            tableHeader.deleteCharAt(tableHeader.length() - 1);
            //logger.info("header: {}", tableHeader.toString());

            csv.write(tableHeader.toString() + Sys.LINE_SEP);

            StringBuilder tableRow;
            for (int i = 0; i < model.getRowCount(); i++) {
                tableRow = new StringBuilder();
                for (int j = 0; j < model.getColumnCount(); j++) {
                    tableRow.append(model.getValueAt(i, j).toString()).append(",");
                }
                tableRow.deleteCharAt(tableRow.length() - 1);
                //logger.info("row: {}", tableRow.toString());
                csv.write(tableRow.toString() + Sys.LINE_SEP);
            }

        }catch (IOException e) {
            logger.debug(e.getMessage());
            logger.info("JTable2CSVWorker: {}", e.getMessage());
        } finally {
            try {
                if (csv != null) {
                    csv.close();
                }
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        }
        return file.toString();
    }
}
