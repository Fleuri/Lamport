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
         parseConf(args);
    }

    private static void parseConf(String[] args) {
        HashMap<Integer, NodeStruct> structs = new HashMap();
        File file = new File(args[0]);
        int ownPort;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.trim().split("\\s+");
                if (!tmp[0].equals(args[1])) {
                    structs.put(Integer.parseInt(tmp[0]), new NodeStruct((tmp[1]), Integer.parseInt(tmp[2])));
                } else {
                 ownPort = Integer.parseInt(tmp[2]);   
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("No configuration file provided. Aborting.");
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(Lamport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
