package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.message.JacksonMessageBuilder;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.fromClientToServer.CreateLobbyRequest;
import it.polimi.ingsw.network.message.fromClientToServer.JoinLobbyRequest;
import it.polimi.ingsw.network.message.fromClientToServer.LoginRequest;
import it.polimi.ingsw.network.message.fromServerToClient.LobbyCreatedEvent;
import it.polimi.ingsw.network.message.fromServerToClient.UserJoinedLobbyEvent;
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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server-side client communication and message handler
 * <p>
 * For each new user connecting to the server, a VirtualClient object is created; its purpose is to receive, send and route the messages
 * from/to the socket connected to the client.
 * <br>
 * Note that <i>connecting</i> means establishing a connection with the server, even without choosing an username.
 */
public class VirtualClient extends Thread implements ServerMessageManagerVisitor {
    private static final String PING = "ping";
    private static final Logger logger = Logger.getLogger(VirtualClient.class.getName());
    private final Server server;
    private final Socket clientConnection;
    private final JacksonMessageBuilder jsonParser;
    private final User user;
    private BufferedReader inputSocket;
    private OutputStreamWriter outputSocket;
    private ScheduledThreadPoolExecutor ex;

    /**
     * Default constructor
     * <p>
     * Given a socket, the constructor tries to open i/o streams; if it fails, the connection is closed.
     *
     * @param server           the server object
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
        try {
            while (true) {
                Message message;
                input = inputSocket.readLine();
                if (input.equals(PING))
                    new Thread(this::ping).start();
                else {
                    logger.log(Level.FINE, "Message received");
                    message = jsonParser.fromStringToMessage(input);
                    ((MessageFromClientToServer) message).callVisitor(this);
                }
            }
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, ("Message format non valid, kicking " + user.getUsername() + ": " + e.getMessage()) + "\n" + input, e);
            server.onDisconnect(this.user);
        }
    }

    /**
     * <i>user</i> getter
     * @return the User object associated to this
     */
    public User getUser() {
        return user;
    }

    /**
     * Provides all the info about the server available lobbies.
     *
     * @return a Map containing lobby name and its information
     * @see Lobby#lobbyInfo()
     */
    private Map<String, List<String>> getAvailableLobbies() {
        Map<String, List<String>> availableLobbies = new LinkedHashMap<>();
        server.getGameLobbies().values().forEach(l -> availableLobbies.put(l.getRoomName(), l.lobbyInfo()));
        return availableLobbies;
    }

    /**
     * Sends a {@link Message} to this user's client
     *
     * @param message the Message to send
     */
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

