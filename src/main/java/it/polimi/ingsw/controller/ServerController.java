package it.polimi.ingsw.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseWorkerPositionRequest;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;
import it.polimi.ingsw.network.server.User;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public class ServerController implements AddWorkerListener, BuildableCellsListener, BuildActionListener, EndGameListener, BuildingBlocksListener,
        EndTurnListener, MoveActionListener, WalkableCellsListener, PlayerLostListener, SelectWorkerListener {

    private final static Logger logger = Logger.getLogger(Logger.class.getName());
    private final GameInterface game;
    private final Map<String, PlayerInterface> playerMap;
    private final MessageManagerParser parser;
    private int cont = 0;
    private final File file;


    public ServerController(GameInterface game, Map<User, PlayerInterface> players, MessageManagerParser parser, File gameToSave) {
        this.game = game;
        playerMap = new LinkedHashMap<>();
        players.forEach((u, p) -> playerMap.put(u.getUsername(), p));
        this.parser = parser;
        this.file = gameToSave;
        game.setBuildActionListener(this);
        game.setEndGameListener(this);
        game.setEndTurnListener(this);
        game.setMoveActionListener(this);
        game.addPlayerLostListener(this);
        playerMap.values().forEach(playerInterface
                -> {
            playerInterface.setAddWorkerListener(this);
            playerInterface.setBuildableCellsListener(this);
            playerInterface.setWalkableCellsListener(this);
            playerInterface.setSelectWorkerListener(this);
            playerInterface.setBuildingBlocksListener(this);
        });
    }


    public void handleGameRestore(){
        PlayerData currPlayerData = game.buildGameData().getCurrentTurn().getCurrentPlayer();
        PlayerInterface currPlayerInterface=  playerMap.get(currPlayerData.getName());
        if(currPlayerData.getSelectedWorker()!=null) { //Now i have selectedWorker inside the playerDataClass
            try {
                parser.parseMessageFromServerToClient(new GameBoardMessage(currPlayerData.getName(), game.buildBoardData())); //need this otherwise client doesn't have gameboard saved locally!
                currPlayerInterface.setSelectedWorker(currPlayerData.getSelectedWorker());//simulate a setWorker, in this way the player will call the SelectWorkerlistener.
            } catch (NotYourWorkerException e) {
                e.printStackTrace(); //shouldn't go here
            }
        } else  parser.parseMessageFromServerToClient(new GameStartResponse(Type.OK, game.buildGameData()));
    }

    public void addWorker(String username, Cell cell) {
        try {
            playerMap.get(username).addWorker(cell);

            if (!playerMap.get(username).allWorkersArePlaced())
                parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(username, game.buildBoardData()));
            else {
                cont++;
                if (cont < playerMap.values().size()) {
                    List<String> usernames = new ArrayList<>(playerMap.keySet());
                    parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(usernames.get(cont), game.buildBoardData()));
                } else parser.parseMessageFromServerToClient(new GameStartResponse(Type.OK, game.buildGameData()));
            }
        } catch (AddingFailedException e) {
            logger.log(Level.INFO, username + " failed to add a worker");
            parser.parseMessageFromServerToClient(new AddWorkerResponse(Type.ADDING_FAILED, username, game.buildBoardData()));
            parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(username, game.buildBoardData()));
        }
    }

    public void selectWorker(String username, Worker worker) {
        try {
            playerMap.get(username).setSelectedWorker(worker);
            //parser.parseMessageFromServerToClient(new SelectWorkerResponse(Type.OK, username));
        } catch (NotYourWorkerException e) {
            parser.parseMessageFromServerToClient(new SelectWorkerResponse(Type.NOT_YOUR_WORKER, username, null, null));
        }
    }

    public void obtainWalkableCells(String username) {
        try {
            playerMap.get(username).obtainWalkableCells();
            //parser.parseMessageFromServerToClient(new WalkableCellsResponse(Type.OK,username, walkableCells));
        } catch (WrongSelectionException e) {
            parser.parseMessageFromServerToClient(new WalkableCellsResponse(Type.NOT_YOUR_WORKER, username, null));
        }
    }

    public void obtainBuildableCells(String username) {
        try {
            playerMap.get(username).obtainBuildableCells();
            //parser.parseMessageFromServerToClient(new BuildableCellsResponse(Type.OK,username, buildableCells));
        } catch (WrongSelectionException e) {
            parser.parseMessageFromServerToClient(new BuildableCellsResponse(Type.NOT_YOUR_WORKER, username, null));
        }
    }

    public void selectBuildingCell(String username, Cell selectedCell) {
        try {
            playerMap.get(username).obtainBuildingBlocks(selectedCell);
        } catch (IllegalActionException e) {
            parser.parseMessageFromServerToClient(new PlayerBuildResponse(Type.ILLEGAL_BUILD, username, game.buildBoardData()));
        }
    }

    public void handleMoveAction(String username, MoveAction moveAction) {
        try {
            playerMap.get(username).useAction(moveAction);
            //parser.parseMessageFromServerToClient(new PlayerMoveResponse(Type.OK, username, game.cloneGameBoard()));
        } catch (IllegalActionException e) {
            parser.parseMessageFromServerToClient(new PlayerMoveResponse(Type.ILLEGAL_MOVEMENT, username, game.buildBoardData()));
        }

    }

    public void handleBuildAction(String username, BuildAction buildAction) {
        try {
            playerMap.get(username).useAction(buildAction);
            //parser.parseMessageFromServerToClient(new PlayerBuildResponse(Type.OK, username, game.cloneGameBoard()));
        } catch (IllegalActionException e) {
            parser.parseMessageFromServerToClient(new PlayerBuildResponse(Type.ILLEGAL_BUILD, username, game.buildBoardData()));
        }
    }

    public void passTurn(String username) {
        try {
            playerMap.get(username).askPassTurn();
            // parser.parseMessageFromServerToClient(new EndTurnResponse(Type.OK, username));
        } catch (IllegalEndingTurnException e) {
            parser.parseMessageFromServerToClient(new EndTurnResponse(Type.CANNOT_END_TURN, username, null));
        }
    }

    public void saveState() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerFor(Game.class).withDefaultPrettyPrinter().writeValue(file, game);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMoveAction(List<Cell> cells) {
        saveState();
        parser.parseMessageFromServerToClient(new PlayerMoveResponse(Type.OK, ReservedUsernames.BROADCAST.toString(), cells));
    }

    @Override
    public void onWorkerAdd(List<Cell> cells) {
        parser.parseMessageFromServerToClient(new AddWorkerResponse(Type.OK, ReservedUsernames.BROADCAST.toString(), cells));
    }

    @Override
    public void onBuildAction(List<Cell> cells) {
        saveState();
        parser.parseMessageFromServerToClient(new PlayerBuildResponse(Type.OK, ReservedUsernames.BROADCAST.toString(), cells));
    }

    @Override
    public void onBuildableCell(String name, List<Cell> cells) {
        parser.parseMessageFromServerToClient(new BuildableCellsResponse(Type.OK, name, cells));
    }

    @Override
    public void onBlocksObtained(String name, List<Block> blocks) {
        parser.parseMessageFromServerToClient(new SelectBuildingCellResponse(Type.NOTIFY, name, blocks));
    }

    @Override
    public void onEndGame(String name) {
        file.delete();
        parser.parseMessageFromServerToClient(new WinnerDeclaredResponse(name));
        parser.endGame();
    }

    @Override
    public void onTurnEnd(String name) {
        saveState();
        parser.parseMessageFromServerToClient(new EndTurnResponse(Type.OK, ReservedUsernames.BROADCAST.toString(), name));
    }


    @Override
    public void onWalkableCells(String name, List<Cell> cells) {
        parser.parseMessageFromServerToClient(new WalkableCellsResponse(Type.OK, name, cells));
    }

    @Override
    public void onPlayerLoss(String username, List<Cell> gameboard) {
        parser.parseMessageFromServerToClient(new PlayerRemovedResponse(username, gameboard));
        playerMap.remove(username);
        file.delete();
        saveState();
    }

    @Override
    public void onSelectedWorker(String username, List<PossibleActions> possibleActions, Worker selectedWorker) {
        saveState();
        parser.parseMessageFromServerToClient(new SelectWorkerResponse(Type.OK, username, possibleActions, selectedWorker));
    }
}