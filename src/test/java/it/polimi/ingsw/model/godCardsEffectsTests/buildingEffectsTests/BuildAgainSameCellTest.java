package it.polimi.ingsw.model.godCardsEffectsTests.buildingEffectsTests;

import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.exceptions.IllegalEndingTurnException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildAgainSameCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildAgainSameCellTest {
    private Game game;
    private Worker currentWorker, worker2;
    private BuildAction buildAction;
    private Cell firstCell;
    private Block firstBlock, secondBlock;
    private final List<Player> players = new ArrayList<>();


    @BeforeEach
    void SetUp() throws AddingFailedException, IllegalActionException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Hephaestus",2, ""));
        gods.get(0).setStrategy(new BuildAgainSameCell());
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

        firstCell = game.getGameBoard().getCell(3, 2);
        Action moveAction = new MoveAction(currentWorker, firstCell);
        moveAction.getValidation(game);
    }

    @Test
    void correctBuildAgainSameCellTest() throws IllegalActionException {
        firstCell = game.getGameBoard().getCell(4, 2);
        firstBlock = Block.LEVEL2;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.PASS_TURN));
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(2, possibleActions.size());

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(firstBlock, firstCell.getBlock());

        secondBlock = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, firstCell, secondBlock);
        buildAction.getValidation(game);

        // assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        // assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        // assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals(secondBlock, firstCell.getBlock());

    }

    @Test
    void endTurnAutomaticallyAfterSecondBuildTest() throws IOException, IllegalActionException {
        firstCell = game.getGameBoard().getCell(2, 3);
        firstBlock = Block.LEVEL1;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(firstBlock, firstCell.getBlock());

        secondBlock = Block.LEVEL2;
        buildAction = new BuildAction(currentWorker, firstCell, secondBlock);
        buildAction.getValidation(game);

        // assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        // assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        // assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals(secondBlock, firstCell.getBlock());
    }

    @Test
    void endTurnAutomaticallyAfterBuildingLevel3Test() throws IOException, IllegalActionException {
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(1, possibleActions.size());
        firstCell = game.getGameBoard().getCell(3, 3);
        firstBlock = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals(firstBlock, firstCell.getBlock());
    }

    @Test
    void endTurnAutomaticallyAfterBuildingDomeTest() throws IOException, IllegalActionException {
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(1, possibleActions.size());

        firstCell = game.getGameBoard().getCell(4, 1);
        firstBlock = Block.DOME;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals(firstBlock, firstCell.getBlock());
    }

    @Test
    void cannotBuildOnADifferentCellTest() throws IOException, IllegalActionException {
        firstCell = game.getGameBoard().getCell(4, 2);
        firstBlock = Block.LEVEL2;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(firstBlock, firstCell.getBlock());

        firstCell = game.getGameBoard().getCell(3, 3);
        secondBlock = Block.LEVEL3;
        buildAction = new BuildAction(currentWorker, firstCell, secondBlock);
        try{
            buildAction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }

        // assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        // assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        // assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL2, firstCell.getBlock());
    }

    @Test
    void cannotBuildWith2DifferentWorkersTest() throws IOException, IllegalActionException {
        firstCell = game.getGameBoard().getCell(4, 2);
        firstBlock = Block.LEVEL2;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(firstBlock, firstCell.getBlock());
        secondBlock = Block.LEVEL3;
        buildAction = new BuildAction(worker2, firstCell, secondBlock);
        try{
            buildAction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(firstBlock, firstCell.getBlock());
    }

    @Test
    void canEndTurnAfter1BuildTest() throws IOException, IllegalActionException, IllegalEndingTurnException {
        firstCell = game.getGameBoard().getCell(2, 3);
        firstBlock = Block.LEVEL1;
        buildAction = new BuildAction(currentWorker, firstCell, firstBlock);
        buildAction.getValidation(game);

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(firstBlock, firstCell.getBlock());

        game.endTurn();
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
    }
}