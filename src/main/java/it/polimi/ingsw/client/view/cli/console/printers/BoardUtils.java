package it.polimi.ingsw.client.view.cli.console.printers;

import it.polimi.ingsw.client.view.cli.console.Console;
import it.polimi.ingsw.client.view.cli.console.graphics.components.PrintableObject;
import it.polimi.ingsw.client.view.cli.console.graphics.components.Window;
import it.polimi.ingsw.shared.dataClasses.GameBoard;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.dataClasses.PlayerData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.view.Constants.ANSI_RESET;

public abstract class BoardUtils extends Window {

    protected List<PlayerData> playerData;
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

    /**
     * Default constructor
     * <br>
     * Creates a new BoardUtils window, loading its settings from file
     *  @param parent the parent
     *
     */
    public BoardUtils(Window parent) throws IOException {
        super(parent);

        lastGameBoardPrinted = new GameBoard().cloneAllCells();

        boardWidth = Integer.parseInt(properties.getProperty("boardWidth"));
        boardHeight = Integer.parseInt(properties.getProperty("boardHeight"));
        verticalWallWidth = Integer.parseInt(properties.getProperty("verticalWallWidth"));
        horizontalWallWidth = Integer.parseInt(properties.getProperty("horizontalWallWidth"));
        cellWidth = Integer.parseInt(properties.getProperty("cellWidth"));
        cellHeight = Integer.parseInt(properties.getProperty("cellHeight"));
        emptyBoard = new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("emptyBoardPath")), boardWidth, boardHeight).getObject();
        cachedBoard = cloneMatrix(emptyBoard);

        try {
            cellFrames.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("standardCellFramePath")), cellWidth, cellHeight));

            buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("firstLevelTopPath")), cellWidth, cellHeight));
            buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("secondLevelTopPath")), cellWidth, cellHeight));
            buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("thirdLevelTopPath")), cellWidth, cellHeight));
            buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("domeTopPath")), cellWidth, cellHeight));

            workers.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("blueWorkerPath")), cellWidth, cellHeight));
            workers.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("purpleWorkerPath")), cellWidth, cellHeight));
            workers.add(new PrintableObject(this.getClass().getResourceAsStream(properties.getProperty("redWorkerPath")), cellWidth, cellHeight));
        } catch (IOException e) {
            Console.close();
        }
    }

    public abstract void showGameBoard();

    /**
     * Shows the current gameBoard on the screen, highlighting some cells
     *
     * @param toHighlight the cells to highlight
     */
    public abstract void showGameBoard(List<Cell> toHighlight);

    /**
     * Highlights the user's workers
     * @param cells the cells containing the user's workers
     */
    public abstract void highlightWorkers(List<Cell> cells);

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
     * @return a list containing the updated cells
     */
    protected List<Cell> updateCachedBoard(List<Cell> board) {
        List<Cell> updatedCells = new ArrayList<>();
        Cell currentCell;
        for (int i = 0; i < board.size(); i++) {
            currentCell = board.get(i);
            if (!board.get(i).equals(lastGameBoardPrinted.get(i))) {
                placeBlock(currentCell);
                updatedCells.add(currentCell);
            }
            if (currentCell.getOccupiedBy() != lastGameBoardPrinted.get(i).getOccupiedBy()) {
                placeWorker(currentCell);
                if (!updatedCells.contains(currentCell))
                    updatedCells.add(currentCell);
            }
        }
        lastGameBoardPrinted = board;
        return updatedCells;
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
            if (finalCol >= 0) System.arraycopy(input[row + startRow], startCol, subMatrix[row], 0, finalCol);
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
            if (worker != null)
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
