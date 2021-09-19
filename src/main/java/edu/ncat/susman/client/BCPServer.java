package edu.ncat.susman.client;

import edu.ncat.susman.Parameters;
import org.apache.logging.log4j.core.jmx.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class BCPServer extends Thread {

    private ServerSocket serverSocket;
    private static HashMap<String, Long> connectedAppliances;

    private static BCPServer Instance = new BCPServer();

    public static BCPServer getInstance() {return Instance;}

    public BCPServer () {
        connectedAppliances = new HashMap<>();
    }

    public void init () {
        try {
            serverSocket = new ServerSocket(Parameters.BCP_PORT);
            Instance.start();
            Instance.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ConnectionWorker(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateConnectAppliances(String ipAddress) {
        if (connectedAppliances.containsKey(ipAddress)) {
            connectedAppliances.replace(ipAddress, System.currentTimeMillis());
        } else {
            connectedAppliances.put(ipAddress, System.currentTimeMillis());
        }
    }
}
