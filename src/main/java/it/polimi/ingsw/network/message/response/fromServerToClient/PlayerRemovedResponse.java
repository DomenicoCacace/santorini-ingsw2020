package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class PlayerRemovedResponse extends Message {

    private final List<Cell> payload;
    private final String outcome;

    @JsonCreator
    public PlayerRemovedResponse(@JsonProperty("outcome")String outcome, @JsonProperty("payload") List<Cell> payload) {
        super("broadcast", Content.PLAYER_REMOVED);
        this.outcome = outcome;
        this.payload = payload;
    }

    public List<Cell> getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }
}
