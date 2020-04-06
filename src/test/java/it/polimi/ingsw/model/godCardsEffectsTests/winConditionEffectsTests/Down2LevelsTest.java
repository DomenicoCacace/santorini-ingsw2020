package it.polimi.ingsw.model.godCardsEffectsTests.winConditionEffectsTests;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.godCardsEffects.winConditionEffects.Down2Levels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Down2LevelsTest {
    private Game game;
    private Worker currentWorker;
    private MoveAction moveAction;

    @BeforeEach
    void SetUp () throws IOException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Pan"));
        gods.get(0).setStrategy(new Down2Levels());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());


        List<Player> players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.WHITE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);

        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);

        game.generateNextTurn();
    }


    @Test
    void fromLevel2toLevel0Test() throws IOException {
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);


        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertEquals(game.getWinner(), currentWorker.getOwner());
    }
    @Test
    void fromLevel3toLevel1Test() throws IOException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL1);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertEquals(game.getWinner(), currentWorker.getOwner());
    }
    @Test
    void fromLevel3toLevel0Test() throws IOException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL0);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertEquals(game.getWinner(), currentWorker.getOwner());
    }
    @Test
    void normalWinTest() throws IOException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL3);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertEquals(game.getWinner(), currentWorker.getOwner());
    }

    @Test
    void cannotBuildAfterWinningTest() throws IOException {

        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);
        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        Action buildAction = new BuildAction(currentWorker, game.getGameBoard().getCell(3,2), Block.LEVEL3);
        buildAction.getValidation(game);

//TODO:        assertEquals(Block.LEVEL2, game.getGameBoard().getCell(3,2).getBlock());


    }

    @Test
    void notWinTest() throws IOException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL3);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertNull(game.getWinner());
    }
}