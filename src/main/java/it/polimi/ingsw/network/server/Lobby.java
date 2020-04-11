package it.polimi.ingsw.network.server;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.controller.MessageParser;
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.message.request.AssignGodRequest;
import it.polimi.ingsw.network.message.request.ChooseInitialGodsRequest;
import it.polimi.ingsw.network.message.request.ChooseYourGodRequest;
import it.polimi.ingsw.network.message.response.ChosenGodsResponse;
import it.polimi.ingsw.network.message.response.GameStartResponse;
import it.polimi.ingsw.network.message.response.LoginResponse;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Lobby {

    private final static int MAXNUMPLAYERS = 3;

    private Logger logger;
    private List<String> userNames;
    private Map<String, Player> playerMap = new LinkedHashMap<>();
    private MessageParser parser;
    private Server server;
    private ServerController controller;

    private List<God> chosenGods = new ArrayList<>();
    private List<God> allGods;

    public Lobby(MessageParser parser, Server server, List<String> userNames) throws IOException {
        this.parser = parser;
        this.server = server;
        this.userNames = userNames;
        parser.setLobby(this);
        ObjectMapper objectMapper = new ObjectMapper();
        allGods = objectMapper.readerFor(God.class).readValue(new File("../allGods.json"));
        askGods(allGods);

    }

    public void addUser(String username) {
        userNames.add(username);
    }

    public void assignGod(String username, God god) {
        playerMap.put(username, new Player(username, god, Color.BLUE)); //TODO: color random generator
        chosenGods.remove(god);
        notify(); //TODO: Test wait and notify
    }

    public void askToChooseGod(String username) throws IOException, InterruptedException {
        server.getVirtualClient(username).notify(new ChooseYourGodRequest(username, chosenGods));
    }

    public void askGods(List<God> gods) throws IOException {
        server.getVirtualClient(userNames.get(0)).notify(new ChooseInitialGodsRequest(userNames.get(0), allGods));
    }

    public void chooseGods(List<God> gods) throws IOException, InterruptedException {
        if ((int) gods.stream().distinct().count() == userNames.size() && gods.size() == userNames.size()) {
            chosenGods = gods;
            parser.parseMessageFromServerToClient(new ChosenGodsResponse("OK", chosenGods));
            for (int i = 1; i <= userNames.size(); i++) {
                askToChooseGod(userNames.get(i % userNames.size()));
                while (!playerMap.containsKey(userNames.get(i % userNames.size()))) //TODO: lazy evaluator
                    wait();
            }
            createGame();
        } else {
            parser.parseMessageFromServerToClient(new ChosenGodsResponse("Illegal gods choice", null));
            askGods(allGods);
        }
    }

    public void createGame() throws IOException {
        List<Player> players = new ArrayList<>(playerMap.values());
        GameBoard board = new GameBoard();
        Game game = new Game(board, players);
        parser.parseMessageFromServerToClient(new GameStartResponse("OK", game));
    }
}
