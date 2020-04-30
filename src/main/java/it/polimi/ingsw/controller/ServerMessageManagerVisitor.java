package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.request.fromClientToServer.*;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseInitialGodsResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseStartingPlayerResponse;
import it.polimi.ingsw.network.message.response.fromClientToServer.ChooseYourGodResponse;

import javax.swing.*;

public interface ServerMessageManagerVisitor {

    void chooseInitialGods(ChooseInitialGodsResponse message);
    void chooseGod(ChooseYourGodResponse message);
    void chooseStartingPlayer(ChooseStartingPlayerResponse message);
    void selectWorker(SelectWorkerRequest message);
    void walkableCells(WalkableCellsRequest message);
    void buildableCells(BuildableCellsRequest message);
    void selectCellToBuild(SelectBuildingCellRequest message);
    void managePlayerMove(PlayerMoveRequest message);
    void managePlayerBuild(PlayerBuildRequest message);
    void addWorkerOnBoard(AddWorkerRequest message);
    void endTurn(EndTurnRequest message);



}
