package it.polimi.ingsw.server.model.godCardsEffectsTests.affectOpponentTurnEffectsTests;

import it.polimi.ingsw.server.exceptions.AddingFailedException;
import it.polimi.ingsw.server.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.action.Action;
import it.polimi.ingsw.server.model.action.BuildAction;
import it.polimi.ingsw.server.model.action.MoveAction;
import it.polimi.ingsw.shared.dataClasses.*;
import it.polimi.ingsw.server.model.godCardsEffects.affectOpponentTurnEffects.CannotMoveUp;
import it.polimi.ingsw.server.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.server.model.godCardsEffects.movementEffects.Push;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CannotMoveUpTest {
    private Game game;
    private Cell targetCell;
    private Action moveAction, buildAction;
    private Worker targetWorker;
    private Block targetBlock;

    @BeforeEach
    void setUp() throws AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("minotaur",2,""));
        gods.get(0).setStrategy(new Push());
        gods.add(new God("Athena",2, ""));
        gods.get(1).setStrategy(new CannotMoveUp());
        gods.add(new God("Atlas",2, ""));
        gods.get(2).setStrategy(new BuildDome());

        List<Player> players = new ArrayList<>();
        players.add(new Player("P1", gods.get(0), Color.BLUE));
        players.add(new Player("P2", gods.get(1), Color.PURPLE));
        players.add(new Player("P3", gods.get(2), Color.RED));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.getGameBoard().getCell(3, 4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3, 3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4, 2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(3, 1).setBlock(Block.DOME);
        game.getGameBoard().getCell(4, 1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1, 2).setBlock(Block.LEVEL1);

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(4, 3));
        players.get(1).addWorker(game.getGameBoard().getCell(4, 2));
        players.get(2).addWorker(game.getGameBoard().getCell(2, 4));
        players.get(2).addWorker(game.getGameBoard().getCell(1, 2));
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));
        game.generateNextTurn();
    }

    @Test
    void opponentsCannotMoveUpTest() throws IllegalActionException {
        targetCell = game.getGameBoard().getCell(1, 1);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(targetWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(1, 1), targetWorker.getPosition());

        targetCell = game.getGameBoard().getCell(1, 0);
        targetBlock = Block.LEVEL1;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(Block.LEVEL1, game.getGameBoard().getCell(1, 0).getBlock());

        targetCell = game.getGameBoard().getCell(3, 3);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(targetWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(3, 3), targetWorker.getPosition());

        targetCell = game.getGameBoard().getCell(4, 4);
        targetBlock = Block.LEVEL1;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(Block.LEVEL1, game.getGameBoard().getCell(4, 4).getBlock());

        assertEquals(0, game.getPlayers().get(0).getGod().getStrategy().getMovesUpAvailable());
        assertEquals(0, game.getPlayers().get(2).getGod().getStrategy().getMovesUpAvailable());
        assertEquals(1, game.getPlayers().get(1).getGod().getStrategy().getMovesUpAvailable());

        targetCell = game.getGameBoard().getCell(1, 4);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(targetWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(1, 4), targetWorker.getPosition());

        targetCell = game.getGameBoard().getCell(0, 4);
        targetBlock = Block.DOME;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(Block.DOME, game.getGameBoard().getCell(0, 4).getBlock());

        targetCell = game.getGameBoard().getCell(1, 2);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e) {
            e.getMessage();
        }

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertNotEquals(targetWorker.getPosition(), targetCell);
        assertEquals(game.getGameBoard().getCell(1, 1), targetWorker.getPosition());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(moveAction.getStartingCell(), targetWorker.getPosition());
        assertEquals(game.getPlayers().get(2).getWorkers().get(1), game.getGameBoard().getCell(1, 2).getOccupiedBy());

        targetCell = game.getGameBoard().getCell(0, 1);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(targetWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertEquals(game.getGameBoard().getCell(0, 1), targetWorker.getPosition());
        assertEquals(Block.LEVEL0, game.getGameBoard().getCell(0, 1).getBlock());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());


        game.generateNextTurn();
        game.generateNextTurn();
        assertEquals(1, game.getPlayers().get(0).getGod().getStrategy().getMovesUpAvailable());
        assertEquals(1, game.getPlayers().get(2).getGod().getStrategy().getMovesUpAvailable());
        assertEquals(1, game.getPlayers().get(1).getGod().getStrategy().getMovesUpAvailable());
    }

    @Test
    void opponentsCanMoveUpTest() throws IOException, IllegalActionException {
        targetCell = game.getGameBoard().getCell(1, 1);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(targetWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(1, 1), targetWorker.getPosition());

        targetCell = game.getGameBoard().getCell(1, 0);
        targetBlock = Block.LEVEL1;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(Block.LEVEL1, game.getGameBoard().getCell(1, 0).getBlock());

        targetCell = game.getGameBoard().getCell(4, 4);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(targetWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(4, 4), targetWorker.getPosition());

        targetCell = game.getGameBoard().getCell(4, 3);
        targetBlock = Block.LEVEL1;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(Block.LEVEL1, game.getGameBoard().getCell(4, 3).getBlock());

        assertEquals(1, game.getPlayers().get(0).getGod().getStrategy().getMovesUpAvailable());
        assertEquals(1, game.getPlayers().get(2).getGod().getStrategy().getMovesUpAvailable());
        assertEquals(1, game.getPlayers().get(1).getGod().getStrategy().getMovesUpAvailable());

        targetCell = game.getGameBoard().getCell(1, 4);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(targetWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(1, 4), targetWorker.getPosition());

        targetCell = game.getGameBoard().getCell(0, 4);
        targetBlock = Block.DOME;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(Block.DOME, game.getGameBoard().getCell(0, 4).getBlock());

        targetCell = game.getGameBoard().getCell(1, 2);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(targetWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesUpAvailable());
        assertEquals(targetCell, targetWorker.getPosition());
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getPlayers().get(2).getWorkers().get(1), game.getGameBoard().getCell(1, 3).getOccupiedBy());


        game.generateNextTurn();
        game.generateNextTurn();
        assertEquals(1, game.getPlayers().get(0).getGod().getStrategy().getMovesUpAvailable());
        assertEquals(1, game.getPlayers().get(2).getGod().getStrategy().getMovesUpAvailable());
        assertEquals(1, game.getPlayers().get(1).getGod().getStrategy().getMovesUpAvailable());
    }
}