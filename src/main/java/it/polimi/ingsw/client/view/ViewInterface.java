package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.view.inputManagers.InputManager;
import it.polimi.ingsw.shared.dataClasses.*;

import java.util.List;
import java.util.Map;

/**
 * Common methods for the UI
 */
public interface ViewInterface {

    //FIXME
    default void printCol(){
        // defined by the children, if needed
    }

    /**
     * Sets the {@linkplain InputManager} to parse the inputs
     *
     * @param inputManager te inputManager to use
     */
    void setInputManager(InputManager inputManager);

    /**
     * Shows an error message
     *
     * @param error the error message
     */
    void showErrorMessage(String error);

    /**
     * Shows a success message
     *
     * @param message the message
     */
    void showSuccessMessage(String message);

    /**
     * Prints the start screen
     */
    void printLogo();

    /**
     * Asks the user if it wants to reload a previously saved address/username combo
     *
     * @param savedUsers the address/username combos
     */
    void askToReloadLastSettings(List<String> savedUsers);

    /**
     * Shows the user the address/username combos
     *
     * @param options the address/username combos
     */
    default void printUserServerCombos(List<String> options){
        // defined by the children, if needed
    }

    /**
     * Asks the user the server address to connect to
     */
    void askIP();

    /**
     * Asks the user the username it wants to use
     */
    void askUsername();

    /**
     * Asks the user if it wants to join or create a lobby
     *
     * @param options the possible options
     */
    void lobbyOptions(List<String> options);

    /**
     * Asks the user the name for the lobby to be created
     */
    void askLobbyName();

    /**
     * Asks the user the number of players for its lobby
     */
    void askLobbySize();

    /**
     * Asks the user which lobby it wants to join
     *
     * @param lobbiesAvailable a map containing lobbies and their info
     */
    void chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable);

    /**
     * Asks the user if it wants to reload an existing saved match
     */
    void chooseMatchReload();

    /**
     * Asks the lobby owner to choose the gods for the game
     *
     * @param allGods the list of available gods
     * @param size the number of players
     */
    void chooseGameGods(List<GodData> allGods, int size);

    /**
     * Asks the user to pick its god
     *
     * @param possibleGods a list containing the available gods
     */
    void chooseUserGod(List<GodData> possibleGods);

    /**
     * Asks the user which player will play first
     *
     * @param players the list of players
     */
    void chooseStartingPlayer(List<String> players);

    /**
     * Sets up the game board and its graphics
     *
     * @param gameBoard the starting game board
     * @param playerData the players data
     */
    void gameStartScreen(List<Cell> gameBoard, List<PlayerData> playerData);

    /**
     * Prints the game board on the screen
     *
     * @param gameBoard the board to print
     */
    void showGameBoard(List<Cell> gameBoard);

    /**
     * Refreshes the game screen
     *
     * @param gameBoard the board to start with
     * @param players information about the players
     */
    void gameBoardUpdate(List<Cell> gameBoard, List<PlayerData> players);

    /**
     * Asks the user to place its worker on the board
     */
    void placeWorker();

    /**
     * Asks the user to choose a worker
     *
     * @param cells the cells containing the player's workers
     */
    void chooseWorker(List<Cell> cells);

    /**
     * Asks the user which action to perform
     *
     * @param possibleActions a list of possible actions
     */
    void chooseAction(List<PossibleActions> possibleActions);

    /**
     * Asks the user to select a cell to move its current worker on
     *
     * @param gameBoard the current game board
     * @param walkableCells the cells on which the worker can be moved to
     */
    void moveAction(List<Cell> gameBoard, List<Cell> walkableCells);

    /**
     * Asks the user to select a cell to build on
     *
     * @param gameBoard the current game board
     * @param buildableCells the cells on which the worker can build
     */
    void buildAction(List<Cell> gameBoard, List<Cell> buildableCells);

    /**
     * Asks the user which block to build on a cell
     *
     * @param buildableBlocks the possible blocks (always more than one)
     */
    void chooseBlockToBuild(List<Block> buildableBlocks);
}