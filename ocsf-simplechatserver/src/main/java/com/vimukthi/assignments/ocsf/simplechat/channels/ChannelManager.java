package com.vimukthi.assignments.ocsf.simplechat.channels;

import com.vimukthi.assignments.ocsf.ConnectionToClient;
import com.vimukthi.assignments.ocsf.simplechat.Commands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vimukthi
 */
public class ChannelManager implements ChannelHandler {

    private Map<String, List<ConnectionToClient>> channels = new HashMap<String, List<ConnectionToClient>>();

    @Override
    public boolean isChannelCommand(String input) {
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

    @Override
    public boolean isChannelListCommand(String input) {
        if (input != null && input.equals(Commands.LIST_CHANNELS)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isChannelJoinCommand(String input) {
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

    @Override
    public boolean isChannelExitCommand(String input) {
        if (input != null && input.equals(Commands.EXIT_CHANNEL)) {
            return true;
        }
        return false;
    }

    @Override
    public void handleChannelCreation(Object msg, ConnectionToClient client) {
        String channelName = msg.toString().split(" ")[1];
        if (channels.containsKey(channelName)) {
            client.sendToClient("Channel already exists\n");
        } else {
            channels.put(channelName, new ArrayList<ConnectionToClient>());
            client.sendToClient("Channel created\n");
        }
    }

    @Override
    public void handleChannelListing(ConnectionToClient client) {
        String channelStr = "";
        for (String channel : channels.keySet()) {
            channelStr += channel + "\n";
        }
        client.sendToClient(channelStr + "\n");
    }

    @Override
    public void handleChannelJoining(Object msg, ConnectionToClient client) {
        String channelName = msg.toString().split(" ")[1];
        if (channels.containsKey(channelName) && client.getInfo("channel") == null) {
            channels.get(channelName).add(client);
            client.setInfo("channel", channelName);
            client.sendToClient("You have been subscribed to " + channelName + "\n");
        } else {
            client.sendToClient("Ivalid channel name\n");
        }
    }

    @Override
    public void handleChannelExit(ConnectionToClient client) {
        if (client.getInfo("channel") != null) {
            channels.get(client.getInfo("channel").toString()).remove(client);
            client.setInfo("channel", null);
        }
        client.sendToClient("You have been unsubscribed from the channel\n");
    }

    @Override
    public void sendChannelMessage(ConnectionToClient client, String message) {
        for (ConnectionToClient connection : channels.get(client.getInfo("channel").toString())) {
            connection.sendToClient(message + "\n");
        }
    }
    
    @Override
    public void addChannel(String channelName) {
        channels.put(channelName, new ArrayList<ConnectionToClient>());
    }
}
