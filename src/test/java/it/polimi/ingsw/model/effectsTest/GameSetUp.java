package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.movementEffects.Push;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public class GameSetUp {

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

        gods.get(0).getStrategy().setGame(game);

        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        //players.get(0).addWorker(game.getGameBoard().getCell(4,2));
        players.get(0).addWorker(game.getGameBoard().getCell(3,2));
        players.get(1).addWorker(game.getGameBoard().getCell(3,1));
        //players.get(1).addWorker(game.getGameBoard().getCell(1,1));

        game.getGameBoard().getCell(3,4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL1); //One adjacent cell has a level 2 block on it: a worker can build but cannot move from level0
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);

        game.generateNextTurn();

    }
}
