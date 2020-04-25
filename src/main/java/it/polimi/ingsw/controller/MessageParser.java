package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.network.server.Lobby;
import it.polimi.ingsw.network.server.Server;

import java.io.IOException;

public class MessageParser {

    private Lobby lobby;
    private ServerController serverController;
    private final Server server;

    public MessageParser(Server server) {
        this.server = server;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public void parseMessageFromClientToServer(Message message) throws IOException, InterruptedException {
        switch (message.getContent()) {
            case LOGIN:
                lobby.addUser(message.getUsername());
                break;
            case CHOOSE_INITIAL_GODS:
                lobby.chooseGods(((ChooseInitialGodsResponse) message).getPayload());
                break;
            case CHOOSE_GOD:
                lobby.assignGod(message.getUsername(), ((ChooseYourGodResponse) message).getGod());
                break;
            case STARTING_PLAYER:
                lobby.selectStartingPlayer(((ChooseStartingPlayerResponse) message).getPayload());
                break;
            case SELECT_WORKER:
                serverController.selectWorker(message.getUsername(), ((SelectWorkerRequest) message).getTargetWorker());
                break;
            case WALKABLE_CELLS:
                serverController.obtainWalkableCells(message.getUsername());
                break;
            case BUILDABLE_CELLS:
                serverController.obtainBuildableCells(message.getUsername());
                break;
            case SELECT_BUILDING_CELL:
                serverController.selectBuildingCell(message.getUsername(), ((SelectBuildingCellRequest) message).getSelectedCell());
                break;
            case PLAYER_MOVE:
                MoveAction moveAction = new MoveAction(((PlayerMoveRequest) message).getTargetWorker(), ((PlayerMoveRequest) message).getTargetCell());
                serverController.handleMoveAction(message.getUsername(), moveAction);
                break;
            case PLAYER_BUILD:
                BuildAction buildAction = new BuildAction(((PlayerBuildRequest) message).getTargetWorker(), ((PlayerBuildRequest) message).getTargetCell(), ((PlayerBuildRequest) message).getTargetBlock());
                serverController.handleBuildAction(message.getUsername(), buildAction);
                break;
            case ADD_WORKER:
                serverController.addWorker(message.getUsername(), ((AddWorkerRequest) message).getTargetCell());
                break;
            case END_TURN:
                serverController.passTurn(message.getUsername());
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + message.getContent());
        }
    }

    //Client -> sends message request -> virtualClient -> server -> parseMessageFromClientToServer(MessageRequest) -> message parser will call methods of ServerController and Lobby
    //Controller will call methods of model -> the model will return responses to the Controller -> Controller will pass the message to the Parser with parseMessageFromServerToClient
    //Parser will pass messages to the Server -> Server will pass messages to the virtualClient -> Client

    public void parseMessageFromServerToClient(Message message) {
        //Passerà messaggi al server il quale farà virtualClient.notify(message)
        server.send(message.getUsername(), message);
    }

}
