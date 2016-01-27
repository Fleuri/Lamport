/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    ArrayList<Integer> keys;
    volatile int time;
    volatile int eventTimer;

    LamportNode(String id, int ownPort, HashMap<Integer, NodeStruct> structs) {
        this.id = id;
        this.ownPort = ownPort;
        this.structs = structs;
        keys = new ArrayList(structs.keySet());
        time = 0;
    }

    public void start() {
        try {
            System.out.println("Starting!");
            new Thread(new ConnectionListener()).start();
            sleep(20000);
            while (true) {
                Random random = new Random();
                if (random.nextBoolean()) { //Local event
                    int increase = random.nextInt(4) + 1;
                    time = time + increase;
                    System.out.format("l %d %n", increase);
                    
                } else {
                    int seed = random.nextInt(keys.size());
                    sendMessage(keys.get(seed),structs.get(keys.get(seed))); // Select a random recipient from the list to send a message to.
                }
                eventTimer++;
                observeEventCount();
                sleep(4000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LamportNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendMessage(int recipientId, NodeStruct recipient) {
        try {
            time++;
            try (Socket socket = new Socket(recipient.address, recipient.port)) {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(id + " " + time);
                out.flush();
                out.close();
            }
            System.out.format("s %d %d %n", recipientId, time);
            
        } catch (IOException ex) {
          // System.err.println("Couldn't connect to node" + recipientId);
            System.out.format("s %d %d %n", recipientId, time);
            
        }
    }
    
    public void observeEventCount(){
        if (eventTimer >= 100) {
            System.exit(1);
        }
    }

    private class ConnectionListener implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(ownPort);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String[] tmp = in.readLine().trim().split("\\s+"); // Read a message in the form 's t' where s is the sender id and t timestamp
                    System.out.format("r %s %s %d %n", tmp[0], tmp[1], time);
                    time = Math.max(time, Integer.parseInt(tmp[1])) + 1;
                    in.close();
                    clientSocket.close();
                    eventTimer++;
                    observeEventCount();
                    
                }
            } catch (IOException ex) {
                System.err.println("Error");
            }
        }
    }
}
