package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleSetBaseTest {

    private List<Cell> workerCells;
    private List<Player> players;
    private GameBoard board;
    private Game game;
    private Worker worker;

    @BeforeEach
    void SetUp() {
        board = new GameBoard();
        players = new ArrayList<Player>();
        //workerCells = new ArrayList<Cell>(); Not needed
        players.add(new Player("name1", new God("artemis"), Color.BLUE));
        //workerCells.add(new Cell(4, 2)); Not needed
        board.getCell(3,2).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        board.getCell(3,1).setBlock(Block.LEVEL3); //One adjacent cell has a level 2 block on it: a worker can build but cannot move from level0
        board.getCell(3,3).setBlock(Block.LEVEL2);
        board.getCell(4,2).setBlock(Block.LEVEL1);//The worker is on a Level1 Cell
        worker = new Worker(board.getCell(4,2), players.get(0));
        game = new Game(board, players);
    }

    @Test
    void getWalkableCellsTest(){
        worker.setWalkableCells(game.getWalkableCells(worker));
        System.out.println(worker.getWalkableCells().toString());
    }

    @Test
    void getBuildableCellsTest(){
        worker.setBuildableCells(game.getBuildableCells(worker));
        System.out.println(worker.getBuildableCells().toString());
    }

}