package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.fromServerToClient.LoginResponse;
import it.polimi.ingsw.network.message.fromServerToClient.MovedToWaitingRoomResponse;
import it.polimi.ingsw.network.server.exceptions.RoomFullException;

import java.io.*;
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
    private final static int MAX_WAITING_CLIENTS = 15;    //FIXME: load from file
    private final Map<String, Lobby> gameLobbies;
    private final List<User> waitingRoom;
    private final HashMap<User, Lobby> users;
    private final int socketGreeterPort;
    private ServerSocket serverSocket;
    private Socket newClientSocket;

    /**
     * Default constructor
     * <p>
     * Creates a new server instance
     *
     * @throws IOException if an I/O error occurs while creating the log file
     */
    public Server() throws IOException {
        int port;
        this.gameLobbies = new LinkedHashMap<>();
        this.users = new LinkedHashMap<>();
        this.waitingRoom = new LinkedList<>();
        try {
            InputStream socketPort = this.getClass().getResourceAsStream("socketPort.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(socketPort);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            port = Integer.parseInt(bufferedReader.readLine().trim());
        }catch (Exception e){
            port = 4321;
        }
        socketGreeterPort = port;
        File logDir = new File("./logs");
        logDir.mkdir();
        File logFile = new File(logDir + File.separator + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + ".txt");
        logFile.createNewFile();
        File directory = new File(logFile.getAbsoluteFile().getParent());
        deleteOldFiles(directory);
        File[] files = directory.listFiles();
        if (files!=null) {
            for (File file : files){
                if (file.getName().endsWith(".lck")) {
                    file.delete();
                }
            }
        }
        deleteOldFiles(new File("./savedGames"));
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler(logFile.getPath());
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteOldFiles(File directory){
        final long time = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        File[] filesJson = directory.listFiles();
        if (filesJson!=null) {
            for (File file : filesJson){
                if (file.lastModified() < time) {
                    file.delete();
                }
            }
        }
    }

    /**
     * Creates and runs the server
     *
     * @param args currently none
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer();
    }


    /**
     * Opens the Socket connection
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(socketGreeterPort);
            logger.log(Level.INFO, "Socket server is up and listening on port " + socketGreeterPort);
        } catch (IOException ioException) {    // mainly because the port is already in use, should never happen
            logger.log(Level.SEVERE, ioException.getMessage());
            System.exit(1);
        }
        this.start();
    }

    /**
     * Client greeter
     * <p>
     * Every time the server receives a Socket connection, this method creates and runs a new {@linkplain VirtualClient}
     * object, which handles all the messages.
     * <br>
     * In the case of a Socket-thrown exception, the connection with the client is closed.
     */
    @Override
    public void run() {
        while (true) {
            try {
                newClientSocket = serverSocket.accept();
                logger.log(Level.INFO, "New client accepted: " + newClientSocket.getRemoteSocketAddress());
                Thread thread = new VirtualClient(this, newClientSocket);
                thread.start();
            } catch (IOException ioException) {
                logger.log(Level.SEVERE, ioException.getMessage());
                try {
                    newClientSocket.close();
                    logger.log(Level.WARNING, "Closing " + newClientSocket.getRemoteSocketAddress() + " connection");
                } catch (IOException ioException1) {
                    logger.log(Level.SEVERE, ioException1.getMessage());
                }
            }
        }
    }


    /**
     * Adds a new User to the server
     * <p>
     * Upon receiving a login request (in the {@linkplain VirtualClient}), this method checks if the username is
     * valid (usernames have to be unique and non-reserved keywords, such as ReservedUsernames.BROADCAST) and if there is
     * "enough space" on the server
     *
     * @param virtualClient the user to add
     * @throws RoomFullException if the room the user is trying to join is full
     */
    public synchronized void addClient(VirtualClient virtualClient) throws RoomFullException {        // Login of the player
        String username = virtualClient.getUser().getUsername();
        if (waitingRoom.size() < MAX_WAITING_CLIENTS) {
            virtualClient.getUser().setUsername(username);
            waitingRoom.add(virtualClient.getUser());
            users.put(virtualClient.getUser(), null);
            //QOL: loginReponse now has the info about the lobbies
            Map<String, List<String>> lobbies = new LinkedHashMap<>();
            gameLobbies.values().forEach(lobby -> lobbies.put(lobby.getRoomName(), lobby.lobbyInfo()));
            virtualClient.getUser().notify(new LoginResponse(Type.OK, username, lobbies));
            logger.log(Level.INFO, "Login completed, added " + virtualClient.getUser().getUsername());
        } else
            throw new RoomFullException();
    }


    /**
     * Provides a list of the users in a lobby
     *
     * @param lobby the lobby to check
     * @return the list of users in the lobby
     */
    public List<User> getUsersInRoom(Lobby lobby) {
        return users.keySet().stream().filter(u -> users.get(u) != null).filter(u -> users.get(u).equals(lobby)).collect(Collectors.toList());
    }

    public void sendMessageToWaitingRoom(Message message) {
        waitingRoom.forEach(user -> user.notify(message));
    }

    /**
     * _
     * Provides a list of the users in a lobby
     *
     * @return the list of users in the waiting room
     */
    public List<User> getUsersInWaitingRoom() {
        return waitingRoom;
    }

    /**
     * Removes a lobby
     * <p>
     * If there are players in the lobby, those are moved to the waiting room
     *
     * @param lobby the lobby to delete
     */
    public void removeRoom(Lobby lobby) {
        getUsersInRoom(lobby).forEach(this::moveToWaitingRoom);
        getUsersInRoom(lobby).forEach(lobby::removeUser);
        gameLobbies.remove(lobby.getRoomName(), lobby);
    }

    /**
     * Disconnects an user from the server
     *
     * @param user the user to kick
     */
    public void onDisconnect(User user) {
        Lobby lobby = users.get(user);
        if (lobby != null) {
            users.replace(user, null);
            if (!lobby.gameStarted() || lobby.hasLost(user)) {
                lobby.removeUser(user);
                if (getUsersInRoom(lobby).size() == 0)
                    gameLobbies.remove(lobby.getRoomName());
            } else {
                lobby.removeUser(user);
                removeRoom(lobby);
            }
        } else
            waitingRoom.remove(user);
        users.remove(user);
        logger.log(Level.INFO, user.getUsername() + " has been kicked from the lobby");
        user.closeConnection();
    }

    public HashMap<User, Lobby> getUsers() {
        return users;
    }

    /**
     * Moves an user to another lobby
     *
     * @param user  the user to move
     * @param lobby the lobby to move the user to
     */
    public void moveToRoom(User user, Lobby lobby) {
        Lobby oldRoom = users.replace(user, lobby);
        if (oldRoom == null) {
            logger.log(Level.INFO, user.getUsername() + " moved from waiting room to " + lobby.getRoomName());
            waitingRoom.remove(user);
        } else {
            logger.log(Level.INFO, user.getUsername() + " moved from " + oldRoom.getRoomName() + " to " + lobby.getRoomName());
            if (getUsersInRoom(oldRoom).size() == 0)
                removeRoom(oldRoom);
        }
    }

    public void moveToWaitingRoom(User user) {
        Lobby oldRoom = users.replace(user, null);
        if (oldRoom != null) {
            waitingRoom.add(user);
            Map<String, List<String>> lobbyNames = new LinkedHashMap<>();
            gameLobbies.values().forEach(lobbies -> lobbyNames.put(lobbies.getRoomName(), lobbies.lobbyInfo()));
            user.notify(new MovedToWaitingRoomResponse(user.getUsername(), Type.OK, lobbyNames, null));
            logger.log(Level.INFO, user.getUsername() + " moved from " + oldRoom.getRoomName() + " waiting room");
        }
    }


    public User getUser(String username, Lobby lobby) {
        return users.keySet().stream()
                .filter(u -> (username.equals(u.getUsername()) && lobby.equals(u.getRoom()))).collect(Collectors.toList()).get(0);
    }

    public Map<String, Lobby> getGameLobbies() {
        return gameLobbies;
    }


}
