package it.polimi.ingsw.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.ObserverPattern.ObserverInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;

import java.io.IOException;
import java.util.Map;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public class ServerController implements ObserverInterface {

    private Game game;
    private final Map<String, Player> playerMap;
    private MessageParser parser;

    public ServerController(Game game, Map<String, Player> players) {
        this.game = game;
        this.playerMap = players;
        //TODO: Register observers
        for(Event event: Event.values()){
            if(event != Event.ADD_WORKER &&
                event!= Event.BUILDABLE_CELLS &&
                event!= Event.WALKABLE_CELLS)
                game.addObserver(this, event);
            else{
                playerMap.forEach((s, player) -> player.addObserver(this, event));
            }
        }
    }

    public void addWorker(String username, Cell cell) {
       try{
           playerMap.get(username).addWorker(cell);
           //parser.parseMessageFromServerToClient(new AddWorkerResponse("OK", username, game.cloneGameBoard()));
       } catch (AddingFailedException e){
           System.out.println("Adding failed");
           parser.parseMessageFromServerToClient(new AddWorkerResponse("Adding failed", username, game.cloneGameBoard()));
       }
    }

    public void selectWorker(String username, Worker worker) {
        try{
            playerMap.get(username).setSelectedWorker(worker);
            //parser.parseMessageFromServerToClient(new SelectWorkerResponse("OK", username));
        } catch(NotYourWorkerException e){
            parser.parseMessageFromServerToClient(new SelectWorkerResponse("Not your worker", username));
        }
    }

    public void obtainWalkableCells(String username) {
        try {
            playerMap.get(username).obtainWalkableCells();
            //parser.parseMessageFromServerToClient(new WalkableCellsResponse("OK",username, walkableCells));
        } catch (WrongSelectionException e){
            parser.parseMessageFromServerToClient(new WalkableCellsResponse("Wrong selection", username, null));
        }
    }

    public void obtainBuildableCells(String username) {
        try {
            playerMap.get(username).obtainBuildableCells();
            //parser.parseMessageFromServerToClient(new BuildableCellsResponse("OK",username, buildableCells));
        } catch (WrongSelectionException e){
            parser.parseMessageFromServerToClient(new BuildableCellsResponse("Wrong selection", username, null));
        }
    }

    public void handleMoveAction(String username, MoveAction moveAction) {
        try{
            playerMap.get(username).setAction(moveAction);
            playerMap.get(username).useAction();
            //parser.parseMessageFromServerToClient(new PlayerMoveResponse("OK", username, game.cloneGameBoard()));
        } catch (IllegalActionException | IOException e) {
            parser.parseMessageFromServerToClient(new PlayerMoveResponse("Illegal move", username, game.cloneGameBoard()));
        }

    }

    public void handleBuildAction(String username, BuildAction buildAction) {
        try{
            playerMap.get(username).setAction(buildAction);
            playerMap.get(username).useAction();
            //parser.parseMessageFromServerToClient(new PlayerBuildResponse("OK", username, game.cloneGameBoard()));
        } catch (IllegalActionException | IOException e) {
            parser.parseMessageFromServerToClient(new PlayerBuildResponse("Illegal build", username, game.cloneGameBoard()));
        }
    }

    public void passTurn(String username) {
        try{
            game.endTurn();
            // parser.parseMessageFromServerToClient(new EndTurnResponse("OK", username));
        } catch (IllegalEndingTurnException | IOException e) {
            parser.parseMessageFromServerToClient(new EndTurnResponse("You cannot end turn now", username, null));
        }
    }


    @Override
    public void update(Message updateMessage) {
        parser.parseMessageFromServerToClient(updateMessage);
    }
}
