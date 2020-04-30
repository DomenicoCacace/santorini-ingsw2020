package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;


public class AddWorkerRequest extends MessageFromClientToServer {
    private final Cell targetCell;

    @JsonCreator
    public AddWorkerRequest(@JsonProperty("username") String username, @JsonProperty("targetCell") Cell targetCell) {
        super(username, Content.ADD_WORKER);
        this.targetCell = targetCell;
    }

    public Cell getTargetCell() {
        return targetCell;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.addWorkerOnBoard(this);
    }
}
