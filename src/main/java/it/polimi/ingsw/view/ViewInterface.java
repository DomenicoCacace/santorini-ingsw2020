package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;

import java.util.List;

public interface ViewInterface {

    String askIP();

    String askUsername();

    void gameStartScreen(List<Cell> gameBoard);

    //void showLobby(); TODO: implement

    Cell chooseWorker();

    boolean chooseMatchReload();

    Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells);

    Cell buildAction(List<Cell> gameBoard, List<Cell> buildableCells);

    Block chooseBlockToBuild(List<Block> buildableBlocks);

    void showGameBoard(List<Cell> gameBoard);

    void showErrorMessage(String error);

    void showSuccessMessage(String message);

    Cell placeWorker();

    GodData chooseUserGod(List<GodData> possibleGods);

    List<GodData> chooseGameGods(List<GodData> allGods, int size);

    int choosePlayersNumber();

    String chooseStartingPlayer(List<String> players);

    PossibleActions chooseAction(List<PossibleActions> possibleActions);


}
