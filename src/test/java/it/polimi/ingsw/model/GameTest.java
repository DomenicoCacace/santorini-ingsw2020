package it.polimi.ingsw.model;

import it.polimi.ingsw.model.action.Action;
import it.polimi.ingsw.model.action.MoveAction;
import it.polimi.ingsw.model.godCardsEffects.buildingEffects.BuildDome;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Push;
import it.polimi.ingsw.model.rules.RuleSetBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GameTest {
    private List<Player> players;
    private List<God> gods;
    private Game game;

    @BeforeEach
    void setUp() throws IOException, LostException {
        gods = new ArrayList<>();
        gods.add(new God("minotaur"));
        gods.get(0).setStrategy(new Push());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());
        gods.add(new God("Atlas"));
        gods.get(2).setStrategy(new BuildDome());

        players = new ArrayList<>();
        players.add(new Player("P1", gods.get(0), Color.BLUE));
        players.add(new Player("P2", gods.get(1), Color.WHITE));
        players.add(new Player("P3", gods.get(2), Color.GREY));

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
    void NextTurnGenerationTest() throws IOException, LostException {
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
    void persistenceTest() throws IOException, LostException {
        game.generateNextTurn();
        Worker currentWorker = game.getPlayers().get(0).getWorkers().get(0);
        Cell targetCell = game.getGameBoard().getCell(2, 3);
        Action moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        game = game.restoreState();

        currentWorker = game.getPlayers().get(0).getWorkers().get(0);
        assertEquals(game.getGameBoard().getCell(2,3), game.getPlayers().get(0).getWorkers().get(0).getPosition());
        assertEquals(game.getGameBoard().getCell(2,3), currentWorker.getPosition());
        targetCell = game.getGameBoard().getCell(1, 3);
        moveAction= new MoveAction(currentWorker,targetCell);
        moveAction.getValidation(game);
        assertEquals(game.getGameBoard().getCell(2,3), currentWorker.getPosition());

        game  = game.restoreState();

        currentWorker = game.getPlayers().get(0).getWorkers().get(0);
        targetCell = game.getGameBoard().getCell(3, 3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction = new MoveAction(currentWorker, targetCell);
        assertEquals(game.getGameBoard().getCell(2,3), currentWorker.getPosition());

        game.generateNextTurn();
        game.generateNextTurn();
        currentWorker=game.getCurrentTurn().getCurrentPlayer().getWorkers().get(0);
        targetCell=game.getGameBoard().getCell(2,3);
        moveAction = new MoveAction(currentWorker, targetCell);
        moveAction.getValidation(game);
        System.out.println("YOLO");
    }

    @Test
    void correctLoseManagement3PlayersTest() throws IOException, LostException {

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
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(0));
        assertEquals(players.get(0).getName(), "P3" );
        assertNull(game.getGameBoard().getCell(2,2).getOccupiedBy());
        assertNull(game.getGameBoard().getCell(3,2).getOccupiedBy());
        assertEquals(game.getWinner(), players.get(0));
    }

    @Test
    void twoConsecutiveLossesTest() throws IOException, LostException {
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
        game.saveState();
        game.generateNextTurn();


        assertEquals(game.getPlayers().size(), 1);
        assertEquals(game.getPlayers().get(0), players.get(0));
        assertEquals(game.getCurrentTurn().getCurrentPlayer(), players.get(0));
        assertEquals(players.get(0).getName(), "P1" );
        assertNull(game.getGameBoard().getCell(1,2).getOccupiedBy());
        assertNull(game.getGameBoard().getCell(2,4).getOccupiedBy());
        assertEquals(game.getWinner(), players.get(0));
    }




}