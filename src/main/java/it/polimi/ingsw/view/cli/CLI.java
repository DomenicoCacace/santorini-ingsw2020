package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.view.Constants;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.utils.PrettyPrinter;
import it.polimi.ingsw.view.inputManagers.InputManager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Command Line Interface manager
 */
public class CLI implements ViewInterface {

    private final PrettyPrinter printer;
    private final Lock lock = new ReentrantLock();
    private InputManager inputManager;
    private List<PlayerData> players = new ArrayList<>();

    public CLI() throws IOException {
        printer = new PrettyPrinter();
        new Thread(this::readInput).start();
    }

    public void setInputManager(InputManager inputManager) {
        lock.lock();
        this.inputManager = inputManager;
        lock.unlock();
    }

    @Override
    public void printCol() {
        printer.printMessage("col: ");
    }

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

    @Override
    public void chooseUserGod(List<GodData> possibleGods) {
        System.out.println("Choose your god: ");
        possibleGods.forEach(g -> System.out.println(possibleGods.indexOf(g) + 1 + "-" + g.getName() + " -> Effect: " + g.getDescriptionStrategy()));
    }

    @Override
    public void chooseGameGods(List<GodData> allGods, int size) {
        System.out.println("Choose the game gods:");
        allGods.forEach(g -> System.out.println(allGods.indexOf(g) + 1 + "-" + g.getName() + " -> Effect: " + g.getDescriptionStrategy()));
    }

    private void readInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext()) {
                lock.lock();
                String input = scanner.nextLine();
                if (!input.isBlank())
                    inputManager.manageInput(input.trim());
                lock.unlock();
            }
        }
    }

    @Override
    public void askToReloadLastSettings(List<String> savedUsers) {
        System.out.print("\t\tThere are some settings saved! do you want to load one of them? [" + Constants.YES + "/" + Constants.NO + "]: ");
    }

    @Override
    public void printLogo() {
        printer.printLogin();
    }

    @Override
    public void askIP() throws CancellationException {
        System.out.print("\t\tInsert the server address: ");
    }

    @Override
    public void askUsername() throws CancellationException {
        System.out.print("\t\tInsert your username: ");
    }

    @Override
    public void gameStartScreen(List<Cell> gameBoard, List<PlayerData> players) {
        System.out.println("Let the game begin!");
        initGameScreen(gameBoard, players);
        //TODO: enhance
    }

    @Override
    public void lobbyOptions(List<String> options) throws CancellationException {
        printOptions(options);
    }

    @Override
    public void askLobbyName() throws CancellationException {
        System.out.print("Choose your lobby name:\n");
    }

    @Override
    public void askLobbySize() throws CancellationException {
        System.out.print("Choose the room size:\n");
    }

    @Override
    public void showGameBoard(List<Cell> gameBoard) {
        printer.printBoard(gameBoard);
    }

    @Override
    public void initGameScreen(List<Cell> gameBoard, List<PlayerData> players) {
        printer.setCachedBoard(gameBoard);
        showGameBoard(gameBoard);
        if(this.players.isEmpty()) {
            this.players = players;
            StringBuilder builder = new StringBuilder();
            players.forEach(playerData ->
                    builder.append("The player ")
                            .append(playerData.getName())
                            .append(" is the color ")
                            .append(playerData.getColor().toString())
                            .append(" and is playing with the God ")
                            .append(playerData.getGod().getName())
                            .append("\n"));
            System.out.println(builder.toString());
        }
    }

    @Override
    public void chooseWorker() throws CancellationException {
        System.out.println("Choose your worker! \nrow: ");

    }

    @Override
    public void chooseMatchReload() throws CancellationException {
        System.out.println("I found a match to reload! do you want to reload? (" + Constants.YES + "/" + Constants.NO + ")");
    }

    @Override
    public void moveAction(List<Cell> gameBoard, List<Cell> walkableCells) throws CancellationException {
        printer.printBoard(gameBoard, walkableCells);
        printer.printMessage("Select the cell where you want to to move! \nrow: ");
    }

    @Override
    public void buildAction(List<Cell> gameBoard, List<Cell> buildableCells) throws CancellationException {
        printer.printBoard(gameBoard, buildableCells);
        printer.printMessage("Select the cell where you want to to build! \nrow: ");
    }

    @Override
    public void chooseBlockToBuild(List<Block> buildableBlocks) throws CancellationException {
        //buildableBlocks.size always > 1, see player in model
        System.out.println("Choose the block to build: ");
        List<String> blocks = new ArrayList<>();
        buildableBlocks.forEach(b -> blocks.add(b.toString()));
        printOptions(blocks);
    }

    @Override
    public void showErrorMessage(String error) {
        printer.printError(error);   //TODO: enhance
    }

    @Override
    public void showSuccessMessage(String message) {
        printer.printMessage(message);   //TODO: enhance

    }

    @Override
    public void placeWorker() throws CancellationException {
        System.out.println("Place your Worker!");
        System.out.println("row: ");
    }

    @Override
    public void chooseStartingPlayer(List<String> players) throws CancellationException {
        System.out.println("Choose the first player:");
        printOptions(players);
    }

    @Override
    public void chooseAction(List<PossibleActions> possibleActions) throws CancellationException {
        System.out.println("Select an action: ");
        List<String> actions = new ArrayList<>();
        possibleActions.forEach(a -> actions.add(a.toString()));
        printOptions(actions);
    }


    @Override
    public void printOptions(List<String> list) throws CancellationException {
        for (int i = 1; i < list.size() + 1; i++)
            System.out.println(i + "- " + list.get(i - 1));
    }
}


