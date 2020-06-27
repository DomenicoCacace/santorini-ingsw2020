package it.polimi.ingsw.model.rules;

import it.polimi.ingsw.controller.ServerController;
import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
    private ServerController controller;

    @BeforeEach
    void SetUp() throws IOException, AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("base1",2,""));
        gods.get(0).setStrategy(new RuleSetBase());
        gods.add(new God("base",2,""));
        gods.get(1).setStrategy(new RuleSetBase());


        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.PURPLE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));


        game.getGameBoard().getCell(3, 4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3, 3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4, 2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(3, 1).setBlock(Block.DOME);

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));

        players.get(1).addWorker(game.getGameBoard().getCell(4, 4));

        game.generateNextTurn();

        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);



    }

    @Test
    void getWalkableCellsTest() {
        List<Cell> walkableCells;
        walkableCells = game.getWalkableCells(currentWorker);
        assertEquals(5, walkableCells.size());
        assertEquals(game.getGameBoard().getCell(1,1),walkableCells.get(0) );
        assertEquals(game.getGameBoard().getCell(1,2),walkableCells.get(1) );
        assertEquals(game.getGameBoard().getCell(1,3),walkableCells.get(2) );
        assertEquals(game.getGameBoard().getCell(2,1),walkableCells.get(3) );
        assertEquals(game.getGameBoard().getCell(2,3),walkableCells.get(4) );
    }



    @Test
    void getBuildableCellsTest() throws IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        List<Cell> buildableCells;
        buildableCells = game.getBuildableCells(currentWorker);
        assertEquals(6, buildableCells.size());
        assertEquals(game.getGameBoard().getCell(1,2),buildableCells.get(0) );
        assertEquals(game.getGameBoard().getCell(1,3),buildableCells.get(1) );
        assertEquals(game.getGameBoard().getCell(1,4),buildableCells.get(2) );
        assertEquals(game.getGameBoard().getCell(2,2),buildableCells.get(3) );
        assertEquals(game.getGameBoard().getCell(2,4),buildableCells.get(4) );
        assertEquals(game.getGameBoard().getCell(3,3),buildableCells.get(5) );
    }


    @Test
    void correctMovementTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        List<PossibleActions> possibleActions = game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(1, possibleActions.size());
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(targetCell, currentWorker.getPosition());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void correctMoveUpTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        targetCell.setBlock(Block.LEVEL1);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(targetCell, currentWorker.getPosition());
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void correctMoveDownTest() throws IOException, IllegalActionException {
        currentWorker.getPosition().setBlock(Block.LEVEL2);
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(targetCell, currentWorker.getPosition());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void cannotMoveTooFarTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 4);
        moveAction = new MoveAction(currentWorker, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(moveAction.getStartingCell(), currentWorker.getPosition());
    }

    @Test
    void cannotMoveTooHighTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        targetCell.setBlock(Block.LEVEL2);
        moveAction = new MoveAction(currentWorker, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(moveAction.getStartingCell(), currentWorker.getPosition());
    }

    @Test
    void cantMoveOnDome() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        targetCell.setBlock(Block.DOME);
        moveAction = new MoveAction(currentWorker, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(moveAction.getStartingCell(), currentWorker.getPosition());
    }

    @Test
    void cannotMoveTwiceTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        Cell secondCell = game.getGameBoard().getCell(1,2);
        moveAction= new MoveAction(currentWorker, secondCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(targetCell, currentWorker.getPosition());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void passTurnAutomaticallyAfterBuildingTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());

        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        buildAction.getValidation(game);

        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals(block, targetCell.getBlock());
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());

    }

    @Test
    void cannotBuildWithoutMovingTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        try {
            buildAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL2, targetCell.getBlock());
        assertFalse(targetCell.hasDome());
    }

    @Test
    void correctBuildActionTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());

        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        buildAction.getValidation(game);

        assertEquals(block, targetCell.getBlock());
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
    }

    @Test
    void cannotBuildASmallerBlockTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL1;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        try {
            buildAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL2, targetCell.getBlock());
    }

    @Test
    void cannotBuildWithOtherWorkerTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        currentWorker = players.get(0).getWorkers().get(1);
        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        try {
            buildAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }
        assertNotEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL2, targetCell.getBlock());
    }

    @Test
    void cannotBuildOverDomeTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        targetCell = game.getGameBoard().getCell(3, 4);
        block = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        try {
            buildAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.DOME, targetCell.getBlock());
    }

    @Test
    void cannotBuildOnMyCellTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        block = Block.LEVEL1;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        try {
            buildAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL0, targetCell.getBlock());
    }
}