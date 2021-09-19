package edu.ncat.susman.client;

import edu.ncat.susman.Parameters;
import edu.ncat.susman.ais.AIS;
import edu.ncat.susman.ais.Detector;
import edu.ncat.susman.ais.DetectorSet;
import edu.ncat.susman.ais.Range;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

public class DetectorLifespanWorker extends Thread {
    private DetectorSet owner;

    public DetectorLifespanWorker(DetectorSet owner) {
        super();
        this.owner = owner;
    }

    @Override
    public void run () {
        while (true) {
            for (Map.Entry<String, Detector> entry : owner.getDetectors().entrySet()) {
                Detector d = entry.getValue();
                if (d.checkIncorrectThreshold() || d.checkLifeSpan()) {
                    d.markForRegeneration();

                    try {
                        Socket socket = new Socket(Parameters.IP_ADDRESS_VALIDATOR, Parameters.BCP_PORT);

                        // Create input and output streams to read from and write to the server
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        DataInputStream in = new DataInputStream(socket.getInputStream());

                        // Write BCP Header info
                        byte firstByte = (byte) ((Parameters.DEFAULT_VERSION << 4) | Parameters.DGP_PROTOCOL);
                        byte secondByte = (byte) (Parameters.DGP_REG_FLAG << 4);
                        short thirdFourthByte = (short) (Parameters.HEADER_SIZE + 8);

                        out.writeByte(firstByte);
                        out.writeByte(secondByte);
                        out.writeShort(thirdFourthByte);

                        byte[] msg = {d.getType()};
                        out.write(msg);

                        // Read the BCP Header and corresponding data
                        firstByte = in.readByte();
                        secondByte = in.readByte();
                        thirdFourthByte = in.readShort();

                        byte version = (byte) (firstByte >> 4);
                        byte protocol = (byte) (firstByte - (firstByte >> 4 << 4));
                        byte flags = (byte) (secondByte >> 4);

                        if (version != Parameters.DEFAULT_VERSION) {
                            System.out.println("Incompatible BSP version");
                            return;
                        } else if (protocol != Parameters.DGP_PROTOCOL || flags != Parameters.DGP_DEV_FLAG)

                        for (Range r : d.getRanges()) {
                            float min = in.readFloat();
                            float max = in.readFloat();

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
