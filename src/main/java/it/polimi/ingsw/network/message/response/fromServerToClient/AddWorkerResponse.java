package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class AddWorkerResponse extends Message {

    private final List<Cell> payload;
    private final String outcome;

    @JsonCreator
    public AddWorkerResponse(@JsonProperty("outcome") String outcome, @JsonProperty("username") String username, @JsonProperty("payload") List<Cell> payload) {
        super(username, Content.ADD_WORKER);
        this.outcome = outcome;
        if (outcome.equals("OK") || outcome.equals("Add another worker")) {
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }

    public List<Cell> getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }
}
