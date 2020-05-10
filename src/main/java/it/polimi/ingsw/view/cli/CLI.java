package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.utils.PrettyPrinter;
import it.polimi.ingsw.view.cli.utils.SafeScanner;

import java.io.IOException;
import java.util.*;


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

    public CLI() throws IOException {
        printer = new PrettyPrinter();
    }

    @Override
    public List<String> askToReloadLastSettings(List<String> savedUsers) {
        System.out.print("\t\tThere are some settings saved! do you want to load one of them? [" + YES + "/" + NO + "]: ");
        List<String> chosenConfig = new ArrayList<>();
        while (true) {
            SafeScanner scanner = new SafeScanner(System.in);
            String input = scanner.nextLine();
            if (input.equals(YES)) {
                System.out.println("Select the config you want to load!");
                List<String> savedConfigs = new LinkedList<>();
                savedConfigs.add("Use a different username/IP instead!");
                for (int i = 1; i <= savedUsers.size(); i += 2) {
                    savedConfigs.add(savedUsers.get(i) + " -- " + savedUsers.get(i - 1)); //This convert
                }
                int index = chooseFromList(savedConfigs);
                if (index != 0) { //If index == 0 the player chose to manually input user/Ip
                    index--;
                    index = index*2;
                    chosenConfig.add(savedUsers.get(index));
                    chosenConfig.add(savedUsers.get(index+1));
                }
                return chosenConfig;
            } else if (input.equals(NO))
                return chosenConfig;
            System.out.println("please insert a correct option: (" + YES + "/" + NO + ")");
        }
    }

    @Override
    public void printLogo(){
        printer.printLogin();
    }

    @Override
    public String askIP(){
        SafeScanner scanner = new SafeScanner(System.in);
        System.out.print("\t\tInsert the server address: ");
        return scanner.nextLine();
    }

    @Override
    public String askUsername(){
        //TODO: make the user choose if it wants the default one
        System.out.print("\t\tInsert your username: ");
        SafeScanner scanner = new SafeScanner(System.in);
        return scanner.nextLine();  //TODO: add a maximum username length?
    }

    @Override
    public void gameStartScreen(List<Cell> gameBoard) {
        System.out.println("Let the game begin!");
        showGameBoard(gameBoard);
        //TODO: enhance
    }

    @Override
    public String lobbyOptions(List<String> options) {
        return options.get(chooseFromList(options));
    }

    @Override
    public String askLobbyName() {
        System.out.print("Choose your lobby name:\n");
        SafeScanner scanner = new SafeScanner(System.in);
        return scanner.nextLine();
    }


    @Override
    public int askLobbySize() {
        while(true) {
            System.out.print("Choose the room size:\n");
            SafeScanner scanner = new SafeScanner(System.in);
            int size = scanner.nextInt();
            if (size == 2 || size == 3)
                return size;
            showErrorMessage("Size not valid");
        }
    }

    @Override
    public String chooseLobbyToJoin(Map<String, List<String>> lobbiesAvailable) {
        List<String> lobbies = new LinkedList<>(lobbiesAvailable.keySet());
        lobbies.add(SHOW_INFO_ALL_LOBBIES);
        lobbies.add(REFRESH);
        lobbies.add("Go back");
        System.out.println("Choose which lobby to join!");
        int index = chooseFromList(lobbies);
        if (index == lobbies.size() - 3) {
            StringBuilder stringBuilder = new StringBuilder();
            lobbiesAvailable.values().forEach(info -> {
                stringBuilder.append(showLobbyInfo(info)).append("\n");
            });
            System.out.println(stringBuilder.toString() + "\r");
            return chooseLobbyToJoin(lobbiesAvailable);
        }
        else if (index == lobbies.size() - 2)
            return "";
        else if (index == lobbies.size() - 1){
            return null;
        }
        else if(askToChoose(ASK_TO_VIEW_LOBBY_INFO)) {
            System.out.println(showLobbyInfo(lobbiesAvailable.get(lobbies.get(index))));
            if (!askToChoose("Do you want to join lobby(" + YES + ") or do you want to review the lobbies?(" + NO + ")"))
                return chooseLobbyToJoin(lobbiesAvailable);
        }
        return lobbies.get((index));
    }

    private boolean askToChoose(String question){
        while (true) {
            System.out.println(question);
            SafeScanner scanner = new SafeScanner(System.in);
            String input = scanner.nextLine();
            if (input.equals(YES))
                return true;
            else if (input.equals(NO))
                return false;
        }
    }

    private String showLobbyInfo(List<String> info) {
        StringBuilder stringBuilder = new StringBuilder() ;
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
        return stringBuilder.toString();
    }


    @Override
    public void showGameBoard(List<Cell> gameBoard) {
        printer.printBoard(gameBoard);
    }

    @Override
    public Cell chooseWorker() {
        while (true) {
            System.out.println("Choose your worker: ");
            Cell chosenWorker = chooseCell();
            if (chosenWorker.getOccupiedBy() != null)
                showErrorMessage("There's no worker in that cell!");
            else
                return chosenWorker;
        }
    }

    @Override
    public boolean chooseMatchReload(){
        while (true) {
            System.out.println("I found a match to reload! do you want to reload? (" + YES + "/" + NO + ")");
            SafeScanner scanner = new SafeScanner(System.in);
            String input = scanner.nextLine();
            if (input.equals(YES))
                return true;
            else if (input.equals(NO))
                return false;
        }
    }

    @Override
    public Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {
        printer.printBoard(gameBoard, walkableCells);   //TODO: enhance
        while (true) {
            System.out.println("Choose a cell to step on:");
            Cell chosenCell = chooseCell();
            for (Cell cell : walkableCells) {
                if (cell.getCoordX() == chosenCell.getCoordX() && cell.getCoordY() == chosenCell.getCoordY())
                    /* cannot use the .equals() method, as it checks other attributes too*/
                    return cell;
            }
            showErrorMessage("You can't go on that cell!");
        }
    }

    @Override
    public Cell buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {
        printer.printBoard(gameBoard, buildableCells);   //TODO: enhance
        while (true) {  //TODO: might extract private method for move/buildAction
            System.out.println("Choose a cell to build on:");
            Cell chosenCell = chooseCell();
            for (Cell cell : buildableCells) {
                if (cell.getCoordX() == chosenCell.getCoordX() && cell.getCoordY() == chosenCell.getCoordY())
                    /* cannot use the .equals() method, as it checks other attributes too*/
                    return cell;
            }
            showErrorMessage("You can't build on that cell!");
        }
    }

    @Override
    public Block chooseBlockToBuild(List<Block> buildableBlocks) {
        //buildableBlocks.size always > 1, see player in model
        System.out.println("Choose the block to build: ");
        List<String> blocks = new ArrayList<>();
        buildableBlocks.forEach(b -> blocks.add(b.toString()));
        return buildableBlocks.get(chooseFromList(blocks));
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
    public Cell placeWorker() {
        System.out.println("Place your Worker!");
        return chooseCell();
    }

    @Override
    public GodData chooseUserGod(List<GodData> possibleGods) {
        if (possibleGods.size() == 1) {
            showSuccessMessage("\nYour god is " + possibleGods.get(0).getName() + "\n");
            return possibleGods.get(0);
        }
        System.out.println("Choose your god:");
        List<String> gods = new ArrayList<>();
        possibleGods.forEach(g -> gods.add(g.getName()));
        return possibleGods.get(chooseFromList(gods));
    }

    @Override
    public List<GodData> chooseGameGods(List<GodData> allGods, int size) {
        List<GodData> chosenGods = new ArrayList<>();
        System.out.println("Choose " + (size) + " gods:");
        List<String> gods = new LinkedList<>();
        allGods.forEach(g -> gods.add(g.getName()));

        for (int i = 0; i < size; i++) {
            int choice = chooseFromList(gods);
            chosenGods.add(allGods.get(choice));
            gods.remove(choice);
            allGods.remove(choice);
        }
        return chosenGods;
    }

    @Override
    public String chooseStartingPlayer(List<String> players) {
        System.out.println("Choose the first player:");
        return players.get(chooseFromList(players));
    }

    @Override
    public PossibleActions chooseAction(List<PossibleActions> possibleActions) {
        if (possibleActions.size() == 1)
            return possibleActions.get(0);
        System.out.println("Select an action: ");
        List<String> actions = new ArrayList<>();
        possibleActions.forEach(a -> actions.add(a.toString()));
        return possibleActions.get(chooseFromList(actions));
    }

    private Cell chooseCell() {
        while (true) {
            System.out.println("Insert the cell coordinates (1-5):");
            System.out.print("row: ");
            SafeScanner scanner = new SafeScanner(System.in);
            int coordX = scanner.nextInt();
            System.out.print("col: ");
            int coordY = scanner.nextInt();
            if (!(coordX < 1 || coordY < 1 || coordX > 5 || coordY > 5))
                return new Cell(coordX - 1, coordY - 1);
            showErrorMessage("Coordinates out of bounds");
        }
    }

    private int chooseFromList(List<String> list) {
        int choice;
        while(true) {
            SafeScanner scanner = new SafeScanner(System.in);
            for (int i = 1; i < list.size() + 1; i++)
                System.out.println(i + "- " + list.get(i-1));
            choice = scanner.nextInt();
            if (choice > 0 && choice < list.size() + 1)
                break;
            showErrorMessage("Not valid");
        }

        return choice - 1;
    }
}
