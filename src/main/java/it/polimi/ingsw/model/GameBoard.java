package it.polimi.ingsw.model;

import java.util.ArrayList;

public class GameBoard implements Memento<GameBoard> {
    private static final int DIMENSION = 5;
    private Cell[][] board;

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

    public GameBoard(GameBoard gameBoard){
        this.board = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                Cell tmpCell = gameBoard.getCell(i,j);
                this.board[i][j] = new Cell(i, j, tmpCell.hasDome(), tmpCell.getOccupiedBy(), tmpCell.getBlock());
            }
        }
    }

    @Override
    public GameBoard saveState() {
        return new GameBoard(this);
    }


    @Override
    public void restoreState(GameBoard savedState) {
        this.board = savedState.board;
    }
}