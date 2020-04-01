package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.movementEffects.Swap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SwapTest {

    private List<Player> players;
    private Game game;
    private Action moveAction;
    private Cell myCell, myCell2;
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
        game.getGameBoard().getCell(4,1).setBlock(Block.LEVEL3);

        game.generateNextTurn();

        currentPlayer = game.getCurrentTurn().getCurrentPlayer();
    }

    @Test
    void correctSwapSameLevelTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        myCell2 = game.getGameBoard().getCell(4, 3);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(myCell2);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new Action(myWorker, myCell2);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell2);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void correctSwapHigherOneLevelTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        myCell2 = game.getGameBoard().getCell(4, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(myCell2);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);
        moveAction = new Action(myWorker, myCell2);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell2);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void correctSwapLowerAnyLevelTest(){
        myCell = game.getGameBoard().getCell(3, 3);
        myCell2 = game.getGameBoard().getCell(3, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(myCell2);


        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);
        moveAction = new Action(myWorker, myCell2);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell2);
        assertEquals(opponentWorker.getPosition(), myCell);
    }

    @Test
    void cannotSwapTooHighTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        myCell2 = game.getGameBoard().getCell(3, 3);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(myCell2);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new Action(myWorker, myCell2);
        game.validateAction(moveAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(opponentWorker.getPosition(), myCell2);
    }

    @Test
    void cannotSwapTooFarTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        myCell2 = game.getGameBoard().getCell(1, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(myCell2);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new Action(myWorker, myCell2);
        game.validateAction(moveAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(opponentWorker.getPosition(), myCell2);
    }

    @Test
    void cannotSwapTwiceTest(){
        Cell secondCellOpponent;
        myCell = game.getGameBoard().getCell(3, 2);
        myCell2 = game.getGameBoard().getCell(2, 2);
        secondCellOpponent = game.getGameBoard().getCell(1, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(myCell2);
        players.get(1).addWorker(secondCellOpponent);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker1 = players.get(1).getWorkers().get(0);
        Worker opponentWorker2 = players.get(1).getWorkers().get(1);

        moveAction = new Action(myWorker, myCell2);
        game.validateAction(moveAction);

        moveAction = new Action(myWorker, secondCellOpponent);
        game.validateAction(moveAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell2);
        assertEquals(opponentWorker1.getPosition(), myCell);
        assertEquals(opponentWorker2.getPosition(), secondCellOpponent);


    }

    @Test
    void cannotSwapWithMyWorkerTest(){
        myCell = game.getGameBoard().getCell(3, 2);
        myCell2 = game.getGameBoard().getCell(4, 3);

        players.get(0).addWorker(myCell);
        players.get(0).addWorker(myCell2);

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker myWorker2 = players.get(0).getWorkers().get(1);

        moveAction = new Action(myWorker, myCell2);
        game.validateAction(moveAction);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(myWorker2.getPosition(), myCell2);
    }
}