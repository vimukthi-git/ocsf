

package com.vimukthi.assignments.ocsf;

import java.net.Socket;

/**
 *
 * @author vimukthi
 */
public interface AbstractConnectionFactory {
    
    public ConnectionToClient createConnection(ThreadGroup group, AbstractServer server, Socket clientSocket);

}
