package it.polimi.ingsw.model.effectsTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.godCardEffects.RuleSetBase;
import it.polimi.ingsw.model.godCardEffects.winConditionEffects.Down2Levels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleSetBaseTest {


    private List<Player> players;
    private Game game;
    private List<God> gods;
    private Action normalAction;

    @BeforeEach
    void SetUp () {
        gods = new ArrayList<>();
        gods.add(new God("base1"));
        gods.get(0).setStrategy(new RuleSetBase());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());


        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.WHITE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        gods.get(0).getStrategy().setGame(game);

        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3,4).setHasDome(true); //One adjacent cell has a Dome so a worker can't move nor build
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL1);

        game.generateNextTurn();

    }


    @Test
    void getWalkableCellsTest(){
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        Worker currentWorker = currentPlayer.getWorkers().get(0);
        System.out.println(game.getWalkableCells(currentWorker).toString());
    }

    @Test
    void getBuildableCellsTest(){
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        Worker currentWorker = currentPlayer.getWorkers().get(0);
        System.out.println(game.getBuildableCells(currentWorker).toString());
    }

    @Test
    void cantMoveOnDome() {
        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        Cell startingCell = game.getGameBoard().getCell(3, 2);
        Cell targetCell = game.getGameBoard().getCell(3, 1);
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        Worker currentWorker = currentPlayer.getWorkers().get(0);
        normalAction = new Action(currentPlayer.getWorkers().get(0), game.getGameBoard().getCell(3,1));
        game.validateAction(normalAction);
        assertFalse(game.getCurrentRuleSet().validateMoveAction(normalAction)); //we shouldn't be able to do it

        for(Cell cell : game.getGameBoard().getAllCells()) {
            if (cell.equals(startingCell)) {
                assertNotNull(currentWorker.getPosition().getOccupiedBy());
                assertEquals(startingCell.getOccupiedBy().getOwner(), players.get(0));
                assertEquals(startingCell.getOccupiedBy(), players.get(0).getWorkers().get(0));
                continue;
            }
            assertNull(cell.getOccupiedBy());
        }
    }

    @Test
    void isBuildActionValidTest() {
        Action buildAction;
        Worker currentWorker;
        Cell targetCell;
        Block block;

        game.getGameBoard().getCell(3,1).setBlock(Block.DOME);
        game.getGameBoard().getCell(3,1).setHasDome(true);
        game.getGameBoard().getCell(3,2).setBlock(Block.LEVEL0);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL2);


        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        currentWorker = players.get(0).getWorkers().get(0);

        for (int i = 0; i < game.getPlayers().size(); i++)
            game.generateNextTurn();
        targetCell = game.getGameBoard().getCell(3, 1);
        block = Block.LEVEL1;
        buildAction = new Action(currentWorker, targetCell, block);
        game.getBuildableCells(currentWorker);
        game.validateAction(buildAction);
        assertEquals(targetCell.getBlock(), Block.DOME);
        assertTrue(targetCell.hasDome());

        for (int i = 0; i < game.getPlayers().size(); i++)
            game.generateNextTurn();
        targetCell = game.getGameBoard().getCell(3, 2);
        block = Block.LEVEL1;
        buildAction = new Action(currentWorker, targetCell, block);
        game.getBuildableCells(currentWorker);
        game.validateAction(buildAction);
        assertEquals(targetCell.getBlock(), Block.LEVEL0);
        assertEquals(targetCell.getOccupiedBy(), currentWorker);


        for (int i = 0; i < game.getPlayers().size(); i++)
            game.generateNextTurn();
        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL3;
        buildAction = new Action(currentWorker, targetCell, block);
        game.getBuildableCells(currentWorker);
        game.validateAction(buildAction);
        assertEquals(targetCell.getBlock(), block);
        assertFalse(targetCell.hasDome());

        for (int i = 0; i < game.getPlayers().size(); i++)
            game.generateNextTurn();
        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.LEVEL2;
        buildAction = new Action(currentWorker, targetCell, block);
        game.getBuildableCells(currentWorker);
        game.validateAction(buildAction);
        assertEquals(targetCell.getBlock(), Block.LEVEL3);
        assertFalse(targetCell.hasDome());

        for (int i = 0; i < game.getPlayers().size(); i++)
            game.generateNextTurn();
        targetCell = game.getGameBoard().getCell(3, 3);
        block = Block.DOME;
        buildAction = new Action(currentWorker, targetCell, block);
        game.getBuildableCells(currentWorker);
        game.validateAction(buildAction);
        assertEquals(targetCell.getBlock(), block);
        assertTrue(targetCell.hasDome());

        for (int i = 0; i < game.getPlayers().size(); i++)
            game.generateNextTurn();
        targetCell = game.getGameBoard().getCell(3, 1);
        block = Block.LEVEL1;
        buildAction = new Action(currentWorker, targetCell, block);
        game.getBuildableCells(currentWorker);
        game.validateAction(buildAction);
        assertEquals(targetCell.getBlock(), Block.DOME);
        assertTrue(targetCell.hasDome());
    }

}