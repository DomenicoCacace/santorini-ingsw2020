package it.polimi.ingsw.client.view.cli.console.printers.basicPrinter;

import it.polimi.ingsw.client.view.Constants;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.cli.console.Console;
import it.polimi.ingsw.client.view.cli.console.printers.BoardUtils;
import it.polimi.ingsw.client.view.cli.console.printers.Printer;
import it.polimi.ingsw.shared.dataClasses.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BasicPrinter extends Printer {

    /**
     * Default constructor
     *
     * @param cli the view which created the printer
     */
    public BasicPrinter(Console console, CLI cli) throws IOException {
        super(console);
    }

    /**
     * Shows an error message
     *
     * @param errorMsg the error message
     */
    @Override
    public void printError(String errorMsg) {
        System.out.println(errorMsg);
    }

    /**
     * Shows a success message
     *
     * @param msg the message
     */
    @Override
    public void printMessage(String msg) {
        System.out.println(msg);
    }

    /**
     * Shows the logo
     */
    @Override
    public void printStartingScreen() {
        Console.out.drawMatrix(mainLogo.getObject());
    }

    /**
     * Asks the user if it wants to reload a previously saved address/username combo
     */
    @Override
    public void askToReloadSettings() {
        System.out.print("\t\tThere are some settings saved! do you want to load one of them? [" + Constants.YES +
                "/" + Constants.NO + "]: ");
    }

    /**
     * Shows the user the saved address/username combos
     *
     * @param options the address/username combos
     */
    @Override
    public void showSavedSettings(List<String> options) {
        printOptions(options);
    }

    /**
     * Asks the user to insert the server address
     */
    @Override
    public void askIp() {
        System.out.print("\t\tInsert the server address: ");
    }

    /**
     * Asks the user to insert its username
     */
    @Override
    public void askUsername() {
        System.out.print("\t\tInsert your username: ");
    }

    /**
     * Asks the user to choose whether to join or create a lobby
     *
     * @param options a list of possible options
     */
    @Override
    public void lobbyOptions(List<String> options) {
        printOptions(options);
    }

    /**
     * Asks the user to choose a name for the lobby to be created
     */
    @Override
    public void askLobbyName() {
        System.out.print("Choose your lobby name:\n");
    }

    /**
     * Asks the user to choose the lobby size
     */
    @Override
    public void askLobbySize() {
        System.out.print("Choose the room size:\n");
    }

    /**
     * Asks the user which lobby to join
     *
     * @param lobbiesAvailable a map containing the lobbies available and their relative information
     */
    @Override
    public void chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) {
        List<String> lobbies = new LinkedList<>(lobbiesAvailable.keySet());
        List<String> lobbiesWithInfos = new LinkedList<>();
        System.out.println("Choose which lobby to join!");
        lobbies.forEach(lobby -> {
            List<String> info = lobbiesAvailable.get(lobby);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append(" has ")
                    .append(info.get(1))
                    .append(" players connected,\n")
                    .append("   is waiting for ")
                    .append(info.get(2))
                    .append(" players to start.\n");
            if (info.size() > 3) {
                stringBuilder.append("   The players connected are:\n");
                for (int i = 3; i < info.size(); i++) {
                    stringBuilder.append("   -").append(info.get(i)).append("\n");
                }
            }
            lobbiesWithInfos.add(lobby + stringBuilder);
        });
        lobbiesWithInfos.add("Go back");
        printOptions(lobbiesWithInfos);
    }


    /**
     * Asks the user if it wants to reload an existing saved match
     */
    @Override
    public void chooseToReloadMatch() {
        System.out.println("I found a match to reload! do you want to reload? (" + Constants.YES + "/" + Constants.NO +
                ")");
    }

    /**
     * Asks the user to choose the gods for the game
     *
     * @param allGods the list of available gods
     * @param size    the number of players
     */
    @Override
    public void chooseGameGods(List<GodData> allGods, int size) {
        System.out.println("Choose " + size + " gods: ");
        allGods.forEach(g -> System.out.println(allGods.indexOf(g) + 1 + "-" + g.getName() + " -> Effect: " + g.getDescriptionStrategy()));
    }

    /**
     * Asks the user  to choose its personal god for the game
     *
     * @param possibleGods a list containing the available gods
     */
    @Override
    public void chooseUserGod(List<GodData> possibleGods) {
        System.out.println("Choose your god: ");
        possibleGods.forEach(g -> System.out.println(possibleGods.indexOf(g) + 1 + "-" + g.getName() + " -> Effect: " + g.getDescriptionStrategy()));
    }

    /**
     * Asks the user which player will play first
     *
     * @param players the players in game
     */
    @Override
    public void chooseStartingPlayer(List<String> players) {
        System.out.println("Choose the first player:");
        printOptions(players);
    }

    /**
     * Creates a BoardUtils object, based on the printer calling it
     *
     * @return a boardUtils object
     */
    @Override
    protected BoardUtils setBoardUtils() {
        try {
            return new BasicPrinterBoardUtils(this.console);
        }
        catch (IOException e) {
            System.err.println("Could not instantiate BasicPrinterBoardUtils");
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
        /*
         * Does nothing, in this CLI version it has been decided not to implement too many details
         */
    }

    /**
     * Asks the user which action to perform
     *
     * @param possibleActions a list of possible actions
     */
    @Override
    public void chooseAction(List<PossibleActions> possibleActions) {
        System.out.println("Select an action: ");
        List<String> actions = new ArrayList<>();
        possibleActions.forEach(a -> actions.add(a.toString()));
        printOptions(actions);
    }

    /**
     * Asks the user to place its worker on the board
     */
    @Override
    public void placeWorker() {
        System.out.println("Select row and column! ");
    }

    /**
     * Asks the user to choose a worker
     * @param cells the cells containing the user's workers
     */
    @Override
    public void chooseWorker(List<Cell> cells) {
        highlightWorkers(cells);
        System.out.println("Choose your worker! \nSelect row and column! ");
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
        System.out.println("Select the cell where you want to to move! \nSelect row and column! ");
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
        System.out.println("Select the cell where you want to to build! \nSelect row and column! ");
    }

    /**
     * Asks the user which block to build on a cell
     *
     * @param buildableBlocks the possible blocks (always more than one)
     */
    @Override
    public void chooseBlockToBuild(List<Block> buildableBlocks) {
        System.out.println("Choose the block to build: ");
        List<String> blocks = new ArrayList<>();
        buildableBlocks.forEach(b -> blocks.add(b.toString()));
        printOptions(blocks);
    }

    /**
     * Print a list of options, preceded by a number that the user will have to type to choose that option
     *
     * @param list the options
     */
    protected void printOptions(List<String> list) {
        for (int i = 1; i < list.size() + 1; i++)
            System.out.println(i + "- " + list.get(i - 1));
    }
}
