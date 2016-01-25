/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author laursuom
 */
public class LamportNode {

    String id;
    int ownPort;
    HashMap<Integer, NodeStruct> structs;
    volatile int time;

    LamportNode(String id, int ownPort, HashMap<Integer, NodeStruct> structs) {
        this.id = id;
        this.ownPort = ownPort;
        this.structs = structs;
        time = 0;
    }

    public void start() {
        new Thread(new ConnectionListener()).start();
        while (time < 100) {
            Random random = new Random();
            if (random.nextBoolean()) { //Local event
                time += random.nextInt(4) + 1;
            } else {

            }
        }
    }

    private class ConnectionListener implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(ownPort);
                while (true) {
                    serverSocket.accept();
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String[] tmp = in.readLine().trim().split("\\s+"); // Read a message in the form 's t' where s is the sender id and t timestamp
                    time += Math.max(time, Integer.parseInt(tmp[1])) + 1;
                    System.out.format("r %s %s %n", tmp[0], tmp[1], time);
                    in.close();
                    clientSocket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LamportNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
