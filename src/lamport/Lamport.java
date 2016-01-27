/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author laursuom
 */
public class Lamport {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length != 2) {
            System.out.println("You have to provide exactly two arguments: The configuration file and the integer"
                    + " id corresponding to this node");
            System.exit(0);
        }
        LamportNode lamport = parseConf(args); //Initialize the node
        lamport.start(); //Start the node
    }
    
    /*
    Creates a configuration for this running node. Solves own port and saves other nodes to memory.
    */
    private static LamportNode parseConf(String[] args) {
        HashMap<Integer, NodeStruct> structs = new HashMap();
        File file = new File(args[0]);
        int ownPort = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) { //Reading as long as there are lines in the file
                String[] tmp = line.trim().split("\\s+"); //Tokenize the line using whitespace as separator. Example: "1 localhost 5001" is put into an array of three elements.
                if (!tmp[0].equals(args[1])) { // If not reading the line corresponding to this node.
                    NodeStruct struct = new NodeStruct((tmp[1]), Integer.parseInt(tmp[2])); //Create a new struct for another node in the network, containing the address and port.
                    structs.put(Integer.parseInt(tmp[0]), struct); //Save the struct with the corresponding node ID as a key
                } else {
                 ownPort = Integer.parseInt(tmp[2]);  // Retrieve the port this node should listen. 
                }
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println("No configuration file provided. Aborting.");
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(Lamport.class.getName()).log(Level.SEVERE, null, ex);
        }
     return new LamportNode(args[1], ownPort, structs);  //Return the new LamportNode object initializing it with the ID, portnumber and the data of other nodes 
    }

}
