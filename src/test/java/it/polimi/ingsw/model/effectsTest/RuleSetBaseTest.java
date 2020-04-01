package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleSetBaseTest {


    private List<Player> players;
    private Game game;
    private Action buildAction, moveAction;
    private Worker currentWorker;
    private Cell targetCell;
    private Block block;

    @BeforeEach
    void SetUp() {
        List<God> gods = new ArrayList<>();
        gods.add(new God("base1"));
        gods.get(0).setStrategy(new RuleSetBase());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());


        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.WHITE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));


        game.getGameBoard().getCell(3, 4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3, 3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4, 2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(3, 1).setBlock(Block.DOME);

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));

        game.generateNextTurn();

        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);

    }


    @Test
    void getWalkableCellsTest() {
        //TODO: write test assertions
        System.out.println(game.getWalkableCells(currentWorker).toString());
    }

    @Test
    void getBuildableCellsTest() {
        //TODO: write test assertions
        System.out.println(game.getBuildableCells(currentWorker).toString());
    }

    @Test
    void correctMovementTest(){
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void correctMoveUpTest(){
        targetCell = game.getGameBoard().getCell(2, 3);
        targetCell.setBlock(Block.LEVEL1);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void correctMoveDownTest(){
        currentWorker.getPosition().setBlock(Block.LEVEL2);
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void cannotMoveTooFarTest(){
        targetCell = game.getGameBoard().getCell(2, 4);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), moveAction.getStartingCell());
    }

    @Test
    void cannotMoveTooHighTest(){
        targetCell = game.getGameBoard().getCell(2, 3);
        targetCell.setBlock(Block.LEVEL2);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), moveAction.getStartingCell());
    }

    @Test
    void cannotMoveTwiceTest() {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        Cell secondCell = game.getGameBoard().getCell(1,2);
        moveAction= new Action(currentWorker, secondCell);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void cantMoveOnDome() {
        targetCell = game.getGameBoard().getCell(2, 3);
        targetCell.setBlock(Block.DOME);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), moveAction.getStartingCell());
    }

    @Test
    void cannotBuildWithoutMovingTest() {
        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new Action(currentWorker, targetCell, block);
        game.validateAction(buildAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL2);
        assertFalse(targetCell.hasDome());
    }

    @Test
    void correctBuildActionTest() {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new Action(currentWorker, targetCell, block);
        game.validateAction(buildAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 0);
        assertEquals(targetCell.getBlock(), block);
    }

    @Test
    void cannotBuildASmallerBlockTest(){
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL1;
        buildAction = new Action(currentWorker, targetCell, block);
        game.validateAction(buildAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL2);
    }

    @Test
    void cannotBuildWithOtherWorkerTest() {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        currentWorker = players.get(0).getWorkers().get(1);
        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new Action(currentWorker, targetCell, block);
        game.validateAction(buildAction);

        assertNotEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL2);
    }

    @Test
    void cannotBuildOnDomeTest(){
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        targetCell = game.getGameBoard().getCell(3, 4);
        block = Block.LEVEL3;
        buildAction = new Action(currentWorker, targetCell, block);
        game.validateAction(buildAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.DOME);
    }
}