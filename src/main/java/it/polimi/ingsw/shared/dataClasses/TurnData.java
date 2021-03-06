package it.polimi.ingsw.shared.dataClasses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.server.model.Turn;

/**
 * A data class for {@linkplain Turn}
 * <p>
 * It can be useful to send game information over the network, without any reference to the actual game object.
 */
public class TurnData {
    private final int turnNumber;
    private final PlayerData currentPlayer;

    /**
     * Default constructor
     *
     * @param turnNumber    the turn number
     * @param currentPlayer the player currently playing
     */
    @JsonCreator
    public TurnData(@JsonProperty("turnNumber") int turnNumber, @JsonProperty("currentPlayer") PlayerData currentPlayer) {
        this.turnNumber = turnNumber;
        this.currentPlayer = currentPlayer;
    }

    /**
     * <i>turnNumber</i> getter
     *
     * @return the turn number
     */
    @JsonGetter
    @SuppressWarnings("unused")
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * <i>currentPlayer</i> getter
     *
     * @return the player currently playing
     */
    @JsonGetter
    @SuppressWarnings("unused")
    public PlayerData getCurrentPlayer() {
        return currentPlayer;
    }
}
