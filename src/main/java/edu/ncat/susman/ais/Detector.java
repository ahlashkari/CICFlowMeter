package edu.ncat.susman.ais;

import edu.ncat.susman.Parameters;
import edu.ncat.susman.dataset.Sample;
import edu.ncat.susman.dataset.DataSet;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

public class Detector {
    private Range[] ranges;
    private byte type;
    private long creation;
    private String id;
    private byte level;
    private int rValue;

    private int incorrectMatches;
    private boolean markedForRegeneration;

    public Detector () {id = null;}

    public void generateRandom() {
        MessageDigest salt = null;
        try {
            salt = MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

            id = Parameters.bytesToHex(salt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        type = 1;
    }


    public Detector (String line) {
        ranges = new Range[Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES];

        String[] splits = line.split(",");

        int splitIndex = 0;
        for (int i = 0; i < ranges.length; i ++) {
            float min = Float.parseFloat(splits[splitIndex]);
            float max = Float.parseFloat(splits[splitIndex + 1]);

            ranges[i] = new Range(min, max);
            splitIndex += 2;
        }

        this.setType(Byte.parseByte(splits[splits.length-1]));

        MessageDigest salt = null;
        try {
            salt = MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

            id = Parameters.bytesToHex(salt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        creation = System.currentTimeMillis();
        incorrectMatches = 0;
        level = 0;
    }

    public Range[] getRanges () {
        return this.ranges;
    }

    public byte getType() {
        return this.type;
    }

    public void setType (byte type) {
        this.type = type;
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Return true if the detector matches the sample
     * @param s
     * @return
     */
    public synchronized boolean classify (Sample s) {
        if (!markedForRegeneration) {
            int matches = 0;

            for (int i = 0; i < Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES; i++) {
                if (this.ranges[i].between(s.getSingleFeature(i))) {
                    matches ++;
                }

                if (matches >= rValue) return true;
            }

            return false;
        } else {
            return false;
        }
    }

    public synchronized void promoteMature() {
        if (level == 0) {
            level = 1;
            creation = System.currentTimeMillis();
            markedForRegeneration = false;
        }
    }

    public synchronized void promoteMemory() {
        if (level == 1) {
            level = 2;
            creation = System.currentTimeMillis();
            markedForRegeneration = false;
        }
    }

    public synchronized void incrementIncorrectMatch() {
        incorrectMatches++;
    }

    public synchronized int getIncorrectMatches() {
        return incorrectMatches;
    }

    public synchronized boolean isMarkedForRegeneration() {
        return markedForRegeneration;
    }

    public synchronized void markForRegeneration() {
        this.markedForRegeneration = true;
    }

    public synchronized void unmarkForRegeneration() {
        this.markedForRegeneration = false;
        level = 0;
        creation = System.currentTimeMillis();
        incorrectMatches = 0;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public boolean checkIncorrectThreshold() {
        boolean rtrValue = false;

        if (level == 0 && getIncorrectMatches() >= Parameters.IMMATURE_DETECTOR_INCORRECT_DETECTIONS_THRESHOLD)
            rtrValue = true;
        else if (level == 1 && getIncorrectMatches() >= Parameters.MATURE_DETECTOR_INCORRECT_THRESHOLD)
            rtrValue = true;
        else if (level == 2 && getIncorrectMatches() >= Parameters.MEMORY_DETECTOR_INCORRECT_THRESHOLD)
            rtrValue = true;

        return rtrValue;
    }

    public boolean checkLifeSpan() {
        long currentTime = System.currentTimeMillis();
        boolean rtrValue = false;

        if (level == 0 && currentTime - creation >= Parameters.IMMATURE_DETECTOR_LIFESPAN)
            rtrValue = true;
        else if (level == 1 && currentTime - creation >= Parameters.MATURE_DETECTOR_LIFESPAN)
            rtrValue = true;
        else if (level == 2 && currentTime - creation >= Parameters.MEMORY_DETECTOR_LIFESPAN)
            rtrValue = true;

        return rtrValue;
    }

    public int getrValue() {
        return rValue;
    }

    public void setrValue(int rValue) {
        this.rValue = rValue;
    }
}
