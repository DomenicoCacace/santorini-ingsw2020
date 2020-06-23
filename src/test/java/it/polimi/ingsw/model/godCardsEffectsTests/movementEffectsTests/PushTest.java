package it.polimi.ingsw.model.godCardsEffectsTests.movementEffectsTests;

import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
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
        gods.add(new God("minotaur",2,""));
        gods.get(0).setStrategy(new Push());
        gods.add(new God("base",2,""));
        gods.get(1).setStrategy(new RuleSetBase());

        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.PURPLE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL1); //One adjacent cell has a level 2 block on it: a worker can build but cannot move from level0
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);
    }


    @Test
    void correctPushTest () throws IOException, AddingFailedException, IllegalActionException {
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
                assertEquals(players.get(1).getWorkers().get(0), pushedCell.getOccupiedBy());
                assertEquals(players.get(1).getWorkers().get(0).getPosition(), pushedCell);
                continue;
            }

            if (cell.equals(targetCell)) {
                assertTrue(currentPlayer.getWorkers().contains(targetCell.getOccupiedBy()));
                assertEquals(players.get(0).getWorkers().get(0), targetCell.getOccupiedBy());
                assertEquals(players.get(0).getWorkers().get(0).getPosition(), targetCell);
                continue;
            }
            assertNull(cell.getOccupiedBy());
        }
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertTrue(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }

    @Test
    void cannotPushOutsideTest() throws IOException, AddingFailedException, IllegalActionException {
        players.get(0).addWorker(game.getGameBoard().getCell(1, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(0, 2));
        game.generateNextTurn();
        Worker currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);

        Cell startingCell = game.getGameBoard().getCell(1, 2);
        Cell targetCell = game.getGameBoard().getCell(0, 2);
        moveAction = new MoveAction(currentWorker, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }

        for(Cell cell : game.getGameBoard().getAllCells()) {
            if (cell.equals(startingCell)) {
                assertNotNull(currentWorker.getPosition().getOccupiedBy());
                assertTrue(players.get(0).getWorkers().contains(startingCell.getOccupiedBy()));
                assertEquals(players.get(0).getWorkers().get(0), startingCell.getOccupiedBy());
                continue;
            }
            if (cell.equals(targetCell)) {
                assertNotNull(players.get(1).getWorkers().get(0).getPosition().getOccupiedBy());
                assertTrue(players.get(1).getWorkers().contains(targetCell.getOccupiedBy()));
                assertEquals(players.get(1).getWorkers().get(0), targetCell.getOccupiedBy());
                continue;
            }
            assertNull(cell.getOccupiedBy());
        }
        assertNull(game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(1, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
    }
}
