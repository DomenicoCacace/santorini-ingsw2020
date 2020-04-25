package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class SelectWorkerResponse extends Message {
    private final String outcome;
    private final List<PossibleActions> possibleActions;

    @JsonCreator
    public SelectWorkerResponse(@JsonProperty("outcome")String outcome, @JsonProperty("username") String username, @JsonProperty("possible actions") List<PossibleActions> possibleActions) {
        super(username, Content.SELECT_WORKER);
        this.outcome = outcome;
        this.possibleActions = possibleActions;
    }

    public List<PossibleActions> getPossibleActions() {
        return possibleActions;
    }

    public String getOutcome() {
        return outcome;
    }
}
