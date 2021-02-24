package susman.cs.ncat.edu.dataset;

import cic.cs.unb.ca.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Normalizer {
    protected static final Logger logger = LoggerFactory.getLogger(Normalizer.class);

    private float[] maxes;
    private float[] mins;

    private final float normalizedMax = 1.0f;
    private final float normalizedMin = 0.0f;

    public static Normalizer Instance = new Normalizer();

    public static Normalizer getInstance() { return Instance; }

    public void init () {
        maxes = new float[DataSet.getInstance().NUMBER_OF_FEATURES];
        mins = new float[DataSet.getInstance().NUMBER_OF_FEATURES];

        for (int i = 0; i < DataSet.getInstance().NUMBER_OF_FEATURES; i++) {
            maxes[i] = Float.MIN_VALUE;
            mins[i] = Float.MAX_VALUE;
        }

        readDataFiles();
    }

    public void readDataFiles () {
        /*System.out.println("Enter the directory of the data files:");
        Scanner input = new Scanner(System.in);
        String dirName = input.nextLine();*/
        String homeDir = System.getProperty("user.dir") + Sys.FILE_SEP + "src" + Sys.FILE_SEP + "main" + Sys.FILE_SEP + "java" + Sys.FILE_SEP;
        String dataDir = "susman" + Sys.FILE_SEP + "cs" + Sys.FILE_SEP + "ncat" + Sys.FILE_SEP + "edu" + Sys.FILE_SEP + "data";

        String dirName = homeDir + dataDir;
        System.out.println(dirName);

        File dir = new File(dirName);

        // System.out.println(System.getProperty("user.dir"));

        if (!dir.isDirectory()) {
            logger.error("Directory doesn't exist");
            System.exit(0);
        }
        File[] dataFiles = dir.listFiles();

        for (File df : dataFiles) {
            try {
                Scanner reader = new Scanner(df);

                String header = reader.nextLine();

                while (reader.hasNextLine()) {
                    String line = reader.nextLine();

                    String[] dataList = line.split(",");

                    int index = 0;
                    for (int i = 3; i < dataList.length - 1; i++) {
                        maxes[index] = Float.max(maxes[index], Float.parseFloat(dataList[i]));
                        mins[index] = Float.min(mins[index], Float.parseFloat(dataList[i]));

                        index++;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    public float[] normalize (String[] features) {
        float[] normalizedDataList = new float[DataSet.getInstance().NUMBER_OF_FEATURES];

        int index = 0;
        for (int i = 7; i < features.length - 1; i++) {
            float max = maxes[index];
            float min = mins[index];

            float a = (1)/(max-min);
            float b = max - a * max;

            float value = Float.parseFloat(features[i]);

            float newValue = ((value - min) / (max - min));

            normalizedDataList[index] = newValue;

            index++;
        }
        String str = "";
		for (float f: normalizedDataList) {
			str += f + ",";
		}
		logger.info(str);


        return normalizedDataList;
    }
}
