package it.polimi.ingsw.network.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.controller.MessageParser;
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseInitialGodsRequest;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseYourGodRequest;
import it.polimi.ingsw.network.message.response.fromServerToClient.ChosenGodsResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.GameStartResponse;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Lobby {
    private final List<String> userNames;
    private final Map<String, Player> playerMap = new LinkedHashMap<>();
    private final MessageParser parser;
    private List<GodData> chosenGods = new ArrayList<>();
    private final Map<GodData, God> godsMap = new HashMap<>();

    public Lobby(MessageParser parser, List<String> userNames) throws IOException {
        this.parser = parser;
        this.userNames = userNames;
        parser.setLobby(this);
        ObjectMapper objectMapper = new ObjectMapper();
        List<God> allGods = objectMapper.readerFor(new TypeReference<List<God>>() {
        }).readValue(new File("GodsConfigFile.json"));
        for (God god : allGods) {
            godsMap.put(god.buildDataClass(), god);
        }
        askGods(new ArrayList<>(godsMap.keySet()));
    }

    public void addUser(String username) {
        userNames.add(username);
    }

    public void assignGod(String username, GodData god) {
        playerMap.put(username, new Player(username, godsMap.get(god), Color.BLUE)); //TODO: color random generator
        chosenGods.remove(god);
        notify(); //TODO: Test wait and notify
    }

    public void askToChooseGod(String username) {
        parser.parseMessageFromServerToClient(new ChooseYourGodRequest(username, chosenGods));
    }

    public void askGods(List<GodData> gods) {
        parser.parseMessageFromServerToClient(new ChooseInitialGodsRequest(userNames.get(0), gods));
    }

    public void chooseGods(List<GodData> gods) throws InterruptedException {
        if ((int) gods.stream().distinct().count() == userNames.size() && gods.size() == userNames.size()) {
            chosenGods = gods;
            parser.parseMessageFromServerToClient(new ChosenGodsResponse("OK", "broadcast", chosenGods));
            for (int i = 1; i <= userNames.size(); i++) {
                askToChooseGod(userNames.get(i % userNames.size()));
                while (!playerMap.containsKey(userNames.get(i % userNames.size()))) //TODO: lazy evaluator
                    wait();
            }
            createGame();
        } else {
            parser.parseMessageFromServerToClient(new ChosenGodsResponse("Illegal gods choice", userNames.get(0), null));
            askGods(gods);
        }
    }

    public void createGame() {
        List<Player> players = new ArrayList<>(playerMap.values());
        GameBoard board = new GameBoard();
        Game game = new Game(board, players);
        ServerController controller = new ServerController(game, playerMap);
        parser.setServerController(controller);
        parser.parseMessageFromServerToClient(new GameStartResponse("OK", game.buildGameData()));
    }

}
