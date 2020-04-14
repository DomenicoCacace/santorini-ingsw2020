package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.action.*;
import it.polimi.ingsw.network.message.request.*;
import it.polimi.ingsw.network.message.response.MessageResponse;
import it.polimi.ingsw.network.server.Lobby;

import java.io.IOException;

public class MessageParser {

    private Lobby lobby;
    private ServerController serverController;

    public void parseMessageFromClientToServer(MessageRequest message) throws IOException, InterruptedException {
        switch (message.content) {
            case LOGIN:
                lobby.addUser(message.username);
                break;
            case PLAYER_MOVE:
                MoveAction moveAction = new MoveAction(((PlayerMoveRequest) message).targetWorker , ((PlayerMoveRequest) message).targetCell);
                // serverController.handleMoveAction(moveAction);
                break;
            case PLAYER_BUILD:
                BuildAction buildAction = new BuildAction(((PlayerBuildRequest) message).targetWorker , ((PlayerBuildRequest) message).targetCell, ((PlayerBuildRequest) message).targetBlock);
                // serverController.handleBuildAction(buildAction);
                break;
            case ADD_WORKER:
                // serverController.addWorker(message.username, ((AddWorkerRequest) message).targetCell);
                break;
            case ASSIGN_GOD:
                lobby.assignGod(message.username, ((AssignGodRequest) message).god);
                break;
            case END_TURN:
                // serverController.passTurn(message.username);
                break;
            case CHOOSE_INITIAL_GODS:
                lobby.chooseGods(((ChooseInitialGodsRequest) message).gods);
                break;

            default: break;
        }


        //calling methods of lobby e controller
    }

    public void parseMessageFromServerToClient(MessageResponse message){
        //calling methods on the network
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }
}
