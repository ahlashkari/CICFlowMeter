package susman.cs.ncat.edu.ais.worker;

import susman.cs.ncat.edu.ais.AIS;
import susman.cs.ncat.edu.ais.Detector;
import susman.cs.ncat.edu.ais.DetectorSet;
import susman.cs.ncat.edu.dataset.Sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.net.Socket;

public class DetectionWorker extends Thread {
    private DetectorSet owner;

    public DetectionWorker(DetectorSet detectorSet) {
        owner = detectorSet;
    }

    @Override
    public void run() {
        Sample newSample = owner.pop();
        Detector detectedDetector = new Detector();

        List<Detector> detectors = owner.getDetectors();
        int rValue = owner.getrValue();

        boolean detected = false;

        for (Detector d: detectors) {
            if (d.classify(newSample, rValue)) {
                detectedDetector = d;
                detected = true;
                break;
            }
        }

        // If the sample was detected by a detector
        // Establish a connection with the DNN
        // Send the sample
        // Retrieve if the DNN validated the detection
        // Retrieve if the System Admin confirmed the detection
        if (detected && detectedDetector.getId() != null) {
            // Open socket to DNN
            // Send the Detector ID and Sample features

            try {
                Socket socket = new Socket(AIS.getInstance().DNN_IP_ADDR, AIS.getInstance().DNN_PORT);

                // Create input and output streams to read from and write to the server
                PrintStream out = new PrintStream( socket.getOutputStream() );
                BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

                String sendString = owner.getType() + "," + detectedDetector.getId();

                for (float value : newSample.getFeatures()) {
                    sendString += "," + value;
                }
                out.print(sendString);

                /*
                byte typeLength = (byte) owner.getType().getBytes().length;
                out.print(typeLength);
                out.print(owner.getType());
                out.print(detectedDetector.getId());
                for (float value : newSample.getFeatures()) {
                    byte valueLength = (byte) Float.toString(value).getBytes().length;
                    out.print(valueLength);
                    out.print(Float.toString(value));
                }
                */

                String line = in.readLine();
                boolean validated = Boolean.parseBoolean(line);

                if (validated) {
                    detectedDetector.promoteMature();
                } else {
                    detectedDetector.incrementIncorrectMatch();
                    detectedDetector.markForRegeneration();
                }

                in.close();
                out.close();
                socket.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
