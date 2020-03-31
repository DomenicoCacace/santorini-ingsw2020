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
    private List<God> gods;
    private Action normalAction;

    @BeforeEach
    void SetUp() {
        gods = new ArrayList<>();
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
        game.getGameBoard().getCell(3, 4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3, 3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4, 2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(3, 1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3, 1).setHasDome(true);


        game.generateNextTurn();

    }


    @Test
    void getWalkableCellsTest() {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        Worker currentWorker = currentPlayer.getWorkers().get(0);
        System.out.println(game.getWalkableCells(currentWorker).toString());
    }

    @Test
    void getBuildableCellsTest() {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        Worker currentWorker = currentPlayer.getWorkers().get(0);
        System.out.println(game.getBuildableCells(currentWorker).toString());
    }

    @Test
    void correctMovementTest(){
        Action moveAction;
        Worker currentWorker;
        Cell targetCell;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
        Action moveAction;
        Worker currentWorker;
        Cell targetCell;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
        Action moveAction;
        Worker currentWorker;
        Cell targetCell;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
        Action moveAction;
        Worker currentWorker;
        Cell targetCell;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
        Action moveAction;
        Worker currentWorker;
        Cell targetCell;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
    void cannotMoveTwiceTest(){
        Action moveAction;
        Worker currentWorker;
        Cell targetCell, targetCell2;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);
        targetCell2 = game.getGameBoard().getCell(1,2);
        moveAction= new Action(currentWorker, targetCell2);
        game.validateAction(moveAction);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void cantMoveOnDome() {
        Action moveAction;
        Worker currentWorker;
        Cell targetCell;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
        Action buildAction;
        Worker currentWorker;
        Cell targetCell;
        Block block;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new Action(currentWorker, targetCell, block);
        game.getBuildableCells(currentWorker);
        game.validateAction(buildAction);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL2);
        assertFalse(targetCell.hasDome());
    }

    @Test
    void correctBuildActionTest() {
        Action buildAction, moveAction;
        Worker currentWorker;
        Cell targetCell;
        Block block;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
        Action buildAction, moveAction;
        Worker currentWorker;
        Cell targetCell;
        Block block;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
        Action buildAction, moveAction;
        Worker currentWorker;
        Cell targetCell;
        Block block;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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
        Action buildAction, moveAction;
        Worker currentWorker;
        Cell targetCell;
        Block block;
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);
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