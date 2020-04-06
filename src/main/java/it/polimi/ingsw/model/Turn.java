package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

public class Turn {
    private final int turnNumber;
    private final Player currentPlayer;
    private final RuleSetStrategy ruleSetStrategy;

    @JsonCreator
    public Turn(@JsonProperty("turnNumer") int turnNumber,@JsonProperty("currentPlayer") Player currentPlayer) {
        this.turnNumber = turnNumber;
        this.currentPlayer = currentPlayer;
        this.ruleSetStrategy = currentPlayer.getGod().getStrategy();
    }

@JsonGetter
    public int getTurnNumber() {
        return turnNumber;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
@JsonGetter
    public RuleSetStrategy getRuleSetStrategy() {
        return ruleSetStrategy;
    }

}
