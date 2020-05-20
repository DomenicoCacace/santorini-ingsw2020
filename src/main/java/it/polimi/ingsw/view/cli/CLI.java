package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.utils.PrettyPrinter;
import it.polimi.ingsw.view.cli.utils.SafeScanner;
import it.polimi.ingsw.view.inputManager.InputManager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Command Line Interface manager
 */
public class CLI implements ViewInterface {
    public static final String SHOW_INFO_ALL_LOBBIES = "Show me info about all lobbies";
    public static final String YES = "y";
    public static final String NO = "n";
    public static final String ASK_TO_VIEW_LOBBY_INFO = "Do you want to view the lobby information? (" + YES + "/" + NO + ")";
    public static final String REFRESH = "refresh available lobbies";
    private final PrettyPrinter printer;
    private InputManager inputManager;
    private final List<Future> futureList = new ArrayList<>();
    private ExecutorService ex = Executors.newSingleThreadScheduledExecutor();
    private Lock lock = new ReentrantLock();

    public CLI() throws IOException {
        printer = new PrettyPrinter();
        new Thread(this::readInput).start();
    }

    public void stopInput() {
        futureList.forEach(future -> future.cancel(true));
        futureList.clear();
        ex.shutdown();
        ex = Executors.newSingleThreadScheduledExecutor();
    }

   /* private void interruptibleScanner(){
        Future<String> futureInput = ex.submit(this::readInput);
        while (true){
            inputManager(futureInput.get());
        }
    }
    */

    public void setInputManager(InputManager inputManager) {
        lock.lock();
        this.inputManager = inputManager;
        lock.unlock();
    }


