package it.polimi.ingsw.view.cli.utils;

import it.polimi.ingsw.model.Cell;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Console Printing Utilities
 */
public class PrettyPrinter {

    private final int width;
    private final int height;
    private final int boardSize;
    private final int rowToColumnRatio;
    private final int horizontalDashes;
    private final int verticalDashes;

    /**
     * Default constructor
     * <p>
     * Loads its values from a file
     * </p>
     */
    public PrettyPrinter() {
        this.width = 145;
        this.height = 125;
        this.boardSize = 5;
        this.rowToColumnRatio = 3;
        this.horizontalDashes = (maxPrintableSize() - boardSize - 1) / boardSize;
        this.verticalDashes = horizontalDashes / rowToColumnRatio;
    }

    /**
     * Prints the login screen
     */
    public void printLogin() {
        String text = fileToString(this.getClass().getResourceAsStream("mainLogo.txt"));
        System.out.println(text);
    }

    /**
     * Provides the game board
     * <p>
     * Provides a string containing the full board representation, starting from a list of {@linkplain Cell}s
     * containing ALL the cells of the game board (25 total cells by default)
     * </p>
     *
     * @param cells the game board, represented as an array of cells
     */
    public String getGameBoard(List<Cell> cells) {
        StringBuilder board = new StringBuilder(emptyGameBoard());

        int center;
        for (Cell cell : cells) {
            center = (horizontalDashes - cell.getBlock().toString().length()) / 2 + 2;
            board.replace((maxPrintableSize() + 1) * (2 + cell.getCoordX() * (1 + verticalDashes)) +
                            cell.getCoordX() * (1 + verticalDashes) + 1 +
                            cell.getCoordY() * (horizontalDashes + 2) + center,
                    (maxPrintableSize() + 1) * (2 + cell.getCoordX() * (1 + verticalDashes)) +
                            cell.getCoordX() * (1 + verticalDashes) + 1 +
                            cell.getCoordY() * (horizontalDashes + 2) + cell.getBlock().toString().length() + center,
                    cell.getBlock().name());
            if (cell.getOccupiedBy() != null) {
                center = (horizontalDashes - cell.getOccupiedBy().getColor().toString().length()) / 2 + 2;
                board.replace((maxPrintableSize() + 1) * (4 + cell.getCoordX() * (1 + verticalDashes)) +
                                cell.getCoordX() * (1 + verticalDashes) + 1 +
                                cell.getCoordY() * (horizontalDashes + 4) + center,
                        (maxPrintableSize() + 1) * (4 + cell.getCoordX() * (1 + verticalDashes)) +
                                cell.getCoordX() * (1 + verticalDashes) + 1 +
                                cell.getCoordY() * (horizontalDashes + 4) + cell.getOccupiedBy().getColor().toString().length() + center,
                        cell.getOccupiedBy().getColor().toString());
            }
        }
        return board.toString();
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
     * Provides a game board with highlighted cells
     * <p>
     * Given:
     * <ul>
     *     <li>n: number of horizontal dashes per cell</li>
     *     <li>k: ratio between horizontal and vertical dashes</li>
     *     <li>len: length of a row, including the '\n' character (maxPrintableSize() + 1)</li>
     *     <li>x: the X coordinate of the cell to highlight</li>
     *     <li>y: the Y coordinate of the cell to highlight</li>
     * </ul>
     * we can define a bijection between the printed matrix and the string; for example, to find the position of the
     * top left corner of the frame for a cell, we can apply the following formula:
     * <br>
     * <code>
     *     TCL = (len*(1+x*n/k)) + (x*(1+n/k)+1) + (y*(2+n)) + 1
     * </code>
     * where:
     * <ul>
     * <li><code>len*(1+x*(1+k/n))</code> is the vertical offset per board row</li>
     * <li><code>x*(1+n/k)+1</code> is the number of '\n' characters at the end of each line</li>
     * <li><code>y*(2+n)</code> is the horizontal offset per board column</li>
     * <li><code>+1</code> to "move" into the top left corner</li>
     * </ul>
     * By changing the factors of the equation, we can obtain every position inside the drawn cell, in order to draw
     * a frame to highlight some cells.
     * <br>
     * Could probably be organized better but it's not worth it, so we'll leave it as is.
     * </p>
     *
     * @param gameBoard the board to build
     * @param cells,    the list of cells to highlight
     * @return the board, as a string, with highlighted cells
     */
    public String highlightCells(List<Cell> gameBoard, List<Cell> cells) {
        String board = getGameBoard(gameBoard);
        StringBuilder toPrint = new StringBuilder(String.valueOf(board));
        for (Cell cell : cells) {

            toPrint.setCharAt((maxPrintableSize() + 1) * (1 + cell.getCoordX() * (1 + verticalDashes)) +
                            cell.getCoordX() * (1 + verticalDashes) + 1 +
                            cell.getCoordY() * (horizontalDashes + 2) + 1,
                    TableDividers.TOP_LEFT_CORNER.getAsChar());

            for (int i = 1; i < horizontalDashes; i++) {
                toPrint.setCharAt((maxPrintableSize() + 1) * (1 + cell.getCoordX() * (1 + verticalDashes)) +
                                cell.getCoordX() * (1 + verticalDashes) + 1 +
                                cell.getCoordY() * (horizontalDashes + 2) + 1 + i,
                        TableDividers.HORIZONTAL_LINE.getAsChar());

                toPrint.setCharAt((maxPrintableSize() + 1) * (verticalDashes + cell.getCoordX() * (1 + verticalDashes)) +
                                cell.getCoordX() * (1 + verticalDashes) + verticalDashes +
                                cell.getCoordY() * (horizontalDashes + 2) + 1 + i,
                        TableDividers.HORIZONTAL_LINE.getAsChar());
            }

            for (int i = 2; i < verticalDashes; i++) {
                toPrint.setCharAt((maxPrintableSize() + 1) * (i + cell.getCoordX() * (1 + verticalDashes)) +
                                (cell.getCoordX()) * (1 + verticalDashes) + i +
                                cell.getCoordY() * (horizontalDashes + 2) + 1,
                        TableDividers.VERTICAL_LINE.getAsChar());

                toPrint.setCharAt((maxPrintableSize() + 1) * (i + cell.getCoordX() * (1 + verticalDashes)) +
                                (cell.getCoordX()) * (1 + verticalDashes) + i +
                                cell.getCoordY() * (horizontalDashes + 2) + horizontalDashes + 1,
                        TableDividers.VERTICAL_LINE.getAsChar());
            }

            toPrint.setCharAt((maxPrintableSize() + 1) * (1 + cell.getCoordX() * (1 + verticalDashes)) +
                            cell.getCoordX() * (1 + verticalDashes) + 2 +
                            cell.getCoordY() * (horizontalDashes + 2) + horizontalDashes,
                    TableDividers.TOP_RIGHT_CORNER.getAsChar());

            toPrint.setCharAt((maxPrintableSize() + 1) * (verticalDashes + cell.getCoordX() * (1 + verticalDashes)) +
                            cell.getCoordX() * (1 + verticalDashes) + verticalDashes +
                            cell.getCoordY() * (horizontalDashes + 2) + 1,
                    TableDividers.BOTTOM_LEFT_CORNER.getAsChar());


            toPrint.setCharAt((maxPrintableSize() + 1) * (verticalDashes + cell.getCoordX() * (1 + verticalDashes)) +
                            cell.getCoordX() * (1 + verticalDashes) + 1 + verticalDashes +
                            cell.getCoordY() * (horizontalDashes + 2) + horizontalDashes,
                    TableDividers.BOTTOM_RIGHT_CORNER.getAsChar());

        }
        return toPrint.toString();
    }

    public void printError(String error) {
        System.out.println(error); //TODO: enhance
    }


    /**
     * Provides the shortest side of the console
     * <p>
     * In order to print a well-formatted square board, it is needed not to exceed the shortest side of the console.
     * <br>
     * The result is provided in terms of number of printable characters,
     * </p>
     *
     * @return the minimum <i>dimension</i> of the console
     */
    private int maxPrintableSize() {
        return Math.min(width, height);
    }

    /**
     * Provides a printable gameBoard
     * <p>
     * Given some dimensions (see {@linkplain #PrettyPrinter()}), this method creates a printable String, containing a
     * formatted game board.
     * </p>
     *
     * @return a String containing the board
     */
    private String emptyGameBoard() {
        StringBuilder firstLine = new StringBuilder(TableDividers.TOP_LEFT_CORNER.toString());
        for (int cellNum = 0; cellNum < boardSize; cellNum++) {
            for (int i = 0; i < (maxPrintableSize() - boardSize) / boardSize; i++)
                firstLine.append(TableDividers.HORIZONTAL_LINE.toString());
            firstLine.append(TableDividers.HORIZONTAL_T_DOWN.toString());
        }
        firstLine = new StringBuilder(firstLine.substring(0, firstLine.length() - 1));
        firstLine.append(TableDividers.TOP_RIGHT_CORNER.toString());

        String middleLine = firstLine.toString().replace(TableDividers.TOP_LEFT_CORNER.toString(),
                TableDividers.VERTICAL_LINE.toString());
        middleLine = middleLine.replace(TableDividers.HORIZONTAL_T_DOWN.toString(),
                TableDividers.VERTICAL_LINE.toString());
        middleLine = middleLine.replace(TableDividers.TOP_LEFT_CORNER.toString(),
                TableDividers.VERTICAL_LINE.toString());
        middleLine = middleLine.replace(TableDividers.TOP_RIGHT_CORNER.toString(),
                TableDividers.VERTICAL_LINE.toString());
        middleLine = middleLine.replace(TableDividers.HORIZONTAL_LINE.toString(), " ");

        String middleDividerLine = firstLine.toString().replace(TableDividers.TOP_LEFT_CORNER.toString(),
                TableDividers.VERTICAL_T_RIGHT.toString());
        middleDividerLine = middleDividerLine.replace(TableDividers.TOP_RIGHT_CORNER.toString(),
                TableDividers.VERTICAL_T_LEFT.toString());
        middleDividerLine = middleDividerLine.replace(TableDividers.HORIZONTAL_T_DOWN.toString(),
                TableDividers.CROSS.toString());

        String lastLine = firstLine.toString().replace(TableDividers.TOP_LEFT_CORNER.toString(),
                TableDividers.BOTTOM_LEFT_CORNER.toString());
        lastLine = lastLine.replace(TableDividers.TOP_RIGHT_CORNER.toString(),
                TableDividers.BOTTOM_RIGHT_CORNER.toString());
        lastLine = lastLine.replace(TableDividers.HORIZONTAL_T_DOWN.toString(),
                TableDividers.HORIZONTAL_T_UP.toString());

        StringBuilder toPrint = new StringBuilder(firstLine + "\n");
        for (int cellNum = 0; cellNum < boardSize; cellNum++) {
            for (int i = 0; i < (maxPrintableSize() - boardSize - 1) / (rowToColumnRatio * boardSize); i++)
                toPrint.append(middleLine).append("\n");

            toPrint.append(middleDividerLine).append("\n");
        }
        toPrint = new StringBuilder(toPrint.substring(0, toPrint.length() - middleDividerLine.length() - 1));
        toPrint.append(lastLine).append("\n");


        return toPrint.toString();
    }

    /**
     * Converts a plaintext file to a String object
     * <p>
     * Since the CLI uses some ASCII arts that might be difficult to recreate, we decided to include them in the
     * resources instead of generating them every time.
     * </p>
     *
     * @param filepath the file to convert, as an {@linkplain InputStream}
     * @return the String representation of the file
     */
    private String fileToString(InputStream filepath) {
        StringBuilder string = new StringBuilder();
        Scanner reader = new Scanner(filepath);
        while (reader.hasNextLine())
            string.append(reader.nextLine()).append("\n");
        reader.close();
        return string.toString();
    }
}
