package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface ViewInterface {

    void stopInput();

    List<String> askToReloadLastSettings(List<String> savedUsers) throws TimeoutException, InterruptedException;
    boolean chooseMatchReload() throws TimeoutException, InterruptedException;

    void printLogo();
    String askIP() throws TimeoutException, InterruptedException;
    String askUsername() throws TimeoutException, InterruptedException;

    void gameStartScreen(List<Cell> gameBoard);
    String lobbyOptions(List<String> options) throws TimeoutException, InterruptedException;  //FIXME: write decent code
    String askLobbyName() throws TimeoutException, InterruptedException;
    int askLobbySize() throws TimeoutException, InterruptedException;
    String chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) throws TimeoutException, InterruptedException;
    Cell chooseWorker() throws TimeoutException, InterruptedException;
    Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells) throws TimeoutException, InterruptedException;
    Cell buildAction(List<Cell> gameBoard, List<Cell> buildableCells) throws TimeoutException, InterruptedException;
    Block chooseBlockToBuild(List<Block> buildableBlocks) throws TimeoutException, InterruptedException;
    GodData chooseUserGod(List<GodData> possibleGods) throws TimeoutException, InterruptedException;
    List<GodData> chooseGameGods(List<GodData> allGods, int size) throws TimeoutException, InterruptedException;

    String chooseStartingPlayer(List<String> players) throws TimeoutException, InterruptedException;
    Cell placeWorker() throws TimeoutException, InterruptedException;

    PossibleActions chooseAction(List<PossibleActions> possibleActions) throws TimeoutException, InterruptedException;
    void showGameBoard(List<Cell> gameBoard);
    void showErrorMessage(String error);
    void showSuccessMessage(String message);

}
