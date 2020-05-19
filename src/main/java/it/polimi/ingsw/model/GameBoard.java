package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Representation of the game field
 * <p>
 * The GameBoard class contains the actual representation of the board via a square {@link Cell} matrix (the single
 * cells contain each further information about the buildings and, if present, the occupant).
 * <br>
 * During the development, we decided to allow the board to be transformed, via a dedicated method, into a
 * {@link ArrayList}, since it can be easier to manipulate (e.g. during tests or to be sent over the network).
 * <br>
 * This class uses the {@link JsonCreator}, {@link JsonProperty} and {@link JsonIdentityInfo} in order to
 * serialize and save the board on a file (as a part of the {@link Game} class), to be able to restore the game
 * status upon a server failure.
*/
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = GameBoard.class)
public class GameBoard {
    private static final int DIMENSION = 5;
    private final Cell[][] board;

    /**
     * Jackson constructor
     * @param allCells the gameBoard to restore, as array of cells
     */
    @JsonCreator
    public GameBoard(@JsonProperty("allCells") ArrayList<Cell> allCells) {
        this.board = new Cell[DIMENSION][DIMENSION];
        int tmpX, tmpY;
        for (int i = 0; i < DIMENSION * DIMENSION; i++) {
            tmpX = allCells.get(i).getCoordX();
            tmpY = allCells.get(i).getCoordY();
            this.board[tmpX][tmpY] = new Cell(tmpX, tmpY, allCells.get(i).hasDome(), allCells.get(i).getOccupiedBy(), allCells.get(i).getBlock());
        }
    }

