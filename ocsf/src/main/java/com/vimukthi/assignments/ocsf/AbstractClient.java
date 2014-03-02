package com.vimukthi.assignments.ocsf;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vimukthi
 */
public abstract class AbstractClient implements Runnable {

    private String host;
    private InetAddress inetAddress;
    private int port;
    private boolean connected = false;
    private Socket clientSocket;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;
    private Thread input;
    private static final Logger logger = Logger.getLogger(AbstractClient.class.getName());

    public AbstractClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public final void openConnection() {
        try {
            //Opens the connection with the server.
            clientSocket = new Socket(host, port);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            connected = true;
            input = new Thread(this);
            input.start();
            connectionEstablished();
        } catch (Exception ex) {
            connectionException(ex);
            logger.log(Level.FINE, "connection failed", ex);
        }
    }

    public void sendToServer(Object msg) {
        if(isConnected()){
            try {
                //Sends an object to the server.             
                outToServer.writeBytes(msg.toString());
                logger.log(Level.FINE, "Sent: {0}", msg);
            } catch (IOException ex) {
                connectionException(ex);
                logger.log(Level.FINE, null, ex);
            }
        }
    }

    public final void closeConnection() {
        try {
            // Closes the connection to the server.
            clientSocket.close();
            connected = false;
            input = null;
            connectionClosed();
        } catch (IOException ex) {
            connectionException(ex);
            logger.log(Level.FINE, "Error while trying to disconnect", ex);
        }
    }

    protected void connectionEstablished() {
        //Hook method called after a connection has been established.
    }

    protected void connectionClosed() {
        //Hook method called after the connection has been closed.
    }

    protected void connectionException(Exception exception) {
        // Hook method called each time an exception is thrown by the client's thread that is reading messages from the server.
    }

    public final String getHost() {
        return host;
    }

    public final InetAddress getInetAddress() {
        return inetAddress;
    }

    public final int getPort() {
        return port;
    }

    public final boolean isConnected() {
        return connected;
    }

    public final void setHost(String host) {
        //Sets the server host for the next connection.
        this.host = host;
    }

    public final void setPort(int port) {
        //Sets the server port number for the next connection.
        this.port = port;
    }

    @Override
    public final void run() {
        //Waits for messages from the server.
        String serverSentence;
        try {                
            while (isConnected()) {
                serverSentence = inFromServer.readLine();
                if (serverSentence == null) {
                    closeConnection();
                } else {
                    logger.log(Level.FINE, "Received: {0}", serverSentence);
                    handleMessageFromServer(serverSentence);
                }
            }
        } catch (Exception ex) {
            //connectionException(ex);
            logger.log(Level.FINE, null, ex);
        }
    }

    protected abstract void handleMessageFromServer(Object msg);
    //Handles a message sent from the server to this client.
}
