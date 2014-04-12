/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vimukthi.assignments.ocsf.simplechat.channels;

import com.vimukthi.assignments.ocsf.ConnectionToClient;

/**
 *
 * @author vimukthi
 */
public interface ChannelHandler {

    public void handleChannelCreation(Object msg, ConnectionToClient client);

    public void handleChannelExit(ConnectionToClient client);

    public void handleChannelJoining(Object msg, ConnectionToClient client);

    public void handleChannelListing(ConnectionToClient client);

    public boolean isChannelCommand(String input);

    public boolean isChannelExitCommand(String input);

    public boolean isChannelJoinCommand(String input);

    public boolean isChannelListCommand(String input);

    public void sendChannelMessage(ConnectionToClient client, String message);

    public void addChannel(String CLUSTER_CHANNEL);
    
}
