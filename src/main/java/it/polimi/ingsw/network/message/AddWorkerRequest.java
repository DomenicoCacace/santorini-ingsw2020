package it.polimi.ingsw.network.message;

import it.polimi.ingsw.model.Cell;

public class AddWorkerRequest extends MessageRequest {

    public AddWorkerRequest(String username, Cell targetCell) {
        super(username, Content.ADD_WORKER);
    }
}
