package it.polimi.ingsw.model.dataClass;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TurnData {
    private final int turnNumber;
    private final PlayerData currentPlayer;

    @JsonCreator
    public TurnData(@JsonProperty("turnNumber") int turnNumber, @JsonProperty("currentPlayer") PlayerData currentPlayer) {
        this.turnNumber = turnNumber;
        this.currentPlayer = currentPlayer;
    }

    @JsonGetter
    public int getTurnNumber() {
        return turnNumber;
    }

    @JsonGetter
    public PlayerData getCurrentPlayer() {
        return currentPlayer;
    }
}
