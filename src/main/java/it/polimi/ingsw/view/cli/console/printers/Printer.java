package it.polimi.ingsw.view.cli.console.printers;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.graphics.components.PrintableObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Console Printing Utilities
 */
public abstract class Printer {

    public static final int SCENE_WIDTH = 190;
    public static final int SCENE_HEIGHT = 62;

    /**
     * The UI which created the printer
     */
    protected final CLI cli;
    protected BoardUtils boardUtils;
    protected final Console console;
    protected final PrintableObject mainLogo;


    protected final  Properties properties = new Properties();

    /**
     * Default constructor
     *
     * @param cli the view which created the printer
     * @param console the Console object containing this
     */
    protected Printer(CLI cli, Console console) throws IOException {
        this.cli = cli;
        this.console = console;
        properties.loadFromXML(this.getClass().getResourceAsStream(this.getClass().getSimpleName() + ".xml"));
        mainLogo = PrintableObject.load(this, properties.getProperty("logoPath"), 151, 24);
        boardUtils = setBoardUtils();
    }

    /**
     * <i>console</i> getter
     *
     * @return the console which spawned this
     */
    public Console getConsole() {
        return console;
    }

    /**
     * Shows an error message
     *
     * @param errorMsg the error message
     */
    public abstract void printError(String errorMsg);

    /**
     * Shows a success message
     *
     * @param msg the message
     */
    public abstract void printMessage(String msg);

    /**
     * Shows the logo
     */
    public abstract void printStartingScreen();

    /**
     * Asks the user if it wants to reload a previously saved address/username combo
     */
    public abstract void askToReloadSettings();

    /**
     * Shows the user the saved address/username combos
     *
     * @param options the address/username combos
     */
    public abstract void showSavedSettings(List<String> options);

    /**
     * Asks the user to insert the server address
     */
    public abstract void askIp();

    /**
     * Asks the user to insert its username
     */
    public abstract void askUsername();

    /**
     * Asks the user to choose whether to join or create a lobby
     *
     * @param options a list of possible options
     */
    public abstract void lobbyOptions(List<String> options);

    /**
     * Asks the user to choose a name for the lobby to be created
     */
    public abstract void askLobbyName();

    /**
     * Asks the user to choose the lobby size
     */
    public abstract void askLobbySize();

    /**
     * Asks the user which lobby to join
     *
     * @param lobbiesAvailable a map containing the lobbies available and their relative information
     */
    public abstract void chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable);

    /**
     * Asks the user if it wants to reload an existing saved match
     */
    public abstract void chooseToReloadMatch();

    /**
     * Asks the user to choose the gods for the game
     *
     * @param allGods the list of available gods
     * @param size    the number of players
     */
    public abstract void chooseGameGods(List<GodData> allGods, int size);

    /**
     * Asks the user  to choose its personal god for the game
     *
     * @param possibleGods a list containing the available gods
     */
    public abstract void chooseUserGod(List<GodData> possibleGods);

    /**
     * Asks the user which player will play first
     *
     * @param players the players in game
     */
    public abstract void chooseStartingPlayer(List<String> players);

    /**
     * Prints the game board on the screen
     *
     * @param gameBoard the board to print
     */
    public void showGameBoard(List<Cell> gameBoard) {
        boardUtils.updateCachedBoard(gameBoard);
        boardUtils.showGameBoard();
    }

    /**
     * Creates a BoardUtils object, based on the printer calling it
     *
     * @return a boardUtils object
     */
    protected abstract BoardUtils setBoardUtils();

    /**
     * Updates information about the game and the players
     *
     * @param board   the game board
     * @param players information about the players
     */
    public abstract void updateGameData(List<Cell> board, List<PlayerData> players);

    /**
     * Prints the game board on the screen
     *
     * @param gameBoard the board to print
     */
    public void showGameBoard(List<Cell> gameBoard, List<Cell> toHighlight) {
        boardUtils.updateCachedBoard(gameBoard);
        boardUtils.showGameBoard(toHighlight);
    }

    /**
     * Asks the user to place its worker on the board
     */
    public abstract void placeWorker();

    /**
     * Asks the user to pick a worker
     * @param cells the cells containing the player's workers
     */
    public abstract void chooseWorker(List<Cell> cells);

    /**
     * Highlights the user's workers
     * @param cells the cells containing the user's workers
     */
    protected void highlightWorkers(List<Cell> cells) {
        boardUtils.highlightWorkers(cells);
    }

    /**
     * Asks the user which action to perform
     *
     * @param possibleActions a list of possible actions
     */
    public abstract void chooseAction(List<PossibleActions> possibleActions);

    /**
     * Asks the user to select a cell to move its current worker to
     *
     * @param gameBoard     the current game board
     * @param walkableCells the cells on which the worker can be moved to
     */
    public abstract void moveAction(List<Cell> gameBoard, List<Cell> walkableCells);

    /**
     * Asks the user to select a cell to build on
     *
     * @param gameBoard      the current game board
     * @param buildableCells the cells on which the worker can build
     */
    public abstract void buildAction(List<Cell> gameBoard, List<Cell> buildableCells);

    /**
     * Asks the user which block to build on a cell
     *
     * @param buildableBlocks the possible blocks (always more than one)
     */
    public abstract void chooseBlockToBuild(List<Block> buildableBlocks);
}