    /**
     * Sends a string to this user's client
     *
     * @param string the string to send
     */
    public void notify(String string) {
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

    /**
     * Terminates the connection with the corresponding client
     */
    public void closeConnection() {
        try {
            clientConnection.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Disconnects this user from the server
     *
     * @see Server#onDisconnect(User)
     */
    public void disconnectFromServer() {
        server.onDisconnect(user);
    }

    /**
     * Manages the {@linkplain LoginRequest}
     * <p>
     * This method tries to add the user to the server; if the username is invalid
     * or already taken, it sends a response asking for a new username; if the server waiting room is full,
     * sends a message and disconnects the client;
     *
     * @param message the message to manage
     */
    @Override
    public void login(LoginRequest message) {
        try {
            this.user.setUsername(message.getUsername());
            server.addClient(this);
        } catch (RoomFullException roomFullException) {
            logger.log(Level.WARNING, "Server full, will not accept new clients");
            server.onDisconnect(this.getUser());
        }
    }

    /**
     * Manages the {@link JoinLobbyRequest}
     *
     * @param message the message to manage
     */
    @Override
    public synchronized void joinLobby(JoinLobbyRequest message) {
        Lobby lobby = server.getGameLobbies().get(message.getLobbyName());
        if (lobby == null) {    // lobby not exists
            Map<String, List<String>> availableLobbies = new LinkedHashMap<>();
            server.getGameLobbies().values().forEach(l -> availableLobbies.put(l.getRoomName(), l.lobbyInfo()));
            notify(new UserJoinedLobbyEvent(message.getUsername(), Type.NO_LOBBY_AVAILABLE, availableLobbies, 0, null));
            logger.log(Level.WARNING, message.getUsername() + " failed to join " + message.getLobbyName() + ": lobby isn't available");
        } else {
            try {
                logger.log(Level.INFO, message.getUsername() + " joined " + message.getLobbyName());
                lobby.addUser(this.user);
                server.getUsersInWaitingRoom().remove(user);
            } catch (RoomFullException e) {
                server.getUsersInWaitingRoom().remove(user);
                logger.log(Level.INFO, message.getUsername() + " failed to join " + message.getLobbyName() + ": lobby full");
                Map<String, List<String>> availableLobbies = new LinkedHashMap<>();
                server.getGameLobbies().values().forEach(l -> availableLobbies.put(l.getRoomName(), l.lobbyInfo()));
                notify(new UserJoinedLobbyEvent(message.getUsername(), Type.LOBBY_FULL, availableLobbies, 0, null));
            } catch (InvalidUsernameException e2) {
                server.getUsersInWaitingRoom().remove(user);
                logger.log(Level.INFO, message.getUsername() + " is already taken in lobby " + message.getLobbyName());
                Map<String, List<String>> availableLobbies = new LinkedHashMap<>();
                server.getGameLobbies().values().forEach(l -> availableLobbies.put(l.getRoomName(), l.lobbyInfo()));
                notify(new UserJoinedLobbyEvent(message.getUsername(), Type.INVALID_NAME, availableLobbies, 0, null));
            }
        }
    }

    /**
     * Manages the {@link CreateLobbyRequest}
     *
     * Tries to create a new lobby, based on the user input; if the parameters are correct (name not already taken, size
     * between 2 and 3), the lobby is created and a confirmation {@linkplain LobbyCreatedEvent} is sent to the lobby
     * creator and all the users in the waiting room; otherwise, an error message is sent to the creator only.
     *
     * @param message the message to manage
     */
    @Override
    public synchronized void createLobby(CreateLobbyRequest message) {
        if (server.getGameLobbies().containsKey(message.getLobbyName())) {
            notify(new LobbyCreatedEvent(message.getUsername(), Type.INVALID_NAME, getAvailableLobbies()));
            logger.log(Level.INFO, message.getUsername() + " failed to create " + message.getLobbyName() + ": name already taken");
        } else {
            Lobby newLobby = new Lobby(server, message.getLobbyName(), user, message.getLobbySize());
            server.getGameLobbies().put(newLobby.getRoomName(), newLobby);
            server.sendMessageToWaitingRoom(new LobbyCreatedEvent(ReservedUsernames.BROADCAST.toString(), Type.OK, getAvailableLobbies()));
            notify(new LobbyCreatedEvent(message.getUsername(), Type.OK, null));
            logger.log(Level.INFO, message.getUsername() + " successfully created \"" + message.getLobbyName() + "\"");

        }
    }

    /**
     * Manages all non-lobby related messages (e.g. game messages), redirecting them to the user's corresponding
     * lobby, which will take care of it; if the user is not associated to any lobby, the message is ignored and the
     * incident is reported in the server log
     *
     * @param message the message to handle
     */
    @Override
    public synchronized void cannotHandleMessage(Message message) {
        Lobby lobby = user.getRoom();

        if (lobby != null) {
            ((MessageFromClientToServer) message).callVisitor(lobby.getRoomParser());
            logger.log(Level.INFO, "Forwarding message: " + message.getClass().toGenericString() + " to lobby: " + lobby.getRoomName());
        } else {
            logger.log(Level.WARNING, "No lobby associated with user, cannot handle message");
        }
    }

    /**
     * Manages the ping mechanism <i>manually</i>, disconnecting the user if no pong message is received after a
     * certain amount of time after the ping is sent
     */
    public void ping() {
        if (ex != null)
            ex.shutdownNow();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, e.toString());
            Thread.currentThread().interrupt();
        }
        notify(PING);
        ex = new ScheduledThreadPoolExecutor(5);
        ex.schedule(() -> {
            System.out.println("User " + user.getUsername() + " disconnected!");
            disconnectFromServer();
        }, 5, TimeUnit.SECONDS);
    }
}
