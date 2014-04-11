package com.vimukthi.assignments.ocsf;

/**
 *
 * @author vimukthi
 */
public class AdaptableClient extends AbstractClient {

    private ObservableClient client;

    public AdaptableClient(String host, int port, ObservableClient client) {
        super(host, port);
        this.client = client;
    }

    /**
     * Hook method called after the connection has been closed.
     */
    @Override
    final protected void connectionClosed() {
        client.connectionClosed();
    }

    /**
     * Hook method called after an exception is raised by the client listening
     * thread.
     *
     * @param exception the exception raised.
     */
    @Override
    final protected void connectionException(Exception exception) {
        client.connectionException(exception);
    }

    /**
     * Hook method called after a connection has been established.
     */
    @Override
    final protected void connectionEstablished() {
        client.connectionEstablished();
    }

    /**
     * Handles a message sent from the server to this client.
     *
     * @param msg the message sent.
     */
    @Override
    final protected void handleMessageFromServer(Object msg) {
        client.handleMessageFromServer(msg);
    }
}
