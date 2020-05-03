package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.request.fromServerToClient.*;
import it.polimi.ingsw.network.message.response.fromServerToClient.*;

public interface ClientMessageManagerVisitor {

    void onLogin(LoginResponse message);
    void onPlayerMove(PlayerMoveResponse message);
    void onPlayerBuild(PlayerBuildResponse message);
    void onTurnEnd(EndTurnResponse message);
    void chooseYourWorkerPosition(ChooseWorkerPositionRequest message);
    void onWorkerAdd(AddWorkerResponse message);
    void chooseInitialGods(ChooseInitialGodsRequest message);
    void onWinnerDeclared(WinnerDeclaredResponse message);
    void onPlayerRemoved(PlayerRemovedResponse message);
    void choosePlayerNumber(ChooseNumberOfPlayersRequest message);
    void chooseYourGod(ChooseYourGodRequest message);
    void onGodChosen(ChosenGodsResponse message);
    void chooseStartingPlayer(ChooseStartingPlayerRequest message);
    void onWorkerSelected(SelectWorkerResponse message);
    void onWalkableCellsReceived(WalkableCellsResponse message);
    void onBuildableCellsReceived(BuildableCellsResponse message);
    void onBuildingCellSelected(SelectBuildingCellResponse message);
    void onGameStart(GameStartResponse message);
    void onQuit(QuitRequest message);
    void chooseToReloadMatch(ChooseToReloadMatchRequest message);
    void onGameBoardUpdate(GameBoardMessage message);

}
