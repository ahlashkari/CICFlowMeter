package cic.cs.unb.ca.jnetpcap;

import org.apache.tika.Tika;
import org.jnetpcap.PcapClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Utils {
    protected static final Logger logger = LoggerFactory.getLogger(Utils.class);
    public static final String FILE_SEP = System.getProperty("file.separator");
    public static final String LINE_SEP = System.lineSeparator();
    private final static String PCAP = "application/vnd.tcpdump.pcap";
    public static final String FLOW_SUFFIX = "_Flow.csv";


    private static boolean isPcapFile(String contentType) {

        return PCAP.equalsIgnoreCase(contentType);
    }

    public static boolean isPcapFile(File file) {

        if (file == null) {
            return false;
        }

        try {

            //Files.probeContentType returns null on Windows
            /*Path filePath = Paths.get(file.getPath());
            contentType = Files.probeContentType(filePath);*/

            return isPcapFile(new Tika().detect(file));

        } catch (IOException e) {
            logger.debug(e.getMessage());
        }

        return false;
    }

    public static boolean isPcapFile(InputStream stream) {

        if (stream == null) {
            return false;
        }

        try {
            return isPcapFile(new Tika().detect(stream));
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }

        return false;
    }

    public static long countLines(String fileName) {
        File file =new File(fileName);
        int linenumber = 0;
        FileReader fr;
        LineNumberReader lnr = null;
        try {
            fr = new FileReader(file);
            lnr = new LineNumberReader(fr);

            while (lnr.readLine() != null){
                linenumber++;
            }

        } catch (IOException e) {
            logger.debug(e.getMessage());
        } finally {

            if (lnr != null) {

                try {
                    lnr.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
        }
        return linenumber;
    }

}
