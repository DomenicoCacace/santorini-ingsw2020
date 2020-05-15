package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.request.fromClientToServer.CreateLobbyRequest;
import it.polimi.ingsw.network.message.request.fromClientToServer.JoinLobbyRequest;
import it.polimi.ingsw.network.message.request.fromClientToServer.LoginRequest;
import it.polimi.ingsw.network.message.response.fromServerToClient.LobbyCreatedEvent;
import it.polimi.ingsw.network.message.response.fromServerToClient.UserJoinedLobbyEvent;
import it.polimi.ingsw.network.server.exceptions.InvalidUsernameException;
import it.polimi.ingsw.network.server.exceptions.RoomFullException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server-side client communication and message handler
 * <p>
 *     For each new user connecting to the server, a VirtualClient object is created; its purpose is to receive, send and route the messages
 *     from/to the socket connected to the client.
 *     <br>
 *     Note that <i>connecting</i> means establishing a connection with the server, even without choosing an username.
 * </p>
 */
public class VirtualClient extends Thread implements ServerMessageManagerVisitor {
    private   final static String PING = "ping";
    private final static Logger logger = Logger.getLogger(Logger.class.getName());
    private final Server server;
    private final Socket clientConnection;
    private final JacksonMessageBuilder jsonParser;
    private final User user;
    private BufferedReader inputSocket;
    private OutputStreamWriter outputSocket;
    private ScheduledThreadPoolExecutor ex;
    private final BlockingQueue<MessageFromClientToServer> queue = new ArrayBlockingQueue<>(5);

    /**
     * Default constructor
     * <p>
     *     Given a socket, the constructor tries to open i/o streams; if it fails, the connection is closed.
     * </p>
     * @param server the server object
     * @param clientConnection the socket connection to/from the client
     */
    public VirtualClient(Server server, Socket clientConnection) {
        this.server = server;
        this.clientConnection = clientConnection;
        try {
            inputSocket = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
            outputSocket = new OutputStreamWriter(clientConnection.getOutputStream());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            closeConnection();
        }
        jsonParser = new JacksonMessageBuilder();
        user = new User(this, server);
    }

    /**
     * Receives messages and passes them to the parser
     */
    @Override
    public void run() {
        notify(PING);
        String input = null;

        new Thread(() -> {
            try {
                while(true)
                    queue.take().callVisitor(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, "Cannot retrieve message from queue " + e.getMessage(), e);
            }
        }).start();

        try {
            while (true) {
                Message message;
                input = inputSocket.readLine();
                if (input.equals(PING))
                    new Thread(this::ping).start();
                else {
                    logger.log(Level.FINE, "Message received");
                    message = jsonParser.fromStringToMessage(input);
                    queue.put((MessageFromClientToServer) message);
                }
            }
        } catch (IOException | NullPointerException | InterruptedException e) {
            logger.log(Level.SEVERE, ("Message format non valid, kicking " + user.getUsername() + ": " + e.getMessage()) + "\n" + input, e);

            server.onDisconnect(this.user);
        }
    }


    public User getUser(){
        return user;
    }



