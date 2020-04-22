package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

public class SelectWorkerResponse extends Message {
    private final String outcome;

    public SelectWorkerResponse(String outcome, String username) {
        super(username, Content.SELECT_WORKER);
        this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }
}
