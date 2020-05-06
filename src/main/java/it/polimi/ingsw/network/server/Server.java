package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.message.response.fromServerToClient.LoginResponse;
import it.polimi.ingsw.network.server.exceptions.InvalidUsernameException;
import it.polimi.ingsw.network.server.exceptions.RoomFullException;
import jdk.jfr.Timestamp;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * Manages the client connections
 */
public class Server extends Thread {
    private final static int MAX_STORED_LOGS = 100;
    private final static Logger logger = Logger.getLogger(Logger.class.getName());
    private final Map<String, Lobby> gameLobbies;
    private final List<User> waitingRoom;
    private final static int MAX_WAITING_CLIENTS = 15;    //FIXME: load from file
    private final HashMap<User, Lobby> users;
    private final int socketGreeterPort;
    private ServerSocket serverSocket;
    private Socket newClientSocket;
    private File logFile;

    /**
     * Default constructor
     * <p>
     *     Creates a new server instance
     * </p>
     */
    public Server() throws IOException {   //FIXME: implement singleton pattern for the Server
        socketGreeterPort = 4321; // TODO: get from file
        //FIXME: define additional type for this lobby
        this.gameLobbies = new LinkedHashMap<>();
        this.users = new LinkedHashMap<>();
        this.waitingRoom = new LinkedList<>();
        File logDir = new File("./logs");
        logDir.mkdir();
        this.logFile = new File(logDir + File.separator + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ".txt");
        logFile.createNewFile();
        File directory = new File(logFile.getAbsoluteFile().getParent());
        String[] filesInDirectory = directory.list();
        if(filesInDirectory!=null) {
            for (String filename : filesInDirectory) {
                if (filename.endsWith(".lck")) {
                    String absoluteFilePath = directory.toString() + File.separator + filename;
                    File fileToDelete = new File(absoluteFilePath);
                    fileToDelete.delete();
                }
            }
        }
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler(logFile.getPath());
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and runs the server
     * @param args currently none
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer();
        //TODO: implement server console to monitor things
    }


    /**
     * Opens the Socket connection
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(socketGreeterPort);
            logger.log(Level.INFO, "Socket server is up and listening on port " + socketGreeterPort);
        }
        catch (IOException ioException) {    // mainly because the port is already in use, should never happen
            logger.log(Level.SEVERE, ioException.getMessage());
            System.exit(1);
        }
        this.start();
    }

    /**
     * Client greeter
     * <p>
     *     Every time the server receives a Socket connection, this method creates and runs a new {
     *     @linkplain VirtualClient} object, which handles all the messages.
     *     <br>
     *         In the case of a Socket-thrown exception, the connection with the client is closed.
     * </p>
     */
    @Override
    public void run() {
        while (true) {
            try {
                newClientSocket = serverSocket.accept();
                logger.log(Level.INFO, "New client accepted: " + newClientSocket.getRemoteSocketAddress());
                Thread thread = new VirtualClient(this, newClientSocket);
                thread.start();
            }
            catch (IOException ioException) {
                logger.log(Level.SEVERE, ioException.getMessage());
                try {
                    newClientSocket.close();
                    logger.log(Level.WARNING, "Closing " + newClientSocket.getRemoteSocketAddress() + " connection");
                }
                catch (IOException ioException1) {
                    logger.log(Level.SEVERE, ioException1.getMessage());
                }
            }
        }
    }


    /**
     * Adds a new User to the server
     * <p>
     *     Upon receiving a login request (in the {@linkplain VirtualClient}), this method checks if the username is
     *     valid (usernames have to be unique and non-reserved keywords, such as ReservedUsernames.BROADCAST) and if there is
     *     "enough space" on the server
     * </p>
     * @param virtualClient the user to add
     */
    public synchronized void addClient(VirtualClient virtualClient) throws RoomFullException {        // Login of the player
        String username = virtualClient.getUser().getUsername();
        if (waitingRoom.size()  < MAX_WAITING_CLIENTS) {
            virtualClient.getUser().setUsername(username);
            waitingRoom.add(virtualClient.getUser());
            users.put(virtualClient.getUser(), null);
            virtualClient.getUser().notify(new LoginResponse(Type.OK, username, new LinkedList<>(gameLobbies.keySet())));
            logger.log(Level.INFO, "Login completed, added " + virtualClient.getUser().getUsername());
        }
        else
            throw new RoomFullException();
    }


    /**
     * Provides a list of the users in a lobby
     * @param lobby the lobby to check
     * @return the list of users in the lobby
     */
    public List<User> getUsersInRoom(Lobby lobby) {
        return users.keySet().stream().filter(u -> users.get(u) != null).filter(u -> users.get(u).equals(lobby)).collect(Collectors.toList());
    }

    /**
     * Provides a list of the users in a lobby
     * @return the list of users in the waiting room
     */
    public List<User> getUsersInWaitingRoom() {
        return waitingRoom;
    }

    /**
     * Removes a lobby
     * <p>
     *     If there are players in the lobby, those are moved to the waiting room
     * </p>
     * @param lobby the lobby to delete
     */
    public void removeRoom(Lobby lobby) {
        getUsersInRoom(lobby).forEach(lobby::removeUser);
        //waitingRoom.addAll(getUsersInRoom(lobby));    removing all the users before the method call
        gameLobbies.remove(lobby.getRoomName());
    }

    /**
     * Disconnects an user from the server
     * @param user the user to kick
     */
    public void onDisconnect(User user) {
        Lobby lobby = users.get(user);
        if (lobby != null) {
            List<User> usersInLobby = getUsersInRoom(lobby);

            if (!lobby.gameStarted() || lobby.hasLost(user.getUsername())) {
                lobby.removeUser(user);
            }
            else {
                usersInLobby.forEach(User::closeConnection);
                removeRoom(lobby);
                return;
            }
        }
        else
            waitingRoom.remove(user);

        users.remove(user);
        logger.log(Level.INFO, user.getUsername() + " has been kicked from the lobby");
        //user.closeConnection(); not needed, user already disconnected himself

    }

    public HashMap<User, Lobby> getUsers() {
        return users;
    }

    /**
     * Moves an user to another lobby
     *
     * @param user the user to move
     * @param lobby the lobby to move the user to
     */
    public void moveToRoom(User user, Lobby lobby) {
        Lobby oldRoom = users.replace(user, lobby);
        if (oldRoom == null) {
            logger.log(Level.INFO, user.getUsername() + " moved from waiting room to " + lobby.getRoomName());
            waitingRoom.remove(user);
        }
        else {
            logger.log(Level.INFO, user.getUsername() + " moved from " + oldRoom.getRoomName() + " to " + lobby.getRoomName());
            if(getUsersInRoom(oldRoom).size() == 0)
                removeRoom(oldRoom);
        }
    }

    public void moveToWaitingRoom(User user) {
        Lobby oldRoom = users.replace(user, null);
        if (oldRoom != null) {
            logger.log(Level.INFO, user.getUsername() + " moved from " + oldRoom.getRoomName() + " waiting room");
            Lobby oldLobby = users.replace(user, null);
            if (getUsersInRoom(oldLobby).size() == 0)
                removeRoom(oldLobby);
        }
    }


    public User getUser(String username) {
        return users.keySet().stream().filter(u -> u.getUsername().equals(username)).collect(Collectors.toList()).get(0);
    }

    public Map<String, Lobby> getGameLobbies() {
        return gameLobbies;
    }


}
