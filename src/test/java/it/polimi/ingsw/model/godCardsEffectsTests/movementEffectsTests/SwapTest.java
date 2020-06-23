package it.polimi.ingsw.model.godCardsEffectsTests.movementEffectsTests;

import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Swap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        gods.add(new God("Apollo",2, ""));
        gods.get(0).setStrategy(new Swap());
        gods.add(new God("base",2, ""));
        gods.get(1).setStrategy(new RuleSetBase());


        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.PURPLE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,1).setBlock(Block.LEVEL3);


    }

    @Test
    void correctSwapSameLevelTest() throws IOException, AddingFailedException, IllegalActionException {
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(4, 3);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);
        game.generateNextTurn();

        currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new MoveAction(myWorker, opponentCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
        assertEquals(opponentCell.getOccupiedBy(), myWorker);
        assertEquals(myCell.getOccupiedBy(), opponentWorker);

    }

    @Test
    void correctSwapHigherOneLevelTest() throws IOException, AddingFailedException, IllegalActionException {
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(4, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        game.generateNextTurn();
        currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);
        moveAction = new MoveAction(myWorker, opponentCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
        assertEquals(opponentCell.getOccupiedBy(), myWorker);
        assertEquals(myCell.getOccupiedBy(), opponentWorker);
    }

    @Test
    void correctSwapLowerAnyLevelTest() throws IOException, AddingFailedException, IllegalActionException {
        myCell = game.getGameBoard().getCell(3, 3);
        opponentCell = game.getGameBoard().getCell(3, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        game.generateNextTurn();
        currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);
        moveAction = new MoveAction(myWorker, opponentCell);
        moveAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker.getPosition(), myCell);
        assertEquals(opponentCell.getOccupiedBy(), myWorker);
        assertEquals(myCell.getOccupiedBy(), opponentWorker);
    }

    @Test
    void cannotSwapTooHighTest() throws IOException, AddingFailedException, IllegalActionException {
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(3, 3);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        game.generateNextTurn();
        currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new MoveAction(myWorker, opponentCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(opponentWorker.getPosition(), opponentCell);
        assertEquals(myCell.getOccupiedBy(), myWorker);
        assertEquals(opponentCell.getOccupiedBy(), opponentWorker);
    }

    @Test
    void cannotSwapTooFarTest() throws IOException, AddingFailedException, IllegalActionException {
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(1, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);

        game.generateNextTurn();
        currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker = players.get(1).getWorkers().get(0);

        moveAction = new MoveAction(myWorker, opponentCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(opponentWorker.getPosition(), opponentCell);
        assertEquals(myCell.getOccupiedBy(), myWorker);
        assertEquals(opponentCell.getOccupiedBy(), opponentWorker);
    }

    @Test
    void cannotSwapTwiceTest() throws IOException, AddingFailedException, IllegalActionException {
        Cell secondCellOpponent;
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(2, 2);
        secondCellOpponent = game.getGameBoard().getCell(1, 2);

        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);
        players.get(1).addWorker(secondCellOpponent);

        game.generateNextTurn();
        currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker opponentWorker1 = players.get(1).getWorkers().get(0);
        Worker opponentWorker2 = players.get(1).getWorkers().get(1);

        moveAction = new MoveAction(myWorker, opponentCell);
        moveAction.getValidation(game);

        moveAction = new MoveAction(myWorker, secondCellOpponent);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), myWorker);
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), opponentCell);
        assertEquals(opponentWorker1.getPosition(), myCell);
        assertEquals(opponentWorker2.getPosition(), secondCellOpponent);
        assertEquals(opponentCell.getOccupiedBy(), myWorker);
        assertEquals(myCell.getOccupiedBy(), opponentWorker1);
        assertEquals(opponentWorker2, secondCellOpponent.getOccupiedBy());



    }

    @Test
    void cannotSwapWithMyWorkerTest() throws IOException, AddingFailedException, IllegalActionException {
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(4, 3);

        players.get(0).addWorker(myCell);
        players.get(0).addWorker(opponentCell);

        game.generateNextTurn();
        currentPlayer = game.getCurrentTurn().getCurrentPlayer();

        Worker myWorker = currentPlayer.getWorkers().get(0);
        Worker myWorker2 = players.get(0).getWorkers().get(1);

        moveAction = new MoveAction(myWorker, opponentCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(myWorker.getPosition(), myCell);
        assertEquals(myWorker2.getPosition(), opponentCell);
        assertEquals(myCell.getOccupiedBy(), myWorker);
        assertEquals(opponentCell.getOccupiedBy(), myWorker2);
    }

    @Test
    void cannotKillYourselfIfYouCanMoveWithOtherWorkerTest() throws AddingFailedException {
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(4, 2);
        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(4,3).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,3).setBlock(Block.DOME);
        game.getGameBoard().getCell(2,0).setBlock(Block.DOME);
        game.getGameBoard().getCell(2,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,0).setBlock(Block.LEVEL1);
        players.get(0).addWorker(game.getGameBoard().getCell(4,0));
        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);
        players.get(1).addWorker(game.getGameBoard().getCell(3,0));
        game.generateNextTurn();

        List<PossibleActions> action =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(players.get(0).getWorkers().get(0));
        assertEquals(1, action.size());
        action =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(players.get(0).getWorkers().get(1));
        assertEquals(2, action.size());
        assertEquals(0, game.getWalkableCells(players.get(0).getWorkers().get(0)).size());
        assertEquals(2, game.getWalkableCells(players.get(0).getWorkers().get(1)).size());
        assertEquals(game.getGameBoard().getCell(2,2), game.getWalkableCells(players.get(0).getWorkers().get(1)).get(0));
        assertEquals(game.getGameBoard().getCell(2,3), game.getWalkableCells(players.get(0).getWorkers().get(1)).get(1));
    }

    @Test
    void canKillYourselfIfIsTheOnlyOptionTest() throws AddingFailedException {
        myCell = game.getGameBoard().getCell(3, 2);
        opponentCell = game.getGameBoard().getCell(4, 2);
        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(2,3).setBlock(Block.DOME);
        game.getGameBoard().getCell(2,2).setBlock(Block.DOME);
        game.getGameBoard().getCell(4,3).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,3).setBlock(Block.DOME);
        game.getGameBoard().getCell(2,0).setBlock(Block.DOME);
        game.getGameBoard().getCell(2,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,0).setBlock(Block.LEVEL1);
        players.get(0).addWorker(game.getGameBoard().getCell(4,0));
        players.get(0).addWorker(myCell);
        players.get(1).addWorker(opponentCell);
        players.get(1).addWorker(game.getGameBoard().getCell(3,0));
        game.generateNextTurn();

        List<PossibleActions> action =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(players.get(0).getWorkers().get(0));
        assertEquals(2, action.size());
        action =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(players.get(0).getWorkers().get(1));
        assertEquals(2, action.size());
        assertEquals(1, game.getWalkableCells(players.get(0).getWorkers().get(0)).size());
        assertEquals(game.getGameBoard().getCell(3,0), game.getWalkableCells(players.get(0).getWorkers().get(0)).get(0));
        assertEquals(1, game.getWalkableCells(players.get(0).getWorkers().get(1)).size());
        assertEquals(game.getGameBoard().getCell(4,2), game.getWalkableCells(players.get(0).getWorkers().get(1)).get(0));
    }
}