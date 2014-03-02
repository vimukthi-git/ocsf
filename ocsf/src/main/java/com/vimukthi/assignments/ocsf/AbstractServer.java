package com.vimukthi.assignments.ocsf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vimukthi
 */
public abstract class AbstractServer implements Runnable {

    private int port;
    private int numberOfClients = 0;
    private int timeout = 10000;
    private int backlog = 10;
    private boolean closed = false;
    private boolean listening = false;
    private List<Thread> clientThreads;
    private ServerSocket serverSocket;
    private static final Logger logger = Logger.getLogger(AbstractServer.class.getName());
    private Thread server;

    public AbstractServer(int port) {
        this.port = port;
    }

    protected void clientConnected(ConnectionToClient client) {
        //Hook method called each time a new client connection is accepted.
    }

    protected synchronized void clientDisconnected(ConnectionToClient client) {
        // Hook method called each time a client disconnects.
    }

    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        //Hook method called each time an exception is thrown in a ConnectionToClient thread.
    }

    protected void serverClosed() {
        //Hook method called when the server is clased.
    }

    protected void serverStarted() {
        //Hook method called when the server starts listening for connections.
    }

    protected void serverStopped() {
        //Hook method called when the server stops accepting connections.
    }

    protected void listeningException(Throwable exception) {
        //Hook method called when the server stops accepting connections because an exception has been raised.
    }

    public void sendToAllClients(Object msg) {
        for (Thread thread : clientThreads) {
            ConnectionToClient con = (ConnectionToClient) thread;
            if (msg != null) {
                con.sendToClient(msg);
            }
        }
    }

    public final Thread[] getClientConnections() {
        //Returns an array containing the existing client connections.
        return (Thread[]) clientThreads.toArray();
    }

    public final int getNumberOfClients() {
        //Counts the number of clients currently connected.
        return numberOfClients;
    }

    public final int getPort() {
        //Returns the port number.
        return port;
    }

    public final boolean isClosed() {
        //Returns true if the server is closed.
        return closed;
    }

    public final boolean isListening() {
        //Returns true if the server is ready to accept new clients.
        return listening;
    }

    public final void setBacklog(int backlog) {
        //Sets the maximum number of waiting connections accepted by the operating system.
        this.backlog = backlog;
    }

    public final void setPort(int port) {
        //Sets the port number for the next connection.
        this.port = port;
    }

    public final void setTimeout(int timeout) {
        //Sets the timeout time when accepting connections.
        this.timeout = timeout;
    }

    void removeClient(ConnectionToClient client) {
        numberOfClients--;
        clientThreads.remove(client);
    }

    public final void listen() {
        logger.log(Level.INFO, "Server listening");
        listening = true;
        try {
            serverSocket = new ServerSocket(port);            
            clientThreads = new ArrayList<Thread>();
            server = new Thread(this);
            server.start();
            logger.log(Level.INFO, "Server started");
            serverStarted();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
    }

    public final void stopListening() {
        //Causes the server to stop accepting new connections.
        listening = false;
        serverStopped();
    }

    public final void close() {
        try {
            //Closes the server socket and the connections with all clients.
            serverSocket.close();
            for (Thread thread : clientThreads) {
                ConnectionToClient con = (ConnectionToClient) thread;
                con.close();
            }
            serverClosed();
            logger.log(Level.INFO, "Server closed down");
            closed = true;
        } catch (IOException ex) {
            Logger.getLogger(AbstractServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public final void run() {
        while (listening && backlog >= numberOfClients && !closed) {
            Socket connectionSocket;
            try {
                connectionSocket = serverSocket.accept();
                ConnectionToClient client = new ConnectionToClient(this, connectionSocket.getInetAddress(), connectionSocket);
                clientThreads.add(client);
                numberOfClients++;
                clientConnected(client);
                client.start();
            } catch (IOException ex) {
                listeningException(ex);
            }
        }
    }

    protected abstract void handleMessageFromClient(Object msg, ConnectionToClient client);
}
