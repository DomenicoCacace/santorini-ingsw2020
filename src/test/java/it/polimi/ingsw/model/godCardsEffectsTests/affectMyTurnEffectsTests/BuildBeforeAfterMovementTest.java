package it.polimi.ingsw.model.godCardsEffectsTests.affectMyTurnEffectsTests;

import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.godCardsEffects.affectMyTurnEffects.BuildBeforeAfterMovement;
import it.polimi.ingsw.model.rules.RuleSetBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildBeforeAfterMovementTest {
    private Game game;
    private Worker currentWorker;
    private Cell toMoveCell;
    private List<Player> players = new ArrayList<>();


    @BeforeEach
    void SetUp() throws AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Prometeus", 2, ""));
        gods.get(0).setStrategy(new BuildBeforeAfterMovement());
        gods.add(new God("base",2, ""));
        gods.get(1).setStrategy(new RuleSetBase());

        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.GREEN));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(2,1).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(3,4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,1).setBlock(Block.LEVEL3);
        
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(4, 3));
        currentWorker = players.get(0).getWorkers().get(0);

        players.get(1).addWorker(game.getGameBoard().getCell(0, 0));

        game.generateNextTurn();
    }

    @Test
    void correctBuildBeforeAfterMoveSameCellTest() throws IllegalActionException {
        List<PossibleActions> possibleActions = game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        assertEquals(possibleActions.size(), 3);

        Cell buildingCell = game.getGameBoard().getCell(3,2);
        Action firstBuildAction = new BuildAction(currentWorker, buildingCell, Block.LEVEL1);
        firstBuildAction.getValidation(game);
        possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertEquals(possibleActions.size(), 1);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(buildingCell.getBlock(), Block.LEVEL1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);

        toMoveCell = game.getGameBoard().getCell(2, 3);
        Action moveAction = new MoveAction(currentWorker, toMoveCell);
        moveAction.getValidation(game);
        possibleActions = game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(possibleActions.size(), 1);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 3));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);

        Action secondBuildAction = new BuildAction(currentWorker, buildingCell, Block.LEVEL2);
        secondBuildAction.getValidation(game);

        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 3));
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
        assertEquals(buildingCell.getBlock(), Block.LEVEL2);
    }

    @Test
    void correctBuildBeforeAfterMoveDifferentCellsTest() throws IOException, IllegalActionException {
        Cell firstBuildingCell = game.getGameBoard().getCell(1,2);
        Action firstBuildAction = new BuildAction(currentWorker, firstBuildingCell, Block.LEVEL1);
        firstBuildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertEquals(possibleActions.size(), 1);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstBuildingCell.getBlock(), Block.LEVEL1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);

        toMoveCell = game.getGameBoard().getCell(3, 2);
        Action moveAction = new MoveAction(currentWorker, toMoveCell);
        moveAction.getValidation(game);
        possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(possibleActions.size(), 1);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);

        Cell secondBuildingCell = game.getGameBoard().getCell(2,2);
        Action secondBuildAction = new BuildAction(currentWorker, secondBuildingCell, Block.LEVEL1);
        secondBuildAction.getValidation(game);

        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(3, 2));
        assertEquals(secondBuildingCell.getBlock(), Block.LEVEL1);
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
    }

    @Test
    void correctMoveUpAndBuildTest() throws IllegalActionException {

        toMoveCell = game.getGameBoard().getCell(2, 1);
        Action moveAction = new MoveAction(currentWorker, toMoveCell);
        moveAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(possibleActions.size(), 1);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 1));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);


        Cell buildingCell = game.getGameBoard().getCell(2,2);
        Action secondBuildAction = new BuildAction(currentWorker, buildingCell, Block.LEVEL1);
        secondBuildAction.getValidation(game);

        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 1));
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
        assertEquals(buildingCell.getBlock(), Block.LEVEL1);


    }

    @Test
    void cannotBuildThenMoveUpTest() throws IllegalActionException {
        Cell firstBuildingCell = game.getGameBoard().getCell(1,2);
        Action firstBuildAction = new BuildAction(currentWorker, firstBuildingCell, Block.LEVEL1);
        firstBuildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertEquals(possibleActions.size(), 1);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstBuildingCell.getBlock(), Block.LEVEL1);

        toMoveCell = game.getGameBoard().getCell(2, 1);
        Action moveAction = new MoveAction(currentWorker, toMoveCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
    }

    @Test
    void cannotBuildTwiceInARowTest() throws IOException, IllegalActionException {
        Cell firstBuildingCell = game.getGameBoard().getCell(1,2);
        Action firstBuildAction = new BuildAction(currentWorker, firstBuildingCell, Block.LEVEL1);
        firstBuildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertEquals(possibleActions.size(), 1);

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(currentWorker.getPosition(), game.getGameBoard().getCell(2, 2));
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(firstBuildingCell.getBlock(), Block.LEVEL1);

        Cell secondBuildingCell = game.getGameBoard().getCell(2,1);
        Action secondBuildAction = new BuildAction(currentWorker, secondBuildingCell, Block.LEVEL2);
        try{
            secondBuildAction.getValidation(game);
        } catch(IllegalActionException e){
            assertNotNull(e);
        }

        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(secondBuildingCell.getBlock(), Block.LEVEL1);

    }

    @Test
    void cannotKillYourselfTest() throws IOException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL0);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(possibleActions.size(), 3);

        Action buildaction = new BuildAction(players.get(0).getWorkers().get(1),
                game.getGameBoard().getCell(4,4), Block.LEVEL1);
        try {
            buildaction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }
        assertEquals(game.getGameBoard().getCell(4,4).getBlock(), Block.LEVEL0);
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 2);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void mustMoveAndBuildTest() {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL1);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(players.get(0).getWorkers().get(1));
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        assertEquals(possibleActions.size(), 2);

        Action buildaction = new BuildAction(players.get(0).getWorkers().get(1),
                game.getGameBoard().getCell(4,4), Block.LEVEL2);
        try {
            buildaction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }
        assertEquals(game.getGameBoard().getCell(4,4).getBlock(), Block.LEVEL1);
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 2);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }



}