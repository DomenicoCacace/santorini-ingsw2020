package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.view.inputManagers.InputManager;

import java.util.List;
import java.util.Map;

public interface ViewInterface {

    void askToReloadLastSettings(List<String> savedUsers);

    void chooseMatchReload();

    void setInputManager(InputManager manager);

    void printLogo();

    void askIP();

    void askUsername();

    void gameStartScreen(List<Cell> gameBoard, List<PlayerData> playerData);

    void lobbyOptions(List<String> options);  //FIXME: write decent code

    void askLobbyName();

    void askLobbySize();

    void chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable);

    void chooseWorker(List<Cell> cells);

    void moveAction(List<Cell> gameBoard, List<Cell> walkableCells);

    void buildAction(List<Cell> gameBoard, List<Cell> buildableCells);

    void chooseBlockToBuild(List<Block> buildableBlocks);

    void chooseUserGod(List<GodData> possibleGods);

    void chooseGameGods(List<GodData> allGods, int size);

    void chooseStartingPlayer(List<String> players);

    //void showLobbyInfo(List<String> info);

    void printCol();

    void placeWorker();

    void chooseAction(List<PossibleActions> possibleActions);

    void showGameBoard(List<Cell> gameBoard);

    void initGameScreen(List<Cell> gameBoard, List<PlayerData> players);

    void showErrorMessage(String error);

    void printOptions(List<String> options);

    void showSuccessMessage(String message);

}
