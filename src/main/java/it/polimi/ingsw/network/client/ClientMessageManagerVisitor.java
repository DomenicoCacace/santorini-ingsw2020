package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.request.fromServerToClient.*;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;

public interface ClientMessageManagerVisitor {

    default void onLogin(LoginResponse message) {cannotHandleMessage(message);}
    default void joinLobby(JoinLobbyResponse message) {cannotHandleMessage(message);}
    default void createLobby(CreateLobbyResponse message) {cannotHandleMessage(message);}
    default void onPlayerMove(PlayerMoveResponse message) {cannotHandleMessage(message);}
    default void onPlayerBuild(PlayerBuildResponse message) {cannotHandleMessage(message);}
    default void onTurnEnd(EndTurnResponse message) {cannotHandleMessage(message);}
    default void chooseYourWorkerPosition(ChooseWorkerPositionRequest message) {cannotHandleMessage(message);}
    default void onWorkerAdd(AddWorkerResponse message) {cannotHandleMessage(message);}
    default void chooseInitialGods(ChooseInitialGodsRequest message) {cannotHandleMessage(message);}
    default void onWinnerDeclared(WinnerDeclaredResponse message) {cannotHandleMessage(message);}
    default void onPlayerRemoved(PlayerRemovedResponse message) {cannotHandleMessage(message);}
    default void chooseYourGod(ChooseYourGodRequest message) {cannotHandleMessage(message);}
    default void onGodChosen(ChosenGodsResponse message) {cannotHandleMessage(message);}
    default void chooseStartingPlayer(ChooseStartingPlayerRequest message) {cannotHandleMessage(message);}
    default void onWorkerSelected(SelectWorkerResponse message) {cannotHandleMessage(message);}
    default void onWalkableCellsReceived(WalkableCellsResponse message) {cannotHandleMessage(message);}
    default void onBuildableCellsReceived(BuildableCellsResponse message) {cannotHandleMessage(message);}
    default void onBuildingCellSelected(SelectBuildingCellResponse message) {cannotHandleMessage(message);}
    default void onGameStart(GameStartResponse message) {cannotHandleMessage(message);}
    default void onQuit(QuitRequest message) {cannotHandleMessage(message);}
    default void chooseToReloadMatch(ChooseToReloadMatchRequest message) {cannotHandleMessage(message);}
    default void onGameBoardUpdate(GameBoardResponse message) {cannotHandleMessage(message);}
    default void onMovedToWaitingRoom(MovedToWaitingRoomResponse message) {cannotHandleMessage(message);}
    default void lobbyRefresh(AvailableLobbiesResponse message) {cannotHandleMessage(message);}

    void cannotHandleMessage(MessageFromServerToClient messageFromServerToClient);
}
