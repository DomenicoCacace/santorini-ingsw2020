package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.view.ViewInterface;
import it.polimi.ingsw.view.cli.utils.PrettyPrinter;
import it.polimi.ingsw.view.cli.utils.SafeScanner;

import java.util.*;


/**
 * Command Line Interface manager
 */
public class CLI implements ViewInterface {
    public static final String YES = "y";
    public static final String NO = "n";
    private final PrettyPrinter printer;

    public CLI() {
        printer = new PrettyPrinter();
    }

    @Override
    public String askIP(){
        printer.printLogin();
        SafeScanner scanner = new SafeScanner(System.in);
        System.out.print("\t\tInsert the server address: ");
        return scanner.nextLine();
    }

    @Override
    public String askUsername(){
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
    public void turnBegin() {

    }


    @Override
    public void showGameBoard(List<Cell> gameBoard) {
        System.out.println(printer.getGameBoard(gameBoard) + "\r");
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
            if (input.equals(NO))
                return false;
        }
    }

    @Override
    public Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {
        System.out.println(printer.highlightCells(gameBoard, walkableCells));   //TODO: enhance
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
        System.out.println(printer.highlightCells(gameBoard, buildableCells));   //TODO: enhance
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
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
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
            showSuccessMessage("Your god is " + possibleGods.get(0).getName());
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
    public int choosePlayersNumber() {
        while (true) {
            System.out.println("Choose the number of players for the match: ");
            SafeScanner scanner = new SafeScanner(System.in);
            int numPlayers = scanner.nextInt();
            if (numPlayers < 2)
                showErrorMessage("You can't play alone!");
            else if (numPlayers > 3)
                showErrorMessage("Too many players!");  // "un'orgia di players"
            else {
                System.out.println("Good!");
                return numPlayers;
            }
        }
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
            System.out.print("X: ");
            SafeScanner scanner = new SafeScanner(System.in);
            int coordX = scanner.nextInt();
            System.out.print("Y: ");
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
