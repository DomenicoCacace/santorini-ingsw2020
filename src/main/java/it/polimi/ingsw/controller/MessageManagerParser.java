package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseToReloadMatchResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;
import it.polimi.ingsw.network.server.Lobby;

public class MessageManagerParser implements ServerMessageManagerVisitor {

    private final Lobby lobby;
    private ServerController serverController;


    public MessageManagerParser(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }
    //Client -> sends message request -> virtualClient -> lobby -> parseMessageFromClientToServer(MessageRequest) -> message parser will call methods of GameController and Lobby
    //Controller will call methods of model -> the model will return responses to the Controller -> Controller will pass the message to the Parser with parseMessageFromServerToClient
    //Parser will pass messages to the Lobby -> Lobby will pass messages to the virtualClient -> Client

    public void parseMessageFromServerToClient(Message message) {
        lobby.sendMessage(message.getUsername(), message);
    }

    public void endGame() {
        lobby.endGame();
    }

    //This methods replace the switch with a visitor pattern


    @Override
    public void onMatchReloadResponse(ChooseToReloadMatchResponse message){
        lobby.reloadMatch(message.wantToReload());
    }

    @Override
    public void chooseInitialGods(ChooseInitialGodsResponse message) {
        lobby.chooseGods(message.getPayload());
    }

    @Override
    public void chooseGod(ChooseYourGodResponse message) {
        lobby.assignGod(message.getUsername(), message.getGod());
    }

    @Override
    public void chooseStartingPlayer(ChooseStartingPlayerResponse message) {
        lobby.selectStartingPlayer(message.getPayload());
    }

    @Override
    public void selectWorker(SelectWorkerRequest message) {
        serverController.selectWorker(message.getUsername(), message.getTargetWorker());
    }

    @Override
    public void walkableCells(WalkableCellsRequest message) {
        serverController.obtainWalkableCells(message.getUsername());
    }

    @Override
    public void buildableCells(BuildableCellsRequest message) {
        serverController.obtainBuildableCells(message.getUsername());
    }

    @Override
    public void selectCellToBuild(SelectBuildingCellRequest message) {
        serverController.selectBuildingCell(message.getUsername(), message.getSelectedCell());
    }

    @Override
    public void managePlayerMove(PlayerMoveRequest message) {
        MoveAction moveAction = new MoveAction(message.getTargetWorker(), message.getTargetCell());
        serverController.handleMoveAction(message.getUsername(), moveAction);
    }

    @Override
    public void managePlayerBuild(PlayerBuildRequest message) {
        BuildAction buildAction = new BuildAction(message.getTargetWorker(), message.getTargetCell(), message.getTargetBlock());
        serverController.handleBuildAction(message.getUsername(), buildAction);
    }

    @Override
    public void addWorkerOnBoard(AddWorkerRequest message) {
        serverController.addWorker(message.getUsername(), message.getTargetCell());
    }

    @Override
    public void endTurn(EndTurnRequest message) {
        serverController.passTurn(message.getUsername());
    }

    @Override
    public void cannotHandleMessage(Message message) {
        //do nothing
    }
}
