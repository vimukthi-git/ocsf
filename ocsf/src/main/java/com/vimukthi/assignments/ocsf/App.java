package com.vimukthi.assignments.ocsf;

/**
 *
 * @author vimukthi
 */
public class App {

    private static class ConcreteServer extends AbstractServer {

        public ConcreteServer(int port) {
            super(port);
        }

        @Override
        protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
            this.sendToAllClients(msg.toString().toUpperCase());
        }
    }

    private static class ConcreteClient extends AbstractClient {

        public ConcreteClient(String host, int port) {
            super(host, port);
        }

        @Override
        protected void handleMessageFromServer(Object msg) {
            System.out.println(msg);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ConcreteServer server = new ConcreteServer(8000);
        new Thread(server).start();
        Thread.sleep(100);
        ConcreteClient client = new ConcreteClient("localhost", 8000);        
        new Thread(client).start();
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100);
            client.sendToServer("test1");
        }

    }
}
