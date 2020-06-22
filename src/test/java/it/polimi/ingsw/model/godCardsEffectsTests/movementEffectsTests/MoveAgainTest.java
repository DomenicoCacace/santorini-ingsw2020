package it.polimi.ingsw.model.godCardsEffectsTests.movementEffectsTests;

import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.MoveAgain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveAgainTest {
    private Game game;
    private Action moveAction;
    private Worker worker1, worker2;
    private Cell targetCell;
    private final List<Player> players = new ArrayList<>();


    @BeforeEach
    void SetUp() throws AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Artemis",2,""));
        gods.get(0).setStrategy(new MoveAgain());
        gods.add(new God("base",2,""));
        gods.get(1).setStrategy(new RuleSetBase());

        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.PURPLE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));


        game.getGameBoard().getCell(3, 4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3, 3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4, 2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(3, 1).setBlock(Block.DOME);
        game.getGameBoard().getCell(4, 1).setBlock(Block.LEVEL3);


        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));

        players.get(1).addWorker(game.getGameBoard().getCell(0, 0));

        game.generateNextTurn();

        worker1 = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        worker2 = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);

    }

    @Test
    void correctDoubleMoveSameLevelTest() throws IOException, IllegalActionException {
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(worker1);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        assertEquals(2, possibleActions.size());
        targetCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);
        possibleActions = game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(worker1);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(2, possibleActions.size());

        assertEquals(worker1, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetCell, worker1.getPosition());

        targetCell = game.getGameBoard().getCell(0, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(worker1);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(1, possibleActions.size());

        assertEquals(worker1, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetCell, worker1.getPosition());
    }

    @Test
    void correctDoubleMoveUpTest() throws IllegalActionException {
        targetCell = game.getGameBoard().getCell(4, 2);
        moveAction = new MoveAction(worker2, targetCell);
        moveAction.getValidation(game);

        assertEquals(worker2, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetCell, worker2.getPosition());

        targetCell = game.getGameBoard().getCell(3, 3);
        moveAction = new MoveAction(worker2, targetCell);
        moveAction.getValidation(game);

        assertEquals(worker2, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetCell, worker2.getPosition());
    }

    @Test
    void cannotGoBackStartingCellTest() throws IllegalActionException {
        targetCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        assertEquals(worker1, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetCell, worker1.getPosition());

        targetCell = game.getGameBoard().getCell(2, 2);
        moveAction = new MoveAction(worker1, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }
        assertEquals(worker1, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(1,2), worker1.getPosition());
    }

    @Test
    void mustBuildAfterFirstMovementTest() throws IllegalActionException {
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(2,1).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(2,3).setBlock(Block.LEVEL2);
        targetCell = game.getGameBoard().getCell(4, 3);
        moveAction = new MoveAction(worker2, targetCell);
        moveAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(worker2);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(1, possibleActions.size());

        assertEquals(worker2, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetCell, worker2.getPosition());

    }

    @Test
    void endTurnAutomaticallyAfterBuildTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        assertEquals(worker1, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetCell, worker1.getPosition());

        targetCell = game.getGameBoard().getCell(1, 3);
        Action buildAction = new BuildAction(worker1, targetCell, Block.LEVEL1);
        buildAction.getValidation(game);

        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals(Block.LEVEL1, targetCell.getBlock());

    }

    @Test
    void CannotMoveWithDifferentWorkers() throws IllegalActionException {
        targetCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);
        assertEquals(worker1, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetCell, worker1.getPosition());

        targetCell = game.getGameBoard().getCell(4, 2);
        moveAction = new MoveAction(worker2, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(worker1, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertNotEquals(worker2.getPosition(), targetCell);
        assertEquals(game.getGameBoard().getCell(3,2), worker2.getPosition());

        targetCell = game.getGameBoard().getCell(0, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(game.getGameBoard().getCell(0, 2), worker1.getPosition());

    }

}