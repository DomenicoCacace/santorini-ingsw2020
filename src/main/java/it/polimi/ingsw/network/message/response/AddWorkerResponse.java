package it.polimi.ingsw.network.message.response;

import it.polimi.ingsw.model.GameBoard;

public class AddWorkerResponse extends MessageResponse {

    public final GameBoard payload;

    public AddWorkerResponse (String outcome, String username, GameBoard payload) {
        super(outcome, username, Content.ADD_WORKER);
        if(outcome.equals("OK")){
            this.payload = payload;
        } else {
            this.payload = null;
        }
    }
}
