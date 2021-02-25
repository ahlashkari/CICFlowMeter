package susman.cs.ncat.edu.ais;

import susman.cs.ncat.edu.dataset.DataSet;
import susman.cs.ncat.edu.dataset.Sample;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.UUID;

public class Detector {
    private Range[] ranges;
    private byte type;
    private Timestamp creation;
    private String id;
    private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private int incorrectMatches;
    private boolean markedForRegeneration;

    public Detector () {id = null;}


    public Detector (String line) {
        ranges = new Range[DataSet.getInstance().NUMBER_OF_FEATURES];

        String[] splits = line.split(",");

        int splitIndex = 0;
        for (int i = 0; i < ranges.length; i ++) {
            float min = Float.parseFloat(splits[splitIndex]);
            float max = Float.parseFloat(splits[splitIndex + 1]);

            ranges[i] = new Range(min, max);
            splitIndex += 2;
        }

        MessageDigest salt = null;
        try {
            salt = MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

            id = bytesToHex(salt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        type = 0;
        creation = new Timestamp(System.currentTimeMillis());
        incorrectMatches = 0;
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
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

    public Timestamp getCreation() {
        return creation;
    }

    public void setCreation(Timestamp creation) {
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
     * @param rValue
     * @return
     */
    public synchronized boolean classify (Sample s, int rValue) {
        if (!markedForRegeneration) {
            int matches = 0;

            for (int i = 0; i < DataSet.getInstance().NUMBER_OF_FEATURES; i++) {
                if (this.ranges[i].between(s.getSingleFeature(i))) {
                    matches += 1;
                }
            }

            return matches >= rValue;
        } else {
            return false;
        }
    }

    public synchronized void promoteMature() {
        if (type == 0) {
            type = 1;
            creation = new Timestamp(System.currentTimeMillis());
        }
    }

    public synchronized void promoteMemory() {
        if (type == 1) {
            type = 2;
            creation = new Timestamp(System.currentTimeMillis());
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
        if (type == 0) {
            if (incorrectMatches > AIS.getInstance().IMMATURE_INCORRECT_MATCHES_THRESHOLD) {
                this.markedForRegeneration = true;
            }
        }
    }
}
