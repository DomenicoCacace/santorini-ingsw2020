package it.polimi.ingsw.model.godCardsEffectsTests.affectOpponentTurnEffectsTests;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.godCardsEffects.affectOpponentTurnEffects.CannotMoveUp;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Push;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CannotMoveUpTest {
    private List<Player> players;
    private List<God> gods;
    private Game game;
    private Cell targetCell;
    private Action moveAction, buildAction;
    private Worker targetWorker;
    private Block targetBlock;

    @BeforeEach
    void setUp() throws IOException, LostException {
        gods = new ArrayList<>();
        gods.add(new God("minotaur"));
        gods.get(0).setStrategy(new Push());
        gods.add(new God("Athena"));
        gods.get(1).setStrategy(new CannotMoveUp());
        gods.add(new God("Atlas"));
        gods.get(2).setStrategy(new BuildDome());

        players = new ArrayList<>();
        players.add(new Player("P1", gods.get(0), Color.BLUE));
        players.add(new Player("P2", gods.get(1), Color.WHITE));
        players.add(new Player("P3", gods.get(2), Color.GREY));

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
    void opponentsCannotMoveUpTest() throws IOException, LostException {
        targetCell = game.getGameBoard().getCell(1, 1);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), targetWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetWorker.getPosition(), game.getGameBoard().getCell(1, 1));

        targetCell = game.getGameBoard().getCell(1, 0);
        targetBlock = Block.LEVEL1;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(1, 0).getBlock(), Block.LEVEL1);

        targetCell = game.getGameBoard().getCell(3, 3);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), targetWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetWorker.getPosition(), game.getGameBoard().getCell(3, 3));

        targetCell = game.getGameBoard().getCell(4, 4);
        targetBlock = Block.LEVEL1;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(4, 4).getBlock(), Block.LEVEL1);

        assertEquals(game.getPlayers().get(0).getGod().getStrategy().getMovesUpAvailable(), 0);
        assertEquals(game.getPlayers().get(2).getGod().getStrategy().getMovesUpAvailable(), 0);
        assertEquals(game.getPlayers().get(1).getGod().getStrategy().getMovesUpAvailable(), 1);

        targetCell = game.getGameBoard().getCell(1, 4);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), targetWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetWorker.getPosition(), game.getGameBoard().getCell(1, 4));

        targetCell = game.getGameBoard().getCell(0, 4);
        targetBlock = Block.DOME;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(0, 4).getBlock(), Block.DOME);

        targetCell = game.getGameBoard().getCell(1, 2);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertNotEquals(targetWorker.getPosition(), targetCell);
        assertEquals(targetWorker.getPosition(), game.getGameBoard().getCell(1, 1));
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetWorker.getPosition(), moveAction.getStartingCell());
        assertEquals(game.getGameBoard().getCell(1, 2).getOccupiedBy(), game.getPlayers().get(2).getWorkers().get(1));

        targetCell = game.getGameBoard().getCell(0, 1);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), targetWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertEquals(targetWorker.getPosition(), game.getGameBoard().getCell(0, 1));
        assertEquals(game.getGameBoard().getCell(0, 1).getBlock(), Block.LEVEL0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());


        game.generateNextTurn();
        game.generateNextTurn();
        assertEquals(game.getPlayers().get(0).getGod().getStrategy().getMovesUpAvailable(), 1);
        assertEquals(game.getPlayers().get(2).getGod().getStrategy().getMovesUpAvailable(), 1);
        assertEquals(game.getPlayers().get(1).getGod().getStrategy().getMovesUpAvailable(), 1);
    }

    @Test
    void opponentsCanMoveUpTest() throws IOException, LostException {
        targetCell = game.getGameBoard().getCell(1, 1);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), targetWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetWorker.getPosition(), game.getGameBoard().getCell(1, 1));

        targetCell = game.getGameBoard().getCell(1, 0);
        targetBlock = Block.LEVEL1;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(1, 0).getBlock(), Block.LEVEL1);

        targetCell = game.getGameBoard().getCell(4, 4);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), targetWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetWorker.getPosition(), game.getGameBoard().getCell(4, 4));

        targetCell = game.getGameBoard().getCell(4, 3);
        targetBlock = Block.LEVEL1;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(4, 3).getBlock(), Block.LEVEL1);

        assertEquals(game.getPlayers().get(0).getGod().getStrategy().getMovesUpAvailable(), 1);
        assertEquals(game.getPlayers().get(2).getGod().getStrategy().getMovesUpAvailable(), 1);
        assertEquals(game.getPlayers().get(1).getGod().getStrategy().getMovesUpAvailable(), 1);

        targetCell = game.getGameBoard().getCell(1, 4);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), targetWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(targetWorker.getPosition(), game.getGameBoard().getCell(1, 4));

        targetCell = game.getGameBoard().getCell(0, 4);
        targetBlock = Block.DOME;
        buildAction = new BuildAction(targetWorker, targetCell, targetBlock);
        buildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(0, 4).getBlock(), Block.DOME);

        targetCell = game.getGameBoard().getCell(1, 2);
        targetWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        moveAction = new MoveAction(targetWorker, targetCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), targetWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesUpAvailable(), 0);
        assertEquals(targetWorker.getPosition(), targetCell);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(1, 3).getOccupiedBy(), game.getPlayers().get(2).getWorkers().get(1));


        game.generateNextTurn();
        game.generateNextTurn();
        assertEquals(game.getPlayers().get(0).getGod().getStrategy().getMovesUpAvailable(), 1);
        assertEquals(game.getPlayers().get(2).getGod().getStrategy().getMovesUpAvailable(), 1);
        assertEquals(game.getPlayers().get(1).getGod().getStrategy().getMovesUpAvailable(), 1);
    }
}