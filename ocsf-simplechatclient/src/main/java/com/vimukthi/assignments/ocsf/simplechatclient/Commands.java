/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vimukthi.assignments.ocsf.simplechatclient;

/**
 *
 * @author vimukthi
 */
public interface Commands {
    
    /*
     * ' quit causes the client to terminate gracefu lly. Make sure the
con nect ion to the server is term inated before exiting the program.
Oi) 	 tlogoff causes the client to disconnect from the server, but not quit.
(iii) 	 ~sethost <host> calls the setHost method jn the client. Only allowed if
the client is logged off; displays an error message otherwise.
(iv) 	 Isetport <port> caUs the setPort method in the client, with the same
constraints as ' sethost.
(v) Hogin causes the client to connect to th e se rver. On ly all owed if the
cli ent is not already connected; displays an error m essage otherwise.
(vi) tgethost displays the current host name.
(vii) 	 ' getport displays the current port number.

     */
    
    public static final String QUIT = "#quit";
    public static final String LOG_OFF = "#logoff";
    public static final String SET_HOST = "#sethost";
    public static final String SET_PORT = "#setport";
    public static final String LOGIN = "#login";
    public static final String GET_HOST = "#gethost";
    public static final String GET_PORT = "#getport";
    
}
