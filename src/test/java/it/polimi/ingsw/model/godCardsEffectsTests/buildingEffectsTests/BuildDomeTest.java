package it.polimi.ingsw.model.godCardsEffectsTests.buildingEffectsTests;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildDome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildDomeTest {
    private Game game;
    private Worker currentWorker;
    private BuildAction buildAction;
    private Cell targetCell;
    private Block block;

    @BeforeEach
    void SetUp() {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Atlas"));
        gods.get(0).setStrategy(new BuildDome());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());

        List<Player> players = new ArrayList<>();
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

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);

        game.generateNextTurn();

        targetCell = game.getGameBoard().getCell(2, 3);
        Action moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
    }

    @Test
    void canBuildDomeAnywhereTest() {
        targetCell = game.getGameBoard().getCell(3, 3);//LEVEL2
        block = Block.DOME;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        buildAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 0);
        assertEquals(targetCell.getBlock(), block);
    }

    @Test
    void cannotBuildDomeOverDomeTest(){
        targetCell = game.getGameBoard().getCell(3, 4);//DOME
        block = Block.DOME;
        buildAction = new BuildAction(currentWorker, targetCell, block);
        buildAction.getValidation(game);

        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getBuildsAvailable(), 1);
        assertEquals(targetCell.getBlock(), Block.DOME);
    }

}
