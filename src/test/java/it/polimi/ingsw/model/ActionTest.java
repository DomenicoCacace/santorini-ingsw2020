package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ActionTest {
    /*
    private Player player;
    private Worker worker;
    private Cell startingCell, endingCell;
    private GameBoard board;
    private Block block;


    @BeforeEach
    void setUp() throws AddingFailedException, IOException {
        player = new Player("name", new God("artemis", 2), Color.BLUE);
        board = new GameBoard();
        block = Block.LEVEL1;
        startingCell = board.getCell(3, 2);
        endingCell = board.getCell(2,3);
        player.addWorker(startingCell);
        worker = player.getWorkers().get(0);
    }

    @Test
    void applierMovementActionTest() {
        Action movementAction = new MoveAction(worker, endingCell);
        movementAction.apply();

        for(Cell cell : board.getAllCells()) {
            if (cell.equals(endingCell)) {
                assertEquals(board.getCell(2, 3), cell);
                assertEquals(cell.getOccupiedBy(), worker);
                assertEquals(worker.getPosition(), cell);
                continue;
            }
            assertNull(cell.getOccupiedBy());
        }
    }

    @Test
    void applierBuildActionTest() {
        Action buildingAction = new BuildAction(worker, endingCell, block);
        buildingAction.apply();
        assertEquals(endingCell.getBlock(), block);

        for(Cell cell : board.getAllCells()) {
            if (cell.equals(endingCell)) {
                assertEquals(cell.getBlock(), block);

                continue;
            }
            assertEquals(cell.getBlock(), Block.LEVEL0);
        }
    }

     */

}