    @Override
    public void gameStartScreen(List<Cell> gameBoard) {

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
                    .append(" players connected and is waiting for ")
                    .append(info.get(2))
                    .append(" players to start. ");
            if (info.size() > 3) {
                stringBuilder.append("The players connected are: ");
                for (int i = 3; i < info.size(); i++) {
                    stringBuilder.append(" ").append("-").append(info.get(i));
                }
            }
            lobbiesWithInfos.add(lobby + stringBuilder);
        });
        lobbiesWithInfos.add("Go back");
        printOptions(lobbiesWithInfos);
    }

    @Override
    public void chooseWorker() {

    }

    @Override
    public void moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {

    }

    @Override
    public void buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {

    }

    @Override
    public void chooseBlockToBuild(List<Block> buildableBlocks) {

    }

    @Override
    public void chooseUserGod(List<GodData> possibleGods) {

    }

    @Override
    public void chooseGameGods(List<GodData> allGods, int size) {

    }

    @Override
    public void chooseStartingPlayer(List<String> players) {

    }

    @Override
    public void placeWorker() {

    }

    @Override
    public void chooseAction(List<PossibleActions> possibleActions) {

    }

    @Override
    public void showGameBoard(List<Cell> gameBoard) {

    }

    @Override
    public void initGameBoard(List<Cell> gameboard) {

    }

    @Override
    public void showErrorMessage(String error) {
        System.out.println("\t\t" + error);
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

    private String askInputString(int timeout) throws TimeoutException, CancellationException {
        String input = null;
        SafeScanner safeScanner = new SafeScanner(System.in);
        Future<String> future = ex.submit(safeScanner::nextLine);
        futureList.add(future);
        try {
            input = future.get(timeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            System.out.println("Something's wrong!");
        } catch (InterruptedException e) { //TODO: check if doing this doesn't break ping management
            stopInput(); //It should work because when the client doesn't receive a pong it Cancel an operation already, we just need to catch that.
            return askInputString(timeout);
        }
        return input;
    }


    private int askInputInt(int timeout) throws TimeoutException, CancellationException {
        int input = -1;
        SafeScanner safeScanner = new SafeScanner(System.in);
        Future<Integer> future = ex.submit(safeScanner::nextInt);
        futureList.add(future);
        try {
            input = future.get(timeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            showErrorMessage("Something's wrong!");
        } catch (InterruptedException e) {
            return askInputInt(timeout);
        }
        return input;
    }

    @Override
    public void askIP() throws CancellationException {

        System.out.print("\t\tInsert the server address: ");
        /*
        try {
            return askInputString(1000);
        } catch (TimeoutException e) {
            stopInput();
            return askIP();
        }

         */
    }

    @Override
    public void askUsername() throws CancellationException {
        //TODO: make the user choose if it wants the default one
        System.out.print("Insert your username: ");
        /*
        try {
            return askInputString(1000);
        } catch (TimeoutException e) {
            System.out.println("\nYou still there?");
            stopInput();
            return askUsername();
        }

         */
    }

    /*

    @Override
    public void gameStartScreen(List<Cell> gameBoard) {
        System.out.println("Let the game begin!");
        showGameBoard(gameBoard);
        //TODO: enhance
    }
*/
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
    public String chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) throws TimeoutException, CancellationException {
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

    private boolean askToChoose(String question) throws TimeoutException, CancellationException {
        while (true) {
            System.out.println(question);

            String input = askInputString(60);
            if (input.equals(YES))
                return true;
            else if (input.equals(NO))
                return false;
        }
    } */

    public void showLobbyInfo(List<String> info) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("The lobby ")
                .append(info.get(0))
                .append(" has ")
                .append(info.get(1))
                .append(" players connected and is waiting for ")
                .append(info.get(2))
                .append(" players to start.\n");
        if (info.size() > 3) {
            stringBuilder.append("The players connected are: ");
            for (int i = 3; i < info.size(); i++) {
                stringBuilder.append("\n").append("-").append(info.get(i));
            }
        }
        System.out.println(stringBuilder.toString());
    }
    /*


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
    public Cell chooseWorker() throws TimeoutException, CancellationException {
        while (true) {
            System.out.println("Choose your worker: ");
            Cell chosenWorker = chooseCell();
            if (chosenWorker.getOccupiedBy() != null)
                showErrorMessage("There's no worker in that cell!");
            else
                return chosenWorker;
        }
    }
*/
    @Override
    public void chooseMatchReload() throws CancellationException {
        System.out.println("I found a match to reload! do you want to reload? (" + YES + "/" + NO + ")");
    }
/*
    @Override
    public Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells) throws TimeoutException, CancellationException {
        printer.printBoard(gameBoard, walkableCells);   //TODO: enhance
        while (true) {
            System.out.println("Choose a cell to step on:");
            Cell chosenCell = chooseCell();
            for (Cell cell : walkableCells) {
                if (cell.getCoordX() == chosenCell.getCoordX() && cell.getCoordY() == chosenCell.getCoordY())
                    /* cannot use the .equals() method, as it checks other attributes too*//*
                    return cell;
            }
            showErrorMessage("You can't go on that cell!");
        }
    }

    @Override
    public Cell buildAction(List<Cell> gameBoard, List<Cell> buildableCells) throws TimeoutException, CancellationException {
        printer.printBoard(gameBoard, buildableCells);   //TODO: enhance
        while (true) {  //TODO: might extract private method for move/buildAction
            System.out.println("Choose a cell to build on:");
            Cell chosenCell = chooseCell();
            for (Cell cell : buildableCells) {
                if (cell.getCoordX() == chosenCell.getCoordX() && cell.getCoordY() == chosenCell.getCoordY())
                    /* cannot use the .equals() method, as it checks other attributes too*//*
                    return cell;
            }
            showErrorMessage("You can't build on that cell!");
        }
    }

    @Override
    public Block chooseBlockToBuild(List<Block> buildableBlocks) throws TimeoutException, CancellationException {
        //buildableBlocks.size always > 1, see player in model
        System.out.println("Choose the block to build: ");
        List<String> blocks = new ArrayList<>();
        buildableBlocks.forEach(b -> blocks.add(b.toString()));
        return buildableBlocks.get(printList(blocks, 60));
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
    public Cell placeWorker() throws TimeoutException, CancellationException {
        System.out.println("Place your Worker!");
        return chooseCell();
    }

    @Override
    public GodData chooseUserGod(List<GodData> possibleGods) throws TimeoutException, CancellationException {
        if (possibleGods.size() == 1) {
            showSuccessMessage("\nYour god is " + possibleGods.get(0).getName() + "\n");
            return possibleGods.get(0);
        }
        System.out.println("Choose your god:");
        List<String> gods = new ArrayList<>();
        possibleGods.forEach(g -> gods.add(g.getName()));
        return possibleGods.get(printList(gods, 60));
    }

    @Override
    public List<GodData> chooseGameGods(List<GodData> allGods, int size) throws TimeoutException, CancellationException {
        List<GodData> chosenGods = new ArrayList<>();
        System.out.println("Choose " + (size) + " gods:");
        List<String> gods = new LinkedList<>();
        allGods.forEach(g -> gods.add(g.getName()));

        for (int i = 0; i < size; i++) {
            int choice = printList(gods, 60);
            chosenGods.add(allGods.get(choice));
            gods.remove(choice);
            allGods.remove(choice);
        }
        return chosenGods;
    }

    @Override
    public String chooseStartingPlayer(List<String> players) throws TimeoutException, CancellationException {
        System.out.println("Choose the first player:");
        return players.get(printList(players, 60));
    }

    @Override
    public PossibleActions chooseAction(List<PossibleActions> possibleActions) throws TimeoutException, CancellationException {
        if (possibleActions.size() == 1)
            return possibleActions.get(0);
        System.out.println("Select an action: ");
        List<String> actions = new ArrayList<>();
        possibleActions.forEach(a -> actions.add(a.toString()));
        return possibleActions.get(printList(actions, 60));
    }

    private Cell chooseCell() throws TimeoutException, CancellationException {
        while (true) {
            System.out.println("Insert the cell coordinates (1-5):");
            System.out.print("row: ");

            int coordX = askInputInt(60);
            System.out.print("col: ");
            int coordY = askInputInt(60);
            if (!(coordX < 1 || coordY < 1 || coordX > 5 || coordY > 5))
                return new Cell(coordX - 1, coordY - 1);
            showErrorMessage("Coordinates out of bounds");
        }
    }
    */

    @Override
    public void printOptions(List<String> list) throws CancellationException {
        for (int i = 1; i < list.size() + 1; i++)
            System.out.println(i + "- " + list.get(i - 1));
    }

    @Override
    public void showSuccessMessage(String message) {
        System.out.println(message);
    }
}


