package it.polimi.ingsw.server.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.server.exceptions.AddingFailedException;
import it.polimi.ingsw.server.exceptions.IllegalActionException;
import it.polimi.ingsw.server.listeners.*;
import it.polimi.ingsw.server.model.action.Action;
import it.polimi.ingsw.server.model.action.BuildAction;
import it.polimi.ingsw.server.model.action.MoveAction;
import it.polimi.ingsw.server.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.server.model.godCardsEffects.movementEffects.Push;
import it.polimi.ingsw.server.model.rules.RuleSetBase;
import it.polimi.ingsw.shared.dataClasses.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest implements BuildActionListener, MoveActionListener, PlayerLostListener, EndGameListener {
    private List<Player> players;
    private List<God> gods;
    private Game game;
    private List<Cell> updatedBoard;
    private List<Cell> workerPosition;
    private String loserName, winnerName;


    @BeforeEach
    void setUp() throws AddingFailedException {
        gods = new ArrayList<>();
        gods.add(new God("Minotaur", 2, ""));
        gods.get(0).setStrategy(new Push());
        gods.add(new God("Base", 2, ""));
        gods.get(1).setStrategy(new RuleSetBase());
        gods.add(new God("Atlas", 2,""));
        gods.get(2).setStrategy(new BuildDome());

        players = new ArrayList<>();
        players.add(new Player("P1", gods.get(0), WorkerColor.BLUE));
        players.add(new Player("P2", gods.get(1), WorkerColor.PURPLE));
        players.add(new Player("P3", gods.get(2), WorkerColor.RED));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
        game.addBuildActionListener(this);
        game.addMoveActionListener(this);
        game.addPlayerLostListener(this);
        game.addEndGameListener(this);
        game.getGameBoard().getCell(3, 4).setBlock(Block.DOME);
        game.getGameBoard().getCell(3, 3).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(4, 2).setBlock(Block.LEVEL1);
        game.getGameBoard().getCell(3, 1).setBlock(Block.DOME);
        game.getGameBoard().getCell(4, 1).setBlock(Block.LEVEL3);

        players.get(0).addWorker(game.getGameBoard().getCell(2, 2));
        players.get(0).addWorker(game.getGameBoard().getCell(3, 2));

        players.get(1).addWorker(game.getGameBoard().getCell(4, 3));
        players.get(1).addWorker(game.getGameBoard().getCell(4, 2));

        players.get(2).addWorker(game.getGameBoard().getCell(2, 4));
        players.get(2).addWorker(game.getGameBoard().getCell(1, 2));


        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));
        game.generateNextTurn();

    }



    @Test
    void NextTurnGenerationTest() {
        Turn currentTurn;

        for(int index = 1; index < 6; index++) {
            game.generateNextTurn();
            currentTurn = game.getCurrentTurn();

            assertEquals(currentTurn.getCurrentPlayer(), players.get((currentTurn.getTurnNumber() - 1) % game.getPlayers().size()));
            assertEquals(currentTurn.getRuleSetStrategy(), players.get((currentTurn.getTurnNumber() - 1) % game.getPlayers().size()).getGod().getStrategy());
            assertEquals(currentTurn.getTurnNumber(), index + 1);
        }
    }

    @Test
    void correctLoseManagement3PlayersTest() {

        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL3);
        game.generateNextTurn();


        assertEquals(game.getCurrentTurn().getTurnNumber(), 3);
        assertEquals(game.getPlayers().size(), 2);
        assertEquals(game.getPlayers().get(0), players.get(0));
        assertEquals(game.getPlayers().get(1), players.get(1));
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
        assertEquals(players.get(0).getName(), "P1" );
        assertEquals(players.get(1).getName(), "P3" );
        assertNull(game.getGameBoard().getCell(4,3).getOccupiedBy());
        assertNull(game.getGameBoard().getCell(4,2).getOccupiedBy());

        game.getGameBoard().getCell(4,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(4,3).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,3).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,3).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(0,2).setBlock(Block.DOME);
        game.generateNextTurn();

        assertEquals(game.getPlayers().size(), 1);
        assertEquals(game.getPlayers().get(0), players.get(0));
        assertEquals(players.get(0).getName(), "P3" );
        assertNull(game.getGameBoard().getCell(2,2).getOccupiedBy());
        assertNull(game.getGameBoard().getCell(3,2).getOccupiedBy());
        assertEquals(game.getWinner(), players.get(0));
    }

    @Test
    void twoConsecutiveLossesTest() {
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,3).setBlock(Block.DOME);
        game.getGameBoard().getCell(1,3).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,4).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(0,1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(0,2).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(0,3).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(1,1).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,1).setBlock(Block.LEVEL3);
        game.generateNextTurn();


        assertEquals(game.getPlayers().size(), 1);
        assertEquals(game.getPlayers().get(0), players.get(0));
        assertEquals(players.get(0).getName(), "P1" );
        assertNull(game.getGameBoard().getCell(1,2).getOccupiedBy());
        assertNull(game.getGameBoard().getCell(2,4).getOccupiedBy());
        assertEquals(game.getWinner(), players.get(0));
    }

    @Test
    void saveStateToVariableTest() throws IOException, IllegalActionException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game savedGame = game.saveStateToVariable(); //Save the current state in savedGame

        String gameToSTring = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(game);
        String savedGameToString = objectMapper.writerFor(Game.class).withDefaultPrettyPrinter().writeValueAsString(savedGame);

        assertEquals(savedGameToString, gameToSTring);

        Worker currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        Cell targetCell = game.getGameBoard().getCell(2, 1);
        Action moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertEquals(currentWorker, targetCell.getOccupiedBy());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());

        targetCell = game.getGameBoard().getCell(1,1 );
        moveAction = new MoveAction(currentWorker, targetCell);
        try {
            moveAction.getValidation(game);
        } catch (IllegalActionException e){
            e.getMessage();
        }
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertNull(targetCell.getOccupiedBy());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        game = savedGame; //Restore to previous state

        gameToSTring = objectMapper.writeValueAsString(game);

        savedGameToString = objectMapper.writeValueAsString(savedGame);
        assertEquals(savedGameToString, gameToSTring);


        //The Player is able to do a different move
        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovedWorker(), currentWorker);
        assertEquals(game.getCurrentRuleSet().getStrategy().getMovesAvailable(), 0);
        assertEquals(currentWorker.getPosition(), targetCell);
        assertEquals(currentWorker, targetCell.getOccupiedBy());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());

        //Save game
        savedGame = game.saveStateToVariable();

        gameToSTring = objectMapper.writeValueAsString(game);

        savedGameToString = objectMapper.writeValueAsString(savedGame);
        assertEquals(savedGameToString, gameToSTring);


        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        targetCell = game.getGameBoard().getCell(1,3);
        BuildAction buildAction = new BuildAction(currentWorker, targetCell, Block.LEVEL1);
        buildAction.getValidation(game);
        assertEquals(Block.LEVEL1 , targetCell.getBlock());
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));

        gameToSTring = objectMapper.writeValueAsString(game);
        savedGameToString = objectMapper.writeValueAsString(savedGame);
        assertNotEquals(savedGameToString, gameToSTring);

        //Restore game
        game = savedGame;

        //Different buildAction
        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        targetCell = game.getGameBoard().getCell(1,4);
        buildAction = new BuildAction(currentWorker, targetCell, Block.LEVEL1);
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(0));
        buildAction.getValidation(game);
        assertEquals(Block.LEVEL1 , targetCell.getBlock());
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(1));
    }

    @Test
    void listenersTests() throws IllegalActionException {
        game.getGameBoard().getCell(4,4).setBlock(Block.LEVEL2);
        game.getGameBoard().getCell(3,3).setBlock(Block.LEVEL3);
        game.getGameBoard().getCell(2,4).setBlock(Block.LEVEL2);

        Worker currentWorker = players.get(0).getWorkers().get(0);
        Cell targetCell = game.getGameBoard().getCell(2,3);
        MoveAction moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        assertEquals(currentWorker, updatedBoard.get(13).getOccupiedBy());
        assertNull(updatedBoard.get(12).getOccupiedBy());
        targetCell = game.getGameBoard().getCell(2,2);
        BuildAction buildAction = new BuildAction(currentWorker, targetCell, Block.LEVEL1);
        buildAction.getValidation(game);
        assertEquals(Block.LEVEL1, updatedBoard.get(12).getBlock());
        assertNull(updatedBoard.get(22).getOccupiedBy());
        assertNull(updatedBoard.get(23).getOccupiedBy());
        assertEquals(loserName, "P2");
        currentWorker = players.get(1).getWorkers().get(0);
        targetCell = game.getGameBoard().getCell(3,3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        assertEquals(winnerName, "P3");
    }

    @Override
    public void onBuildAction(List<Cell> cells) {
        updatedBoard = cells;
    }

    @Override
    public void onMoveAction(List<Cell> gameBoard) {
        updatedBoard = gameBoard;
    }

    @Override
    public void onPlayerLoss(String username, List<Cell> gameBoard) {
        updatedBoard = gameBoard;
        loserName = username;
    }

    @Override
    public void onEndGame(String name) {
        winnerName = name;
    }
}