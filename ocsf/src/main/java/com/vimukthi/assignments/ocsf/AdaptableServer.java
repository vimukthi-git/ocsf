package com.vimukthi.assignments.ocsf;

/**
 * 
 * @author vimukthi
 */
class AdaptableServer extends AbstractServer {
    //Instance variables **********************************************

    /**
     * The adapter used to simulate multiple class inheritance.
     */
    private ObservableServer server;

// CONSTRUCTORS *****************************************************
    /**
     * Constructs the server adapter.
     *
     * @param host the server's host name.
     * @param port the port number.
     */
    public AdaptableServer(int port, ObservableServer server) {
        super(port);
        this.server = server;
    }

// OVERRIDDEN METHODS ---------
    /**
     * Hook method called each time a new client connection is accepted.
     *
     * @param client the connection connected to the client.
     */
    @Override
    final protected void clientConnected(ConnectionToClient client) {
        server.clientConnected(client);
    }

    /**
     * Hook method called each time a client disconnects.
     *
     * @param client the connection with the client.
     */
    @Override
    final protected void clientDisconnected(ConnectionToClient client) {
        server.clientDisconnected(client);
    }

    /**
     * Hook method called each time an exception is raised in a client thread.
     *
     * @param client the client that raised the exception.
     * @param exception the exception raised.
     */
    @Override
    final protected void clientException(ConnectionToClient client,
            Throwable exception) {
        server.clientException(client, exception);
    }

    /**
     * Hook method called when the server stops accepting connections because an
     * exception has been raised.
     *
     * @param exception the exception raised.
     */
    @Override
    final protected void listeningException(Throwable exception) {
        server.listeningException(exception);
    }

    /**
     * Hook method called when the server stops accepting connections.
     */
    @Override
    final protected void serverStopped() {
        server.serverStopped();
    }

    /**
     * Hook method called when the server starts listening for connections.
     */
    @Override
    final protected void serverStarted() {
        server.serverStarted();
    }

    /**
     * Hook method called when the server is closed.
     */
    @Override
    final protected void serverClosed() {
        server.serverClosed();
    }

    /**
     * Handles a command sent from the client to the server.
     *
     * @param msg the message sent.
     * @param client the connection connected to the client that sent the
     * message.
     */
    @Override
    final protected void handleMessageFromClient(Object msg,
            ConnectionToClient client) {
        server.handleMessageFromClient(msg, client);
    }
}