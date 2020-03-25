package it.polimi.ingsw.model;

import java.util.ArrayList;

public class GameBoard {
    private static final int DIMENSION = 5;
    private final Cell[][] board;

    public Cell getCell(int x, int y) {
        return board[x][y];
    }

    public ArrayList<Cell> getAllCells() {
        ArrayList<Cell> cells = new ArrayList<Cell>();
        for(int i = 0; i < DIMENSION; i++) {
            for(int j = 0; j < DIMENSION; j++) {
                cells.add(getCell(i, j));
            }
        }
        return cells;
    }

    public GameBoard() {
        this.board = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                board[i][j] = new Cell(i, j);
            }
        }
    }
}