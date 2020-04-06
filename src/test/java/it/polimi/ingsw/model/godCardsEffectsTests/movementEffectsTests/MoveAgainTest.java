package it.polimi.ingsw.model.godCardsEffectsTests.movementEffectsTests;

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

    private List<Player> players;
    private Game game;
    private Action moveAction;
    private Worker worker1, worker2;
    private Cell targetCell;

    @BeforeEach
    void SetUp() throws IOException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Artemis"));
        gods.get(0).setStrategy(new MoveAgain());
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
        game.getGameBoard().getCell(4, 1).setBlock(Block.LEVEL3);


        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));

        game.generateNextTurn();

        worker1 = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        worker2 = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);

    }

    @Test
    void correctDoubleMoveSameLevelTest() throws IOException {
        targetCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker1.getPosition(), targetCell);

        targetCell = game.getGameBoard().getCell(0, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker1.getPosition(), targetCell);
    }

    @Test
    void correctDoubleMoveUpTest() throws IOException {
        targetCell = game.getGameBoard().getCell(4, 2);
        moveAction = new MoveAction(worker2, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker2);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker2.getPosition(), targetCell);

        targetCell = game.getGameBoard().getCell(3, 3);
        moveAction = new MoveAction(worker2, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker2);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker2.getPosition(), targetCell);
    }

    @Test
    void cannotGoBackStartingCellTest() throws IOException {
        targetCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker1.getPosition(), targetCell);

        targetCell = game.getGameBoard().getCell(2, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker1.getPosition(), game.getGameBoard().getCell(1,2));
    }

    @Test
    void cannotMoveAfterWinningTest() throws IOException {
        worker2.getPosition().setBlock(Block.LEVEL2);
        targetCell = game.getGameBoard().getCell(4, 1);
        moveAction = new MoveAction(worker2, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker2);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker2.getPosition(), targetCell);
        assertEquals(game.getWinner(), worker2.getOwner());

        targetCell = game.getGameBoard().getCell(4, 2);
        moveAction = new MoveAction(worker2, targetCell);
        moveAction.getValidation(game);

    /*TODO: we need the lobby to test this (game must finish after first move)
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker2.getPosition(), game.getGameBoard().getCell(4,1));
    */

    }

    @Test
    void cannotMoveAfterBuildTest() throws IOException {
        targetCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker1.getPosition(), targetCell);

        targetCell = game.getGameBoard().getCell(1, 3);
        Action buildAction = new BuildAction(worker1, targetCell, Block.LEVEL1);
        buildAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(targetCell.getBlock(), Block.LEVEL1);

        targetCell = game.getGameBoard().getCell(0, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(worker1.getPosition(), game.getGameBoard().getCell(1, 2));
    }

    @Test
    void CannotMoveWithDifferentWorkers() throws IOException {
        targetCell = game.getGameBoard().getCell(1, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(worker1.getPosition(), targetCell);

        targetCell = game.getGameBoard().getCell(4, 2);
        moveAction = new MoveAction(worker2, targetCell);
        moveAction.getValidation(game);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), worker1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertNotEquals(worker2.getPosition(), targetCell);
        assertEquals(worker2.getPosition(), game.getGameBoard().getCell(3,2));

        targetCell = game.getGameBoard().getCell(0, 2);
        moveAction = new MoveAction(worker1, targetCell);
        moveAction.getValidation(game);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(worker1.getPosition(), game.getGameBoard().getCell(0, 2));

    }

}