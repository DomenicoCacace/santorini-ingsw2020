package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard();
    }

    @Test
    void cellBehindExistsTest() {
       Cell cell1 = board.getCell(1,2);
       Cell cell2 = board.getCell(2,3);
       Cell cellBehind = board.getCellBehind(cell1, cell2);

       assertEquals(board.getCell(3,4), cellBehind);
    }

    @Test
    void cellBehindOutOfGameboardTest(){
        Cell cell1 = board.getCell(1,2);
        Cell cell2 = board.getCell(0,2);
        Cell cellBehind = board.getCellBehind(cell1, cell2);

        assertNull(cellBehind);
    }

    @Test
    void getAdjacentCellsTest(){
        ArrayList<Cell> cells = new ArrayList<Cell>();
        Cell cell = board.getCell(2,3);
        cells = board.getAdjacentCells(cell);
        assertEquals(cells.get(0), board.getCell(1,2));
        assertEquals(cells.get(1), board.getCell(1,3));
        assertEquals(cells.get(2), board.getCell(1,4));
        assertEquals(cells.get(3), board.getCell(2,2));
        assertEquals(cells.get(4), board.getCell(2,4));
        assertEquals(cells.get(5), board.getCell(3,2));
        assertEquals(cells.get(6), board.getCell(3,3));
        assertEquals(cells.get(7), board.getCell(3,4));

        cell = board.getCell(0,3);
        cells = board.getAdjacentCells(cell);
        assertEquals(cells.get(0), board.getCell(0,2));
        assertEquals(cells.get(1), board.getCell(0,4));
        assertEquals(cells.get(2), board.getCell(1,2));
        assertEquals(cells.get(3), board.getCell(1,3));
        assertEquals(cells.get(4), board.getCell(1,4));
    }
}