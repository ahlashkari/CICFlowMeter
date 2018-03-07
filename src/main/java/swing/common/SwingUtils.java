package swing.common;

import org.apache.tika.Tika;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

/**
 * Created by yzhang29 on 23/11/17.
 */
public class SwingUtils {
    private final static String PCAP = "application/vnd.tcpdump.pcap";

    public static void fitTableColumns(JTable myTable) {
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();

        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) myTable.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col)
                    .getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col)
                        .getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col)
                        .getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column);
            column.setWidth(width + myTable.getIntercellSpacing().width + 10);
        }
    }

    public static void setBorderLayoutPane(Container container, Component comp, Object constraints){

        if (container == null) {
            throw new IllegalArgumentException("BorderLayoutPane cannot be null!!");
        }

        BorderLayout layout = (BorderLayout) container.getLayout();
        Component oldComp = layout.getLayoutComponent(constraints);

        if(oldComp==null) {
            if(comp!=null) {
                container.add(comp,constraints);
            }
            container.repaint();
            container.revalidate();
        }else {
            if(comp!=oldComp) {
                container.remove(oldComp);
                if(comp!=null) {
                    container.add(comp,constraints);
                }
                container.repaint();
                container.revalidate();
            }
        }
    }

    public static boolean isPcapFile(File file) {

        if (file == null) {
            return false;
        }

        try {

            String contentType;

            //Files.probeContentType returns null on Windows
            /*Path filePath = Paths.get(file.getPath());
            contentType = Files.probeContentType(filePath);*/

            contentType = new Tika().detect(file);

            if (PCAP.equalsIgnoreCase(contentType)) {
                return true;
            }else{
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
