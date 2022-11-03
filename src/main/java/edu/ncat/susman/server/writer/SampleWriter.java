package edu.ncat.susman.server.writer;

import edu.ncat.susman.Parameters;
import edu.ncat.susman.dataset.Sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.LinkedBlockingQueue;

public class SampleWriter extends Thread {

    private LinkedBlockingQueue<SampleWrapper> sampleQueue;
    public static BufferedWriter collectedWriter;
    public static BufferedWriter detectedWriter;

    public SampleWriter () {
        this.sampleQueue = new LinkedBlockingQueue<>();

        LocalDate currentTime = LocalDate.now();
        String detectedFileName = Parameters.DATA_DIRECTORY + currentTime + "-detected.csv";
        String collectedFileName = Parameters.DATA_DIRECTORY + currentTime + "-collected.csv";

        try {
            // Detected samples writer
            File f = new File(detectedFileName);
            boolean exists = f.exists();
            if (!exists) {
                f.createNewFile();
            }

            FileWriter fw = new FileWriter(f, true);
            detectedWriter = new BufferedWriter(fw);

            // Collected samples writer
            f = new File(collectedFileName);
            exists = f.exists();
            if (!exists) {
                f.createNewFile();
            }

            fw = new FileWriter(f, true);
            collectedWriter = new BufferedWriter(fw);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run () {
        while (true) {
            try {
                SampleWrapper sw = (SampleWrapper) sampleQueue.take();

                BufferedWriter writer = null;

                if (sw.isCollected()) {
                    writer = collectedWriter;
                } else {
                    writer = detectedWriter;
                }

                writer.write(sw.getFlowDump());
                writer.newLine();
                writer.flush();
            } catch (InterruptedException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addSample(boolean collected, String flowDump) {
        try {
            sampleQueue.put(new SampleWrapper(collected, flowDump));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
