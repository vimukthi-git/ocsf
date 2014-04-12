package com.vimukthi.assignments.ocsf.simplechatclient;

import java.io.IOException;

/**
 *
 * @author vimukthi
 */
public class ClientConsole implements ChatIF {

    private ChatClient client;
    private static final String PROMPT = "simplechat>";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8000;
    private boolean run = true;

    public static void main(String[] args) throws InterruptedException, IOException {
        ClientConsole console = new ClientConsole();
        console.run(args);
    }

    public void run(String[] args) throws InterruptedException, IOException {
        try {
            String loginid = args[0];
            String passwd = args[1];
            try {
                client = new ChatClient(loginid, passwd, args[2], Integer.valueOf(args[3]), this);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Connecting to default host and port");
                client = new ChatClient(loginid, passwd, DEFAULT_HOST, DEFAULT_PORT, this);
            }
            client.openConnection();
            Thread.sleep(100);
            display("");

            while (run) {
                String input = System.console().readLine();
                if (isCommand(input)) {
                    processCommand(input);
                } else {
                    client.handleMessageFromClientUI(input + "\n");
                }
                display("");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please provide login id");
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
            client.quit();
            this.run = false;
        } else if (command.equals(Commands.LOG_OFF)) {
            if (client.isConnected()) {
                System.out.println("Simple chat logging off");
                client.closeConnection();
            } else {
                display("Not connected");
            }

        } else if (command.equals(Commands.SET_HOST)) {
            if (!client.isConnected()) {
                client.setHost(command_arr[1]);
            } else {
                display("Please disconnect before setting host");
            }
        } else if (command.equals(Commands.SET_PORT)) {
            if (!client.isConnected()) {
                try {
                    client.setPort(Integer.valueOf(command_arr[1]));
                } catch (Exception e) {
                    display("error check input again");
                }
            } else {
                display("Please disconnect before setting port");
            }
        } else if (command.equals(Commands.LOGIN)) {
            if (client.isConnected()) {
                display("Already connected");
            } else {
                client.openConnection();
            }
        } else if (command.equals(Commands.GET_HOST)) {
            System.out.println(client.getHost());
        } else if (command.equals(Commands.GET_PORT)) {
            System.out.println(client.getPort());
        } else {
            client.handleMessageFromClientUI(command_str + "\n");
        }
    }
}
