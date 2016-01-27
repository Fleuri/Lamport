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
        eventTimer = 0;
    }
    /*
    This method starts the connection listener and generates randomly either local events or message sendings.
    */
    public void start() {
        try {
            // System.out.println("Starting!"); //This beautiful print is disabled for the actual submission
            new Thread(new ConnectionListener()).start(); //Start a new connection listener
            sleep(20000); //Wait 20 seconds before starting the actual algorithm
            while (true) {
                Random random = new Random(); //Random.nextBoolean is used to pick pseudorandomly between a local event and sending a message
                if (random.nextBoolean()) { //Local event
                    int increase = random.nextInt(4) + 1; //Random increase to clock for a local event. Range 1-5.
                    time = time + increase;
                    System.out.format("l %d %n", increase);
                    
                } else { //Boolean was false, ergo send a message
                    int seed = random.nextInt(keys.size()); //Select a random node from the list.
                    sendMessage(keys.get(seed),structs.get(keys.get(seed))); // Extract the node from the data structure, pass the id and the struct as an argument
                }
                eventTimer++; //Event has occured, increment timer
                observeEventCount(); //Check, if 100 events have occured.
                sleep(4000); //Sleep for 4 seconds to make the program easier and pleasant to observe
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(LamportNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    This method sends a message to a recipient passed as an argument. As this is an event, the clock is incremented by one.
    */
    private void sendMessage(int recipientId, NodeStruct recipient) {
        try {
            time++;
            try (Socket socket = new Socket(recipient.address, recipient.port)) { //Opening a socket
                PrintWriter out = new PrintWriter(socket.getOutputStream()); //Getting the outputstream
                out.println(id + " " + time); //Writing the message
                out.flush();
                out.close();
                socket.close();
            }
            System.out.format("s %d %d %n", recipientId, time);
            
        } catch (IOException ex) {
          // System.err.println("Couldn't connect to node" + recipientId);
            System.out.format("s %d %d %n", recipientId, time); //If the node has left the network, opening a socket fails.
                                                                //This causes an exception, which is masked to look like the sending actually happens.
            
        }
    }
    
    public void observeEventCount(){
        if (eventTimer >= 100) { //When 100 events have occured, program terminates
            System.exit(1);
        }
    }
   
   /*
    A private class running a thread which listens for connections
    */
    
    private class ConnectionListener implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(ownPort);
                while (true) {
                    Socket clientSocket = serverSocket.accept(); //Accepting connections
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Reading the input
                    String[] tmp = in.readLine().trim().split("\\s+"); // Read and tokenize a message in the form 's t' where s is the sender id and t timestamp
                    time = Math.max(time, Integer.parseInt(tmp[1])) + 1; //Running the Lamport's algorithm. If the received timestamp is ahead, it becomes the new timestam. In any case, the clock is incremented by one.
                    System.out.format("r %s %s %d %n", tmp[0], tmp[1], time);
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
