package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.God;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.movementEffects.Push;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PushEffectTest {

    protected List<Player> players;
    protected Game game;
    protected List<Worker> workers;
    protected List<God> gods;


    @BeforeEach
    void SetUp () {
        gods = new ArrayList<>();
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

        //players.get(0).addWorker(game.getGameBoard().getCell(4,2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(3, 1));
        //players.get(1).addWorker(game.getGameBoard().getCell(1,1));

        game.getGameBoard().getCell(3, 4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3, 1).setBlock(Block.LEVEL1); //One adjacent cell has a level 2 block on it: a worker can build but cannot move from level0
        game.getGameBoard().getCell(3, 3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4, 2).setBlock(Block.LEVEL1);

        game.generateNextTurn();
    }


        private Action minotaurPush;

    @Test
    void PushEffectTest () {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();



        Worker currentWorker = currentPlayer.getWorkers().get(0);

        Cell targetCell = game.getGameBoard().getCell(3, 1);
        Cell pushedCell = new Cell(3, 0);

        minotaurPush = new Action(currentWorker, targetCell);
        assertNotNull(game.getGameBoard());

        game.validateAction(minotaurPush);

        for(Cell cell : game.getGameBoard().getAllCells()) {
            if (cell.equals(pushedCell)) {
                assertNotNull(pushedCell.getOccupiedBy());
                assertEquals(pushedCell.getOccupiedBy().getOwner(), players.get(1));
                assertEquals(pushedCell.getOccupiedBy(), players.get(1).getWorkers().get(0));
                continue;
            }
            if (cell.equals(targetCell)) {
                assertEquals(targetCell.getOccupiedBy().getOwner(), currentPlayer);
                assertEquals(pushedCell.getOccupiedBy(), players.get(0).getWorkers().get(0));
                continue;
            }
            assertNull(cell.getOccupiedBy());
        }
    }

    @Test
    void getWalkableCellsTest(){
        Worker currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        currentWorker.setWalkableCells(game.getWalkableCells(currentWorker));
        System.out.println(currentWorker.getWalkableCells().toString());
    }
}
