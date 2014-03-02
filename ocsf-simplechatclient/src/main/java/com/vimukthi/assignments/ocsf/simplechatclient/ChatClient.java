/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vimukthi.assignments.ocsf.simplechatclient;

import com.vimukthi.assignments.ocsf.AbstractClient;
import java.util.logging.Logger;

/**
 *
 * @author vimukthi
 */
public class ChatClient extends AbstractClient {

    private ChatIF ui;
    private static final Logger logger = Logger.getLogger(ChatClient.class.getName());

    public ChatClient(String host, int port, ChatIF ui) {
        super(host, port);
        this.ui = ui;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        ui.display(msg.toString());
    }

    @Override
    protected void connectionClosed() {
        super.connectionClosed();
        ui.display("Connection was closed..");
    }

    @Override
    protected void connectionException(Exception exception) {
        super.connectionException(exception);
        ui.display("Connection closed unexpectedly...");
    }

    public void handleMessageFromClientUI(Object msg) {
        if (msg != null && !msg.equals("\n")) {
            if (isConnected()) {
                sendToServer(msg);
            } else {
                ui.display("Not connected");
            }
        }
    }

    public void quit() {
        closeConnection();
    }

    public ChatIF getUi() {
        return ui;
    }

    public void setUi(ChatIF ui) {
        this.ui = ui;
    }
}
