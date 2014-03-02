package com.vimukthi.assignments.ocsf.simplechat;

import com.vimukthi.assignments.ocsf.AbstractServer;
import com.vimukthi.assignments.ocsf.ConnectionToClient;

/**
 *
 * @author vimukthi
 */
public class EchoServer extends AbstractServer {

    public EchoServer(int port) {
        super(port);
    }    

    public static void main(String[] args) {
        new Thread(new EchoServer(8000)).start();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        this.sendToAllClients(msg.toString() + "\n");
    }

    @Override
    protected void serverStarted() {
        super.serverStarted(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void serverStopped() {
        super.serverStopped(); //To change body of generated methods, choose Tools | Templates.
    }   
    
}
