package swing.common;

import cic.cs.unb.ca.Sys;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Vector;

public class CsvFileWrapper {

    protected static final Logger logger = LoggerFactory.getLogger(CsvFileWrapper.class);


    private File file;

    CsvFileWrapper(File file) {
        this.file = file;
    }

    @SuppressWarnings("unused")
    public CsvFileWrapper(String fullpath) {
        file = new File(fullpath);
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = simpleDateFormat.format(file.lastModified());
        String suffix = "("+ file.getName()+ ")";
        return dateStr + suffix;
    }

    public static Vector<CsvFileWrapper> loadCSVFile(File csvPath){
        Vector<CsvFileWrapper> vector = new Vector<>();

        if (csvPath == null) {
            return vector;
        }


        File[] csvFiles = csvPath.listFiles((dir, name) ->
                (name.toLowerCase().endsWith("csv") && !name.equals(LocalDate.now().toString()+"_online.csv"))
        );

        if(csvFiles == null) {
            return vector;
        }

        Arrays.sort(csvFiles, (f1, f2) -> Long.valueOf(f2.lastModified()).compareTo(f1.lastModified()));

        for(File f: csvFiles) {
				/*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String dateStr = simpleDateFormat.format(f.lastModified());
				logger.info("file: {}--lastM: {}",f.getName(),dateStr);*/
            vector.add(new CsvFileWrapper(f));
        }
        logger.info("vector:{}",vector.size());
        return vector;
    }

    public static Vector<CsvFileWrapper> loadCSVFile(File path, FileFilter filter) {
        Vector<CsvFileWrapper> vector = new Vector<>();

        if (path == null) {
            return vector;
        }

        if (filter == null) {
            filter = pathname -> {
                String name = pathname.getName();
                String extension = FilenameUtils.getExtension(name);

                if (StringUtils.equalsAnyIgnoreCase(extension, "csv") && !StringUtils.contains(name, LocalDate.now().toString())) {
                    return true;
                } else {
                    return false;
                }
            };
        }

        File[] csvFiles = path.listFiles(filter);

        if(csvFiles == null) {
            return vector;
        }
        Arrays.sort(csvFiles, (f1, f2) -> Long.valueOf(f2.lastModified()).compareTo(f1.lastModified()));
        for(File f: csvFiles) {
            vector.add(new CsvFileWrapper(f));
        }
        return vector;
    }

    @SuppressWarnings("unused")
    public static Vector<CsvFileWrapper> loadCSVFile(String csvPath) {

        if(csvPath==null) {
            String rootPath = System.getProperty("user.dir");
            csvPath = rootPath+ Sys.FILE_SEP+"data"+Sys.FILE_SEP+"out"+Sys.FILE_SEP;
        }

        return loadCSVFile(new File(csvPath));

			/*Vector<CsvFileWrapper> vector = new Vector<CsvFileWrapper>();
			if(csvPath==null) {
				String rootPath = System.getProperty("user.dir");
				csvPath = rootPath+FlowUtils.file_separator+"data"+FlowUtils.file_separator+"out"+FlowUtils.file_separator;
			}
			//logger.info(csvPath);
			File[] csvFiles = new File(csvPath).listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return (name.toLowerCase().endsWith("csv") && !name.equals(LocalDate.now().toString()+"_online.csv")) ;
				}});

			if(csvFiles == null) {
				return vector;
			}

			Arrays.sort(csvFiles,new Comparator<File>() {

				@Override
				public int compare(File f1, File f2) {
					return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
				}});


			for(File f: csvFiles) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String dateStr = simpleDateFormat.format(f.lastModified());
				logger.info("file: {}--lastM: {}",f.getName(),dateStr);
				vector.add(new CsvFileWrapper(f));
			}
			return vector;*/
    }

}
