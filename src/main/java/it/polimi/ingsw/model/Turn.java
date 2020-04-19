package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.stream.Collectors;

public class Turn {
    private final int turnNumber;
    private final Player currentPlayer;
    private final RuleSetStrategy ruleSetStrategy;

    @JsonCreator
    public Turn(@JsonProperty("turnNumber") int turnNumber,@JsonProperty("currentPlayer") Player currentPlayer) {
        this.turnNumber = turnNumber;
        this.currentPlayer = currentPlayer;
        this.ruleSetStrategy = currentPlayer.getGod().getStrategy();
    }

    private Turn(Turn turn, Game game){
        this.turnNumber = turn.turnNumber;
        this.currentPlayer = game.getPlayers().stream().filter(player -> player.getName().equals(turn.currentPlayer.getName())).collect(Collectors.toList()).get(0);
        this.ruleSetStrategy = currentPlayer.getGod().getStrategy();
    }

    public Turn cloneTurn(Game game){
        return new Turn(this, game);
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
