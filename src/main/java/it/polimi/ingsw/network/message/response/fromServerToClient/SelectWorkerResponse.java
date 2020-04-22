package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class SelectWorkerResponse extends Message {
    private final String outcome;

    @JsonCreator
    public SelectWorkerResponse(@JsonProperty("outcome")String outcome, @JsonProperty("username") String username) {
        super(username, Content.SELECT_WORKER);
        this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }
}
