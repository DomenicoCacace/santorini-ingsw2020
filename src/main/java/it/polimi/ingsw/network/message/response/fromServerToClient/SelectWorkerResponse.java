package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.PossibleActions;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class SelectWorkerResponse extends Message {
    private final String outcome;
    private final List<PossibleActions> possibleActions;
    private final Worker selectedWorker;

    @JsonCreator
    public SelectWorkerResponse(@JsonProperty("outcome") String outcome, @JsonProperty("username") String username,
                                @JsonProperty("possible actions") List<PossibleActions> possibleActions, @JsonProperty("SelectedWorker") Worker selectedWorker) {
        super(username, Content.SELECT_WORKER);
        this.outcome = outcome;
        if (outcome.equals("OK")) {
            this.possibleActions = possibleActions;
            this.selectedWorker = selectedWorker;
        } else {
            this.possibleActions = null;
            this.selectedWorker = null;
        }
    }

    public List<PossibleActions> getPossibleActions() {
        return possibleActions;
    }

    public String getOutcome() {
        return outcome;
    }

    public Worker getSelectedWorker() {
        return selectedWorker;
    }
}
