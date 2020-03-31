package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.movementEffects.Push;
import it.polimi.ingsw.model.godCardEffects.winConditionEffects.Down2Levels;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Down2LevelsTest {

    private List<Player> players;
    private Game game;
    private List<Worker> workers;
    private List<God> gods;
    private Action panAction;

    @BeforeEach
    void SetUp () {
        gods = new ArrayList<>();
        gods.add(new God("Pan"));
        gods.get(0).setStrategy(new Down2Levels());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());


        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.WHITE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        //gods.get(0).getStrategy().setGame(game);

        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);

        game.generateNextTurn();

    }


    @Test
    void fromLevel2toLevel0Test() {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        panAction = new Action(currentPlayer.getWorkers().get(0), game.getGameBoard().getCell(3,1));
        game.validateAction(panAction);
        assertEquals(game.getWinner(), currentPlayer);
    }
    @Test
    void fromLevel3toLevel1Test() {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL1);
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        panAction = new Action(currentPlayer.getWorkers().get(0), game.getGameBoard().getCell(3,1));
        game.validateAction(panAction);
        assertEquals(game.getWinner(), currentPlayer);
    }
    @Test
    void fromLevel3toLevel0Test() {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL0);
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        panAction = new Action(currentPlayer.getWorkers().get(0), game.getGameBoard().getCell(3,1));
        game.validateAction(panAction);
        assertEquals(game.getWinner(), currentPlayer);
    }
    @Test
    void normalWinTest() {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL3);
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        panAction = new Action(currentPlayer.getWorkers().get(0), game.getGameBoard().getCell(3,1));
        game.validateAction(panAction);
        assertEquals(game.getWinner(), currentPlayer);
    }
    @Test
    void notWinTest() {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL3);
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        panAction = new Action(currentPlayer.getWorkers().get(0), game.getGameBoard().getCell(3,1));
        game.validateAction(panAction);
        assertNull(game.getWinner());
    }
}