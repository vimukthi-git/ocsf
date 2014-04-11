package com.vimukthi.assignments.ocsf.simplechat;

import com.vimukthi.assignments.ocsf.ConnectionToClient;
import com.vimukthi.assignments.ocsf.ObservableServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vimukthi
 */
public class EchoServer extends ObservableServer {

    private final static String CLUSTER_CHANNEL = "cluster";
    private Map<String, String> loginsPasswds = new HashMap<String, String>();
    private ChatIF ui;
    private Pattern toUserPattern = Pattern.compile("(#\\w+)\\s(\\S+)\\s(.*)");
    private Map<String, List<ConnectionToClient>> channels = new HashMap<String, List<ConnectionToClient>>();
    private Boolean master = false;

    public EchoServer(int port, ChatIF ui, Boolean master) {
        super(port);
        this.ui = ui;
        this.master = master;
        // start cluster channel
        if (master) {
            channels.put(CLUSTER_CHANNEL, new ArrayList<ConnectionToClient>());
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
        } else if (isChannelCommand(msg.toString())) {
            String channelName = msg.toString().split(" ")[1];
            if (channels.containsKey(channelName)) {
                client.sendToClient("Channel already exists\n");
            } else {
                channels.put(channelName, new ArrayList<ConnectionToClient>());
                client.sendToClient("Channel created\n");
            }
        } else if (isChannelListCommand(msg.toString())) {
            String channelStr = "";
            for (String channel : channels.keySet()) {
                channelStr += channel + "\n";
            }
            client.sendToClient(channelStr + "\n");
        } else if (isChannelJoinCommand(msg.toString())) {
            String channelName = msg.toString().split(" ")[1];
            if (channels.containsKey(channelName) && client.getInfo("channel") == null) {
                channels.get(channelName).add(client);
                client.setInfo("channel", channelName);
                client.sendToClient("You have been subscribed to " + channelName + "\n");
            } else {
                client.sendToClient("Ivalid channel name\n");
            }
        } else if (isChannelExitCommand(msg.toString())) {
            if (client.getInfo("channel") != null) {
                channels.get(client.getInfo("channel").toString()).remove(client);
                client.setInfo("channel", null);
            }
            client.sendToClient("You have been unsubscribed from the channel\n");
        } else {
            String message = client.getInfo("loginid") + ">" + msg.toString();
            ui.display(message);
            // check if this is a channel message
            if (client.getInfo("channel") != null) {
                for (ConnectionToClient connection : channels.get(client.getInfo("channel").toString())) {
                    connection.sendToClient(message + "\n");
                }
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

    private boolean isChannelCommand(String input) {
        try {
            if (input != null && !input.equals("\n") && !input.equals("")
                    && input.substring(0, Commands.CHANNEL.length()).equals(Commands.CHANNEL)) {
                return true;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    private boolean isChannelListCommand(String input) {
        if (input != null && input.equals(Commands.LIST_CHANNELS)) {
            return true;
        }
        return false;
    }

    private boolean isChannelJoinCommand(String input) {
        try {
            if (input != null && !input.equals("\n") && !input.equals("")
                    && input.substring(0, Commands.JOIN_CHANNEL.length()).equals(Commands.JOIN_CHANNEL)) {
                return true;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    private boolean isChannelExitCommand(String input) {
        if (input != null && input.equals(Commands.EXIT_CHANNEL)) {
            return true;
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
    
}
