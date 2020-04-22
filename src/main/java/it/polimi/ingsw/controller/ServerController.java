package it.polimi.ingsw.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public class ServerController implements AddWorkerListener, BuildableCellsListener, BuildActionListener, EndGameListener,
                                         EndTurnListener, MoveActionListener, WalkableCellsListener, PlayerLostListener  {

    private final GameInterface game;
    private final Map<String, PlayerInterface> playerMap;
    private MessageParser parser;

    public ServerController(GameInterface game, Map<String, PlayerInterface> players) {
        this.game = game;
        this.playerMap = players;
        /*for (Event event : Event.values()) {
            if (event != Event.ADD_WORKER && event != Event.BUILDABLE_CELLS && event != Event.WALKABLE_CELLS)
                    game.addObserver(this, event);
            else {
                playerMap.forEach((s, player) -> player.addObserver(this, event));
            }
        }*/
        game.setBuildActionListener(this);
        game.setEndGameListener(this);
        game.setEndTurnListener(this);
        game.setMoveActionListener(this);
        game.setPlayerLostListener(this);
        playerMap.values().forEach(playerInterface
                -> {playerInterface.setAddWorkerListener(this);
                    playerInterface.setBuildableCellsListener(this);
                    playerInterface.setWalkableCellsListener(this);});

    }

    public void addWorker(String username, Cell cell) {
        try {
            playerMap.get(username).addWorker(cell);
            //parser.parseMessageFromServerToClient(new AddWorkerResponse("OK", username, game.cloneGameBoard()));
        } catch (AddingFailedException e) {
            System.out.println("Adding failed");
            parser.parseMessageFromServerToClient(new AddWorkerResponse("Adding failed", username, game.buildBoardData())); //TODO: we can build a whole gameData and do gameData.getBoard()
        }
    }

    public void selectWorker(String username, Worker worker) {
        try {
            playerMap.get(username).setSelectedWorker(worker);
            //parser.parseMessageFromServerToClient(new SelectWorkerResponse("OK", username));
        } catch (NotYourWorkerException e) {
            parser.parseMessageFromServerToClient(new SelectWorkerResponse("Not your worker", username));
        }
    }

    public void obtainWalkableCells(String username) {
        try {
            playerMap.get(username).obtainWalkableCells();
            //parser.parseMessageFromServerToClient(new WalkableCellsResponse("OK",username, walkableCells));
        } catch (WrongSelectionException e) {
            parser.parseMessageFromServerToClient(new WalkableCellsResponse("Wrong selection", username, null));
        }
    }

    public void obtainBuildableCells(String username) {
        try {
            playerMap.get(username).obtainBuildableCells();
            //parser.parseMessageFromServerToClient(new BuildableCellsResponse("OK",username, buildableCells));
        } catch (WrongSelectionException e) {
            parser.parseMessageFromServerToClient(new BuildableCellsResponse("Wrong selection", username, null));
        }
    }

    public void handleMoveAction(String username, MoveAction moveAction) {
        try {
            playerMap.get(username).useAction(moveAction);
            //parser.parseMessageFromServerToClient(new PlayerMoveResponse("OK", username, game.cloneGameBoard()));
        } catch (IllegalActionException e) {
            parser.parseMessageFromServerToClient(new PlayerMoveResponse("Illegal move", username, game.buildBoardData()));
        }

    }

    public void handleBuildAction(String username, BuildAction buildAction) {
        try {
            playerMap.get(username).useAction(buildAction);
            //parser.parseMessageFromServerToClient(new PlayerBuildResponse("OK", username, game.cloneGameBoard()));
        } catch (IllegalActionException e) {
            parser.parseMessageFromServerToClient(new PlayerBuildResponse("Illegal build", username, game.buildBoardData()));
        }
    }

    public void passTurn(String username) {
        try {
            playerMap.get(username).askPassTurn();
            // parser.parseMessageFromServerToClient(new EndTurnResponse("OK", username));
        } catch (IllegalEndingTurnException e) {
            parser.parseMessageFromServerToClient(new EndTurnResponse("You cannot end turn now", username, null));
        }
    }

    @Override
    public void onMoveAction(List<Cell> cells) {
        parser.parseMessageFromServerToClient(new PlayerMoveResponse("OK", "broadcast", cells));
    }

    @Override
    public void onWorkerAdd(List<Cell> cells) {
        parser.parseMessageFromServerToClient(new AddWorkerResponse("OK", "broadcast", cells));
    }

    @Override
    public void onBuildAction(List<Cell> cells) {
        parser.parseMessageFromServerToClient(new PlayerBuildResponse("OK", "broadcast", cells));
    }

    @Override
    public void onBuildableCell(String name, List<Cell> cells) {
        parser.parseMessageFromServerToClient(new BuildableCellsResponse("OK", name, cells));
    }

    @Override
    public void onEndGame(String name) {
        parser.parseMessageFromServerToClient(new WinnerDeclaredResponse("OK", name));
    }

    @Override
    public void onTurnEnd(String name) {
        parser.parseMessageFromServerToClient(new EndTurnResponse("OK", "broadcast", name));
    }


    @Override
    public void onWalkableCells(String name, List<Cell> cells) {
        parser.parseMessageFromServerToClient(new WalkableCellsResponse("OK", name, cells));
    }

    @Override
    public void onPlayerLoss(List<Cell> cells) {
        parser.parseMessageFromServerToClient(new PlayerRemovedResponse("OK", cells));
    }
}
