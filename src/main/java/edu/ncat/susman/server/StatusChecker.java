package edu.ncat.susman.server;

import cic.cs.unb.ca.Sys;
import edu.ncat.susman.Parameters;
import org.apache.commons.lang3.ArrayUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class StatusChecker extends Thread {


    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000 * 60);

                for (Map.Entry<String, Long> entry : BCPServer.getInstance().getConnectedAppliances().entrySet()) {
                    long elapsedTime = System.currentTimeMillis() - entry.getValue();

                    if (elapsedTime > Parameters.CONNECTED_APPLIANCE_STATUS_TIMEOUT) {
                        try {
                            Socket socket = new Socket();
                            SocketAddress socketAddress = new InetSocketAddress(entry.getKey(), Parameters.BCP_PORT);
                            socket.connect(socketAddress, 1000 * 5);

                            // Create input and output streams to read from and write to the server
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            DataInputStream in = new DataInputStream(socket.getInputStream());

                            byte firstByte = (byte) ((Parameters.DEFAULT_VERSION << 4) | Parameters.SP_PROTOCOL);
                            byte secondByte = (byte) (Parameters.SP_REQUEST_FLAG << 4);
                            short thirdFourthByte = (short) (Parameters.HEADER_SIZE + (Parameters.IP_ADDRESS_SIZE * 8 * 2) +
                                    (Parameters.TCP_UDP_PORT_SIZE * 8 * 2) + (Parameters.IP_PROTOCOL_SIZE * 8) +
                                    (Parameters.SAMPLE_NUMBER_OF_FLOAT_VALUES * Parameters.FLOAT_SIZE * 8));

                            byte[] msg = new byte[]{firstByte, secondByte};
                            msg = ArrayUtils.addAll(msg, Parameters.leShortToByteArray(thirdFourthByte));

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

                            in.close();
                            out.close();
                            socket.close();
                            BCPServer.getInstance().updateConnectAppliances(entry.getKey());
                        } catch (IOException e) {
                            BCPServer.getInstance().getConnectedAppliances().remove(entry.getKey());
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
