package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.Arrays;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = GameBoard.class)

public class GameBoard {
    private static final int DIMENSION = 5;
    private final Cell[][] board;

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

    public GameBoard() {
        this.board = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                board[i][j] = new Cell(i, j);
            }
        }
    }

    private GameBoard(GameBoard gameBoard) {
        this.board = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                this.board[i][j] = gameBoard.board[i][j].cloneCell();
            }
        }
    }

    public Cell getCell(int x, int y) {
        if (isInsideGameBoard(x, y))
            return board[x][y];
        return null; //out of bound, will never happen
    }

    public Cell getCell(Cell cell) {
        int tmpX = cell.getCoordX();
        int tmpY = cell.getCoordY();
        return getCell(tmpX, tmpY);
    }


    public boolean isInsideGameBoard(int x, int y) {
        return 0 <= x && x < DIMENSION && 0 <= y && y < DIMENSION;
    }

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

    public GameBoard cloneGameBoard() {
        return new GameBoard(this);
    }


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