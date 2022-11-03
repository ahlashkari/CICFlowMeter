package edu.ncat.susman.server;

import edu.ncat.susman.Parameters;
import edu.ncat.susman.dataset.Normalizer;
import edu.ncat.susman.dataset.Sample;
import edu.ncat.susman.ais.Detector;
import edu.ncat.susman.ais.DetectorSet;
import edu.ncat.susman.server.writer.SampleWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.time.LocalDate;
import java.util.HashMap;
import java.net.Socket;
import java.util.Map;

public class DetectionWorker extends Thread {
    private DetectorSet owner;
    protected static final Logger logger = LoggerFactory.getLogger(DetectionWorker.class);
    private SampleWriter sampleWriter;


    public DetectionWorker(DetectorSet detectorSet, SampleWriter sampleWriter) {
        super();

        owner = detectorSet;

        this.sampleWriter = sampleWriter;
    }

    @Override
    public void run() {
        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        try {
            socket = new Socket(Parameters.IP_ADDRESS_VALIDATOR, Parameters.BCP_PORT);
            socket.setKeepAlive(true);
            socket.setSoTimeout(60000);
            // Create input and output streams to read from and write to the server
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            HashMap<String, Detector> detectors = owner.getDetectors();

            while (true) {
                Sample sample = (Sample) owner.getQueue().take();
                sampleWriter.addSample(true, sample.getFlowDump());
                //System.out.println(sample.getSrcIP() + " - " + sample.getDstIP());

                Detector detectedDetector = new Detector();
                boolean detected = false;

                if (!sample.getDstPort().equals("1891") && !sample.getSrcPort().equals("1891")) {
                        for (Map.Entry<String, Detector> entry : detectors.entrySet()) {
                            Detector d = entry.getValue();
                            if (d.isMarkedForRegeneration()) {
                                continue;
                            }

                            if (d.classify(sample)) {
                                detectedDetector = d;
                                detected = true;
                                break;
                            }
                        }
                }

//                    Sample sample = new Sample();
//                    sample.generateRandom();
//                    Detector detectedDetector = new Detector();
//                    detectedDetector.generateRandom();
//                    boolean detected = true;


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


                    try {


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
                        out.flush();

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
                        byte response = in.readByte();

                        logger.info(sample.getSrcIP() + " - " + sample.getDstIP() + ": " + response);

                        if (response == 1) {
                            detectedDetector.promoteMature();
                        } else {
                            detectedDetector.incrementIncorrectMatch();
                            //detectedDetector.markForRegeneration();
                        }


                        //in.close();
                        //out.close();
                        //socket.close();

                        BCPServer.getInstance().updateConnectAppliances(Parameters.IP_ADDRESS_VALIDATOR);

                        sampleWriter.addSample(false, sample.getFlowDump());
                    } catch (BufferOverflowException e) {
                        e.printStackTrace();
                        owner.addNewSample(sample);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        if (socket.isClosed()) {
                            socket = new Socket(Parameters.IP_ADDRESS_VALIDATOR, Parameters.BCP_PORT);
                            socket.setKeepAlive(true);
                            socket.setSoTimeout(60000);

                            owner.addNewSample(sample);
                            out = new DataOutputStream(socket.getOutputStream());
                            in = new DataInputStream(socket.getInputStream());
                        }
                    } catch (Exception e) {
                        System.out.println("Done");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Done");
        }
    }

    public void close() {
        this.interrupt();
    }
}
