package it.polimi.ingsw.network.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.controller.MessageParser;
import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.request.fromServerToClient.*;
import it.polimi.ingsw.network.message.response.fromServerToClient.ChosenGodsResponse;
import it.polimi.ingsw.network.message.response.fromServerToClient.GameStartResponse;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Lobby {
    private final List<String> userNames;
    private final MessageParser parser;
    private final Map<GodData, God> godsMap = new HashMap<>();
    private Map<String, Player> playerMap = new LinkedHashMap<>();
    private List<GodData> chosenGods = new ArrayList<>();
    private File savedGame;

    public Lobby(MessageParser parser, List<String> userNames) throws IOException {
        this.parser = parser;
        this.userNames = userNames;
        parser.setLobby(this);
        ObjectMapper objectMapper = new ObjectMapper();
        List<God> allGods = objectMapper.readerFor(new TypeReference<List<God>>() {
        }).readValue(this.getClass().getResourceAsStream("GodsConfigFile.json"));
        for (God god : allGods) {
            godsMap.put(god.buildDataClass(), god);
        }
       if(checkSavedGame())
           parser.parseMessageFromServerToClient(new ChooseToReloadMatchRequest(userNames.get(0)));
       else askGods(new ArrayList<>(godsMap.keySet()));
    }

    private boolean checkSavedGame(){
        StringBuilder userPermutation = new StringBuilder();
        List<String> usernameToRotate = new ArrayList<>(userNames);
        for(int i = 0; i < usernameToRotate.size(); i++){
            for(String player: usernameToRotate) { //TODO: alphabetic order
                if (!(usernameToRotate.indexOf(player) == (usernameToRotate.size() - 1)))
                    userPermutation.append(player).append("_");
                else
                    userPermutation.append(player).append(".json");
            }
            System.out.println(userPermutation);
            savedGame = new File("../" + userPermutation);
            if(savedGame.exists()) {
                return true;
            }
            else {
                userPermutation = new StringBuilder();
                Collections.rotate(usernameToRotate, 1);
            }
        }
        savedGame = null;
        return false;
    }

    public void reloadMatch(boolean wantToReload){
        if(wantToReload) {
            ObjectMapper objectMapper = new ObjectMapper();
            Game restoredGame = null;
            try {
                restoredGame = objectMapper.readerFor(Game.class).readValue(savedGame);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String, PlayerInterface> playerInterfaces = new LinkedHashMap<>();
            if(restoredGame!=null) {
                restoredGame.restoreState();
                restoredGame.getPlayers().forEach(player -> playerInterfaces.put(player.getName(), player));
                playerMap.keySet().forEach(s -> playerInterfaces.put(s, playerMap.get(s)));
                ServerController controller = new ServerController(restoredGame, playerInterfaces, parser);
                parser.setServerController(controller);
                parser.parseMessageFromServerToClient(new GameStartResponse("OK", restoredGame.buildGameData()));
            }
        } else askGods(new ArrayList<>(godsMap.keySet()));
    }

    public void assignGod(String username, GodData god) {
        GodData finalGod = god;
        god = godsMap.keySet().stream().filter(godToRemove -> godToRemove.getName().equals(finalGod.getName())).collect(Collectors.toList()).get(0);
        playerMap.put(username, new Player(username, godsMap.get(god), Color.values()[playerMap.keySet().size()]));
        chosenGods = chosenGods.stream().filter(chosenGod -> !finalGod.getName().equals(chosenGod.getName())).collect(Collectors.toList());
        System.out.println("gods to choose  " + chosenGods);
        if (playerMap.values().size() == userNames.size()) {
            parser.parseMessageFromServerToClient(new ChooseStartingPlayerRequest(userNames.get(0), userNames));
        } else askToChooseGod(userNames.get((userNames.indexOf(username) + 1) % userNames.size()));
    }

    public void selectStartingPlayer(String startingPlayer) {
        List<String> keys = new LinkedList<>(playerMap.keySet());
        int position = new ArrayList<>(playerMap.keySet()).indexOf(startingPlayer);
        Collections.rotate(keys, (keys.size() - position) % keys.size());
        Map<String, Player> tmpMap = new LinkedHashMap<>();
        for (String name : keys) {
            tmpMap.put(name, playerMap.get(name));
        }
        playerMap = tmpMap;
        createGame();
    }

    public void askToChooseGod(String username) {
        parser.parseMessageFromServerToClient(new ChooseYourGodRequest(username, chosenGods));
    }

    public void askGods(List<GodData> gods) {
        parser.parseMessageFromServerToClient(new ChooseInitialGodsRequest(userNames.get(0), gods));
    }

    public void chooseGods(List<GodData> gods) {
        if ((int) gods.stream().distinct().count() == userNames.size() && gods.size() == userNames.size()) {
            chosenGods = gods;
            parser.parseMessageFromServerToClient(new ChosenGodsResponse("OK", "broadcast", chosenGods));
            askToChooseGod(userNames.get(1));
        } else {
            parser.parseMessageFromServerToClient(new ChosenGodsResponse("Illegal gods choice", userNames.get(0), null));
            askGods(new ArrayList<>(godsMap.keySet()));
        }
    }

    public void createGame() {
        Map<String, PlayerInterface> playerInterfaces = new LinkedHashMap<>();
        playerMap.keySet().forEach(s -> playerInterfaces.put(s, playerMap.get(s)));
        List<Player> players = new ArrayList<>(playerMap.values());
        GameBoard board = new GameBoard();
        GameInterface game = new Game(board, players);
        ServerController controller = new ServerController(game, playerInterfaces, parser);
        parser.setServerController(controller);
        parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(players.get(0).getName(), game.buildBoardData()));
    }

}
