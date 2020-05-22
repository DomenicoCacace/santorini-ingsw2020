package it.polimi.ingsw.network.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.controller.MessageManagerParser;
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.listeners.EndGameListener;
import it.polimi.ingsw.listeners.PlayerLostListener;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.request.fromServerToClient.*;
import it.polimi.ingsw.network.message.response.fromServerToClient.ChosenGodsEvent;
import it.polimi.ingsw.network.message.response.fromServerToClient.GameBoardUpdate;
import it.polimi.ingsw.network.message.response.fromServerToClient.UserJoinedLobbyEvent;
import it.polimi.ingsw.network.message.response.fromServerToClient.WinnerDeclaredEvent;
import it.polimi.ingsw.network.server.exceptions.InvalidUsernameException;
import it.polimi.ingsw.network.server.exceptions.RoomFullException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The place where a game is created
 * <p>
 *     A Lobby object is a container for a group of players: once created, the owner can select the size and, when full,
 *     start a new game
*/
public class Lobby implements PlayerLostListener, EndGameListener {
    private final static Logger logger = Logger.getLogger(Logger.class.getName());
    private boolean gameStarted;
    private final List<String> usersInLobby;
    private final MessageManagerParser messageParser;
    private Map<User, Player> playerMap;
    private final Map<GodData, God> godsMap;
    private List<GodData> availableGods;
    private final int maxRoomSize;
    private final Server server;
    private ServerController controller;
    private final String roomName;
    private File savedGame;


    /**
     * Default constructor
     * @param server the server object
     * @param roomName the room name
     * @param numOfPlayers the lobby size
     * @param user the first use
     */
    public Lobby(Server server, String roomName, User user, int numOfPlayers) {
        this.server = server;
        this.gameStarted = false;
        this.roomName = roomName;
        this.messageParser = new MessageManagerParser(this);
        this.playerMap = new LinkedHashMap<>(numOfPlayers);
        this.maxRoomSize = numOfPlayers;
        this.godsMap = new LinkedHashMap<>();
        this.availableGods = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        this.usersInLobby = new LinkedList<>(Collections.singleton(ReservedUsernames.BROADCAST.toString()));
        try {
            List<God> gods = objectMapper.readerFor(new TypeReference<List<God>>() {
            }).readValue(this.getClass().getResourceAsStream("GodsConfigFile.json"));
            gods.forEach(g -> godsMap.put(g.buildDataClass(), g));
        } catch (IOException | NullPointerException exception) {
            logger.log(Level.SEVERE, exception.getMessage());
        }
        try {
            this.addUser(user);
        } catch (RoomFullException | InvalidUsernameException roomFullException) {
            logger.log(Level.SEVERE, roomFullException.getMessage());
        }
    }

    private boolean checkSavedGame() {
        File savedGameDir = new File("./savedGames");
        savedGameDir.mkdir();
        savedGame = new File(savedGameDir + File.separator + getFileName());
        if (savedGame.exists()) {
            try {
                if (!Files.readString(Paths.get(String.valueOf(savedGame))).isBlank())
                    return true;
            } catch (IOException ioException) {
                logger.log(Level.INFO, "No saved match found");
            }
        }
        savedGame = null;
        return false;
    }

    private String getFileName() {
        StringBuilder orderedNames = new StringBuilder(this.getRoomName());
        orderedNames.append("_");
        List<String> usernames = new ArrayList<>();
        playerMap.keySet().forEach(u -> usernames.add(u.getUsername()));
        List<String> sortedNames = usernames.stream().sorted().collect(Collectors.toList());
        for (String name : sortedNames)
            orderedNames.append(name).append("_");
        orderedNames.deleteCharAt(orderedNames.length() - 1);
        orderedNames.append(".json");
        System.out.println(orderedNames);
        return orderedNames.toString();
    }

    /**
     * Sends a message to a given user
     * <p>
     *     The message is sent only if the recipient is in the same room of the sender.
    * @param username the message recipient, as a string
     * @param message the message to send
     */
    public void sendMessage(String username, Message message) {
        User recipient = playerMap.keySet().stream().filter(u -> username.equals(u.getUsername())).findFirst().orElse(null);
        if (username.equals(ReservedUsernames.BROADCAST.toString()))
            broadcastMessage(message);
        else if (recipient != null)
            recipient.notify(message);
    }


