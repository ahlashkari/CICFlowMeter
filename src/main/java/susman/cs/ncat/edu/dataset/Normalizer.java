package susman.cs.ncat.edu.dataset;

import cic.cs.unb.ca.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Normalizer {
    protected static final Logger logger = LoggerFactory.getLogger(Normalizer.class);
    public final String DATA_SET_DIRECTORY = System.getProperty("user.dir") + Sys.FILE_SEP + "data";

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
        System.out.println(DATA_SET_DIRECTORY);

        File dir = new File(DATA_SET_DIRECTORY);

        // System.out.println(System.getProperty("user.dir"));

        if (!dir.isDirectory()) {
            logger.error("Directory doesn't exist");
            System.exit(0);
        }
        File[] dataFiles = dir.listFiles();

        for (File df : dataFiles) {
            try {
                Scanner reader = new Scanner(df);

                String line = reader.nextLine();

                String[] values = line.split(",");

                for (int i = 0; i < mins.length; i++) {
                    mins[i] = isNumber(Float.parseFloat(values[i]));
                }

                line = reader.nextLine();

                values = line.split(",");

                for (int i = 0; i < maxes.length; i++) {
                    maxes[i] = isNumber(Float.parseFloat(values[i]));
                }
                /*String header = reader.nextLine();

                while (reader.hasNextLine()) {
                    String line = reader.nextLine();

                    String[] dataList = line.split(",");

                    int index = 0;
                    for (int i = 3; i < dataList.length - 1; i++) {
                        maxes[index] = Float.max(maxes[index], Float.parseFloat(dataList[i]));
                        mins[index] = Float.min(mins[index], Float.parseFloat(dataList[i]));

                        index++;
                    }
                }*/
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    public synchronized float[] normalize (String[] features) {
        float[] normalizedDataList = new float[DataSet.getInstance().NUMBER_OF_FEATURES];

        int index = 0;
        for (int i = 7; i < features.length - 1; i++) {
            float max = maxes[index];
            float min = mins[index];

            float value = Float.parseFloat(features[i]);

            float newValue = ((value - min) / (max - min));

            normalizedDataList[index] = isNumber(newValue);

            index++;
        }
        String str = "";
		for (float f: normalizedDataList) {
			str += f + ",";
		}
		logger.info(str);


        return normalizedDataList;
    }

    private float isNumber(float value) {
        boolean ini = Float.isInfinite(value);
        boolean nan = Float.isNaN(value);

        if (ini || nan) {
            value = 0.0f;
        }

        return value;
    }
}
