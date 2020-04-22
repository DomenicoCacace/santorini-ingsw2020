package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.MessageParser;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseNumberOfPlayersRequest;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseNumberOfPlayerResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.LoginResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server extends Thread {
    private final int socketPort;
    private Socket newClientConnection;
    private int MAX_PLAYER_NUMBER;
    private Lobby lobby;
    private final Map<String, User> usernames;
    private final MessageParser messageParser;
    private ServerSocket server;
    private static final Logger logger = Logger.getLogger("server");

    public Server() {
        this.usernames = new LinkedHashMap<>();
        //TODO: manage socket connection
        this.socketPort = 4321;
        this.messageParser = new MessageParser(this);
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startServer();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public void startServer() {
        try {
            server = new ServerSocket(socketPort);
            logger.log(Level.INFO, "Socket server is on");
        } catch (IOException e) {
            System.err.println("Could not listen on port " + socketPort);
            logger.log(Level.WARNING, e.getMessage());
        }
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                newClientConnection = server.accept();
                System.out.println(newClientConnection.toString());
                System.out.println("connection Accepted");
                Thread t = new VirtualClient(this, newClientConnection);
                t.start();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
                try {
                    newClientConnection.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }


    public synchronized void addClient(VirtualClient virtualClient) throws IOException {        // Login of the player
        if (usernames.size() < 3 && lobby == null) {
            if (usernames.containsKey(virtualClient.getUsername()) || virtualClient.getUsername().equals("broadcast")) {
                virtualClient.notify(new LoginResponse("Invalid username!", null));
            } else {
                User user = new User(virtualClient);
                usernames.put(user.getUsername(), user);
                user.notify(new LoginResponse("OK", user.getUsername()));
                logger.log(Level.INFO, "Connection accepted");
                if (usernames.size() == 1) {
                    user.notify(new ChooseNumberOfPlayersRequest(user.getUsername()));
                } else if (usernames.size() == MAX_PLAYER_NUMBER) {
                    List<String> names = new ArrayList<>(usernames.keySet()); //TODO: test if order is maintained
                    lobby = new Lobby(messageParser, names);
                }
            }
        } else{
            virtualClient.notify(new LoginResponse("Match already started", null));
        }
    }

    public void send(String username, Message message) {
        if (usernames.containsKey(username)) {
            usernames.get(username).notify(message);
        } else if (username.equals("broadcast"))
            sendToEveryone(message);
    }

    public void sendToEveryone(Message message) {
        for (String username : usernames.keySet()) {
            send(username, message);
        }
    }

    public boolean containClient(String username) {
        return this.usernames.containsKey(username);
    }

    public void onDisconnect(String username) {
        //TODO: End match or Save match and restart once the client reconnected (persistence even when the client crashes)
        System.out.println(username + " removed");
        this.usernames.remove(username);
    }

    public synchronized void handleMessage(Message message) throws IOException, InterruptedException {
        if (message.content == Message.Content.CHOOSE_PLAYER_NUMBER) {
            if (((ChooseNumberOfPlayerResponse) message).numberOfPlayers == 2 || ((ChooseNumberOfPlayerResponse) message).numberOfPlayers == 3) {
                MAX_PLAYER_NUMBER = ((ChooseNumberOfPlayerResponse) message).numberOfPlayers;
                if(usernames.values().size() == MAX_PLAYER_NUMBER){
                    List<String> names = new ArrayList<>(usernames.keySet());
                    lobby = new Lobby(messageParser, names);
                } else if (usernames.values().size() > MAX_PLAYER_NUMBER){
                    List<String> names = new ArrayList<>(usernames.keySet());
                    for(int i = MAX_PLAYER_NUMBER; i<names.size(); i++ ){
                        onDisconnect(names.get(i));
                    }
                    names = new ArrayList<>(usernames.keySet());
                    lobby = new Lobby(messageParser, names);
                }
            } else {
                usernames.get(message.username).notify(new ChooseNumberOfPlayersRequest(message.username));
            }
        } else messageParser.parseMessageFromClientToServer(message);
    }
}