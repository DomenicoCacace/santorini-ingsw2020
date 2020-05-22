package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.utils.PrettyPrinter;
import it.polimi.ingsw.view.inputManagers.InputManager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Command Line Interface manager
 */
public class CLI implements ViewInterface {
    public static final String YES = "y";
    public static final String NO = "n";
    private final PrettyPrinter printer;
    private InputManager inputManager;
    private ExecutorService ex = Executors.newSingleThreadScheduledExecutor();
    private Lock lock = new ReentrantLock();

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
        possibleGods.forEach(g -> System.out.println(possibleGods.indexOf(g)+1 + "-" + g.getName()));
    }

    @Override
    public void chooseGameGods(List<GodData> allGods, int size) {
        System.out.println("Choose the game gods:");
        allGods.forEach(g -> System.out.println(allGods.indexOf(g)+1 + "-" + g.getName()));
    }

    private void readInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext()) {
                lock.lock();
                String input = scanner.nextLine();
                if(!input.isBlank())
                    inputManager.manageInput(input.trim());
                lock.unlock();
            }
        }
    }

    @Override
    public void askToReloadLastSettings(List<String> savedUsers) {
        System.out.print("\t\tThere are some settings saved! do you want to load one of them? [" + YES + "/" + NO + "]: ");
        /*List<String> chosenConfig = new ArrayList<>();
        while (true) {

            String input = null;
            try {
                input = askInputString(1000);
            } catch (TimeoutException e) {
                System.out.println("\n\t\tYou still there?");
                stopInput();
                return askToReloadLastSettings(savedUsers);
            }
            if (input.equals(YES)) {
                System.out.println("Select the config you want to load!");
                List<String> savedConfigs = new LinkedList<>();
                savedConfigs.add("Use a different username/IP instead!");
                for (int i = 1; i <= savedUsers.size(); i += 2) {
                    savedConfigs.add(savedUsers.get(i) + " -- " + savedUsers.get(i - 1)); //This convert
                }
                int index = -1;
                try {
                    index = printList(savedConfigs, 1000);
                } catch (TimeoutException e) {
                    System.out.println("\n\t\tYou still there?");
                    stopInput();
                    return askToReloadLastSettings(savedUsers);
                }
                if (index != 0) { //If index == 0 the player chose to manually input user/Ip
                    index--;
                    index = index * 2;
                    chosenConfig.add(savedUsers.get(index));
                    chosenConfig.add(savedUsers.get(index + 1));
                }
                return chosenConfig;
            } else if (input.equals(NO))
                return chosenConfig;
            System.out.println("please insert a correct option: (" + YES + "/" + NO + ")");
        }

         */
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
        //TODO: make the user choose if it wants the default one
        System.out.print("\t\tInsert your username: ");
    }

    @Override
    public void gameStartScreen(List<Cell> gameBoard) {
        System.out.println("Let the game begin!");
        showGameBoard(gameBoard);
        //TODO: enhance
    }

    @Override
    public void lobbyOptions(List<String> options) throws CancellationException {
        printOptions(options);
    }

    @Override
    public void  askLobbyName() throws CancellationException {
        System.out.print("Choose your lobby name:\n");
    }

    @Override
    public void askLobbySize() throws CancellationException {
        System.out.print("Choose the room size:\n");
    }
/*
    @Override
    public String chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) throws CancellationException {
        List<String> lobbies = new LinkedList<>(lobbiesAvailable.keySet());
        lobbies.add(SHOW_INFO_ALL_LOBBIES);
        lobbies.add("Go back");
        System.out.println("Choose which lobby to join!");
        int index = printList(lobbies, 1000);
        if (index == lobbies.size() - 2) {
            StringBuilder stringBuilder = new StringBuilder();
            lobbiesAvailable.values().forEach(info -> {
                stringBuilder.append(showLobbyInfo(info)).append("\n");
            });
            System.out.println(stringBuilder.toString() + "\r");
            return chooseLobbyToJoin(lobbiesAvailable);
        } else if (index == lobbies.size() - 1) {
            return null;
        } else if (askToChoose(ASK_TO_VIEW_LOBBY_INFO)) {
            System.out.println(showLobbyInfo(lobbiesAvailable.get(lobbies.get(index))));
            if (!askToChoose("Do you want to join lobby(" + YES + ") or do you want to review the lobbies?(" + NO + ")"))
                return chooseLobbyToJoin(lobbiesAvailable);
        }
        return lobbies.get((index));
    }

    private boolean askToChoose(String question) throws CancellationException {
        while (true) {
            System.out.println(question);

            String input = askInputString(60);
            if (input.equals(YES))
                return true;
            else if (input.equals(NO))
                return false;
        }
    } */

    @Override
    public void showGameBoard(List<Cell> gameBoard) {
        printer.printBoard(gameBoard);
    }

    @Override
    public void initGameBoard(List<Cell> gameboard) {
        printer.setCachedBoard(gameboard);
        showGameBoard(gameboard);
    }

    @Override
    public void chooseWorker() throws CancellationException {
        System.out.println("Choose your worker! \nrow: ");
    }

    @Override
    public void chooseMatchReload() throws CancellationException {
        System.out.println("I found a match to reload! do you want to reload? (" + YES + "/" + NO + ")");
    }

    @Override
    public void moveAction(List<Cell> gameBoard, List<Cell> walkableCells) throws CancellationException {
        printer.printBoard(gameBoard, walkableCells);
        printer.printMessage("Select the cell where you want to to move! \nrow: ");
    }

    @Override
    public void buildAction(List<Cell> gameBoard, List<Cell> buildableCells) throws  CancellationException {
        printer.printBoard(gameBoard, buildableCells);   //TODO: we can put walkable and buildable together
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


