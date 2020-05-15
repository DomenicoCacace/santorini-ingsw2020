package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.request.fromServerToClient.*;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;

public interface ClientMessageManagerVisitor {

    void onLogin(LoginResponse message);
    void joinLobby(UserJoinedLobbyEvent message);
    void createLobby(LobbyCreatedEvent message);
    void onPlayerMove(PlayerMoveEvent message);
    void onPlayerBuild(PlayerBuildEvent message);
    void onTurnEnd(EndTurnEvent message);
    void chooseYourWorkerPosition(ChooseWorkerPositionRequest message);
    void onWorkerAdd(WorkerAddedEvent message);
    void chooseInitialGods(ChooseInitialGodsRequest message);
    void onWinnerDeclared(WinnerDeclaredEvent message);
    void onPlayerRemoved(PlayerRemovedEvent message);
    void chooseYourGod(ChooseYourGodRequest message);
    void onGodChosen(ChosenGodsEvent message);
    void chooseStartingPlayer(ChooseStartingPlayerRequest message);
    void onWorkerSelected(WorkerSelectedResponse message);
    void onWalkableCellsReceived(WalkableCellsResponse message);
    void onBuildableCellsReceived(BuildableCellsResponse message);
    void onBuildingCellSelected(PossibleBuildingBlockResponse message);
    void onGameStart(GameStartEvent message);
    void chooseToReloadMatch(ChooseToReloadMatchRequest message);
    void onGameBoardUpdate(GameBoardUpdate gameBoardUpdate);
    void onMovedToWaitingRoom(MovedToWaitingRoomResponse message);
}
