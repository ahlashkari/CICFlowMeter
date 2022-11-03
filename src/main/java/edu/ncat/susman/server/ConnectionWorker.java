package edu.ncat.susman.server;

import edu.ncat.susman.Parameters;
import edu.ncat.susman.ais.AIS;
import org.apache.commons.lang3.ArrayUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionWorker extends Thread {
    private Socket clientSocket;

    public ConnectionWorker (Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run () {
        BCPServer.getInstance().updateConnectAppliances(clientSocket.getInetAddress().getHostAddress());
        try {

            // Create input and output streams to read from and write to the server
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());

            byte firstByte = in.readByte();
            byte secondByte = in.readByte();
            short thirdFourthByte = in.readShort();

            byte version = (byte) (firstByte >> 4);
            byte protocol = (byte) (firstByte - (firstByte >> 4 << 4));
            byte flags = (byte) (secondByte >> 4);

            if (version != Parameters.DEFAULT_VERSION) {
                System.out.println("Incompatible BSP version");
                return;
            }

            switch (protocol) {
                case Parameters.SP_PROTOCOL:
                    switch (flags) {
                        case Parameters.SP_REQUEST_FLAG:
                            secondByte = (byte) (1 << 4);
                            thirdFourthByte = (short) Parameters.HEADER_SIZE;
                            byte[] msg = new byte[]{firstByte, secondByte};
                            msg = ArrayUtils.addAll(msg, Parameters.leShortToByteArray(thirdFourthByte));

                            out.write(msg);
                    }
                    break;
                case Parameters.IDP_PROTOCOL:
                    //No code yet
                    break;
                case Parameters.DVCP_PROTOCOL:
                    switch (flags) {
                        case Parameters.DVCP_ACK_FLAG:
                            byte response = (byte) (in.readByte());
                            byte[] uuid = new byte[16];

                            for (int i = 0; i < uuid.length; i++) {
                                uuid[i] = in.readByte();
                            }

                            String detectorId = Parameters.bytesToHex(uuid);

                            if (response == 1)
                                AIS.getInstance().getDetectors().get(detectorId).promoteMemory();
                            else
                                AIS.getInstance().getDetectors().get(detectorId).incrementIncorrectMatch();
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
