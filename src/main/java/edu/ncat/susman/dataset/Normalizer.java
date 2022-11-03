package edu.ncat.susman.dataset;

import cic.cs.unb.ca.Sys;
import edu.ncat.susman.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Normalizer {
    protected static final Logger logger = LoggerFactory.getLogger(Normalizer.class);


    private double[] maxes;
    private double[] mins;

    private final float normalizedMax = 1.0f;
    private final float normalizedMin = 0.0f;

    public static Normalizer Instance = new Normalizer();

    public static Normalizer getInstance() { return Instance; }

    public void init () {
        maxes = new double[Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES];
        mins = new double[Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES];

        /*for (int i = 0; i < Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES; i++) {
            maxes[i] = Double.MIN_VALUE;
            mins[i] = Double.MAX_VALUE;
        }*/

        readDataFiles();
    }

    public void readDataFiles () {

        File minMaxFile = new File(Parameters.DATA_SET_DIRECTORY);

        // System.out.println(System.getProperty("user.dir"));

        if (!minMaxFile.exists()) {
            logger.error("Min Max File doesn't exist");
            System.exit(-1);
        }

            try {
                Scanner reader = new Scanner(minMaxFile);

                String line = reader.nextLine();

                String[] values = line.split(",");

                for (int i = 0; i < mins.length; i++) {
                    mins[i] = isNumber(Double.parseDouble(values[i]));
                }

                line = reader.nextLine();

                values = line.split(",");

                for (int i = 0; i < maxes.length; i++) {
                    maxes[i] = isNumber(Double.parseDouble(values[i]));
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }



    public synchronized float[] normalize (String[] features) {
        float[] normalizedDataList = new float[Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES];

        int index = 0;
        for (int i = 7; i < features.length - 1; i++) {
            double max = maxes[index];
            double min = mins[index];

            if (isFloat(features[i])) {

                double value = Double.parseDouble(features[i]);

                if (value > max) {
                    maxes[index] = value;
                    max = value;
                }

                double newValue;
                if (max == 0.0f) {
                    newValue = 0.0f;
                } else {
                    newValue = (((value - min) / (max - min)));
                }

                normalizedDataList[index] = Math.min((float) isNumber(newValue), 1.0f);
            } else {
                normalizedDataList[index] = 0.0f;
            }

            index++;
        }
        String str = "";
		for (float f: normalizedDataList) {
			str += f + ",";
		}
		// logger.info(str);


        return normalizedDataList;
    }

    private boolean isFloat(String val) {
        if (val.equals("NaN") || val.equals("Infinity"))
            return false;
        else
            return true;
    }

    private double isNumber(double value) {
        boolean ini = Double.isInfinite(value);
        boolean nan = Double.isNaN(value);

        if (ini || nan) {
            value = 0.0f;
        }

        return value;
    }
}
