package it.polimi.ingsw.network.server;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.controller.MessageParser;
import it.polimi.ingsw.model.God;
import it.polimi.ingsw.network.message.request.ChooseInitialGodsRequest;
import it.polimi.ingsw.network.message.request.ChooseNumberOfPlayersRequest;
import it.polimi.ingsw.network.message.response.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.ChooseNumberOfPlayerResponse;
import it.polimi.ingsw.network.message.response.LoginResponse;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Server {

    private int MAX_PLAYER_NUMBER = 3;
    private Lobby lobby;
    private Map<String, User> usernames;
    private MessageParser messageParser;
    private boolean received = false;



    //private static Logger logger = Logger.getLogger("server");


    private Server() throws IOException {
        this.usernames = new LinkedHashMap<>();
       //TODO: manage socket connection
        this.messageParser = new MessageParser();
    }

/*
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startServer(Configuration.getInstance().getSocketPort());
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

 */

/*
    private void startServer(int socketPort) {
        socketServer.startServer(socketPort);
        socketServer.start();
    }

 */


    public synchronized void addClient(VirtualClient virtualClient) throws IOException, InterruptedException {        // Login of the player
        if (usernames.size() < MAX_PLAYER_NUMBER && lobby==null) {
            if (usernames.containsKey(virtualClient.getUsername())) {
                    virtualClient.notify(new LoginResponse("Name already taken", null));
            } else {
                User user = new User(virtualClient);
                usernames.put(user.getUsername(), user);
                user.notify(new LoginResponse("OK", virtualClient.getUsername()));
                if (usernames.size() == 1) {
                    virtualClient.notify(new ChooseNumberOfPlayersRequest(virtualClient.getUsername()));
                    while (!received) {
                        wait();
                    }
                } else if (usernames.size() == MAX_PLAYER_NUMBER) {
                    List<String> names = new ArrayList<>(usernames.keySet()); //TODO: test if order is maintained
                    lobby = new Lobby(messageParser, this, names);
                }
            }

        }
    }


/*
    public void send(String username, Message message) {
        if (usernames.contains(username)) {
            usernames.get(username).notify(message);
        }
    }

 */

    public VirtualClient getVirtualClient(String name) {
        return usernames.get(name).getVirtualClient();
    }

    public void handleMessage (MessageResponse message){
        if (message.content == MessageResponse.Content.CHOOSE_PLAYER_NUMBER) {
            if (((ChooseNumberOfPlayerResponse) message).numberOfPlayers == 2 || ((ChooseNumberOfPlayerResponse) message).numberOfPlayers == 3) {
                MAX_PLAYER_NUMBER = ((ChooseNumberOfPlayerResponse) message).numberOfPlayers;
                received = true;
            }
        } else {
            messageParser.parseMessageFromServerToClient(message);
        }
    }
}