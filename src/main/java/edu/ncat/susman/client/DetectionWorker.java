package edu.ncat.susman.client;

import edu.ncat.susman.Parameters;
import edu.ncat.susman.dataset.Sample;
import edu.ncat.susman.ais.AIS;
import edu.ncat.susman.ais.Detector;
import edu.ncat.susman.ais.DetectorSet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.net.Socket;
import java.util.Map;

public class DetectionWorker extends Thread {
    private DetectorSet owner;

    public DetectionWorker(DetectorSet detectorSet) {
        super();

        owner = detectorSet;
    }

    @Override
    public void run() {
        Sample sample = owner.pop();
        Detector detectedDetector = new Detector();

        HashMap<String, Detector> detectors = owner.getDetectors();
        int rValue = owner.getrValue();

        boolean detected = false;

        for (Map.Entry<String, Detector> entry: detectors.entrySet()) {
            Detector d = entry.getValue();
            if (d.classify(sample, rValue)) {
                detectedDetector = d;
                detected = true;
                break;
            }
        }

        // If the sample was detected by a detector
        // Establish a connection with the Validator
        // Send the sample
        // Retrieve if the DNN validated the detection
        // Retrieve if the System Admin confirmed the detection
        if (detected && detectedDetector.getId() != null) {
            // Open socket to DNN
            // Send the Detector ID and Sample features

            try {

                Socket socket = new Socket(Parameters.IP_ADDRESS_VALIDATOR, Parameters.BCP_PORT);

                // Create input and output streams to read from and write to the server
                DataOutputStream out = new DataOutputStream( socket.getOutputStream() );
                DataInputStream in = new DataInputStream( socket.getInputStream() );

                byte firstByte = (byte) ((Parameters.DEFAULT_VERSION << 4) | Parameters.DVCP_PROTOCOL);
                byte secondByte = (byte) (Parameters.DVCP_DET_FLAG << 4);
                short thirdFourthByte = (short) (Parameters.HEADER_SIZE + (Parameters.IP_ADDRESS_SIZE * 8 * 2) +
                        (Parameters.TCP_UDP_PORT_SIZE * 8 * 2) + (Parameters.IP_PROTOCOL_SIZE * 8) +
                        (Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES * Parameters.FLOAT_SIZE * 8));

                out.writeByte(firstByte);
                out.writeByte(secondByte);
                out.writeShort(thirdFourthByte);
                out.write(Parameters.ipAddressBytes(sample.getSrcIP()));
                out.write(Parameters.ipAddressBytes(sample.getDstIP()));
                out.write(Parameters.portBytes(sample.getSrcPort()));
                out.write(Parameters.portBytes(sample.getDstPort()));
                out.writeByte(Byte.parseByte(sample.getProtocol()));

                for (float value : sample.getFeatures()) {
                    out.writeFloat(value);
                }

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
                byte response = (byte) (in.readByte() >> 7);
               
                if (response == 1) {
                    detectedDetector.promoteMature();
                } else {
                    detectedDetector.incrementIncorrectMatch();
                    //detectedDetector.markForRegeneration();
                }

                in.close();
                out.close();
                socket.close();


            } catch (IOException e) {
                e.printStackTrace();
                owner.addNewSample(sample);
            }
        }
    }
    
    
}
