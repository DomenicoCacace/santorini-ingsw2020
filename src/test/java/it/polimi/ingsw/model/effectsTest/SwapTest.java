package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.movementEffects.Swap;
import it.polimi.ingsw.model.godCardEffects.winConditionEffects.Down2Levels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SwapTest {

    private List<Player> players;
    private Game game;
    private Action moveAction;
    private Cell myCell, opponentCell;
    Player currentPlayer;

    @BeforeEach
    void SetUp () {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Apollo"));
        gods.get(0).setStrategy(new Swap());
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

        currentPlayer = game.getCurrentTurn().getCurrentPlayer();
    }

    @Test
    void correctSwapSameLevelTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(4, 3);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new Action(myWorker, opponentCell);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void correctSwapHigherOneLevelTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(4, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);
        moveAction = new Action(myWorker, opponentCell);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void correctSwapLowerAnyLevelTest(){
        myCell = game.getGameBoard().getCell(3, 3);
        opponentCell = game.getGameBoard().getCell(3, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);


        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);
        moveAction = new Action(myWorker, opponentCell);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void cannotSwapTooHighTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(3, 3);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new Action(myWorker, opponentCell);
        game.validateAction(moveAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(opponentWorker.getPosition(), opponentCell);
    }

    @Test
    void cannotSwapTooFarTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(1, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new Action(myWorker, opponentCell);
        game.validateAction(moveAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(opponentWorker.getPosition(), opponentCell);
    }

}