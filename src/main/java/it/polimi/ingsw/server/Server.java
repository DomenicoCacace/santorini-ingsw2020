package it.polimi.ingsw.server;

import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.User;
import it.polimi.ingsw.server.network.VirtualClient;
import it.polimi.ingsw.server.network.exceptions.RoomFullException;
import it.polimi.ingsw.shared.messages.Message;
import it.polimi.ingsw.shared.messages.Type;
import it.polimi.ingsw.shared.messages.fromServerToClient.LoginResponse;
import it.polimi.ingsw.shared.messages.fromServerToClient.MovedToWaitingRoomResponse;

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
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int MAX_CLIENTS = 15;
    private static final int DEFAULT_PORT = 4321;

    private final Map<String, Lobby> gameLobbies;
    private final List<User> waitingRoom;
    private final HashMap<User, Lobby> users;
    private final int socketGreeterPort;
    private final int waitingRoomSize;
    private ServerSocket serverSocket;
    private Socket newClientSocket;

    /**
     * Default constructor
     * <p>
     * Creates a new server instance
     *
     * @throws IOException if an I/O error occurs while creating the log file
     */
    public Server(int port, int maxClients) throws IOException {
        this.gameLobbies = new LinkedHashMap<>();
        this.users = new LinkedHashMap<>();
        this.waitingRoom = new LinkedList<>();

        if (port < 1024 || port > 65535) {
            logger.log(Level.WARNING, "Port value invalid, using default");
            int defaultPort;
            try {
                InputStream socketPort = this.getClass().getResourceAsStream("socketPort.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(socketPort);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                defaultPort = Integer.parseInt(bufferedReader.readLine().trim());
            } catch (Exception e) {
                defaultPort = DEFAULT_PORT;
            }
            socketGreeterPort = defaultPort;
        }
        else {
            socketGreeterPort = port;
        }
        if (maxClients < 2) {
            logger.log(Level.WARNING,"maxClients value invalid, using defaults");
            int maximumClientsInWaitingRoom;
            try {
                InputStream maxClientsStream = this.getClass().getResourceAsStream("maxClients.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(maxClientsStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                maximumClientsInWaitingRoom = Integer.parseInt(bufferedReader.readLine().trim());
            } catch (Exception e) {
                maximumClientsInWaitingRoom = MAX_CLIENTS;
            }
            waitingRoomSize = maximumClientsInWaitingRoom;
        }
        else {
            waitingRoomSize = maxClients;
        }
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
            logger.log(Level.SEVERE, e.toString());
        }
    }

    /**
     * Creates and runs the server
     *
     * @param args currently none
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        int port = 0;
        int maxClients = 0;
        List<String> arguments = Arrays.stream(args).collect(Collectors.toList());

        if (arguments.contains("--port")) {
            int ind = arguments.indexOf("--port") + 1;
            if (ind < arguments.size()) {
                try {
                    port = Integer.parseInt(arguments.get(ind));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "--port argument invalid, using default value (4321)");
                }
            }
        }

        if (arguments.contains("--maxClients")) {
            int ind = arguments.indexOf("--maxClients") + 1;
            if (ind < arguments.size()) {
                try {
                    maxClients = Integer.parseInt(arguments.get(ind));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "--maxClients argument invalid, using default value (15)");
                }
            }
        }

        Server server = new Server(port, maxClients);
        server.startServer();
    }

    /**
     * <i>gameLobbies</i> getter
     *
     * @return the LobbyName-Lobby map
     */
    public Map<String, Lobby> getGameLobbies() {
        return gameLobbies;
    }

    /**
     * <i>users</i> getter
     *
     * @return the user-lobby map
     */
    public Map<User, Lobby> getUsers() {
        return users;
    }

    /**
     * Finds an User, looking for its username in a lobby
     *
     * @param username the user's username
     * @param lobby the lobby to search for the user
     *
     * @return the User object corresponding to the up mentioned description
     */
    public User getUser(String username, Lobby lobby) {
        return users.keySet().stream()
                .filter(u -> (username.equals(u.getUsername()) && lobby.equals(u.getRoom()))).collect(Collectors.toList()).get(0);
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
        if (waitingRoom.size() < waitingRoomSize) {
            virtualClient.getUser().setUsername(username);
            waitingRoom.add(virtualClient.getUser());
            users.put(virtualClient.getUser(), null);
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

    /**
     * Sends a message to all the clients in the waiting room
     * @param message the message to send
     */
    public void sendMessageToWaitingRoom(Message message) {
        waitingRoom.forEach(user -> user.notify(message));
    }

    /**
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
                if (getUsersInRoom(lobby).isEmpty())
                    gameLobbies.remove(lobby.getRoomName());
            } else {
                lobby.removeUser(user);
                removeRoom(lobby);
            }
        } else
            waitingRoom.remove(user);
        users.remove(user);
        logger.log(Level.INFO, user.getUsername() + " has been removed from the lobby");
        user.closeConnection();
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
            if (getUsersInRoom(oldRoom).isEmpty())
                removeRoom(oldRoom);
        }
    }

    /**
     * Moves an user to the waiting room
     *
     * @param user the user to move
     */
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

    /**
     * Deletes log files older than 7 days
     *
     * @param directory the directory containing the log files
     */
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
}
