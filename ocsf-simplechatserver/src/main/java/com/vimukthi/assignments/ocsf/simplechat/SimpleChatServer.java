package com.vimukthi.assignments.ocsf.simplechat;

import com.vimukthi.assignments.ocsf.simplechat.channels.ChannelHandler;
import com.vimukthi.assignments.ocsf.ConnectionToClient;
import com.vimukthi.assignments.ocsf.ObservableServer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vimukthi
 */
public class SimpleChatServer extends ObservableServer {

    private final static String CLUSTER_CHANNEL = "cluster";
    private Map<String, String> loginsPasswds = new HashMap<String, String>();
    private ChatIF ui;
    private Pattern toUserPattern = Pattern.compile("(#\\w+)\\s(\\S+)\\s(.*)");
    private ChannelHandler channelHandler;
    private Boolean master = false;

    public SimpleChatServer(int port, ChatIF ui, Boolean master, ChannelHandler channelHandler) {
        super(port);
        this.ui = ui;
        this.master = master;
        this.channelHandler = channelHandler;
        // start cluster channel
        if (master) {
            channelHandler.addChannel(CLUSTER_CHANNEL);
        }
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (isLoginCommand(msg.toString())) {
            if (client.getInfo("loginid") == null) {
                String[] credentials = extractLoginPasswd(msg.toString());
                if (isAuthentic(credentials)) {
                    client.setInfo("loginid", credentials[0]);
                } else {
                    client.sendToClient("Login invalid");
                    client.close();
                    this.removeClient(client);
                }
            } else {
                client.sendToClient("Login already received\n");
            }
        } else if (client.getInfo("loginid") != null) {
            handleMessageFromAuthorizedClient(msg, client);
        } else {
            client.sendToClient("I didn't receive your login\n");
            client.close();
            this.removeClient(client);
        }
    }
    
    /**
     * 
     * @param msg
     * @param client 
     */
    private void handleMessageFromAuthorizedClient(Object msg, ConnectionToClient client) {
        if (isListUsersCommand(msg.toString())) {
            Thread[] clients = getClientConnections();
            String clientList = "";
            for (int i = 0; i < clients.length; i++) {
                clientList += ((ConnectionToClient) clients[i]).getInfo("loginid") + "\n";
            }
            client.sendToClient(clientList + "\n");
        } else if (isToUserCommand(msg.toString())) {
            String[] msgParts = getToUserMsgParts(msg.toString());
            Thread[] clients = getClientConnections();
            for (int i = 0; i < clients.length; i++) {
                ConnectionToClient connection = (ConnectionToClient) clients[i];
                if (connection.getInfo("loginid").equals(msgParts[1])) {
                    String server_message = client.getInfo("loginid") + " to " + connection.getInfo("loginid") + ">" + msgParts[2];
                    String message = client.getInfo("loginid") + ">" + msgParts[2] + "\n";
                    ui.display(server_message);
                    connection.sendToClient(message);
                    break;
                }
            }
        } else if (channelHandler.isChannelCommand(msg.toString())) {
            channelHandler.handleChannelCreation(msg, client);
        } else if (channelHandler.isChannelListCommand(msg.toString())) {
            channelHandler.handleChannelListing(client);
        } else if (channelHandler.isChannelJoinCommand(msg.toString())) {
            channelHandler.handleChannelJoining(msg, client);
        } else if (channelHandler.isChannelExitCommand(msg.toString())) {
            channelHandler.handleChannelExit(client);
        } else {
            String message = client.getInfo("loginid") + ">" + msg.toString();
            ui.display(message);
            // check if this is a channel message
            if (client.getInfo("channel") != null) {
                channelHandler.sendChannelMessage(client, message);
            } else {
                for (Thread connection : getClientConnections()) {
                    ConnectionToClient c = (ConnectionToClient)connection;
                    if(c.getInfo("channel") == null){
                        c.sendToClient(message + "\n");
                    }
                }
            }
        }
    }

    private boolean isAuthentic(String[] credentials) {
        if (loginsPasswds.containsKey(credentials[0])) {
            if (loginsPasswds.get(credentials[0]).equals(credentials[1])) {
                return true;
            } else {
                return false;
            }
        } else {
            loginsPasswds.put(credentials[0], credentials[1]);
            return true;
        }
    }

    private String[] extractLoginPasswd(String command_str) {
        String[] command_arr = command_str.split(" ");
        String[] credentials = new String[2];
        credentials[0] = command_arr[1];
        credentials[1] = command_arr[2];
        return credentials;
    }

    private boolean isLoginCommand(String input) {
        try {
            if (input != null && !input.equals("\n") && !input.equals("")
                    && input.substring(0, Commands.CLIENT_LOGIN.length()).equals(Commands.CLIENT_LOGIN)) {
                return true;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    private boolean isListUsersCommand(String input) {
        if (input != null && input.equals(Commands.LIST_USERS)) {
            return true;
        }
        return false;
    }

    private boolean isToUserCommand(String input) {
        try {
            if (input != null && !input.equals("\n") && !input.equals("")
                    && input.substring(0, Commands.TO_USER.length()).equals(Commands.TO_USER)) {
                return true;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    

    /**
     * Extract message parts from a #touser command
     *
     * @param message
     * @return
     */
    private String[] getToUserMsgParts(String message) {
        Matcher matcher = toUserPattern.matcher(message);
        String[] parts = new String[3];
        if (matcher.find()) {
            parts[0] = matcher.group(1);
            parts[1] = matcher.group(2);
            parts[2] = matcher.group(3);
        }
        return parts;
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

    public ChannelHandler getChannelHandler() {
        return channelHandler;
    }

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }   
    
}
