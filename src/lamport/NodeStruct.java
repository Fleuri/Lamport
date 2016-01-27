/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamport;

/**
 *
 * @author laursuom
 */
/*
This struct keeps track of the details of other nodes in the network.
*/
public class NodeStruct {
    public String address;
    public int port;
    
    public NodeStruct(String address, int port){
        this.address = address;
        this.port = port;
    }
}
