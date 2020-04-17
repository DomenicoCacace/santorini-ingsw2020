package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.network.message.request.MessageRequest;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.response.MessageResponse;
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


    public void parseMessageFromClientToServer(MessageRequest message) throws IOException, InterruptedException {
        switch (message.content) {
            case LOGIN:
                lobby.addUser(message.username);
                break;
            case CHOOSE_GOD:
                lobby.assignGod(message.username, ((AssignGodRequest) message).god);
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
                MoveAction moveAction = new MoveAction(((PlayerMoveRequest) message).targetWorker , ((PlayerMoveRequest) message).targetCell);
                serverController.handleMoveAction(message.username, moveAction);
                break;
            case PLAYER_BUILD:
                BuildAction buildAction = new BuildAction(((PlayerBuildRequest) message).targetWorker , ((PlayerBuildRequest) message).targetCell, ((PlayerBuildRequest) message).targetBlock);
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


        //calling methods of lobby e controller
    }

    //Client -> sends message request -> virtualClient -> server -> parseMessageFromClientToServer(MessageRequest) -> message parser will call methods of ServerController and Lobby
    //Controller will call methods of model -> the model will return responses to the Controller -> Controller will pass the message to the Parser with parseMessageFromServerToClient
    //Parser will pass messages to the Server -> Server will pass messages to the virtualClient -> Client

    public void parseMessageFromServerToClient(MessageResponse message){
        //Passerà messaggi al server il quale farà virtualClient.notify(message)
        switch(message.content){
            case CHOOSE_INITIAL_GODS:

                break;
            case CHOSEN_GODS:

                break;
            case ASSIGN_GOD:

                break;
            case GAME_START:

                break;
            case ADD_WORKER:

                break;
            case SELECT_WORKER:

                break;
            case WALKABLE_CELLS:

                break;
            case BUILDABLE_CELLS:

                break;
            case PLAYER_MOVE:

                break;
            case PLAYER_BUILD:

                break;
            case PLAYER_REMOVED:

                break;
            case END_TURN:

                break;
            case WINNER:
                //game must finish
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.content);
        }
    }

    public void parseMessageFromServerToClient(MessageRequest message) throws IOException {
        switch (message.content) {
            case CHOOSE_INITIAL_GODS:
                server.getVirtualClient(message.username).notify(message);
                break;
            case CHOOSE_GOD:
                server.sendToEveryone(message);
                break;
        }
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }
}