    /**
     * Default constructor
     * Creates a matrix of 25 Cells
     */
    public GameBoard() {
        this.board = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                board[i][j] = new Cell(i, j);
            }
        }
    }

    /**
     * Copy constructor
     * @param gameBoard the gameBoard to restore
     */
    private GameBoard(GameBoard gameBoard) {
        this.board = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                this.board[i][j] = gameBoard.board[i][j].cloneCell();
            }
        }
    }

    /**
     * Returns a cell by its coordinates
     * <p>
     * If the indexes are out of bounds, returns <i>null</i>
    *
     * @param x the X coordinate of the cell to return
     * @param y the Y coordinate of the cell to return
     * @return the cell corresponding to the coordinates if exists, <i>null</i> otherwise
     */
    public Cell getCell(int x, int y) {
        if (isInsideGameBoard(x, y))
            return board[x][y];
        return null; //out of bound, will never happen
    }

    /**
     * "Proper" <i>cell</i> getter
     * <p>
     * Given a generic Cell object, returns the corresponding Cell object contained in the game board, based on its
     * coordinates
    *
     * @param cell the cell to get the coordinates from
     * @return the corresponding cell from the game board
     */
    public Cell getCell(Cell cell) {
        int tmpX = cell.getCoordX();
        int tmpY = cell.getCoordY();
        return getCell(tmpX, tmpY);
    }

    /**
     * Checks if cell coordinates are legal
     * <p>
     * The cell coordinates are considered legal if both of them are
     * between 0 and the board {@linkplain #DIMENSION}, minus one
    *
     * @param x the X coordinate of the cell to check
     * @param y the Y coordinate of the cell to check
     * @return true if the coordinates are legal, false otherwise
     */
    public boolean isInsideGameBoard(int x, int y) {
        return 0 <= x && x < DIMENSION && 0 <= y && y < DIMENSION;
    }

    /**
     * Returns all the cells of the game board
     * <p>
     * This method creates an {@linkplain ArrayList} of {@linkplain Cell}s, containing all the
     * cells in the {@linkplain GameBoard}, sorted by row, low to high
    *
     * @return an {@linkplain ArrayList} containing all the cells of the board
     */
    public ArrayList<Cell> getAllCells() {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                cells.add(getCell(i, j));
            }
        }
        return cells;
    }

    public ArrayList<Cell> cloneAllCells() {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                cells.add(board[i][j].cloneCell());
            }
        }
        return cells;
    }

    /**
     * Calculates the Cell <i>behind</i> a given Cell
     * <p>
     * Given two Cells (src, dest), the <i>Cell behind dest</i>, if exists, is the cell
     * at distance 1 from the <i>dest</i> cell, at distance (dist(src, dest) + 1) from <i>src</i>
     * lying on the line passing by src and dest. If the resulting coordinates are outside the board,
     * the cell behind does not exists. This method DOES NOT check for height differences in any way.
     * <br>
     * <b>NOTE:</b> this method is supposed to be used with cells which distance between
     * each other is one; it still works with <i>more distant</i> cells, if the cells are
     * aligned on an axis (same X or Y coordinate) or on a diagonal (the modulus of the
     * difference between the X coordinates is equal to the modulus of the difference of the
     * Y coordinates of the two Cells. In any other case, for the sake of the project at
     * this state, the behavior of the method is not granted.
     *
    *
     * @param src  the first point of the line
     * @param dest the second point of the line
     * @return the cell behind the dest Cell if exists, <i>null</i> otherwise
     */
    public Cell getCellBehind(Cell src, Cell dest) {
        int x, y;

        if (src.getCoordX() == dest.getCoordX())
            x = dest.getCoordX();
        else if (src.getCoordX() > dest.getCoordX())
            x = dest.getCoordX() - 1;
        else
            x = dest.getCoordX() + 1;

        if (src.getCoordY() == dest.getCoordY())
            y = dest.getCoordY();
        else if (src.getCoordY() > dest.getCoordY())
            y = dest.getCoordY() - 1;
        else
            y = dest.getCoordY() + 1;

        if (isInsideGameBoard(x, y))
            return this.getCell(x, y);
        return null;
    }

    /**
     * Gets all the adjacent cells of a given one
     * <p>
     * A cell is defined <i>adjacent</i> to another cell if the distance between them is
     * exactly one (diametrically opposed cells, e.g. (1,0) and (1,4), are not considered adjacent).
     * cular, the <i>adjacency</i> relation is:
     * <ul>
     *     <li>symmetric: if A is adjacent to B, B is adjacent to A</li>
     *     <li>non-transitive: if A is adjacent to B and B is adjacent to C, A and C are not adjacent</li>
     *     <li>non-reflexive: A is not adjacent to itself</li>
     * </ul>
     * Given these premises, this method will never return an empty ArrayList: the size of the list is
     * <ul>
     *     <li>3, if <i>cell</i> is one of the four corners</li>
     *     <li>5, if <i>cell</i> is a border cell</li>
     *     <li>8, in any other case</li>
     * </ul>
     *
     * @param cell the cell to analyze
     * @return an {@linkplain ArrayList} containing the cells adjacent to <i>cell</i>
     */
    public ArrayList<Cell> getAdjacentCells(Cell cell) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int x = cell.getCoordX() - 1; x <= cell.getCoordX() + 1; x++) {
            for (int y = cell.getCoordY() - 1; y <= cell.getCoordY() + 1; y++) {
                if (0 <= x && x < DIMENSION &&
                        0 <= y && y < DIMENSION &&
                        (!getCell(x, y).equals(cell)))

                    cells.add(getCell(x, y));
            }
        }
        return cells;
    }

    /**
     * Returns a clone of the current game board state
     * <p>
     * In order to save the state of the entire game, it is needed to save the game board and its cells
     * to preserve the references. To do so, we create a new GameBoard instance with the special constructor
     * ({@linkplain #GameBoard(GameBoard)}), which creates an exact replica of the cells of the current game board
     * in the new board.
    *
     * @return a <i>clone</i> of the game board
     */
    public GameBoard cloneGameBoard() {
        return new GameBoard(this);
    }


    /**
     * Compares the argument to the receiver, and answers true if their representation as Arrays is the same
     * @param o the object to be compared with this
     * @return true if the object is the same as the cell, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameBoard)) return false;
        GameBoard board1 = (GameBoard) o;
        return Arrays.equals(board, board1.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}