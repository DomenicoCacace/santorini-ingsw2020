package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.request.MessageRequest;

public class AddWorkerRequest extends MessageRequest {
    public final Cell targetCell;

    public AddWorkerRequest(String username, Cell targetCell) {
        super(username, Content.ADD_WORKER);
        this.targetCell=targetCell;
    }
}
