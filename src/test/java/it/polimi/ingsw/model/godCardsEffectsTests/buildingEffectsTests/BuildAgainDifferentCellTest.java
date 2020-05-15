package it.polimi.ingsw.model.godCardsEffectsTests.buildingEffectsTests;

import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.exceptions.IllegalEndingTurnException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildAgainDifferentCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildAgainDifferentCellTest {
    private Game game;
    private Worker currentWorker, worker2;
    private BuildAction buildAction;
    private Cell targetCell, firstCell, secondCell;
    private Block firstBlock, secondBlock;
    private final List<Player> players = new ArrayList<>();


    @BeforeEach
    void SetUp() throws IOException, AddingFailedException, IllegalActionException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Demeter",2, ""));
        gods.get(0).setStrategy(new BuildAgainDifferentCell());
        gods.add(new God("base",2, ""));
        gods.get(1).setStrategy(new RuleSetBase());

        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.PURPLE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,1).setBlock(Block.LEVEL3);

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(4, 3));
        currentWorker = players.get(0).getWorkers().get(0);
        worker2 = players.get(0).getWorkers().get(1);

        players.get(1).addWorker(game.getGameBoard().getCell(0, 0));

        game.generateNextTurn();

        targetCell = game.getGameBoard().getCell(3, 2);
        Action moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
    }

    @Test
    void correctBuildAgainDifferentCellTest() throws IOException, IllegalActionException {
        firstCell = game.getGameBoard().getCell(4, 2);
        firstBlock = Block.LEVEL2;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertTrue(possibleActions.contains(PossibleActions.PASSTURN));
        assertEquals(possibleActions.size(), 2);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstCell.getBlock(), firstBlock);

        secondCell = game.getGameBoard().getCell(3, 3);
        secondBlock = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, secondCell, secondBlock);
        buildAction.getValidation(game);

        // assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        // assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        // assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
        assertEquals(secondCell.getBlock(), secondBlock);

    }

    @Test
    void cannotBuildOnTheSameCellTest() throws IOException, IllegalActionException {
        firstCell = game.getGameBoard().getCell(4, 2);
        firstBlock = Block.LEVEL2;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstCell.getBlock(), firstBlock);

        secondBlock = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, firstCell, secondBlock);
        try{
            buildAction.getValidation(game);
        } catch(IllegalActionException e){
            assertNotNull(e);
        }

        // assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        // assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        // assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstCell.getBlock(), firstBlock);
    }

    @Test
    void cannotBuildWith2DifferentWorkersTest() throws IllegalActionException {
        firstCell = game.getGameBoard().getCell(4, 2);
        firstBlock = Block.LEVEL2;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);

        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.PASSTURN));
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(possibleActions.size(), 2);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstCell.getBlock(), firstBlock);
        secondBlock = Block.LEVEL3;
        buildAction = new BuildAction(worker2, game.getGameBoard().getCell(3,3), secondBlock);
        try{
            buildAction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(game.getGameBoard().getCell(3,3).getBlock(), Block.LEVEL2);
    }

    @Test
    void endTurnAutomaticallyAfterSecondBuildTest() throws IOException, IllegalActionException {
        firstCell = game.getGameBoard().getCell(2, 3);
        firstBlock = Block.LEVEL1;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstCell.getBlock(), firstBlock);

        secondBlock = Block.LEVEL1;
        buildAction = new BuildAction(currentWorker, game.getGameBoard().getCell(2, 2), secondBlock);
        buildAction.getValidation(game);

        // assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        // assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        // assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getGameBoard().getCell(2, 2).getBlock(), secondBlock);
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
    }

    @Test
    void canEndTurnAfter1BuildTest() throws IOException, IllegalActionException, IllegalEndingTurnException {
        firstCell = game.getGameBoard().getCell(2, 3);
        firstBlock = Block.LEVEL1;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.PASSTURN));
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(possibleActions.size(), 2);


        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstCell.getBlock(), firstBlock);

        game.endTurn();
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
    }
}