    /**
     * Sends a message to all the users in the room
     * @param message the message to send
     */
    public void broadcastMessage(Message message) {
        playerMap.keySet().forEach(user -> user.notify(message));
    }

    /**
     * Adds a user to the room
     * @param user the user to add
     * @throws RoomFullException if the room is full
     * @throws InvalidUsernameException if the username is already taken in the lobby
     */
    public synchronized void addUser(User user) throws RoomFullException, InvalidUsernameException {
        if (usersInLobby.size() - 1 >= maxRoomSize)
            throw new RoomFullException();
        if (usersInLobby.contains(user.getUsername()))
            throw new InvalidUsernameException();
        server.moveToRoom(user, this);
        playerMap.put(user, null);
        usersInLobby.add(user.getUsername());
        messageParser.parseMessageFromServerToClient(new UserJoinedLobbyEvent(ReservedUsernames.BROADCAST.toString(), Type.OK, null, maxRoomSize, user.getUsername()));
        if (playerMap.keySet().size() == maxRoomSize) {
            gameStarted = true;
            server.getGameLobbies().remove(this.roomName);
            if (checkSavedGame())
                messageParser.parseMessageFromServerToClient(new ChooseToReloadMatchRequest(new ArrayList<>(playerMap.keySet()).get(0).getUsername()));
            else
                askGods(new ArrayList<>(godsMap.keySet()));
        }
    }

    private File fileCreation() {
        File gameToSave;
        String fileName = getFileName();
        File savedGameDir = new File("./savedGames");
        savedGameDir.mkdir();
        logger.log(Level.INFO, "Created game backup : " + fileName);
        gameToSave = new File(savedGameDir + File.separator + fileName);
        try {
            if (!gameToSave.exists())
                gameToSave.createNewFile();
        } catch (IOException e) { //Cannot create file
            e.printStackTrace();
        }
        return gameToSave;
    }


    public void reloadMatch(boolean wantToReload){
        if(wantToReload) {
            ObjectMapper objectMapper = new ObjectMapper();
            Game restoredGame = null;
            try {
                restoredGame = objectMapper.readerFor(Game.class).readValue(savedGame);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while reading saved game file");
            }
            Map<User, PlayerInterface> playerInterfaces = new LinkedHashMap<>();
            if(restoredGame != null) {
                restoredGame.restoreState();
                restoredGame.getPlayers().forEach(p -> {
                    playerInterfaces.put(server.getUser(p.getName(), this), p);
                    playerMap.put(server.getUser(p.getName(), this), p);
                });
                restoredGame.addPlayerLostListener(this);
                restoredGame.addEndGameListener(this);
                controller = new ServerController(restoredGame, playerInterfaces, messageParser, savedGame);
                this.gameStarted=true;
                messageParser.setServerController(controller);
                controller.handleGameRestore();
            }
        }
        else
            askGods(new ArrayList<>(godsMap.keySet()));
    }


    public void chooseGods(List<GodData> gods) {
        if ((int)gods.stream().distinct().count() == maxRoomSize && gods.size() == maxRoomSize) {
            availableGods = gods;
            messageParser.parseMessageFromServerToClient(new ChosenGodsEvent(Type.OK, ReservedUsernames.BROADCAST.toString(), gods));
            askToChooseGod(usersInLobby.get(2));
        }
        else {
            String firstPlayerName = usersInLobby.get(1);
            messageParser.parseMessageFromServerToClient(new ChosenGodsEvent(Type.INVALID_GOD_CHOICE, firstPlayerName, null));
            askGods(new ArrayList<>(godsMap.keySet()));
        }
    }

    /**
     * Asks the "owner" to choose the gods for the game
     * @param godData the list of all the available gods
     */
    public void askGods(List<GodData> godData) {
        String firstPlayerName = /*new LinkedList<>(playerMap.keySet()).get(0).getUsername()*/usersInLobby.get(1);
        this.gameStarted = true;
        messageParser.parseMessageFromServerToClient(new ChooseInitialGodsRequest(firstPlayerName, godData));
    }

