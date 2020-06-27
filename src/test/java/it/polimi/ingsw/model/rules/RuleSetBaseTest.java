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

public class RuleSetBaseTest {
    private List<Player> players;
    private Game game;
    private Action buildAction, moveAction;
    private Worker currentWorker;
    private Cell targetCell;
    private Block block;

    @BeforeEach
    void SetUp() throws AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("base1", 2, ""));
        gods.get(0).setStrategy(new RuleSetBase());
        gods.add(new God("base", 2, ""));
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
    void getWalkableCellsTest() throws IOException {
        List<Cell> walkableCells;
        walkableCells = game.getWalkableCells(currentWorker);
        assertEquals(walkableCells.size(), 5);
        assertEquals(walkableCells.get(0), game.getGameBoard().getCell(1, 1));
        assertEquals(walkableCells.get(1), game.getGameBoard().getCell(1, 2));
        assertEquals(walkableCells.get(2), game.getGameBoard().getCell(1, 3));
        assertEquals(walkableCells.get(3), game.getGameBoard().getCell(2, 1));
        assertEquals(walkableCells.get(4), game.getGameBoard().getCell(2, 3));
    }


    @Test
    void getBuildableCellsTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        List<Cell> buildableCells;
        buildableCells = game.getBuildableCells(currentWorker);
        assertEquals(buildableCells.size(), 6);
        assertEquals(buildableCells.get(0), game.getGameBoard().getCell(1, 2));
        assertEquals(buildableCells.get(1), game.getGameBoard().getCell(1, 3));
        assertEquals(buildableCells.get(2), game.getGameBoard().getCell(1, 4));
        assertEquals(buildableCells.get(3), game.getGameBoard().getCell(2, 2));
        assertEquals(buildableCells.get(4), game.getGameBoard().getCell(2, 4));
        assertEquals(buildableCells.get(5), game.getGameBoard().getCell(3, 3));
    }


    @Test
    void correctMovementTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        List<PossibleActions> possibleActions = game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(possibleActions.size(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void correctMoveUpTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        targetCell.setBlock(Block.LEVEL1);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void correctMoveDownTest() throws IOException, IllegalActionException {
        currentWorker.getPosition().setBlock(Block.LEVEL2);
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void cannotMoveTooFarTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 4);
        moveAction = new MoveAction(currentWorker, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), moveAction.getStartingCell());
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
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), moveAction.getStartingCell());
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
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), moveAction.getStartingCell());
    }

    @Test
    void cannotMoveTwiceTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        Cell secondCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(currentWorker, secondCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void passTurnAutomaticallyAfterBuildingTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);

        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        buildAction.getValidation(game);

        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
        assertEquals(targetCell.getBlock(), block);
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);

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
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL2);
        assertFalse(targetCell.hasDome());
    }

    @Test
    void correctBuildActionTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);

        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        buildAction.getValidation(game);

        assertEquals(targetCell.getBlock(), block);
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
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
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL2);
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
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL2);
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
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.DOME);
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
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL0);
    }
}