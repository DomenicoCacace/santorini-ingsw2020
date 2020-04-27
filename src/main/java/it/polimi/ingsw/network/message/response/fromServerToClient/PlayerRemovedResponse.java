package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class PlayerRemovedResponse extends Message {

    private final String payload;
    private final String outcome;

    @JsonCreator
    public PlayerRemovedResponse(@JsonProperty("outcome")String outcome, @JsonProperty("payload") String payload) {
        super("broadcast", Content.PLAYER_REMOVED);
        this.outcome = outcome;
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }
}
