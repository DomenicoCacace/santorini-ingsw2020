package it.polimi.ingsw.client.view.cli.console.printers.fancyPrinter;

import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.cli.console.Console;
import it.polimi.ingsw.client.view.cli.console.CursorPosition;
import it.polimi.ingsw.client.view.cli.console.graphics.*;
import it.polimi.ingsw.client.view.cli.console.graphics.components.Dialog;
import it.polimi.ingsw.client.view.cli.console.printers.BoardUtils;
import it.polimi.ingsw.client.view.cli.console.printers.Printer;
import it.polimi.ingsw.shared.dataClasses.*;

import java.io.IOException;
import java.util.*;


/**
 * Fancy printer for systems supporting non canonical terminal mode
 */
public class FancyPrinter extends Printer {

    private final List<Dialog> dialogs = new ArrayList<>();

    private Status currentStatus;

    /**
     * Default constructor
     *  @param console the console in which the output will be printed
     * @param cli     the UI object
     *
     */
    public FancyPrinter(Console console, CLI cli) throws IOException {
        super(cli, console);
    }

    /**
     * Shows a popup error message
     *
     * @param errorMsg the error message
     */
    @Override
    public void printError(String errorMsg) {
        if (currentStatus != Status.GAME || boardUtils == null) {
            ErrorDialog dialog = new ErrorDialog(errorMsg, Console.currentWindow());
            dialogs.add(dialog);
            dialog.show();
        }
        else {
            ((FancyPrinterBoardUtils) boardUtils).showErrorMessage(errorMsg);
        }
    }

    /**
     * Shows a popup general message
     *
     * @param msg the message
     */
    @Override
    public void printMessage(String msg) {
        if (currentStatus != Status.GAME || boardUtils == null) {
            MessageDialog dialog = new MessageDialog(msg, console);
            dialogs.add(dialog);
            dialog.show();
        }
        else {
            ((FancyPrinterBoardUtils) boardUtils).showMessage(msg);
        }
    }

    /**
     * Shows the "Santorini" logo
     */
    @Override
    public void printStartingScreen() {
        currentStatus = Status.LOGIN;
        console.addToBackground(mainLogo, new CursorPosition(2, (console.getWidth() - 151) / 2));
        switchToConsole();
    }

    /**
     * Asks the user if it wants to reload an already used address/username combo
     */
    @Override
    public void askToReloadSettings() {
        HashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("NO", "n");
        buttons.put("YES", "y");
        new ButtonsDialog("Reload Settings", "There are some settings saved. Reload?", console, buttons, false).show();
    }

    /**
     * Shows a dialog with Yes/No buttons to decide to reload a previously saved address/username combo
     *
     * @param options the address/username combos
     */
    @Override
    public void showSavedSettings(List<String> options) {
        new SingleChoiceListDialog("Reload settings", "Press ENTER to select an option", console, options).show();
    }

    /**
     * Asks the user both the server address and the username
     * <br>
     * At this point, the username is surely not set yet
     */
    @Override
    public void askIp() {
        new TextInputDialog("Login", "Insert the server address", console, 50, 25, "Address", "Username").show();
    }

    /**
     * Asks the user its username only
     * <br>
     * This will generate an InputDialog only if the current state is "LOBBY", a.k.a. the user tried to join a lobby
     * using an already taken username for that lobby
     */
    @Override
    public void askUsername() {
        if (currentStatus == Status.IN_LOBBY)
            new TextInputDialog("Login", "Insert your username", console, 50, 15, "Username").show();
    }

    /**
     * Shows a dialog with one or two buttons, based on the possible lobby options (Create/Join)
     *
     * @param options a list of possible options
     */
    @Override
    public void lobbyOptions(List<String> options) {
        removeStaleMessages();
        switchToConsole();
        HashMap<String, String> buttons = new LinkedHashMap<>();
        options.forEach(o -> buttons.put(o, String.valueOf(options.indexOf(o) + 1)));
        new ButtonsDialog("Lobby options", "Login successful\nChoose what action to do", console, buttons, true).show();
    }

