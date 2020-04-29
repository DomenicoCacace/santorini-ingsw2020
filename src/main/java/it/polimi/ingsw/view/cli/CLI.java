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
    private final PrettyPrinter printer;

    public CLI() {
        printer = new PrettyPrinter();
    }

    @Override
    public List<String> loginScreen() {
        List<String> loginParameters = new ArrayList<>();
        printer.printLogin();
        SafeScanner scanner = new SafeScanner(System.in);
        System.out.print("\t\tInsert the server address: ");
        String address = scanner.nextLine();
        //TODO: perform checks
        loginParameters.add(0, address);
        System.out.print("\t\tInsert your username: ");
        String username = scanner.nextLine();  //TODO: add a maximum username length?
        loginParameters.add(1, username);
        return loginParameters;
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
    public Cell moveAction(List<Cell> gameBoard, List<Cell> walkableCells) {
        if (walkableCells.size() == 0) {
            showErrorMessage("No walkable cells available.");
            return null;//TODO: make the player choose another action(?)
        } else {
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
    }

    @Override
    public Cell buildAction(List<Cell> gameBoard, List<Cell> buildableCells) {
        if (buildableCells.size() == 0) {
            showErrorMessage("No buildable cells available.");
            return null;//TODO: make the player choose another action(?)
        } else {
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
    }

    @Override
    public Block chooseBlockToBuild(List<Block> buildableBlocks) {
        //buildableBlocks.size always > 1, see player in model
        while (true) {
            System.out.println("Choose the block to build: ");
            buildableBlocks.forEach(block -> System.out.println("-" + block.toString()));

            SafeScanner scanner = new SafeScanner(System.in);
            String chosenBlock = scanner.nextLine();
            for (Block block : buildableBlocks) {
                if (block.toString().equals(chosenBlock))
                    return block;
            }
            showErrorMessage("Block not valid!");
        }
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
        while (true) {
            System.out.println("Choose your god:");
            possibleGods.forEach(godData -> System.out.println("-" + godData.getName()));

            SafeScanner scanner = new SafeScanner(System.in);
            String name = scanner.nextLine();

            for (GodData data : possibleGods) {
                if (data.getName().equals(name)) {
                    showSuccessMessage("Your god is " + data.getName());
                    return data;
                }
            }
            showErrorMessage("God not valid!");
        }
    }

    @Override
    public List<GodData> chooseGameGods(List<GodData> allGods, int size) {
        List<GodData> chosenGods = new ArrayList<>();
        while (true) {
            System.out.println("Choose " + (size - chosenGods.size()) + " gods:");
            allGods.forEach(godData -> System.out.println("-" + godData.getName()));

            for (int i = 0; i < size; i++) {
                SafeScanner scanner = new SafeScanner(System.in);
                String name = scanner.nextLine();

                for (GodData data : allGods) {
                    if (data.getName().equals(name))
                        chosenGods.add(data);
                }
                Set<GodData> checker = new HashSet<>(chosenGods);   // does not accept duplicates
                if (chosenGods.size() <= i && checker.size() < chosenGods.size()) {   // the god was not found, hence not added to the list
                    showErrorMessage("God choice not valid!");
                    i--;
                } else {
                    showSuccessMessage("Nice!");
                }
            }
            return chosenGods;  // its size will always be the same as size
        }
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
        while (true) {
            System.out.println("Choose the first player:");
            players.forEach(player -> System.out.println("-" + player));
            SafeScanner scanner = new SafeScanner(System.in);
            String first = scanner.nextLine();
            if (players.contains(first)) {
                System.out.println("Gotcha!");
                return first;
            }
            showErrorMessage(first + " is not a valid player");  // FIXME: manage upper/lowercase
        }
    }

    @Override
    public PossibleActions chooseAction(List<PossibleActions> possibleActions) {
        while (true) {
            System.out.println("Select an action: ");
            possibleActions.forEach(possibleAction -> System.out.println("-" + possibleAction.toString()));
            SafeScanner scanner = new SafeScanner(System.in);
            String chosenAction = scanner.nextLine();
            for (PossibleActions possibleAction : possibleActions) {
                if (possibleAction.toString().equals(chosenAction))
                    return possibleAction;
            }
            System.out.println(possibleActions + " is not a valid action");
        }
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
}
