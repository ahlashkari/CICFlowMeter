package cic.cs.unb.ca.jnetpcap.worker;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static cic.cs.unb.ca.jnetpcap.Utils.FILE_SEP;
import static cic.cs.unb.ca.jnetpcap.Utils.LINE_SEP;

public class InsertCsvRow implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(InsertCsvRow.class);
    private String header;
    private List<String> rows;
    private String savepath = null;
    private String filename = null;

    public InsertCsvRow(String header, List<String> rows, String savepath, String filename) {
        this.header = header;
        this.rows = rows;
        this.savepath = savepath;
        this.filename = filename;
    }

    public InsertCsvRow(String header, String row, String savepath, String filename) {

        this.header = header;
        this.rows = new ArrayList<>();
        this.savepath = savepath;
        this.filename = filename;

        rows.add(row);
    }

    @Override
    public void run() {
        insert(header,rows,savepath,filename);
    }

    public static void insert(String header,List<String>  rows,String savepath, String filename) {
        if (savepath == null || filename == null || rows == null || rows.size() <= 0) {
            String ex = String.format("savepath=%s,filename=%s", savepath, filename);
            throw new IllegalArgumentException(ex);
        }

        File fileSavPath = new File(savepath);

        if(!fileSavPath.exists()) {
            fileSavPath.mkdirs();
        }


        if(!savepath.endsWith(FILE_SEP)){
            savepath += FILE_SEP;
        }

        File file = new File(savepath+filename);
        FileOutputStream output = null;

        try {
            if (file.exists()) {
                output = new FileOutputStream(file, true);
            }else{
                if (file.createNewFile()) {
                    output = new FileOutputStream(file);
                }
                if (header != null) {
                    output.write((header+LINE_SEP).getBytes());
                }
            }
            for (String row : rows) {
                output.write((row+LINE_SEP).getBytes());
            }

        } catch (IOException e) {
                logger.debug(e.getMessage());
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        }
    }
}