    public void notify(Message message) {
        String stringMessage = jsonParser.fromMessageToString(message);
        try {
            outputSocket.write(stringMessage + "\n");
            outputSocket.flush();
            logger.log(Level.INFO, ("Message" + message.getClass() + " sent from room to: " + message.getUsername()).replace(ReservedUsernames.BROADCAST.toString(), "all players"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            try {
                logger.log(Level.SEVERE, "Message format non valid, disconnecting " + user.getUsername());
                clientConnection.close();
            } catch (IOException e2) {
                logger.log(Level.SEVERE, " ");
                logger.log(Level.SEVERE, e2.getMessage(), e2);
            }

            server.onDisconnect(user);
        }
    }

    public void notify(String string){
        try {
            outputSocket.write(string + "\n");
            outputSocket.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Connection closed", e);
            try {
                logger.log(Level.SEVERE, "Can't send ping to:  " + user.getUsername());
                clientConnection.close();
            } catch (IOException e2) {
                logger.log(Level.SEVERE, " ");
                logger.log(Level.SEVERE, e2.getMessage(), e2);
            }
            server.onDisconnect(user);
        }
    }

    public void closeConnection() {
        try {
            clientConnection.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void disconnectFromServer(){
        server.onDisconnect(user);
    }


    /**
     * Manages the {@linkplain LoginRequest}
     * <p>
     *     This method tries to add the user to the server; if the username is invalid
     *     or already taken, it sends a response asking for a new username; if the server waiting room is full,
     *     sends a message and disconnects the client;
     * </p>
     * @param message the message to manage
     */
    @Override
    public void login(LoginRequest message) {
        try {
            this.user.setUsername(message.getUsername());
            server.addClient(this);
        }
        catch (RoomFullException roomFullException) {
            logger.log(Level.WARNING, "Server full, won't accept new clients");
            server.onDisconnect(this.getUser());
        }
    }

    /**
     * Manages the {@link JoinLobbyRequest}
     * <p>
     *     
     * </p>
     * @param message the message to manage
     */
    @Override
    public synchronized void joinLobby(JoinLobbyRequest message) {
        Lobby lobby = server.getGameLobbies().get(message.getLobbyName());
        if (lobby == null) {    // lobby not exists
            Map<String, List<String>> availableLobbies = new LinkedHashMap<>();
            server.getGameLobbies().values().forEach(l -> availableLobbies.put(l.getRoomName(), l.lobbyInfo()));
            notify(new UserJoinedLobbyEvent(message.getUsername(), Type.NO_LOBBY_AVAILABLE, availableLobbies, 0, null));
            logger.log(Level.WARNING, message.getUsername() + " failed to join " + message.getLobbyName() +": lobby isn't available");
        }
        else {
            try {
                logger.log(Level.INFO, message.getUsername() + " joined " + message.getLobbyName());
                lobby.addUser(this.user);
                //server.moveToRoom(this.getUser(), lobby);
            }
            catch (RoomFullException e) {
                logger.log(Level.INFO, message.getUsername() + " failed to join " + message.getLobbyName() +": lobby full");
                Map<String, List<String>> availableLobbies = new LinkedHashMap<>();
                server.getGameLobbies().values().forEach(l -> availableLobbies.put(l.getRoomName(), l.lobbyInfo()));
                notify(new UserJoinedLobbyEvent(message.getUsername(), Type.LOBBY_FULL, availableLobbies, 0, null));
            }
            catch (InvalidUsernameException e2) {
                logger.log(Level.INFO, message.getUsername() + " is already taken in lobby " + message.getLobbyName());
                Map<String, List<String>> availableLobbies = new LinkedHashMap<>();
                server.getGameLobbies().values().forEach(l -> availableLobbies.put(l.getRoomName(), l.lobbyInfo()));
                notify(new UserJoinedLobbyEvent(message.getUsername(), Type.INVALID_NAME, availableLobbies, 0, null));
            }
        }
    }



    @Override
    public synchronized void createLobby(CreateLobbyRequest message) {
        if (server.getGameLobbies().containsKey(message.getLobbyName())) {
            notify(new LobbyCreatedEvent(message.getUsername(), Type.INVALID_NAME, getAvailableLobbies()));
            logger.log(Level.INFO, message.getUsername() + " failed to create " + message.getLobbyName() + ": name already taken");
        }
        else {
            Lobby newLobby = new Lobby(server, message.getLobbyName(),user ,message.getLobbySize());
            server.getGameLobbies().put(newLobby.getRoomName(), newLobby);
            server.sendMessageToWaitingRoom(new LobbyCreatedEvent(ReservedUsernames.BROADCAST.toString(), Type.OK, getAvailableLobbies()));
            notify(new LobbyCreatedEvent(message.getUsername(), Type.OK, null));
            logger.log(Level.INFO, message.getUsername() + " successfully created \"" + message.getLobbyName() + "\"");

        }
    }

    private Map<String, List<String>> getAvailableLobbies() {
        Map<String, List<String>> availableLobbies = new LinkedHashMap<>();
        server.getGameLobbies().values().forEach(l -> availableLobbies.put(l.getRoomName(), l.lobbyInfo()));
        return availableLobbies;
    }


    @Override
    public  synchronized void cannotHandleMessage(Message message) {
        Lobby lobby = user.getRoom();

        if (lobby != null) {
            ((MessageFromClientToServer) message).callVisitor(lobby.getRoomParser());
            logger.log(Level.INFO, "Forwarding message: " + message.getClass().toGenericString() + " to lobby: " + lobby.getRoomName());
        }
        else {
            logger.log(Level.WARNING, "No lobby associated with user, cannot handle message");
        }
    }



    public void ping() {
        if(ex!=null)
            ex.shutdownNow();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        notify(PING);
        ex = new ScheduledThreadPoolExecutor(5);
        ex.schedule(() -> {
            System.out.println("User " + user.getUsername() + " disconnected!");
            disconnectFromServer();
        }, 5, TimeUnit.SECONDS);
    }
}
