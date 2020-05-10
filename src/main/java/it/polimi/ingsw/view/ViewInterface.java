package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;

import java.util.List;
import java.util.Map;

public interface ViewInterface {

    List<String> askToReloadLastSettings(List<String> savedUsers);
    boolean chooseMatchReload();

    void printLogo();
    String askIP();
    String askUsername();

    void gameStartScreen(List<Cell> gameBoard);
    String lobbyOptions(List<String> options);  //FIXME: write decent code
    String askLobbyName();
    int askLobbySize();
    String chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable);
    Cell chooseWorker();
    Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells);
    Cell buildAction(List<Cell> gameBoard, List<Cell> buildableCells);
    Block chooseBlockToBuild(List<Block> buildableBlocks);
    GodData chooseUserGod(List<GodData> possibleGods);
    List<GodData> chooseGameGods(List<GodData> allGods, int size);

    String chooseStartingPlayer(List<String> players);
    Cell placeWorker();

    PossibleActions chooseAction(List<PossibleActions> possibleActions);
    void showGameBoard(List<Cell> gameBoard);
    void showErrorMessage(String error);
    void showSuccessMessage(String message);

}
