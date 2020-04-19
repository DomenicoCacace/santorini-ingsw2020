package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

public class SelectWorkerResponse extends Message {
    public final String outcome;

    public SelectWorkerResponse(String outcome, String username) {
        super(username, Content.SELECT_WORKER);
        this.outcome = outcome;
    }
}
