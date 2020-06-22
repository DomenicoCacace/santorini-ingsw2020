package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.network.server.Lobby;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.console.Console;
import it.polimi.ingsw.view.cli.console.printers.Printer;
import it.polimi.ingsw.view.cli.console.printers.basicPrinter.BasicPrinter;
import it.polimi.ingsw.view.cli.console.printers.fancyPrinter.FancyPrinter;
import it.polimi.ingsw.view.inputManagers.InputManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Command Line Interface manager
 */
public class CLI implements ViewInterface {
    protected static Lock lock = new ReentrantLock();
    private final Printer printer;
    private InputManager inputManager;
    private final boolean enableRawMode;

    /**
     * Default constructor
     *
     * @param enableRawMode determines if the terminal in which the program is running allows non-canonical mode
     */
    public CLI(boolean enableRawMode) throws IOException {
        this.enableRawMode = enableRawMode;
        if (enableRawMode)
            printer = new FancyPrinter(Console.init(this), this);
        else
            printer = new BasicPrinter(Console.init(this), this);
        new Thread(this::readInput).start();
    }

    /**
     * Determines if the terminal running the application allows non-canonical mode
     *
     * @return true if the terminal allows non-canonical mode, false otherwise
     */
    public boolean enableRawMode() {
        return this.enableRawMode;
    }

    /**
     * Sets the {@linkplain InputManager} to parse the inputs
     *
     * @param inputManager te inputManager to use
     */
    @Override
    public void setInputManager(InputManager inputManager) {
        lock.lock();
        this.inputManager = inputManager;
        lock.unlock();
    }

    /**
     * Passes a string to the inputManager
     *
     * @param input the inputString
     */
    public void evaluateInput(String input) {
        lock.lock();
        inputManager.manageInput(input);
        lock.unlock();
    }

    private void readInput() {
        while (true) {
            if (Console.in.hasNext()) {
                lock.lock();
                inputManager.manageInput(Console.in.nextLine());
                lock.unlock();
            }
        }
    }

    /**
     * Shows an error message
     *
     * @param error the error message
     */
    @Override
    public void showErrorMessage(String error) {
        printer.printError(error);
    }

    /**
     * Shows a success message
     *
     * @param message the message
     */
    @Override
    public void showSuccessMessage(String message) {
        printer.printMessage(message);
    }

    /**
     * Prints the start screen
     */
    @Override
    public void printLogo() {
        printer.printStartingScreen();
    }

    /**
     * Asks the user if it wants to reload a previously saved address/username combo
     *
     * @param savedUsers the address/username combos
     */
    @Override
    public void askToReloadLastSettings(List<String> savedUsers) {
        printer.askToReloadSettings();
    }

    /**
     * Shows the user the address/username combos
     *
     * @param options the address/username combos
     */
    @Override
    public void printUserServerCombos(List<String> options) {
        printer.showSavedSettings(options);
    }

    /**
     * Asks the user the server address to connect to
     */
    @Override
    public void askIP() {
        printer.askIp();
    }

    /**
     * Asks the user the username it wants to use
     */
    @Override
    public void askUsername() {
        printer.askUsername();
    }

    /**
     * Asks the user if it wants to join or create a lobby
     *
     * @param options the possible options
     */
    @Override
    public void lobbyOptions(List<String> options) {
        printer.lobbyOptions(options);
    }

    /**
     * Asks the user the name for the lobby to be created
     */
    @Override
    public void askLobbyName() {
        printer.askLobbyName();
    }

    /**
     * Asks the user the number of players for its lobby
     */
    @Override
    public void askLobbySize() {
        printer.askLobbySize();
    }

    /**
     * Asks the user which lobby it wants to join
     *
     * @param lobbiesAvailable a map containing lobbies and their info
     * @see Lobby#lobbyInfo()
     */
    @Override
    public void chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) {
        printer.chooseLobbyToJoin(lobbiesAvailable);
    }

    /**
     * Asks the user if it wants to reload an existing saved match
     */
    @Override
    public void chooseMatchReload() {
        printer.chooseToReloadMatch();
    }

    /**
     * Asks the user to choose the gods for the game
     *
     * @param allGods the list of available gods
     * @param size    the number of players
     */
    @Override
    public void chooseGameGods(List<GodData> allGods, int size) {
        printer.chooseGameGods(allGods, size);
    }

    /**
     * Asks the user to choose its personal god for the game
     *
     * @param possibleGods a list containing the available gods
     */
    @Override
    public void chooseUserGod(List<GodData> possibleGods) {
        printer.chooseUserGod(possibleGods);
    }

    /**
     * Asks the user which player will play first
     *
     * @param players the list of players
     */
    @Override
    public void chooseStartingPlayer(List<String> players) {
        printer.chooseStartingPlayer(players);
    }

    /**
     * Updates information about the players and the game
     *
     * @param gameBoard the starting game board
     * @param playerData the players data
     */
    public void gameStartScreen(List<Cell> gameBoard, List<PlayerData> playerData) {
        printer.updateGameData(gameBoard, playerData);
    }

    /**
     * Prints the game board on the screen
     *
     * @param gameBoard the board to print
     */
    @Override
    public void showGameBoard(List<Cell> gameBoard) {
        printer.showGameBoard(gameBoard);
    }

    /**
     * Refreshes the game screen
     *
     * @param gameBoard the board to start with
     * @param players information about the players
     */
    public void gameBoardUpdate(List<Cell> gameBoard, List<PlayerData> players) {
        printer.updateGameData(gameBoard, players);
        printer.showGameBoard(gameBoard);
    }

    /**
     * Asks the user to place its worker on the board
     */
    @Override
    public void placeWorker() {
        printer.placeWorker();
    }

    /**
     * Asks the user to choose a worker
     *
     * @param cells the cells containing the user's workers
     */
    @Override
    public void chooseWorker(List<Cell> cells) {
        printer.chooseWorker(cells);
    }

    /**
     * Asks the user which action to perform
     *
     * @param possibleActions a list of possible actions
     */
    @Override
    public void chooseAction(List<PossibleActions> possibleActions) {
        printer.chooseAction(possibleActions);
    }

    /**
     * Asks the user to select a cell to move its current worker on
     *
     * @param gameBoard     the current game board
     * @param walkableCells the cells on which the worker can be moved to
     */
    @Override
    public void moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {
        printer.moveAction(gameBoard, walkableCells);
    }

    /**
     * Asks the user to select a cell to build on
     *
     * @param gameBoard      the current game board
     * @param buildableCells the cells on which the worker can build
     */
    @Override
    public void buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {
        printer.buildAction(gameBoard, buildableCells);
    }

    /**
     * Asks the user which block to build on a cell
     *
     * @param buildableBlocks the possible blocks (always more than one)
     */
    @Override
    public void chooseBlockToBuild(List<Block> buildableBlocks) {
        printer.chooseBlockToBuild(buildableBlocks);
        //buildableBlocks.size always > 1, see player in model
    }
}