package it.polimi.ingsw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.TurnData;
import it.polimi.ingsw.model.rules.RuleSetStrategy;

import java.util.stream.Collectors;

public class Turn {
    private final int turnNumber;
    private final Player currentPlayer;
    private final RuleSetStrategy ruleSetStrategy;

    @JsonCreator
    public Turn(@JsonProperty("turnNumber") int turnNumber, @JsonProperty("currentPlayer") Player currentPlayer) {
        this.turnNumber = turnNumber;
        this.currentPlayer = currentPlayer;
        this.ruleSetStrategy = currentPlayer.getGod().getStrategy();
    }

    private Turn(Turn turn, Game game) {
        this.turnNumber = turn.turnNumber;
        this.currentPlayer = game.getPlayers().stream().filter(player -> player.getName().equals(turn.currentPlayer.getName())).collect(Collectors.toList()).get(0);
        this.ruleSetStrategy = currentPlayer.getGod().getStrategy();
    }

    public Turn cloneTurn(Game game) {
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


    /*
        The player built in this method and the one built in game.buildDataClass()
        have different references but parameters with equal values (same name, color, etc)
        to fix that we can:
            -Remove PlayerData from TurnData constructor and use a setter instead
            -keep the PlayerData inside the TurnData constructor but pass the correct PlayerData
             to this method that will use that one instead of creating another one.
        to select the correct PlayerData (so to keep the same reference between TurnData.currentPlayer
        and game.players) game will use:
        playersData.stream().filter(playerData -> playerData.getName()
                             .equals(currentTurn.getCurrentPlayer().getName()))
                             .collect(Collectors.toList()).get(0);
         */
    public TurnData buildDataClass() {
        return new TurnData(turnNumber, currentPlayer.buildDataClass());
    }

}
