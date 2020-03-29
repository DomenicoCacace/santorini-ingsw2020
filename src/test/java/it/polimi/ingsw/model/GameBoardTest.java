package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;


class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard();
    }

    @Test
    void getCellBehind() {
       Cell cell1 = board.getCell(1,2);
       Cell cell2 = board.getCell(2,3);
       Cell cellBehind= board.getCellBehind(cell1, cell2);
       assertEquals(board.getCell(3,4), cellBehind);
       cell1 = board.getCell(1,2);
       cell2 = board.getCell(0,2);
       cellBehind= board.getCellBehind(cell1, cell2); //Has to be null because it would be out of bounds
       assertNull(cellBehind);
    }
}