package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.model.godCardEffects.movementEffects.Push;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildDomeTest {
    private List<Player> players;
    private Game game;
    private List<God> gods;
    private Action panAction;
    private Worker currentWorker;


    @BeforeEach
    void SetUp() {
        gods = new ArrayList<>();
        gods.add(new God("Atlas"));
        gods.get(0).setStrategy(new BuildDome());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());

        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.WHITE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);

        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);

        game.generateNextTurn();

    }

    @Test
    void canBuildDomeAnywhereTest() {
        Action buildAction, moveAction;
        Cell targetCell;
        Block block;

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));

        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        targetCell = game.getGameBoard().getCell(3, 3);//LEVEL2
        block = Block.DOME;
        buildAction = new Action(currentWorker, targetCell, block);
        game.validateAction(buildAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 0);
        assertEquals(targetCell.getBlock(), block);
    }

    @Test
     void cannotBuildOnMyCellTest() {
        Action buildAction, moveAction;
        Cell targetCell;
        Block block;

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));

        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        targetCell = game.getGameBoard().getCell(2, 3);//LEVEL2
        block = Block.DOME;
        buildAction = new Action(currentWorker, targetCell, block);
        game.validateAction(buildAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.LEVEL0);
    }

    @Test
    void cannotBuildDomeOverDomeTest(){
        Action buildAction, moveAction;
        Cell targetCell;
        Block block;

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));

        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new Action(currentWorker, targetCell);
        game.validateAction(moveAction);

        targetCell = game.getGameBoard().getCell(3, 4);//DOME
        block = Block.DOME;
        buildAction = new Action(currentWorker, targetCell, block);
        game.validateAction(buildAction);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.DOME);
    }

}
