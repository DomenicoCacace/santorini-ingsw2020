package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.response.fromClientToServer.*;

public interface ServerMessageManagerVisitor {

    // server and lobby logging, handled by the VirtualClient
    default void login(LoginRequest message) {cannotHandleMessage(message);}
    default void joinLobby(JoinLobbyRequest message) {cannotHandleMessage(message);}
    default void createLobby(CreateLobbyRequest message) {cannotHandleMessage(message);}


    // game setup related, handled by the MessageManagerParser
    default void chooseInitialGods(ChooseInitialGodsResponse message) {cannotHandleMessage(message);}
    default void chooseGod(ChooseYourGodResponse message) {cannotHandleMessage(message);}
    default void chooseStartingPlayer(ChooseStartingPlayerResponse message) {cannotHandleMessage(message);}
    default void onMatchReloadResponse(ChooseToReloadMatchResponse message) {cannotHandleMessage(message);}

    // strictly game related, handled by the MessageManagerParser
    default void addWorkerOnBoard(AddWorkerRequest message) {cannotHandleMessage(message);}
    default void selectWorker(SelectWorkerRequest message) {cannotHandleMessage(message);}
    default void walkableCells(WalkableCellsRequest message) {cannotHandleMessage(message);}
    default void buildableCells(BuildableCellsRequest message) {cannotHandleMessage(message);}
    default void managePlayerMove(PlayerMoveRequest message) {cannotHandleMessage(message);}
    default void selectCellToBuild(SelectBuildingCellRequest message) {cannotHandleMessage(message);}
    default void managePlayerBuild(PlayerBuildRequest message) {cannotHandleMessage(message);}
    default void endTurn(EndTurnRequest message) {cannotHandleMessage(message);}


    void cannotHandleMessage(Message message);
}

