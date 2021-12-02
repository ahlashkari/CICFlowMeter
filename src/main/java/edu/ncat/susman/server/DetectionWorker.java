package edu.ncat.susman.server;

import edu.ncat.susman.Parameters;
import edu.ncat.susman.dataset.Normalizer;
import edu.ncat.susman.dataset.Sample;
import edu.ncat.susman.ais.Detector;
import edu.ncat.susman.ais.DetectorSet;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.BufferOverflowException;
import java.time.LocalDate;
import java.util.HashMap;
import java.net.Socket;
import java.util.Map;

public class DetectionWorker extends Thread {
    private DetectorSet owner;
    protected static final Logger logger = LoggerFactory.getLogger(DetectionWorker.class);
    public static BufferedWriter collectedWriter;
    public static BufferedWriter detectedWriter;

    public DetectionWorker(DetectorSet detectorSet) {
        super();

        owner = detectorSet;

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

    private void writeCSV(boolean collected, String header, String flowDump) {
        try {

            BufferedWriter writer = null;

            if (collected) {
                writer = collectedWriter;
            } else {
                writer = detectedWriter;
            }

            writer.write(flowDump);
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                Sample sample = (Sample) owner.getQueue().take();
                writeCSV(true, sample.getHeader(), sample.getFlowDump());

                Detector detectedDetector = new Detector();

                HashMap<String, Detector> detectors = owner.getDetectors();
                int rValue = owner.getrValue();

                boolean detected = false;

                if (!sample.getDstPort().equals("1891") && !sample.getSrcPort().equals("1891")) {
                    for (Map.Entry<String, Detector> entry : detectors.entrySet()) {
                        Detector d = entry.getValue();
                        if (d.classify(sample, rValue)) {
                            detectedDetector = d;
                            detected = true;
                            break;
                        }
                    }
                }

                // if (!detected)
                    // logger.info(sample.getSrcIP() + " - " + sample.getDstIP());

                // If the sample was detected by a detector
                // Establish a connection with the Validator
                // Send the sample
                // Retrieve if the DNN validated the detection
                // Retrieve if the System Admin confirmed the detection
                if (detected && detectedDetector.getId() != null) {
                    // Open socket to DNN
                    // Send the Detector ID and Sample features

                    writeCSV(false, sample.getHeader(), sample.getFlowDump());

                    try {

                        Socket socket = new Socket(Parameters.IP_ADDRESS_VALIDATOR, Parameters.BCP_PORT);

                        // Create input and output streams to read from and write to the server
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        DataInputStream in = new DataInputStream(socket.getInputStream());

                        byte firstByte = (byte) ((Parameters.DEFAULT_VERSION << 4) | Parameters.DVCP_PROTOCOL);
                        byte secondByte = (byte) (Parameters.DVCP_DET_FLAG << 4);
                        short thirdFourthByte = (short) (Parameters.HEADER_SIZE + (Parameters.IP_ADDRESS_SIZE * 8 * 2) +
                                (Parameters.TCP_UDP_PORT_SIZE * 8 * 2) + (Parameters.IP_PROTOCOL_SIZE * 8) +
                                (Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES * Parameters.FLOAT_SIZE * 8));

                        byte[] msg = new byte[]{firstByte, secondByte};
                        msg = ArrayUtils.addAll(msg, Parameters.leShortToByteArray(thirdFourthByte));
                        msg = ArrayUtils.addAll(msg, Parameters.hexToBytes(detectedDetector.getId()));
                        msg = ArrayUtils.addAll(msg, detectedDetector.getType());
                        msg = ArrayUtils.addAll(msg, Parameters.ipAddressBytes(sample.getSrcIP()));
                        msg = ArrayUtils.addAll(msg, Parameters.ipAddressBytes(sample.getDstIP()));
                        msg = ArrayUtils.addAll(msg, Parameters.portBytes(sample.getSrcPort()));
                        msg = ArrayUtils.addAll(msg, Parameters.portBytes(sample.getDstPort()));
                        msg = ArrayUtils.addAll(msg, Byte.parseByte(sample.getProtocol()));

//                    out.writeByte(firstByte);
//                    out.writeByte(secondByte);
//                    out.writeShort(thirdFourthByte);
//                    out.write(Parameters.ipAddressBytes(sample.getSrcIP()));
//                    out.write(Parameters.ipAddressBytes(sample.getDstIP()));
//                    out.write(Parameters.portBytes(sample.getSrcPort()));
//                    out.write(Parameters.portBytes(sample.getDstPort()));
//                    out.writeByte(Byte.parseByte(sample.getProtocol()));

                        for (float value : sample.getFeatures()) {
                            msg = ArrayUtils.addAll(msg, Parameters.leFloatToByteArray(value));
//                        out.writeFloat(value);
                        }

                        out.write(msg);

                        firstByte = in.readByte();
                        secondByte = in.readByte();
                        thirdFourthByte = in.readShort();

                        byte version = (byte) (firstByte >> 4);
                        byte protocol = (byte) (firstByte - (firstByte >> 4 << 4));
                        byte flags = (byte) (secondByte >> 4);

                        if (version != Parameters.DEFAULT_VERSION) {
                            System.out.println("Incompatible BSP version");
                            return;
                        }
                        byte response = (byte) (in.readByte());

                        logger.info(sample.getSrcIP() + " - " + sample.getDstIP() + ": " + response);

                        if (response == 1) {
                            detectedDetector.promoteMature();
                        } else {
                            detectedDetector.incrementIncorrectMatch();
                            //detectedDetector.markForRegeneration();
                        }

                        in.close();
                        out.close();
                        socket.close();

                        BCPServer.getInstance().updateConnectAppliances(Parameters.IP_ADDRESS_VALIDATOR);
                    } catch (IOException | BufferOverflowException e) {
                        e.printStackTrace();
                        owner.addNewSample(sample);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