    /**
     * Shows a TextInputDialog asking for both the lobby name and size
     */
    @Override
    public void askLobbyName() {
        removeStaleMessages();
        new TextInputDialog("New Lobby", "Insert the lobby information", console, 50, 24, "Name", "Size").show();
        currentStatus = Status.CREATING_LOBBY;
    }

    /**
     * Does nothing, the lobby size is asked in {@linkplain #askLobbyName()} for this printer
     */
    @Override
    public void askLobbySize() {
        removeStaleMessages();
        if (currentStatus == Status.IN_LOBBY) {
            HashMap<String, String> buttons = new LinkedHashMap<>();
            buttons.put("2 players", "2");
            buttons.put("3 players", "3");
            new ButtonsDialog("Lobby size", "Invalid input\nSelect the lobby size", console, buttons, true).show();
        }
        currentStatus = Status.IN_LOBBY;
    }

    /**
     * Asks the user to choose which lobby to join, showing a list of available lobbies and their details
     *
     * @param lobbiesAvailable a map containing the lobbies available and their relative information
     */
    @Override
    public void chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) {
        removeStaleMessages();
        LinkedHashMap<String, LinkedList<String>> lobbyDetails = new LinkedHashMap<>();

        lobbiesAvailable.forEach((name, details) -> {
            /*
             * details structure:
             * 0: Lobby name
             * 1: Connected users
             * 2: Available slots
             * 3-5: Usernames
             */

            LinkedList<String> decoratedDetails = new LinkedList<>();
            decoratedDetails.add("Lobby: " + details.get(0));
            decoratedDetails.add("Slots available: " + details.get(2) + "/" + (Integer.parseInt(details.get(1)) + Integer.parseInt(details.get(2))));
            decoratedDetails.add("\n");
            decoratedDetails.add(" --- Players --- ");
            decoratedDetails.add("\n");
            decoratedDetails.add(" - " + details.get(3) + " (owner)");
            for (int i = 4; i < details.size(); i++) {
                decoratedDetails.add(" - " + details.get(i));
            }
            lobbyDetails.put(name, decoratedDetails);
        });

        new DetailedSingleChoiceListDialog("Join Lobby", "ARROW KEYS: navigate lobbies \n ENTER: select lobby", console, lobbyDetails).show();
        currentStatus = Status.IN_LOBBY;
    }

    /**
     * Shows the user a dialog with two buttons (Yes/No), asking the user if it wants to reload a previously saved game status
     */
    @Override
    public void chooseToReloadMatch() {
        removeStaleMessages();
        HashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("NO", "n");
        buttons.put("YES", "y");
        new ButtonsDialog("Reload match", "I found a saved game for you. Do you want to reload it?", console, buttons, false).show();
    }

    /**
     * Shows the user a MultipleChoiceListDialog, containing the gods and their descriptions
     *
     * @param allGods the list of available gods
     * @param size    the number of players
     */
    @Override
    public void chooseGameGods(List<GodData> allGods, int size) {
        removeStaleMessages();
        if (currentStatus.equals(Status.IN_LOBBY)) {
            LinkedHashMap<String, LinkedList<String>> godsDetails = generateGodsDescriptions(allGods);
            new MultipleChoiceListDialog("Pick " + size + " Gods", "ARROW KEYS: navigate Gods\nENTER: select/deselect God", console, godsDetails, size).show();
            currentStatus = Status.GODS_CHOSEN;
        }
    }

    /**
     * Shows the user a DetailedSingleChoiceListDialog, containing the available gods and descriptions
     *
     * @param possibleGods a list containing the available gods
     */
    @Override
    public void chooseUserGod(List<GodData> possibleGods) {
        removeStaleMessages();
        LinkedHashMap<String, LinkedList<String>> godsDetails = generateGodsDescriptions(possibleGods);
        new DetailedSingleChoiceListDialog("Choose your god", "", console, godsDetails).show();
        currentStatus = Status.GAME;
    }

    /**
     * Asks the user which player will play first
     *
     * @param players the players in game
     */
    @Override
    public void chooseStartingPlayer(List<String> players) {
        new SingleChoiceListDialog("Starting player", "Choose which player will play first", console, players).show();
        currentStatus = Status.GAME;
    }

    /**
     * Creates a FancyPrinterBoardUtils
     *
     * @return a boardUtils object
     */
    @Override
    protected BoardUtils setBoardUtils() {
        try {
            return new FancyPrinterBoardUtils(this);
        }
        catch (IOException e) {
            System.err.println("Could not instantiate FancyPrinterBoardUtils");
            Console.close();
            return null;
        }
    }

    /**
     * Updates information about the game and the players
     *
     * @param board   the game board
     * @param players information about the players
     */
    @Override
    public void updateGameData(List<Cell> board, List<PlayerData> players) {
        boardUtils.updateGameData(board, players);
        ((FancyPrinterBoardUtils) boardUtils).setPlayerData(players);
        boardUtils.show();
        currentStatus = Status.GAME;
    }

    /**
     * Asks the user which action to perform
     *
     * @param possibleActions a list of possible actions
     */
    @Override
    public void chooseAction(List<PossibleActions> possibleActions) {
        ((FancyPrinterBoardUtils) boardUtils).enableActionSelector(possibleActions);
    }

    /**
     * Asks the user to place its worker on the board
     */
    @Override
    public void placeWorker() {
        ((FancyPrinterBoardUtils) boardUtils).enableGridInput();
    }

    /**
     * Asks the user to choose a worker
     * @param cells the cells containing the player's workers
     */
    @Override
    public void chooseWorker(List<Cell> cells) {
        highlightWorkers(cells);
        ((FancyPrinterBoardUtils) boardUtils).enableGridInput(cells);
    }

    /**
     * Asks the user to select a cell to move its current worker on
     *
     * @param gameBoard     the current game board
     * @param walkableCells the cells on which the worker can be moved to
     */
    @Override
    public void moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {
        showGameBoard(gameBoard, walkableCells);
        ((FancyPrinterBoardUtils) boardUtils).enableGridInput(walkableCells);
    }

    /**
     * Asks the user to select a cell to build on
     *
     * @param gameBoard      the current game board
     * @param buildableCells the cells on which the worker can build
     */
    @Override
    public void buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {
        showGameBoard(gameBoard, buildableCells);
        ((FancyPrinterBoardUtils) boardUtils).enableGridInput(buildableCells);
    }

    /**
     * Asks the user which block to build on a cell
     *
     * @param buildableBlocks the possible blocks (always more than one)
     */
    @Override
    public void chooseBlockToBuild(List<Block> buildableBlocks) {
        ((FancyPrinterBoardUtils) boardUtils).enableBlockSelector(buildableBlocks);
    }

    /**
     * Decorates the gods descriptions to be printed
     *
     * @param possibleGods the list of gods
     * @return a map containing god/description tuples
     */
    private LinkedHashMap<String, LinkedList<String>> generateGodsDescriptions(List<GodData> possibleGods) {
        LinkedHashMap<String, LinkedList<String>> godsDetails = new LinkedHashMap<>();
        possibleGods.forEach(g -> {
            LinkedList<String> description = new LinkedList<>();
            description.add("--- " + g.getName() + " ---");
            description.add("");
            description.add("-- Effect Description --");
            description.add(g.getDescriptionStrategy());
            description.add("");
            description.add("Number of workers: " + g.getWorkersNumber());
            godsDetails.put(g.getName(), description);
        });
        return godsDetails;
    }

    /**
     * Removes stale messages not closed by the user at the right time
     */
    private void removeStaleMessages() {
        if (!dialogs.isEmpty()) {
            for (Dialog dialog : dialogs) {
                dialog.remove();
            }
        }
    }

    /**
     * The internal state,
     * <p>
     * used to manage certain dialogs which might show more or less information/fields based on the
     * internal status
     */
    private enum Status {
        LOGIN, CREATING_LOBBY, IN_LOBBY, GODS_CHOSEN, GAME
    }

    private void switchToConsole() {
        if (boardUtils != null && Console.isOpen(boardUtils)) {
            Console.closeWindow(boardUtils);
            boardUtils.remove();
        }
        Console.cursor.setCoordinates(0, 0);
        Console.cursor.setAsHome();
        Console.cursor.moveToHome();
        console.show();
    }
}
