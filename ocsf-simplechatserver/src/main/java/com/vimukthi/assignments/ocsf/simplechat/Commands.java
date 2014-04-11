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
    
    /**
     * 'quit causes the server to q uit gracefully.
( ii) 	 ist op causes the se rver to stop listening fo r new cl ients.
(iii) 	tclose ca uses the server no t only to stop listeni ng for new clients, but
aJ so to d iscon nect aU existi ng d ieots.
(iv) 	 'setport <port> calls the setPort m ethod in th e server. Only allowed if
the server is d osed .
(v) 	 hurt causes the server to stare li stening for new dients. DnJy valid if
the server is stopped.
(vi ) 	 tgetport displays th e current port number.

     */
    
    public static final String QUIT = "#quit";
    public static final String STOP = "#stop";
    public static final String SET_PORT = "#setport";
    public static final String CLOSE = "#close";
    public static final String START = "#start";
    public static final String GET_PORT = "#getport";
    
    public static final String CLIENT_LOGIN = "#login";
    public static final String LIST_USERS = "#listusers";
    public static final String TO_USER = "#touser";
    public static final String CHANNEL = "#channel";
    public static final String LIST_CHANNELS = "#listchannels";
    public static final String JOIN_CHANNEL = "#join";
    public static final String EXIT_CHANNEL = "#exitchannel";
    
    
}
