package com.vimukthi.assignments.ocsf.simplechat;

import com.vimukthi.assignments.ocsf.simplechat.channels.ChannelManager;

/**
 *
 * @author vimukthi
 */
public class ServerConsole implements ChatIF {

    private SimpleChatServer server;
    private static final String PROMPT = "chatserver>";
    private static final int DEFAULT_PORT = 8000;
    private boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        ServerConsole console = new ServerConsole();
        console.run(args);
    }

    public void run(String[] args) throws InterruptedException {
        int port;
        Boolean master;
        try{
            port = Integer.valueOf(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            port = DEFAULT_PORT;
        }   
        try{
            master = Boolean.valueOf(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            master = false;
        }   
        server = new SimpleChatServer(port, this, master, new ChannelManager());
        server.listen();
        Thread.sleep(100);
        display("");

        while (run) {
            String input = System.console().readLine();
            if (isCommand(input)) {
                processCommand(input);
            } else {
                server.handleMessageFromServerUI(input + "\n");
            }
            display("");
        }
    }

    @Override
    public void display(String msg) {
        if (msg.equals("")) {
            System.out.print(PROMPT);
        } else {
            System.out.print("\n");
            System.out.println(msg);
            System.out.print(PROMPT);
        }
    }

    private boolean isCommand(String input) {
        if (input != null && !input.equals("\n") && !input.equals("") && input.substring(0, 1).equals("#")) {
            return true;
        }
        return false;
    }

    private void processCommand(String command_str) {
        String[] command_arr = command_str.split(" ");
        String command = command_arr[0];
        if (command.equals(Commands.QUIT)) {
            System.out.println("Simple chat shutting down");
            server.close();
            this.run = false;
        } else if (command.equals(Commands.START)) {
            if (!server.isListening()) {
                server.listen();
            } else {
                display("Already listening");
            }
        } else if (command.equals(Commands.CLOSE)) {
            if (!server.isClosed()) {
                server.close();
            } else {
                display("Already closed");
            }
        } else if (command.equals(Commands.SET_PORT)) {
            if (server.isClosed()) {
                try {
                    server.setPort(Integer.valueOf(command_arr[1]));
                } catch (Exception e) {
                    display("Close the server first");
                }                
            } else {
                display("Please disconnect before setting port");
            }
        } else if (command.equals(Commands.STOP)) {
            if (server.isListening()) {
                server.stopListening();
            } else {
                display("Not listening");
            }
        } else if (command.equals(Commands.GET_PORT)) {
            System.out.println(server.getPort());
        } else {
            System.out.println("Unknown command");
        }
    }
}
