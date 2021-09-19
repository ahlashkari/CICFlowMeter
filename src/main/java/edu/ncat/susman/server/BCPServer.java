package edu.ncat.susman.server;

import edu.ncat.susman.Parameters;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;

public class BCPServer extends Thread {

    private ServerSocket serverSocket;
    private HashMap<String, Long> connectedAppliances;

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
                new StatusChecker().start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, Long> getConnectedAppliances () {
        return this.connectedAppliances;
    }

    public void updateConnectAppliances(String ipAddress) {
        if (connectedAppliances.containsKey(ipAddress)) {
            connectedAppliances.replace(ipAddress, System.currentTimeMillis());
        } else {
            connectedAppliances.put(ipAddress, System.currentTimeMillis());
        }
    }
}
