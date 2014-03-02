package com.vimukthi.assignments.ocsf.simplechat;

import com.vimukthi.assignments.ocsf.AbstractServer;
import com.vimukthi.assignments.ocsf.ConnectionToClient;

/**
 *
 * @author vimukthi
 */
public class EchoServer extends AbstractServer {

    private ChatIF ui;

    public EchoServer(int port, ChatIF ui) {
        super(port);
        this.ui = ui;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (isLoginCommand(msg.toString())) {
            if (client.getInfo("loginid") == null) {
                String loginId = extractLoginId(msg.toString());
                client.setInfo("loginid", loginId);
            } else {
                client.sendToClient("Login already received\n");
            }            
        } else if (client.getInfo("loginid") != null) {
            String message = client.getInfo("loginid") + ">" + msg.toString();
            ui.display(message);
            this.sendToAllClients(message + "\n");
        } else {
            client.sendToClient("I didn't receive your login\n");
            client.close();
            this.removeClient(client);
        }
    }

    private String extractLoginId(String command_str) {
        String[] command_arr = command_str.split(" ");
        return command_arr[1];       
    }

    private boolean isLoginCommand(String input) {
        try {
            if (input != null && !input.equals("\n") && !input.equals("") && input.substring(0, 6).equals("#login")) {
                return true;
            }
        } catch (StringIndexOutOfBoundsException e){
            return false;
        }
        return false;
    }

    @Override
    protected void serverStarted() {
        super.serverStarted(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void serverStopped() {
        super.serverStopped(); //To change body of generated methods, choose Tools | Templates.
    }

    void handleMessageFromServerUI(Object msg) {
        if (msg != null && !msg.equals("\n")) {
            this.sendToAllClients("SERVER MESSAGE>" + msg.toString() + "\n");
        }
    }
}
