package it.polimi.ingsw.model;

import it.polimi.ingsw.model.rules.RuleSetBase;
import it.polimi.ingsw.model.godCardsEffects.movementEffects.Push;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private List<Player> players;
    private List<God> gods;
    private Game game;


    @BeforeEach
    void setUp() {
        gods = new ArrayList<>();
        gods.add(new God("minotaur"));
        gods.get(0).setStrategy(new Push());
        gods.add(new God("base"));
        gods.get(1).setStrategy(new RuleSetBase());

        players = new ArrayList<>();
        players.add(new Player("P1", gods.get(0), Color.BLUE));
        players.add(new Player("P2", gods.get(1), Color.WHITE));

        GameBoard gameBoard = new GameBoard();
        game = new Game(gameBoard, players);

        gods.get(0).getStrategy().setGame(game);
        gods.get(1).getStrategy().setGame(game);

        game.setCurrentTurn(new Turn(0, players.get(players.size() - 1)));

    }

    @Test
    void NextTurnGenerationTest() {
        Turn currentTurn;

        for(int index = 1; index < 6; index++) {
            game.generateNextTurn();
            currentTurn = game.getCurrentTurn();

            assertEquals(currentTurn.getCurrentPlayer(), players.get((currentTurn.getTurnNumber() - 1) % game.getPlayers().size()));
            assertEquals(currentTurn.getRuleSetStrategy(), players.get((currentTurn.getTurnNumber() - 1) % game.getPlayers().size()).getGod().getStrategy());
            assertEquals(currentTurn.getTurnNumber(), index);
        }
    }
}