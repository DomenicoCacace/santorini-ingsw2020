package it.polimi.ingsw.view.cli.utils;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.GameBoard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Console Printing Utilities
 */
public class PrettyPrinter {
    private final int verticalWallWidth;
    private final int horizontalWallWidth;
    private final int cellWidth;
    private final int cellHeight;
    private final List<PrintableObject> buildingBlocks;
    private final List<PrintableObject> workers;
    private final PrintableObject cellFrame;
    private final String[][] emptyBoard;
    private final String[][] logo;
    private final String[][] cachedBoard;
    private List<Cell> lastGameBoardPrinted;

    /**
     * Default constructor
     * <p>
     * Loads the <i>graphics</i> from file into PrintableObjects
     *
     * @throws IOException if an I/O error occurs
     */
    public PrettyPrinter() throws IOException {
        // FIXME: Load from configuration file
        int tableWidth = 120 + 1;  // includes the ansi reset sequence as last element

        int tableHeight = 61;
        this.verticalWallWidth = 2;
        this.horizontalWallWidth = 1;
        this.cellWidth = 21 + 1;
        this.cellHeight = 11;

        buildingBlocks = new ArrayList<>();
        workers = new ArrayList<>();

        emptyBoard = new PrintableObject(this.getClass().getResourceAsStream("board.art"), tableWidth, tableHeight).getObject();
        cachedBoard = cloneMatrix(emptyBoard);
        lastGameBoardPrinted = new GameBoard().getAllCells();

        cellFrame = new PrintableObject(this.getClass().getResourceAsStream("cellFrame.art"), cellWidth, cellHeight);

        logo = new PrintableObject(this.getClass().getResourceAsStream("mainLogo.art"), 151, 24).getObject();

        buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream("blocks/firstLevelTop.art"), cellWidth, cellHeight));
        buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream("blocks/secondLevelTop.art"), cellWidth, cellHeight));
        buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream("blocks/thirdLevelTop.art"), cellWidth, cellHeight));
        buildingBlocks.add(new PrintableObject(this.getClass().getResourceAsStream("blocks/domeTop.art"), cellWidth, cellHeight));

        workers.add(new PrintableObject(this.getClass().getResourceAsStream("workers/blueWorker.art"), cellWidth, cellHeight));
        workers.add(new PrintableObject(this.getClass().getResourceAsStream("workers/purpleWorker.art"), cellWidth, cellHeight));
        workers.add(new PrintableObject(this.getClass().getResourceAsStream("workers/redWorker.art"), cellWidth, cellHeight));
    }

    /**
     * Prints the login screen
     */
    public void printLogin() {
        showMatrix(logo);
    }

    /**
     * Prints a given message
     *
     * @param message the message to print
     */
    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prints an error message
     *
     * @param error the error message to print
     */
    public void printError(String error) {
        System.out.println(error); //TODO: enhance
    }

    /**
     * Prints the gameBoard
     *
     * @param board the board to print
     */
    public void printBoard(List<Cell> board) {
        updateCachedBoard(board);
        showMatrix(cachedBoard);
    }

    /**
     * Prints the gameBoard, highlighting
     *
     * @param board       the board to print
     * @param toHighlight the cells to highlight
     */
    public void printBoard(List<Cell> board, List<Cell> toHighlight) {
        updateCachedBoard(board);
        String[][] gameBoard = cloneMatrix(cachedBoard);
        for (Cell cell : toHighlight) {
            highlight(cell, gameBoard);
        }
        showMatrix(gameBoard);
    }

    /**
     * Sets the first version of a board, useful in case of a restoration from a saved game
     *
     * @param gameBoard the new gameBoard to set
     */
    public void setCachedBoard(List<Cell> gameBoard) {
        this.lastGameBoardPrinted = gameBoard;
        overrideCachedBoard(gameBoard);
    }

    /**
     * Updates the cached board
     * <p>
     * Assuming that both the cached and new board are ordered in the same way (see {@linkplain GameBoard#getAllCells()}),
     * this method updates the cached board to a new provided version, ready to be printed.
     *
     * @param board the updated board
     */
    private void updateCachedBoard(List<Cell> board) {
        for (int i = 0; i < board.size(); i++) {
            if (!board.get(i).equals(lastGameBoardPrinted.get(i)))
                drawBlock(board.get(i));
            if (board.get(i).getOccupiedBy() != lastGameBoardPrinted.get(i).getOccupiedBy())
                drawWorker(board.get(i));
        }
        lastGameBoardPrinted = board;   //TODO: check if safe
    }

    private void overrideCachedBoard(List<Cell> board) {
        board.forEach(c -> {
            restoreBuild(c);
            drawWorker(c);
        });
    }

    /**
     * Draws a frame around some given cells
     * <p>
     * Since the frame should not be saved on the <i>original</i> board, the {@linkplain #drawOnCell} method requires
     * a String[][] parameter on which the frame will be printed.
     *
     * @param cell  the cell to highlight
     * @param board the board to print the frame on
     */
    private void highlight(Cell cell, String[][] board) {
        drawOnCell(cellFrame, board, cell.getCoordX(), cell.getCoordY());
    }

    /**
     * Draws a building block on a cell
     *
     * @param cell the cell to draw the building on
     */
    private void drawBlock(Cell cell) {
        if (cell.getBlock().getHeight() > 0) {
            PrintableObject block = buildingBlocks.get(cell.getBlock().getHeight() - 1);
            //restoreBuild(cell); //TODO: send specific message from server to client to avoid restoring
            drawOnCell(block, cachedBoard, cell.getCoordX(), cell.getCoordY());
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
            drawOnCell(buildingBlocks.get(i), cachedBoard, cell.getCoordX(), cell.getCoordY());
    }

    /**
     * Draws a Worker on a cell
     * <p>
     * In case the given {@linkplain Cell#getOccupiedBy()} is not null, calls the {@link #drawOnCell} method, passing
     * the <i>sprite</i> corresponding to the worker's color; if the cell contains no worker, the original board
     * texture is reloaded, then the building, if existing, has to be restored.
     *
     * @param cell the cell containing
     */
    private void drawWorker(Cell cell) {
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
            drawOnCell(worker, cachedBoard, cell.getCoordX(), cell.getCoordY());
        } else {
            int rowOffset = (horizontalWallWidth + cell.getCoordX() * (horizontalWallWidth + cellHeight));
            int colOffset = (verticalWallWidth + cell.getCoordY() * (verticalWallWidth + cellWidth));
            for (int row = rowOffset; row < cellHeight + rowOffset; row++) {
                for (int col = colOffset; col < cellWidth + colOffset; col++) {
                    if (!emptyBoard[row][col].contains("\033[0m"))  // ignores resets to preserve the original background
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
    private void drawOnCell(PrintableObject object, String[][] board, int coordX, int coordY) {
        int rowOffset = (horizontalWallWidth + coordX * (horizontalWallWidth + cellHeight));
        int colOffset = (verticalWallWidth + coordY * (verticalWallWidth + cellWidth));

        for (int row = 0; row < cellHeight; row++) {
            for (int col = 0; col < cellWidth; col++) {
                if (!object.getObject()[row][col].contains("\033[0m"))  // ignores resets to preserve the original background
                    board[rowOffset + row][colOffset + col] = object.getObject()[row][col];
            }

        }
    }

    /**
     * Prints a given String matrix
     *
     * @param matrix the Matrix to print
     */
    private void showMatrix(String[][] matrix) {
        System.out.println();
        for (String[] strings : matrix) {
            System.out.print("\t");
            for (int col = 0; col < matrix[0].length; col++) {
                System.out.print(strings[col]);
            }
            System.out.println();
        }
        System.out.println();
    }


    /**
     * Clones a String matrix
     *
     * @param input the matrix to clone
     * @return a copy of the input
     */
    private String[][] cloneMatrix(String[][] input) {
        String[][] clone = new String[input.length][input[0].length];
        for (int row = 0; row < input.length; row++) {
            System.arraycopy(input[row], 0, clone[row], 0, input[0].length);
        }
        return clone;
    }

    /**
     * Clears the screen by creating several new lines
     */
    private void clearScreen() {
        for (int i = 0; i < 100; i++)
            System.out.println();
    }

}
