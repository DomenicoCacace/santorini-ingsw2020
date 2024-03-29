package it.polimi.ingsw.server.model.godCardsEffectsTests.affectMyTurnEffectsTests;

import it.polimi.ingsw.server.exceptions.AddingFailedException;
import it.polimi.ingsw.server.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.action.Action;
import it.polimi.ingsw.server.model.action.BuildAction;
import it.polimi.ingsw.server.model.action.MoveAction;
import it.polimi.ingsw.shared.dataClasses.*;
import it.polimi.ingsw.server.model.godCardsEffects.affectMyTurnEffects.BuildBeforeAfterMovement;
import it.polimi.ingsw.server.model.rules.RuleSetBase;
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
    private final List<Player> players = new ArrayList<>();


    @BeforeEach
    void SetUp() throws AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Prometheus", 2, ""));
        gods.get(0).setStrategy(new BuildBeforeAfterMovement());
        gods.add(new God("base",2, ""));
        gods.get(1).setStrategy(new RuleSetBase());

        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.PURPLE));

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
        assertEquals(3, possibleActions.size());

        Cell buildingCell = game.getGameBoard().getCell(3,2);
        Action firstBuildAction = new BuildAction(currentWorker, buildingCell, Block.LEVEL1);
        firstBuildAction.getValidation(game);
        possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertEquals(1, possibleActions.size());

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getGameBoard().getCell(2, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL1, buildingCell.getBlock());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());

        toMoveCell = game.getGameBoard().getCell(2, 3);
        Action moveAction = new MoveAction(currentWorker, toMoveCell);
        moveAction.getValidation(game);
        possibleActions = game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(1, possibleActions.size());

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(2, 3), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());

        Action secondBuildAction = new BuildAction(currentWorker, buildingCell, Block.LEVEL2);
        secondBuildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(2, 3), currentWorker.getPosition());
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals(Block.LEVEL2, buildingCell.getBlock());
    }

    @Test
    void correctBuildBeforeAfterMoveDifferentCellsTest() throws IOException, IllegalActionException {
        Cell firstBuildingCell = game.getGameBoard().getCell(1,2);
        Action firstBuildAction = new BuildAction(currentWorker, firstBuildingCell, Block.LEVEL1);
        firstBuildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertEquals(1, possibleActions.size());

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getGameBoard().getCell(2, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL1, firstBuildingCell.getBlock());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());

        toMoveCell = game.getGameBoard().getCell(3, 2);
        Action moveAction = new MoveAction(currentWorker, toMoveCell);
        moveAction.getValidation(game);
        possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(1, possibleActions.size());

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());

        Cell secondBuildingCell = game.getGameBoard().getCell(2,2);
        Action secondBuildAction = new BuildAction(currentWorker, secondBuildingCell, Block.LEVEL1);
        secondBuildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(3, 2), currentWorker.getPosition());
        assertEquals(Block.LEVEL1, secondBuildingCell.getBlock());
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
    }

    @Test
    void correctMoveUpAndBuildTest() throws IllegalActionException {

        toMoveCell = game.getGameBoard().getCell(2, 1);
        Action moveAction = new MoveAction(currentWorker, toMoveCell);
        moveAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(1, possibleActions.size());

        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(2, 1), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());


        Cell buildingCell = game.getGameBoard().getCell(2,2);
        Action secondBuildAction = new BuildAction(currentWorker, buildingCell, Block.LEVEL1);
        secondBuildAction.getValidation(game);

        assertEquals(game.getGameBoard().getCell(2, 1), currentWorker.getPosition());
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals(Block.LEVEL1, buildingCell.getBlock());


    }

    @Test
    void cannotBuildThenMoveUpTest() throws IllegalActionException {
        Cell firstBuildingCell = game.getGameBoard().getCell(1,2);
        Action firstBuildAction = new BuildAction(currentWorker, firstBuildingCell, Block.LEVEL1);
        firstBuildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertEquals(1, possibleActions.size());

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getGameBoard().getCell(2, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL1, firstBuildingCell.getBlock());

        toMoveCell = game.getGameBoard().getCell(2, 1);
        Action moveAction = new MoveAction(currentWorker, toMoveCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        assertEquals(game.getGameBoard().getCell(2, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
    }

    @Test
    void cannotBuildTwiceInARowTest() throws IOException, IllegalActionException {
        Cell firstBuildingCell = game.getGameBoard().getCell(1,2);
        Action firstBuildAction = new BuildAction(currentWorker, firstBuildingCell, Block.LEVEL1);
        firstBuildAction.getValidation(game);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertEquals(1, possibleActions.size());

        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getGameBoard().getCell(2, 2), currentWorker.getPosition());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL1, firstBuildingCell.getBlock());

        Cell secondBuildingCell = game.getGameBoard().getCell(2,1);
        Action secondBuildAction = new BuildAction(currentWorker, secondBuildingCell, Block.LEVEL2);
        try{
            secondBuildAction.getValidation(game);
        } catch(IllegalActionException e){
            assertNotNull(e);
        }

        assertEquals(1, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertEquals(Block.LEVEL1, secondBuildingCell.getBlock());

    }

    @Test
    void cannotKillYourselfTest() throws IOException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL0);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(currentWorker);
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertEquals(3, possibleActions.size());

        Action buildaction = new BuildAction(players.get(0).getWorkers().get(1),
                game.getGameBoard().getCell(4,4), Block.LEVEL1);
        try {
            buildaction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }
        assertEquals(Block.LEVEL0, game.getGameBoard().getCell(4,4).getBlock());
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(2, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void mustMoveAndBuildTest() {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL1);
        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(players.get(0).getWorkers().get(1));
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        assertEquals(2, possibleActions.size());

        Action buildaction = new BuildAction(players.get(0).getWorkers().get(1),
                game.getGameBoard().getCell(4,4), Block.LEVEL2);
        try {
            buildaction.getValidation(game);
        } catch (IllegalActionException e){
            assertNotNull(e);
        }
        assertEquals(Block.LEVEL1, game.getGameBoard().getCell(4,4).getBlock());
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(2, game.getCurrentRuleSet().getStrategy().getBuildsAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void buildAndLoseIfItIsTheOnlyActionYouCanDo() throws IllegalActionException {
        game.generateNextTurn();
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(2,1).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(3,4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,0).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,0).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,0).setBlock(Block.LEVEL3);
        Worker worker1 = players.get(0).getWorkers().get(0);
        worker1.setPosition(game.getGameBoard().getCell(2, 1));
        Worker worker2 = players.get(0).getWorkers().get(1);
        game.getGameBoard().getCell(2, 1).setOccupiedBy(worker1);
        game.getGameBoard().getCell(2, 2).setOccupiedBy(null);
        game.generateNextTurn();

        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(worker1);
        assertEquals(2, possibleActions.size());
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(worker2);
        assertEquals(2, possibleActions.size());
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        List<Cell> cellsToAssert=game.getWalkableCells(worker1);
        assertEquals(0, cellsToAssert.size());
        cellsToAssert=game.getWalkableCells(worker2);
        assertEquals(0, cellsToAssert.size());
        cellsToAssert=(game.getBuildableCells(worker1));
        assertEquals(7, cellsToAssert.size());
        assertEquals(cellsToAssert.get(0), game.getGameBoard().getCell(1,0));
        assertEquals(cellsToAssert.get(1), game.getGameBoard().getCell(1,1));
        assertEquals(cellsToAssert.get(2), game.getGameBoard().getCell(1,2));
        assertEquals(cellsToAssert.get(3), game.getGameBoard().getCell(2,0));
        assertEquals(cellsToAssert.get(4), game.getGameBoard().getCell(2,2));
        assertEquals(cellsToAssert.get(5), game.getGameBoard().getCell(3,0));
        assertEquals(cellsToAssert.get(6), game.getGameBoard().getCell(3,2));
        cellsToAssert=game.getBuildableCells(worker2);
        assertEquals(4, cellsToAssert.size());
        assertEquals(cellsToAssert.get(0), game.getGameBoard().getCell(3,2));
        assertEquals(cellsToAssert.get(1), game.getGameBoard().getCell(3,3));
        assertEquals(cellsToAssert.get(2), game.getGameBoard().getCell(4,2));
        assertEquals(cellsToAssert.get(3), game.getGameBoard().getCell(4,4));
        assertNull(game.getWinner());
        BuildAction buildAction = new BuildAction(worker1, game.getGameBoard().getCell(3,2), Block.LEVEL3);
        buildAction.getValidation(game);
        assertEquals("player2", game.getWinner().getName());

    }

    @Test
    void cannotKillHimselfWhenOnlyOneWorkerIsStuckTest() throws IllegalActionException {
        game.generateNextTurn();
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(2,1).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(3,4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,0).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,0).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,0).setBlock(Block.LEVEL3);
        Worker worker1 = players.get(0).getWorkers().get(0);
        worker1.setPosition(game.getGameBoard().getCell(2, 1));
        Worker worker2 = players.get(0).getWorkers().get(1);
        game.getGameBoard().getCell(2, 1).setOccupiedBy(worker1);
        game.getGameBoard().getCell(2, 2).setOccupiedBy(null);
        game.generateNextTurn();


        List<PossibleActions> possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(worker1);
        assertEquals(1, possibleActions.size());
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        possibleActions =  game.getCurrentTurn().getRuleSetStrategy().getPossibleActions(worker2);
        assertEquals(3, possibleActions.size());
        assertTrue(possibleActions.contains(PossibleActions.BUILD));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        List<Cell> cellsToAssert=game.getWalkableCells(worker1);
        assertEquals(0, cellsToAssert.size());
        cellsToAssert=game.getWalkableCells(worker2);
        assertEquals(1, cellsToAssert.size());
        assertEquals(cellsToAssert.get(0), game.getGameBoard().getCell(4,4));
        cellsToAssert=(game.getBuildableCells(worker1));
        assertEquals(0, cellsToAssert.size());
        cellsToAssert=game.getBuildableCells(worker2);
        assertEquals(3, cellsToAssert.size());
        assertEquals(cellsToAssert.get(0), game.getGameBoard().getCell(3,2));
        assertEquals(cellsToAssert.get(1), game.getGameBoard().getCell(3,3));
        assertEquals(cellsToAssert.get(2), game.getGameBoard().getCell(4,2));
        assertNull(game.getWinner());
        BuildAction buildAction = new BuildAction(worker2, game.getGameBoard().getCell(4,2), Block.LEVEL3);
        buildAction.getValidation(game);
        assertNull(game.getWinner());
    }




}