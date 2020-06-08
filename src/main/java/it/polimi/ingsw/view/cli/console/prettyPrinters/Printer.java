package it.polimi.ingsw.view.cli.console.prettyPrinters;

import it.polimi.ingsw.model.Block;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.GameBoard;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.cli.console.graphics.components.PrintableObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static it.polimi.ingsw.view.Constants.ANSI_RESET;

/**
 * Console Printing Utilities
 */
public abstract class Printer {

    public static final int sceneWidth = 211;
    public static final int sceneHeight = 63;
    /**
     * The UI which created the printer
     */
    protected final CLI cli;
    protected List<PlayerData> playerData;
    protected final PrintableObject mainLogo;
    protected final int boardWidth;
    protected final int boardHeight;
    protected final int verticalWallWidth;
    protected final int horizontalWallWidth;
    protected final int cellWidth;
    protected final int cellHeight;
    protected final List<PrintableObject> buildingBlocks = new ArrayList<>();
    protected final List<PrintableObject> workers = new ArrayList<>();
    protected final List<PrintableObject> cellFrames = new ArrayList<>();
    protected final String[][] emptyBoard;
    protected final String[][] cachedBoard;
    protected List<Cell> lastGameBoardPrinted;

    Properties properties = new Properties();

    /**
     * Default constructor
     *
     * @param cli the view which created the printer
     */
    protected Printer(CLI cli) throws IOException {
        this.cli = cli;
        mainLogo = PrintableObject.load(this, "mainLogo.art", 151, 24);
        properties.loadFromXML(this.getClass().getResourceAsStream(this.getClass().getSimpleName() + ".xml"));

        boardWidth = Integer.parseInt(properties.getProperty("boardWidth"));
        boardHeight = Integer.parseInt(properties.getProperty("boardHeight"));
        verticalWallWidth = Integer.parseInt(properties.getProperty("verticalWallWidth"));
        horizontalWallWidth = Integer.parseInt(properties.getProperty("horizontalWallWidth"));
        cellWidth = Integer.parseInt(properties.getProperty("cellWidth"));
        cellHeight = Integer.parseInt(properties.getProperty("cellHeight"));
        emptyBoard = new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("emptyBoardPath")), boardWidth, boardHeight).getObject();
        cachedBoard = cloneMatrix(emptyBoard);

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
     * Updates information about the game and the players
     *
     * @param board the game board
     * @param players information about the players
     */
    public void updateGameData(List<Cell> board, List<PlayerData> players) {
        this.playerData = players;
        setCachedBoard(board);
        showGameBoard(board);
    }

    /**
     * Loads the graphics resources for the game and sets up stuff
     */
    public void enterGameMode() {
        try {
            lastGameBoardPrinted = new GameBoard().getAllCells();

            cellFrames.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("standardCellFramePath")), cellWidth, cellHeight));

            buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("firstLevelTopPath")), cellWidth, cellHeight));
            buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("secondLevelTopPath")), cellWidth, cellHeight));
            buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("thirdLevelTopPath")), cellWidth, cellHeight));
            buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("domeTopPath")), cellWidth, cellHeight));

            workers.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("blueWorkerPath")), cellWidth, cellHeight));
            workers.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("purpleWorkerPath")), cellWidth, cellHeight));
            workers.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("redWorkerPath")), cellWidth, cellHeight));
        } catch (IOException e) {
        }
    }

    /**
     * Prints the game board on the screen
     *
     * @param gameBoard the board to print
     */
    public abstract void showGameBoard(List<Cell> gameBoard);

    /**
     * Prints the game board on the screen
     *
     * @param gameBoard the board to print
     */
    public abstract void showGameBoard(List<Cell> gameBoard, List<Cell> toHighlight);

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
    protected abstract void highlightWorkers(List<Cell> cells);

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

    /**
     * Sets the first version of a board, useful in case of a restoration from a saved game
     *
     * @param gameBoard the new gameBoard to set
     */
    protected void setCachedBoard(List<Cell> gameBoard) {
        this.lastGameBoardPrinted = gameBoard;
        overrideCachedBoard(gameBoard);
    }

    /**
     * Overrides, without checking for diffs, the cached gameBoard
     *
     * @param board the new board
     */
    protected void overrideCachedBoard(List<Cell> board) {
        board.forEach(c -> {
            restoreBuild(c);
            placeWorker(c);
        });
    }

    /**
     * Updates the cached board
     * <p>
     * Assuming that both the cached and new board are ordered in the same way (see {@linkplain GameBoard#getAllCells()}),
     * this method updates the cached board to a new provided version, ready to be printed.
     *
     * @param board the updated board
     */
    protected void updateCachedBoard(List<Cell> board) {
        for (int i = 0; i < board.size(); i++) {
            if (!board.get(i).equals(lastGameBoardPrinted.get(i)))
                placeBlock(board.get(i));
            if (board.get(i).getOccupiedBy() != lastGameBoardPrinted.get(i).getOccupiedBy())
                placeWorker(board.get(i));
        }
        lastGameBoardPrinted = board;
    }

    /**
     * Clones a String matrix
     *
     * @param input the matrix to clone
     * @return a copy of the input
     */
    protected String[][] cloneMatrix(String[][] input) {
        String[][] clone = new String[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            System.arraycopy(input[row], 0, clone[row], 0, input[0].length);
        }
        return clone;
    }

    /**
     * Retrieves a subMatrix from a bigger matrix
     *
     * @param input the input matrix
     * @param startRow the input row coordinate to start extracting the subMatrix
     * @param startCol the input column coordinate to start extracting the subMatrix
     * @param finalRow the subMatrix number of rows
     * @param finalCol the subMatrix number of columns
     * @return the required subMatrix
     */
    protected String[][] subMatrix(String[][] input, int startRow, int startCol, int finalRow, int finalCol) {
        String[][] subMatrix = new String[finalRow][finalCol];
        for (int row = 0; row < finalRow; row++) {
            for (int col = 0; col < finalCol; col++)
                subMatrix[row][col] = input[row+startRow][col+startCol];
        }
        return subMatrix;
    }

    /**
     * Draws a frame around some given cells
     * <p>
     * Since the frame should not be saved on the <i>original</i> board, the {@linkplain #decorateCell} method requires
     * a String[][] parameter on which the frame will be printed.
     *
     * @param cell  the cell to highlight
     * @param board the board to print the frame on
     */
    protected void highlight(Cell cell, String[][] board) {
        decorateCell(cellFrames.get(0), board, cell.getCoordX(), cell.getCoordY());
    }

    /**
     * Draws a building block on a cell
     *
     * @param cell the cell to draw the building on
     */
    private void placeBlock(Cell cell) {
        if (cell.getBlock().getHeight() > 0) {
            PrintableObject block = buildingBlocks.get(cell.getBlock().getHeight() - 1);
            decorateCell(block, cachedBoard, cell.getCoordX(), cell.getCoordY());
        }
    }

    /**
     * Reloads all the building sprites on a cell
     *
     * @param cell the cell to restore
     */
    private void restoreBuild(Cell cell) {
        int level = cell.getBlock().getHeight();

        for (int i = 0; i < level; i++)
            decorateCell(buildingBlocks.get(i), cachedBoard, cell.getCoordX(), cell.getCoordY());
    }

    /**
     * Draws a Worker on a cell
     * <p>
     * In case the given {@linkplain Cell#getOccupiedBy()} is not null, calls the {@link #decorateCell} method, passing
     * the <i>sprite</i> corresponding to the worker's color; if the cell contains no worker, the original board
     * texture is reloaded, then the building, if existing, has to be restored.
     *
     * @param cell the cell containing
     */
    private void placeWorker(Cell cell) {
        PrintableObject worker;
        if (cell.getOccupiedBy() != null) {
            switch (cell.getOccupiedBy().getColor()) {
                case BLUE:
                    worker = workers.get(0);
                    break;
                case PURPLE:
                    worker = workers.get(1);
                    break;
                case RED:
                    worker = workers.get(2);
                    break;
                default:
                    worker = null;  // never gets here
            }
            decorateCell(worker, cachedBoard, cell.getCoordX(), cell.getCoordY());
        } else {
            int rowOffset = (horizontalWallWidth + cell.getCoordX() * (horizontalWallWidth + cellHeight));
            int colOffset = (verticalWallWidth + cell.getCoordY() * (verticalWallWidth + cellWidth));
            for (int row = rowOffset; row < cellHeight + rowOffset; row++) {
                for (int col = colOffset; col < cellWidth + colOffset; col++) {
                    if (!emptyBoard[row][col].contains(ANSI_RESET))  // ignores resets to preserve the original background
                        cachedBoard[row][col] = emptyBoard[row][col];
                }
            }
            restoreBuild(cell);
        }
    }

    /**
     * "Decorates" a cell
     * Given a PrintableObject and the cell coordinates to draw on, this method alters the board representation.
     * This method requires the PrintableObject to be the same size of a cell; in any other case, the behaviour is not
     * guaranteed.
     *
     * @param object the object to print
     * @param coordX the X coordinate of the cell to print
     * @param coordY the Y coordinate of the cell to print
     */
    private void decorateCell(PrintableObject object, String[][] board, int coordX, int coordY) {
        int rowOffset = (horizontalWallWidth + coordX * (horizontalWallWidth + cellHeight));
        int colOffset = (verticalWallWidth + coordY * (verticalWallWidth + cellWidth));

        for (int row = 0; row < cellHeight; row++) {
            for (int col = 0; col < cellWidth; col++) {
                if (!object.getObject()[row][col].contains("\033[0m"))  // ignores resets to preserve the original background
                    board[rowOffset + row][colOffset + col] = object.getObject()[row][col];
            }

        }
    }
}
