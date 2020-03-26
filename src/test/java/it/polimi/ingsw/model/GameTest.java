package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Action moveAction, buildAction;
    private List<Cell> workerCells;
    private Cell destCell;
    private List<Player> players;
    private GameBoard board;
    private Game game;
    private Worker worker;


    @BeforeEach
    void SetUp() {
        board = new GameBoard();
        players = new ArrayList<Player>();
        workerCells = new ArrayList<Cell>();


        players.add(new Player("name1", new God("artemis"), Color.BLUE));
        workerCells.add(new Cell(4, 2));
        worker = new Worker(workerCells.get(0), players.get(0));

        game = new Game(board, players);

        destCell = new Cell(4, 3);
        moveAction = new Action(worker, destCell);

    }

    @Test
    void ProperOverridingTest() {
        players.get(0).setAction(moveAction);
        players.get(0).useAction();
        //game.validateAction(moveAction);


    }
}