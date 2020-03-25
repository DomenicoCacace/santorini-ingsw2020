package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.WildcardType;

import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    private Player player;
    private Worker worker;
    private Cell startingCell, endingCell;
    private GameBoard board;
    private Block block;


    @BeforeEach
    void setUp() {
        player = new Player();
        board = new GameBoard();
        block = Block.LEVEL1;
        startingCell = new Cell(2, 3);
        endingCell = new Cell(2, 2);
        worker = new Worker(startingCell ,player);
    }

    @Test
    void applierMovementActionTest() {
        Action movementAction = new Action(worker, endingCell);
        movementAction.applier();
        assertEquals(worker.getPosition(), endingCell);
        assertEquals(endingCell.getOccupiedBy(), worker);
        assertNull(startingCell.getOccupiedBy());
    }

    @Test
    void applierBuildActionTest() {
        Action buildingAction = new Action(worker, endingCell, block);
        buildingAction.applier();
        assertEquals(endingCell.getBlock(), block);




    }

}