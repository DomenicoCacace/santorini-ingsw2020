package it.polimi.ingsw.model.godCardsEffectsTests.movementEffectsTests;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Push;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PushTest {

    private Game game;
    private List<Player> players;
    private MoveAction moveAction;

    @BeforeEach
    void SetUp () throws IOException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("minotaur"));
        gods.get(0).setStrategy(new Push());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());

        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.WHITE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL1); //One adjacent cell has a level 2 block on it: a worker can build but cannot move from level0
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);
    }


    @Test
    void correctPushTest () throws IOException {
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(3, 1));
        game.generateNextTurn();
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        Worker currentWorker = currentPlayer.getWorkers().get(0);

        Cell targetCell = game.getGameBoard().getCell(3, 1);
        Cell pushedCell = game.getGameBoard().getCell(3,0);

        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        for(Cell cell : game.getGameBoard().getAllCells()) {
            if (cell.equals(pushedCell)) {
                assertNotNull(pushedCell.getOccupiedBy());
                assertTrue(players.get(1).getWorkers().contains(pushedCell.getOccupiedBy()));
                assertEquals(pushedCell.getOccupiedBy(), players.get(1).getWorkers().get(0));
                continue;
            }

            if (cell.equals(targetCell)) {
                assertTrue(currentPlayer.getWorkers().contains(targetCell.getOccupiedBy()));
                assertEquals(targetCell.getOccupiedBy(), players.get(0).getWorkers().get(0));
                continue;
            }
            assertNull(cell.getOccupiedBy());
        }
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void getWalkableCellsTest() throws IOException {
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(3, 1));
        game.generateNextTurn();
        Worker currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);

        System.out.println(game.getWalkableCells(currentWorker));
    }

    @Test
    void getBuildableCellsTest() throws IOException {
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(3, 1));
        game.generateNextTurn();
        Worker currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);

        System.out.println(game.getBuildableCells(currentWorker));
    }

    @Test
    void cannotPushOutsideTest() throws IOException {
        players.get(0).addWorker(game.getGameBoard().getCell(1, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(0, 2));
        game.generateNextTurn();
        Worker currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);

        Cell startingCell = game.getGameBoard().getCell(1, 2);
        Cell targetCell = game.getGameBoard().getCell(0, 2);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);

        for(Cell cell : game.getGameBoard().getAllCells()) {
            if (cell.equals(startingCell)) {
                assertNotNull(currentWorker.getPosition().getOccupiedBy());
                assertTrue(players.get(0).getWorkers().contains(startingCell.getOccupiedBy()));
                assertEquals(startingCell.getOccupiedBy(), players.get(0).getWorkers().get(0));
                continue;
            }
            if (cell.equals(targetCell)) {
                assertNotNull(players.get(1).getWorkers().get(0).getPosition().getOccupiedBy());
                assertTrue(players.get(1).getWorkers().contains(targetCell.getOccupiedBy()));
                assertEquals(targetCell.getOccupiedBy(), players.get(1).getWorkers().get(0));
                continue;
            }
            assertNull(cell.getOccupiedBy());
        }
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 1);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }
}