    public void askToChooseGod(String username) {
        messageParser.parseMessageFromServerToClient(new ChooseYourGodRequest(username, availableGods));
    }

    /**
     * Assigns a god to the player who chose it
     * @param username the player's username
     * @param godData the chosen god
     */
    public void assignGod(String username, GodData godData) {
        List<String> usernames = new ArrayList<>();
        usersInLobby.stream().filter(u -> !u.equals(ReservedUsernames.BROADCAST.toString())).forEach(usernames::add);
        User user = server.getUser(usersInLobby.get((((int)playerMap.keySet().stream().filter(u -> playerMap.get(u) != null).count() + 1) % usernames.size()) + 1), this);
        playerMap.replace(user, new Player(username, godsMap.get(godData), Color.values()[(int)playerMap.keySet().stream().filter(u -> playerMap.get(u) != null).count()]));
        availableGods.remove(godData);
        if ((int)playerMap.keySet().stream().filter(u -> playerMap.get(u) != null).count() == maxRoomSize) {
            messageParser.parseMessageFromServerToClient(new ChooseStartingPlayerRequest(usernames.get(0), usernames));
        }
        else {
            String nextUsername = usernames.get((usernames.indexOf(username) + 1) % usernames.size());
            messageParser.parseMessageFromServerToClient(new ChooseYourGodRequest(nextUsername, availableGods));
        }
    }

    public void selectStartingPlayer(String username) {
        List<String> usernames = new LinkedList<>();
        usersInLobby.stream().filter(u -> !u.equals(ReservedUsernames.BROADCAST.toString())).forEach(usernames::add);
        int index = usernames.indexOf(username);
        Collections.rotate(usernames, (usernames.size() - index) % usernames.size());
        Map<User, Player> tmpMap = new LinkedHashMap<>();
        usernames.forEach(u -> tmpMap.put(server.getUser(u, this), playerMap.get(server.getUser(u, this))));
        playerMap = tmpMap;
        createGame();
    }

    public void createGame() {
        Map<User, PlayerInterface> playerInterfaceMap = new LinkedHashMap<>();
        playerMap.keySet().forEach(u -> playerInterfaceMap.put(u, playerMap.get(u)));
        List<Player> players = new LinkedList<>(playerMap.values());
        GameInterface gameInterface = new Game(new GameBoard(), players);
        controller = new ServerController(gameInterface, playerInterfaceMap, messageParser, fileCreation());
        gameInterface.addPlayerLostListener(this);
        gameInterface.addEndGameListener(this);
        messageParser.setServerController(controller);
        messageParser.parseMessageFromServerToClient(new GameBoardUpdate(players.get(0).getName(), gameInterface.buildBoardData()));
        messageParser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(players.get(0).getName(), gameInterface.buildBoardData()));
    }

    public boolean gameStarted() {
        return gameStarted;
    }

    public void removeUser(User user) {
        playerMap.remove(user);
        usersInLobby.remove(user.getUsername());
        if(!gameStarted)
            server.getGameLobbies().put(this.roomName, this);
    }

    public List<String> lobbyInfo(){
        List<String> info = new ArrayList<>();
        info.add(this.roomName);
        info.add(String.valueOf(usersInLobby.size() - 1));
        info.add(String.valueOf(this.maxRoomSize - usersInLobby.size() +1));
        info.addAll(this.usersInLobby);
        info.remove(ReservedUsernames.BROADCAST.toString());
        return info;
    }
    public String getRoomName() {
        return this.roomName;
    }

    public MessageManagerParser getRoomParser() {
        return messageParser;
    }

    public boolean hasLost(User user){
        return !playerMap.containsKey(user);
    }

    @Override
    public void onPlayerLoss(String username, List<Cell> gameboard) {
        playerMap.remove(server.getUser(username, this));
        savedGame = fileCreation();
        controller.setFile(savedGame);
    }

    @Override
    public void onEndGame(String name) {
        Message message = new WinnerDeclaredEvent(name);
        savedGame.delete();
        playerMap.keySet().forEach(user -> user.notify(message));
        playerMap.keySet().forEach(server::moveToWaitingRoom);
        server.removeRoom(this);
    }
}