/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vimukthi.assignments.ocsf.simplechat;

/**
 *
 * @author vimukthi
 */
public interface Commands {
    
    public static final String QUIT = "#quit";
    public static final String STOP = "#stop";
    public static final String SET_PORT = "#setport";
    public static final String CLOSE = "#close";
    public static final String START = "#start";
    public static final String GET_PORT = "#getport";
    
    // authentication
    public static final String CLIENT_LOGIN = "#login";
    public static final String LIST_USERS = "#listusers";
    public static final String TO_USER = "#touser";
    
    // channels
    public static final String CHANNEL = "#channel";
    public static final String LIST_CHANNELS = "#listchannels";
    public static final String JOIN_CHANNEL = "#join";
    public static final String EXIT_CHANNEL = "#exitchannel";
    
    // buddylist
    public static final String CREATE_BUDDYLIST = "#create_buddylist";
    public static final String ADD_BUDDY = "#add_buddy";
    public static final String REMOVE_BUDDY = "#remove_buddy";
    public static final String SHOW_BUDDYLIST = "#show_buddylist";
    
    // file sending
    public static final String SEND_FILE = "#send_file";
    public static final String RECEIVE_FILE = "#receive_file";
    
    // voting
    public static final String VOTE = "#vote";
    public static final String VOTING = "#voting";
    
    
}
