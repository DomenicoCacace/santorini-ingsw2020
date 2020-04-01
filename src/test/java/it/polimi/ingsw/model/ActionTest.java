package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.WildcardType;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ActionTest {
    private Player player;
    private Worker worker;
    private Cell startingCell, endingCell;
    private GameBoard board;
    private Block block;


    @BeforeEach
    void setUp() {
        player = new Player("name", new God("artemis"), Color.BLUE);
        board = new GameBoard();
        block = Block.LEVEL1;
        startingCell = board.getCell(3, 2);
        endingCell = board.getCell(2,3);
        worker = new Worker(startingCell , player);
    }

    @Test
    void applierMovementActionTest() {
        Action movementAction = new Action(worker, endingCell);
        movementAction.applier();

        for(Cell cell : board.getAllCells()) {
            if (cell.equals(endingCell)) {
                assertEquals(board.getCell(2, 3), cell);
                assertEquals(cell.getOccupiedBy(), worker);
                assertEquals(worker.getPosition(), cell);
                continue;
            }
            assertNull(cell.getOccupiedBy());
        }
        //TODO: add check on number of moves available
    }

    @Test
    void applierBuildActionTest() {
        Action buildingAction = new Action(worker, endingCell, block);
        buildingAction.applier();
        assertEquals(endingCell.getBlock(), block);

        for(Cell cell : board.getAllCells()) {
            if (cell.equals(endingCell)) {
                assertEquals(cell.getBlock(), block);

                continue;
            }
            assertEquals(cell.getBlock(), Block.LEVEL0);
        }
        //TODO: add check on number of moves available
    }

}