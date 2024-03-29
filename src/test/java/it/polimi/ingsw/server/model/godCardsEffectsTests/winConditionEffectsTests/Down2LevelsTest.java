package it.polimi.ingsw.server.model.godCardsEffectsTests.winConditionEffectsTests;

import it.polimi.ingsw.server.exceptions.AddingFailedException;
import it.polimi.ingsw.server.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.action.MoveAction;
import it.polimi.ingsw.shared.dataClasses.GameBoard;
import it.polimi.ingsw.shared.dataClasses.Color;
import it.polimi.ingsw.shared.dataClasses.Block;
import it.polimi.ingsw.shared.dataClasses.Worker;
import it.polimi.ingsw.server.model.rules.RuleSetBase;
import it.polimi.ingsw.server.model.godCardsEffects.winConditionEffects.Down2Levels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Down2LevelsTest {
    private Game game;
    private Worker currentWorker;
    private MoveAction moveAction;
    private final List<Player> players = new ArrayList<>();

    @BeforeEach
    void SetUp () throws  AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Pan",2,""));
        gods.get(0).setStrategy(new Down2Levels());
        gods.add(new God("base", 2,""));
        gods.get(1).setStrategy(new RuleSetBase());

        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.PURPLE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);

        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(0,0));
        currentWorker = players.get(0).getWorkers().get(0);

        game.generateNextTurn();
    }


    @Test
    void fromLevel2toLevel0Test() throws  IllegalActionException {
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertEquals(game.getWinner(), players.get(0));
    }
    @Test
    void fromLevel3toLevel1Test() throws  IllegalActionException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL1);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertEquals(game.getWinner(), players.get(0));
    }
    @Test
    void fromLevel3toLevel0Test() throws  IllegalActionException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL0);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertEquals(game.getWinner(), players.get(0));
    }

    @Test
    void normalWinTest() throws  IllegalActionException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL3);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertEquals(game.getWinner(), players.get(0));
    }

    @Test
    void notWinTest() throws  IllegalActionException {
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.LEVEL3);

        moveAction = new MoveAction(currentWorker, game.getGameBoard().getCell(3,1));
        moveAction.getValidation(game);
        assertNull(game.getWinner());
    }
}