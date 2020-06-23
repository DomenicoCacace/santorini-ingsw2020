package it.polimi.ingsw.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.exceptions.AddingFailedException;
import it.polimi.ingsw.exceptions.IllegalActionException;
import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.BuildAction;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Push;
import it.polimi.ingsw.model.rules.RuleSetBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private List<Player> players;
    private Game game;

    @BeforeEach
    void setUp() throws AddingFailedException {
        List<God> gods = new ArrayList<>();
        gods.add(new God("minotaur", 2, ""));
        gods.get(0).setStrategy(new Push());
        gods.add(new God("base", 2, ""));
        gods.get(1).setStrategy(new RuleSetBase());
        gods.add(new God("Atlas", 2,""));
        gods.get(2).setStrategy(new BuildDome());

        players = new ArrayList<>();
        players.add(new Player("P1", gods.get(0), Color.BLUE));
        players.add(new Player("P2", gods.get(1), Color.PURPLE));
        players.add(new Player("P3", gods.get(2), Color.RED));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);
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


        assertEquals(3, game.getCurrentTurn().getTurnNumber());
        assertEquals(2, game.getPlayers().size());
        assertEquals(players.get(0), game.getPlayers().get(0));
        assertEquals(players.get(1), game.getPlayers().get(1));
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
        assertEquals("P1", players.get(0).getName() );
        assertEquals("P3", players.get(1).getName() );
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

        assertEquals(1, game.getPlayers().size());
        assertEquals(players.get(0), game.getPlayers().get(0));
        assertEquals("P3", players.get(0).getName() );
        assertNull(game.getGameBoard().getCell(2,2).getOccupiedBy());
        assertNull(game.getGameBoard().getCell(3,2).getOccupiedBy());
        assertEquals(players.get(0), game.getWinner());
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


        assertEquals(1, game.getPlayers().size());
        assertEquals(players.get(0), game.getPlayers().get(0));
        assertEquals("P1", players.get(0).getName() );
        assertNull(game.getGameBoard().getCell(1,2).getOccupiedBy());
        assertNull(game.getGameBoard().getCell(2,4).getOccupiedBy());
        assertEquals(players.get(0), game.getWinner());
    }

    @Test
    void saveStateToVariableTest() throws IOException, IllegalActionException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game savedGame = game.saveStateToVariable(); //Save the current state in savedGame

        String gametostring = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(game);
        String savedGameToString = objectMapper.writerFor(Game.class).withDefaultPrettyPrinter().writeValueAsString(savedGame);

        assertEquals(savedGameToString, gametostring);

        Worker currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        Cell targetCell = game.getGameBoard().getCell(2, 1);
        Action moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
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
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertNotEquals(currentWorker.getPosition(), targetCell);
        assertNull(targetCell.getOccupiedBy());
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());
        game = savedGame; //Restore to previous state

        gametostring = objectMapper.writeValueAsString(game);

        savedGameToString = objectMapper.writeValueAsString(savedGame);
        assertEquals(savedGameToString, gametostring);


        //The Player is able to do a different move
        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        targetCell = game.getGameBoard().getCell(2, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        assertEquals(currentWorker, game.getCurrentRuleSet().getStrategy().getMovedWorker());
        assertEquals(0, game.getCurrentRuleSet().getStrategy().getMovesAvailable());
        assertEquals(targetCell, currentWorker.getPosition());
        assertEquals(targetCell.getOccupiedBy(), currentWorker);
        assertFalse(game.getCurrentRuleSet().getStrategy().hasMovedUp());

        //Save game
        savedGame = game.saveStateToVariable();

        gametostring = objectMapper.writeValueAsString(game);

        savedGameToString = objectMapper.writeValueAsString(savedGame);
        assertEquals(gametostring, savedGameToString);


        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        targetCell = game.getGameBoard().getCell(1,3);
        BuildAction buildAction = new BuildAction(currentWorker, targetCell, Block.LEVEL1);
        buildAction.getValidation(game);
        assertEquals(Block.LEVEL1 , targetCell.getBlock());
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());

        gametostring = objectMapper.writeValueAsString(game);
        savedGameToString = objectMapper.writeValueAsString(savedGame);
        assertNotEquals(savedGameToString, gametostring);

        //Restore game
        game = savedGame;

        //Different buildAction
        currentWorker = game.getCurrentTurn().getCurrentPlayer().getWorkers().get(1);
        targetCell = game.getGameBoard().getCell(1,4);
        buildAction = new BuildAction(currentWorker, targetCell, Block.LEVEL1);
        assertEquals(players.get(0), game.getCurrentTurn().getCurrentPlayer());
        buildAction.getValidation(game);
        assertEquals(Block.LEVEL1 , targetCell.getBlock());
        assertEquals(players.get(1), game.getCurrentTurn().getCurrentPlayer());
    }
}