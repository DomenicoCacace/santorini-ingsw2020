package it.polimi.ingsw.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.network.message.request.fromServerToClient.ChooseWorkerPositionRequest;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public class ServerController implements AddWorkerListener, BuildableCellsListener, BuildActionListener, EndGameListener, BuildingBlocksListener,
                                         EndTurnListener, MoveActionListener, WalkableCellsListener, PlayerLostListener, SelectWorkerListener  {

    private final GameInterface game;
    private final Map<String, PlayerInterface> playerMap;
    private final MessageParser parser;
    private int cont = 0;


    public ServerController(GameInterface game, Map<String, PlayerInterface> players, MessageParser parser) {
        this.game = game;
        this.playerMap = players;
        this.parser = parser;
        game.setBuildActionListener(this);
        game.setEndGameListener(this);
        game.setEndTurnListener(this);
        game.setMoveActionListener(this);
        game.setPlayerLostListener(this);
        playerMap.values().forEach(playerInterface
                -> {playerInterface.setAddWorkerListener(this);
                    playerInterface.setBuildableCellsListener(this);
                    playerInterface.setWalkableCellsListener(this);
                    playerInterface.setSelectWorkerListener(this);
                    playerInterface.setBuildingBlocksListener(this);});
    }

    public void addWorker(String username, Cell cell) {
        try {
            playerMap.get(username).addWorker(cell);

            if(!playerMap.get(username).allWorkersArePlaced())
                parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(username, game.buildBoardData()));
            else {
                cont++;
                if(cont < playerMap.values().size()){
                    List<String> usernames = new ArrayList<>(playerMap.keySet());
                    parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(usernames.get(cont), game.buildBoardData()));
                } else parser.parseMessageFromServerToClient(new GameStartResponse("OK", game.buildGameData()));
            }
        } catch (AddingFailedException e) {
            System.out.println("Adding failed");
            parser.parseMessageFromServerToClient(new AddWorkerResponse("Adding failed", username, game.buildBoardData()));
            parser.parseMessageFromServerToClient(new ChooseWorkerPositionRequest(username, game.buildBoardData())); //TODO: this should be managed by the view when add_worker fails
        }
    }

    public void selectWorker(String username, Worker worker) {
        try {
            playerMap.get(username).setSelectedWorker(worker);
            //parser.parseMessageFromServerToClient(new SelectWorkerResponse("OK", username));
        } catch (NotYourWorkerException e) {
            parser.parseMessageFromServerToClient(new SelectWorkerResponse("Not your worker", username, null, null));
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

    public void selectBuildingCell(String username, Cell selectedCell){
        try {
            playerMap.get(username).obtainBuildingBlocks(selectedCell);
        } catch (IllegalActionException e){
            parser.parseMessageFromServerToClient(new PlayerBuildResponse("Illegal build", username, game.buildBoardData()));
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
    public void onBlocksObtained(String name, List<Block> blocks) {
        parser.parseMessageFromServerToClient(new SelectBuildingCellResponse(name ,blocks));
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

    @Override
    public void onSelectedWorker(String username, List<PossibleActions> possibleActions, Worker selectedWorker) {
        parser.parseMessageFromServerToClient(new SelectWorkerResponse("OK", username, possibleActions, selectedWorker));
    }


}
