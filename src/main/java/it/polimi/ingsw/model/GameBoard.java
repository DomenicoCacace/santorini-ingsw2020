package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.*;
import it.polimi.ingsw.model.utilities.Memento;

import java.util.ArrayList;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="@id", scope = GameBoard.class)

public class GameBoard implements Memento<GameBoard> {
    private static final int DIMENSION = 5;
    private Cell[][] board;

    @JsonCreator
    public GameBoard(@JsonProperty("allCells") ArrayList<Cell> allCells){
        this.board = new Cell[DIMENSION][DIMENSION];
        int tmpX, tmpY;
        for (int i = 0; i < DIMENSION*DIMENSION; i++) {
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

    public GameBoard(GameBoard gameBoard) {
        this.board = new Cell[DIMENSION][DIMENSION];

        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                Cell tmpCell = gameBoard.getCell(i, j);
                this.board[i][j] = new Cell(i, j, tmpCell.hasDome(), tmpCell.getOccupiedBy(), tmpCell.getBlock());
            }
        }
    }

    public Cell getCell(int x, int y) {
        if (isInsideGameBoard(x,y))
                return board[x][y];
        return null; //TODO: out of bound
    }

    public boolean isInsideGameBoard(int x, int y){
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

        if (isInsideGameBoard(x,y))
            return this.getCell(x, y);
        return null;
    }
    public ArrayList<Cell> getAdjacentCells(Cell cell){
        ArrayList<Cell> cells = new ArrayList<>();
        for(int x=cell.getCoordX() -1; x<=cell.getCoordX()+1; x++ ){
            for(int y=cell.getCoordY() -1; y<=cell.getCoordY()+1; y++ ){
                if (0 <= x && x < DIMENSION &&
                        0 <= y && y < DIMENSION &&
                        (!getCell(x,y).equals(cell)))

                    cells.add(getCell(x,y));
            }
        }
        return cells;
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