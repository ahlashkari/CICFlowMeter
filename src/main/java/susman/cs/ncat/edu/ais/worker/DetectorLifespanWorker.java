package susman.cs.ncat.edu.ais.worker;

import susman.cs.ncat.edu.ais.AIS;
import susman.cs.ncat.edu.ais.Detector;
import susman.cs.ncat.edu.ais.DetectorSet;
import susman.cs.ncat.edu.ais.Range;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class DetectorLifespanWorker extends Thread {
    private DetectorSet owner;

    public DetectorLifespanWorker(DetectorSet owner) {
        super();
        this.owner = owner;
    }

    @Override
    public void run () {
        while (true) {
            for (Detector d : owner.getDetectors()) {
                if (d.getIncorrectMatches() >= AIS.getInstance().IMMATURE_INCORRECT_MATCHES_THRESHOLD) {
                    d.markForRegeneration();

                    try {
                        Socket socket = new Socket(AIS.getInstance().DNN_IP_ADDR, AIS.getInstance().REGEN_PORT);

                        // Create input and output streams to read from and write to the server
                        PrintStream out = new PrintStream(socket.getOutputStream());
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String sendString = owner.getType();
                        out.print(sendString);

                        String line = in.readLine();

                        String[] values = line.split(",");

                        int index = 0;
                        for (Range r : d.getRanges()) {
                            float min = Float.parseFloat(values[index++]);
                            float max = Float.parseFloat(values[index++]);

                            r.setMin(min);
                            r.setMax(max);
                        }

                        in.close();
                        out.close();
                        socket.close();

                        d.unmarkForRegeneration();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
