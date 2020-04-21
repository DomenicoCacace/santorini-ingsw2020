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
        switch (message.content) {
            case LOGIN:
                lobby.addUser(message.username);
                break;
            case CHOOSE_INITIAL_GODS:
                lobby.chooseGods(((ChooseInitialGodsResponse) message).payload);
                break;
            case CHOOSE_GOD:
                lobby.assignGod(message.username, ((ChooseYourGodResponse) message).god);
                break;
            case STARTING_PLAYER:
                lobby.selectStartingPlayer(((ChooseStartingPlayerResponse) message).payload);
                break;
            case SELECT_WORKER:
                serverController.selectWorker(message.username, ((SelectWorkerRequest) message).targetWorker);
                break;
            case WALKABLE_CELLS:
                serverController.obtainWalkableCells(message.username);
                break;
            case BUILDABLE_CELLS:
                serverController.obtainBuildableCells(message.username);
                break;
            case PLAYER_MOVE:
                MoveAction moveAction = new MoveAction(((PlayerMoveRequest) message).targetWorker, ((PlayerMoveRequest) message).targetCell);
                serverController.handleMoveAction(message.username, moveAction);
                break;
            case PLAYER_BUILD:
                BuildAction buildAction = new BuildAction(((PlayerBuildRequest) message).targetWorker, ((PlayerBuildRequest) message).targetCell, ((PlayerBuildRequest) message).targetBlock);
                serverController.handleBuildAction(message.username, buildAction);
                break;
            case ADD_WORKER:
                serverController.addWorker(message.username, ((AddWorkerRequest) message).targetCell);
                break;
            case END_TURN:
                serverController.passTurn(message.username);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + message.content);
        }
    }

    //Client -> sends message request -> virtualClient -> server -> parseMessageFromClientToServer(MessageRequest) -> message parser will call methods of ServerController and Lobby
    //Controller will call methods of model -> the model will return responses to the Controller -> Controller will pass the message to the Parser with parseMessageFromServerToClient
    //Parser will pass messages to the Server -> Server will pass messages to the virtualClient -> Client

    public void parseMessageFromServerToClient(Message message) {
        //Passerà messaggi al server il quale farà virtualClient.notify(message)
        server.send(message.username, message);
    }

}
