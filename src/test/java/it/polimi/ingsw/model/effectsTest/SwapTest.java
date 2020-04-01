package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.winConditionEffects.Down2Levels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SwapTest {

    private List<Player> players;
    private Game game;
    private List<God> gods;
    private Action swapAction;

    @BeforeEach
    void SetUp () {
        gods = new ArrayList<>();
        gods.add(new God("Apollo"));
        gods.get(0).setStrategy(new Down2Levels());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());


        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.WHITE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,3).setBlock(Block.LEVEL3);

        game.generateNextTurn();
    }

    @Test
    void correctSwapSameLevelTest(){
        Cell myCell = game.getGameBoard().getCell(3, 2);
        Cell opponentCell = game.getGameBoard().getCell(4, 3);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        swapAction = new Action(myWorker, opponentCell);
        game.validateAction(swapAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void correctSwapHigherOneLevelTest(){
        Cell myCell = game.getGameBoard().getCell(3, 2);
        Cell opponentCell = game.getGameBoard().getCell(4, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        swapAction = new Action(myWorker, opponentCell);
        game.validateAction(swapAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void correctSwapLowerAnyLevelTest(){
        Cell myCell = game.getGameBoard().getCell(3, 3);
        Cell opponentCell = game.getGameBoard().getCell(3, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        swapAction = new Action(myWorker, opponentCell);
        game.validateAction(swapAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void cannotSwapTooHighTest(){
        Cell myCell = game.getGameBoard().getCell(3, 2);
        Cell opponentCell = game.getGameBoard().getCell(3, 3);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        swapAction = new Action(myWorker, opponentCell);
        game.validateAction(swapAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(opponentWorker.getPosition(), opponentCell);
    }

    @Test
    void cannotSwapTooFarTest(){
        Cell myCell = game.getGameBoard().getCell(3, 2);
        Cell opponentCell = game.getGameBoard().getCell(1, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        swapAction = new Action(myWorker, opponentCell);
        game.validateAction(swapAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(opponentWorker.getPosition(), opponentCell);
    }

}