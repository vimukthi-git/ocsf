package com.vimukthi.assignments.ocsf;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vimukthi
 */
public class ConnectionToClient extends Thread {

    private InetAddress inetAddress;
    private Map<String, Object> info = new HashMap<String, Object>();
    private Socket connectionSocket;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;
    private Long clientNo;
    private final AbstractServer server;
    private static final Logger logger = Logger.getLogger(ConnectionToClient.class.getName());
    private boolean closed = false;

    public ConnectionToClient(AbstractServer server, InetAddress inetAddress, Socket connectionSocket) {
        this.server = server;
        this.inetAddress = inetAddress;
        this.connectionSocket = connectionSocket;
    }

    public void sendToClient(Object msg) {
        logger.log(Level.INFO, "Sent: {0} to Client connection {1}", new Object[]{msg.toString(), clientNo});
        try {
            outToClient.writeBytes(msg.toString());
        } catch (IOException ex) {
            getServer().clientException(this, ex);
            logger.log(Level.SEVERE, "Sending exception", ex);
        }
    }

    public void close() {
        closed = true;
        try {
            outToClient.close();
            inFromClient.close();
            connectionSocket.close();
            logger.log(Level.INFO, "Client connection {0} closed", clientNo);
        } catch (IOException ex) {
            getServer().clientException(this, ex);
            logger.log(Level.SEVERE, "closing exception", ex);
        }
        getServer().removeClient(this);
        getServer().clientDisconnected(this);
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInfo(String infoType, Object info) {
        this.info.put(infoType, info);
    }

    public Object getInfo(String infoType) {
        return this.info.get(infoType);
    }

    @Override
    public void run() {
        this.clientNo = Thread.currentThread().getId();
        logger.log(Level.INFO, "Client connection {0} started", clientNo);
        String clientSentence;
        try {
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            while (!closed) {
                clientSentence = inFromClient.readLine();
                if (clientSentence == null) {
                    close();
                } else {
                    logger.log(Level.INFO, "Client connection {0} Received: {1}", new Object[]{clientNo, clientSentence});
                    getServer().handleMessageFromClient(clientSentence, this);                    
                }
            }
        } catch (IOException ex) {
            close();
            getServer().clientException(this, ex);
            logger.log(Level.SEVERE, "connection exception", ex);
        }

    }

    private synchronized AbstractServer getServer() {
        return server;
    }
}
