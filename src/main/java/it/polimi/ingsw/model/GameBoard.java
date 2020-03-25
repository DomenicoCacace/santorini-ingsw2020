package it.polimi.ingsw.model;

public class GameBoard {
    private static final int DIMENSION = 5;
    private final Cell[][] board;

    public GameBoard() {
        this.board = new Cell[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                board[i][j] = new Cell(i, j);
            }
        }
    }
}