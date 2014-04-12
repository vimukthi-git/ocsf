package com.vimukthi.assignments.ocsf.simplechat;

import com.vimukthi.assignments.ocsf.simplechat.channels.ChannelHandler;
import com.vimukthi.assignments.ocsf.ConnectionToClient;
import com.vimukthi.assignments.ocsf.ObservableServer;
import com.vimukthi.assignments.ocsf.simplechat.buddylist.BuddyList;
import com.vimukthi.assignments.ocsf.simplechat.buddylist.BuddyListManager;
import com.vimukthi.assignments.ocsf.simplechat.voting.VotingManager;
import com.vimukthi.assignments.ocsf.simplechat.voting.VotingObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vimukthi
 */
public class SimpleChatServer extends ObservableServer {

    private final static String CLUSTER_CHANNEL = "cluster";
    private Map<String, String> loginsPasswds = new HashMap<String, String>();
    private ChatIF ui;
    private Pattern toUserPattern = Pattern.compile("(#\\w+)\\s(\\S+)\\s(.*)");
    private ChannelHandler channelHandler;
    private VotingManager voteManager = new VotingManager();
    private Boolean master = false;
    Map<String, List<String>> cliendFiles;

    public SimpleChatServer(int port, ChatIF ui, Boolean master, ChannelHandler channelHandler) {
        super(port);
        this.ui = ui;
        this.master = master;
        this.channelHandler = channelHandler;
        // start cluster channel
        if (master) {
            channelHandler.addChannel(CLUSTER_CHANNEL);
        }
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        super.handleMessageFromClient(msg, client);
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
        String clientId = (String) client.getInfo("loginid");
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
        } else if (channelHandler.isChannelCommand(msg.toString())) {
            channelHandler.handleChannelCreation(msg, client);
        } else if (channelHandler.isChannelListCommand(msg.toString())) {
            channelHandler.handleChannelListing(client);
        } else if (channelHandler.isChannelJoinCommand(msg.toString())) {
            channelHandler.handleChannelJoining(msg, client);
        } else if (channelHandler.isChannelExitCommand(msg.toString())) {
            channelHandler.handleChannelExit(client);
        } else if (msg.toString().startsWith(Commands.CREATE_BUDDYLIST)) {
            ui.display("Craeting buddy list. [Client=" + client.getInfo("loginid") + "]");
            createBuddlyList(clientId);
        } else if (msg.toString().startsWith(Commands.ADD_BUDDY)) {
            String buddy_name = extractArgument(msg.toString(), 1);
            ui.display("Adding client to buddy list. [Client=" + clientId + ", Buddy=" + buddy_name + "]");
            addClientToBuddlyList(clientId, buddy_name);
        } else if (msg.toString().startsWith(Commands.REMOVE_BUDDY)) {
            String buddy_name = extractArgument(msg.toString(), 1);
            ui.display("Removing client from buddy list. [Client=" + clientId + ", Buddy=" + buddy_name + "]");
            removeClientFromBuddlyList(clientId, buddy_name);
        } else if (msg.toString().startsWith(Commands.SHOW_BUDDYLIST)) {
            showBuddlyList(client, clientId);
        } else if (msg.toString().startsWith(Commands.SEND_FILE)) {
            String receiver = extractArgument(msg.toString(), 1);
            String file = extractArgument(msg.toString(), 2);
            sendFile(client, receiver, file);
        } else if (msg.toString().startsWith(Commands.RECEIVE_FILE)) {
            String file = extractArgument(msg.toString(), 1);
            subscribeToReaceiveFile(clientId, file);
        } else if (msg.toString().startsWith(Commands.VOTING)) {
            int voteStringSize = "#voting".length();
            String votingTag = msg.toString().trim().substring(voteStringSize);

            int beginIndex = -1;
            int endIndex = -1;

            //<Question> <Answer1> <Answer2> <Answer3> <S|P>
            List<String> votingInfoList = new ArrayList<String>();
            while (votingTag != null && (votingTag.length() > 0)) {

                beginIndex = votingTag.indexOf("<");
                endIndex = votingTag.indexOf(">");


                String substr = votingTag.substring(beginIndex + 1, endIndex);
                System.out.println(substr);
                votingInfoList.add(substr);

                if (endIndex >= -1) {
                    votingTag = votingTag.substring(endIndex + 1);
                } else {
                    votingTag = null;
                }
            }
            //Now add this list in to voting object
            boolean eligible = voteManager.IsEligibleForVote(clientId);

            if (eligible) {
                VotingObject voteObject = new VotingObject();
                voteObject.AddQuestionsAndAnswers(votingInfoList);
                voteManager.addVotingObject(voteObject, clientId);
                channelHandler.sendChannelMessage(client, msg.toString());
            } else {
                client.sendToClient("Already a voting is in progress\n");

            }
        } else if (msg.toString().startsWith(Commands.VOTE)) {

            String[] vote = msg.toString().split(" ");

            if (vote.length == 3) {

                String questionID = vote[1];
                String answerID = vote[2];

                if (!questionID.toLowerCase().equalsIgnoreCase(clientId)) {


                    boolean eligible = voteManager.IsEligibleForVote(questionID);

                    if (!eligible) {
                        VotingObject oVoteObj = voteManager.getVotingObject(questionID);

                        try {
                            Integer answer = Integer.parseInt(answerID);
                            boolean bAdded = oVoteObj.AddVoting(answerID);

                            if (bAdded) {
                                voteManager.addVotingObject(oVoteObj, questionID);
                            } else {
                                client.sendToClient("Invalid Answer\n");
                            }
                        } catch (Exception ex) {
                            client.sendToClient("Proposer can't vote\n");

                        }
                    }
                } else {
                    client.sendToClient("Invalid Command\n");
                }

            }
        } else {
            String message = client.getInfo("loginid") + ">" + msg.toString();
            ui.display(message);
            // check if this is a channel message
            if (client.getInfo("channel") != null) {
                channelHandler.sendChannelMessage(client, message);
            } else {
                for (Thread connection : getClientConnections()) {
                    ConnectionToClient c = (ConnectionToClient) connection;
                    if (c.getInfo("channel") == null) {
                        c.sendToClient(message + "\n");
                    }
                }
            }
        }
    }

    private String extractArgument(String command, int index) {
        String arg_starting_index[] = command.split(" ");

        if (arg_starting_index.length > index) {
            return arg_starting_index[index];
        } else {
            ui.display("Command " + command + " not properly formated");
            return null;
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

    private boolean isCreateBuddyListCommand(String input) {
        if (input != null && input.equals(Commands.CREATE_BUDDYLIST)) {
            return true;
        }
        return false;
    }

    private boolean isShowBuddyListCommand(String input) {
        if (input != null && input.equals(Commands.SHOW_BUDDYLIST)) {
            return true;
        }
        return false;
    }

    private void createBuddlyList(String clientId) {
        (BuddyListManager.getInstance().create(clientId)).addBuddy(clientId);
    }

    private void addClientToBuddlyList(String clientId, String buddy_id) {
        BuddyListManager.getInstance().addClientToBuddyList(clientId, buddy_id);
    }

    private void removeClientFromBuddlyList(String clientId, String buddy_id) {
        BuddyListManager.getInstance().removeClientFromBuddyList(clientId, buddy_id);
    }

    private void showBuddlyList(ConnectionToClient client, String clientId) {
        BuddyList list = BuddyListManager.getInstance().getBuddyListOfClient(clientId);

        if (list != null) {
            String client_list = "Client List:\n";
            int index = 1;

            for (String buddy_name : list.getBuddies()) {
                client_list += index++ + " " + buddy_name + "\n";
            }
            client.sendToClient(client_list);

        }
    }

    private void subscribeToReaceiveFile(String clientId, String file) {
        List<String> list = cliendFiles.get(clientId);
        if (list != null) {
            if (!list.contains(file)) {
                ui.display("Adding ready to receive file. [Client=" + clientId + ", FIle=" + file + "]");
                list.add(file);
            }
        } else {
            ui.display("Adding ready to receive file. [Client=" + clientId + ", FIle=" + file + "]");
            list = new LinkedList<String>();
            list.add(file);
            cliendFiles.put(clientId, list);
        }
    }

    private void sendFile(ConnectionToClient sender, String receiver, String file) {
        File f = new File(file);
        if (f.canRead()) {
            String file_name = file.substring(file.lastIndexOf('\\') + 1);

            List<String> list = cliendFiles.get(receiver);
            if (!list.isEmpty()) {
                if (list.contains(file_name)) {
                    Thread[] connection_threads = getClientConnections();
                    for (Thread connection_thread : connection_threads) {
                        ConnectionToClient connection_to_client = (ConnectionToClient) connection_thread;
                        if (connection_to_client.getInfo("id").equals(receiver)) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                String text;
                                while ((text = reader.readLine()) != null) {
                                    connection_to_client.sendToClient(text + "\n");
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(SimpleChatServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } else {
                    sender.sendToClient("Given receiver is not registered to receive given file. [Receiver=" + receiver + ", File=" + file_name + "]\n");
                }
            } else {
                sender.sendToClient("Given receiver is not registered for file receiving. [Receiver=" + receiver + "]\n");
            }
        } else {
            ui.display("File cannot read. [File=" + file + "]");
            sender.sendToClient("Given file cannot be read. [File=" + file + "]\n");

        }
    }
}
