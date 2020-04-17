package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.GameBoard;
import it.polimi.ingsw.network.message.response.MessageResponse;

public class SelectWorkerResponse extends MessageResponse {


    public SelectWorkerResponse(String outcome, String username) {
        super(outcome, username, Content.SELECT_WORKER);
    }
}
