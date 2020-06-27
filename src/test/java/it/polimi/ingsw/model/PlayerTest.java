package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.exceptions.NotYourWorkerException;
import it.polimi.ingsw.exceptions.WrongSelectionException;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.dataClass.PlayerData;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Swap;
import it.polimi.ingsw.model.rules.RuleSetBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest implements BuildableCellsListener, AddWorkerListener, BuildingBlocksListener, WalkableCellsListener, SelectWorkerListener {
    private List<Player> players;
    private Game game;
    private Action buildAction, moveAction;
    private Worker currentWorker;
    private Cell targetCell;
    private Block block;

    private String name;
    private List<Cell> validCells;
    private List<Cell> board;
    private List<PossibleActions> possibleActions;
    private Worker selectedWorker;
    private List<Block> blocks;


    @BeforeEach
    void SetUp() throws IOException, AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("Apollo",2,""));
        gods.get(0).setStrategy(new Swap());
        gods.add(new God("Atlas",2,""));
        gods.get(1).setStrategy(new BuildDome());


        players = new ArrayList<>();
        players.add(new Player("player1", gods.get(0), Color.BLUE));
        players.add(new Player("player2", gods.get(1), Color.PURPLE));

        players.forEach(player -> {
            player.addWorkerListener(this);
            player.addBuildableCellsListener(this);
            player.addWalkableCellsListener(this);
            player.addSelectWorkerListener(this);
            player.addBuildingBlocksListener(this);
        });

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

        game.getGameBoard().getCell(3, 4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3, 3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4, 2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(3, 1).setBlock(Block.DOME);
        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));
        players.get(1).addWorker(game.getGameBoard().getCell(4, 4));

        name = null;
        validCells = null;
        board = null;
        possibleActions = null;
        selectedWorker = null;

        game.generateNextTurn();
        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
    }

    @Test
    void buildableCellsListenerTest() throws IllegalActionException, NotYourWorkerException {
        Worker workerToMove =  players.get(0).getWorkers().get(0);
        players.get(0).setSelectedWorker(workerToMove);
        players.get(0).useAction(new MoveAction(workerToMove,game.getGameBoard().getCell(1,2)));
        try {
            players.get(0).obtainBuildableCells();
        } catch (WrongSelectionException e) {
            assertNull(e); //If the method launches the Exception the Test has to fail
        }
        assertEquals(8, validCells.size());
        assertTrue(validCells.contains(game.getGameBoard().getCell(0,1)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(0,2)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(0,3)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(1,1)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(1,3)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(2,1)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(2,2)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(2,3)));
        assertEquals(players.get(0).getName(), this.name);
    }

    @Test
    void walkableCellsListenerTest() throws NotYourWorkerException {
        players.get(0).setSelectedWorker(players.get(0).getWorkers().get(0));
        try {
            players.get(0).obtainWalkableCells();
        } catch (WrongSelectionException e) {
            assertNull(e); //If the method launches the Exception the Test has to fail
        }
        assertEquals(5, validCells.size());
        assertTrue(validCells.contains(game.getGameBoard().getCell(1,1)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(1,2)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(1,3)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(2,1)));
        assertTrue(validCells.contains(game.getGameBoard().getCell(2,3)));
    }

    @Test
    void selectWorkerListenerTest(){
        Worker selectedWorker = players.get(0).getWorkers().get(0);
        try {

            players.get(0).setSelectedWorker(selectedWorker);
        } catch (NotYourWorkerException e) {
            assertNull(e); //If the method launches the Exception the Test has to fail
        }
        assertEquals(2, possibleActions.size());
        assertTrue(possibleActions.contains(PossibleActions.MOVE));
        assertTrue(possibleActions.contains(PossibleActions.SELECT_OTHER_WORKER));
        assertEquals(name, players.get(0).getName());
        assertEquals(this.selectedWorker, selectedWorker);
    }

    @Test
    void buildingBlocksListenerTest() throws NotYourWorkerException, IllegalActionException {
        game.generateNextTurn();
        Worker workerToMove =  players.get(1).getWorkers().get(0);
        players.get(1).setSelectedWorker(workerToMove);
        players.get(1).useAction(new MoveAction(workerToMove,game.getGameBoard().getCell(4,3)));
        try {
            players.get(1).obtainBuildingBlocks(game.getGameBoard().getCell(4,4));
        } catch ( IllegalActionException e) {
            assertNull(e); //If the method launches the Exception the Test has to fail
        }
        assertEquals(this.name, players.get(1).getName());
        assertEquals(2, blocks.size());
        assertTrue(blocks.contains(Block.LEVEL1));
        assertTrue(blocks.contains(Block.DOME));
    }

    @Test
    void buildingBlocksListenerWithOnePossibleBlockTest() throws NotYourWorkerException, IllegalActionException {
        Worker workerToMove =  players.get(0).getWorkers().get(0);
        players.get(0).setSelectedWorker(workerToMove);
        players.get(0).useAction(new MoveAction(workerToMove,game.getGameBoard().getCell(1,2)));
        try {
            players.get(0).obtainBuildingBlocks(game.getGameBoard().getCell(2,2));
        } catch ( IllegalActionException e) {
            assertNull(e); //If the method launches the Exception the Test has to fail
        }
        assertEquals(Block.LEVEL1, game.getGameBoard().getCell(2,2).getBlock());
    }

    @Test
    void addWorkerListenerTest(){
        try {
            players.get(1).addWorker(game.getGameBoard().getCell(3,4));
        } catch (AddingFailedException e) {
            assertNull(e); //If the method launches the Exception the Test has to fail
        }
        assertEquals(board.get(19).getOccupiedBy(), players.get(1).getWorkers().get(1));
    }

    @Test
    void useActionTest() throws NotYourWorkerException {
        Worker workerToMove =  players.get(0).getWorkers().get(0);
        players.get(0).setSelectedWorker(workerToMove);
        try {
            players.get(0).useAction(new MoveAction(workerToMove,game.getGameBoard().getCell(1,2)));
        } catch (IllegalActionException e) {
            assertNull(e); //If the method launches the Exception the Test has to fail
        }
        assertEquals(workerToMove, game.getGameBoard().getCell(1,2).getOccupiedBy());
        assertNull(game.getGameBoard().getCell(2,2).getOccupiedBy());
    }

    @Test
    void buildDataClassTest(){
        PlayerData playerData = players.get(0).buildDataClass();
        assertEquals(playerData.getName(), players.get(0).getName());
        assertEquals(playerData.getColor(), players.get(0).getColor());
        assertEquals(playerData.getGod().getName(), players.get(0).getGod().getName());
        assertEquals(playerData.getGod().getDescriptionStrategy(), players.get(0).getGod().getDescriptionStrategy());
        assertEquals(playerData.getGod().getWorkersNumber(), players.get(0).getGod().getWorkersNumber());
        assertEquals(playerData.getWorkers(), players.get(0).getWorkers());
    }


    @Override
    public void onBuildableCell(String name, List<Cell> buildableCells) {
        this.name = name;
        this.validCells = buildableCells;
    }

    @Override
    public void onWorkerAdd(List<Cell> workerCell) {
        this.board = workerCell;
    }

    @Override
    public void onBlocksObtained(String name, List<Block> blocks) {
        this.name = name;
        this.blocks = blocks;
    }

    @Override
    public void onSelectedWorker(String username, List<PossibleActions> possibleActions, Worker selectedWorker) {
        this.name = username;
        this.possibleActions = possibleActions;
        this.selectedWorker = selectedWorker;
    }

    @Override
    public void onWalkableCells(String name, List<Cell> walkableCells) {
        this.name = name;
        this.validCells = walkableCells;
    }
}
