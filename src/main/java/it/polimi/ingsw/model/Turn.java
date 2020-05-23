package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.TurnData;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.stream.Collectors;

/**
 * A container for a player turn
 * <p>
 * During each turn, the currentPlayer performs its actions based on the loaded strategy
 */
public class Turn {
    private final int turnNumber;
    private final Player currentPlayer;
    private final RuleSetStrategy ruleSetStrategy;

    /**
     * Default constructor
     *
     * @param turnNumber    the turn number
     * @param currentPlayer the player playing in this turn
     */
    @JsonCreator
    public Turn(@JsonProperty("turnNumber") int turnNumber, @JsonProperty("currentPlayer") Player currentPlayer) {
        this.turnNumber = turnNumber;
        this.currentPlayer = currentPlayer;
        this.ruleSetStrategy = currentPlayer.getGod().getStrategy();
    }

    /**
     * Copy constructor
     *
     * @param turn the turn to clone
     * @param game the game in which the turn was played
     */
    private Turn(Turn turn, Game game) {
        this.turnNumber = turn.turnNumber;
        this.currentPlayer = game.getPlayers().stream().filter(player -> player.getName().equals(turn.currentPlayer.getName())).collect(Collectors.toList()).get(0);
        this.ruleSetStrategy = currentPlayer.getGod().getStrategy();
    }

    /**
     * Creates a clone of this object
     *
     * @param game the game in which the turn was played
     * @return a clone of the turn
     */
    public Turn cloneTurn(Game game) {
        return new Turn(this, game);
    }

    /**
     * <i>turnNumber</i> getter
     *
     * @return the turn number
     */
    @JsonGetter
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * <i>currentTurn</i> getter
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * <i>ruleSetStrategy</i> getter
     *
     * @return the turn's strategy
     */
    @JsonGetter
    public RuleSetStrategy getRuleSetStrategy() {
        return ruleSetStrategy;
    }

    /**
     * Creates a {@linkplain TurnData} object based on this turn
     *
     * @return this object's data class
     */
    public TurnData buildDataClass() {
        return new TurnData(turnNumber, currentPlayer.buildDataClass());
    }

}
