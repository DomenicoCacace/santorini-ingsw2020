package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.network.message.MessageRequest;

import java.util.List;

public class ServerController {

    private Game game;
    private List<Player> players;

    public MessageRequest addWorker(String username, Cell cell){

    }

    public MessageRequest handleMoveAction(MoveAction moveAction){

    }

    public MessageRequest handleBuildAction(BuildAction buildAction){

    }

    public MessageRequest passTurn(){

    }

}
