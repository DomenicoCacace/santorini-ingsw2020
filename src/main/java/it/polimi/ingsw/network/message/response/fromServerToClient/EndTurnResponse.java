package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class EndTurnResponse extends Message {
    private final String outcome;
    private final String payload;

    @JsonCreator
    public EndTurnResponse(@JsonProperty("outcome")String outcome, @JsonProperty("username")String username, @JsonProperty("payload")String payload) {
        super(username, Content.END_TURN);
        this.payload = payload;
        this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getPayload() {
        return payload;
    }
}
