package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;


public class AddWorkerRequest extends MessageFromClientToServer {
    private final Cell targetCell;

    @JsonCreator
    public AddWorkerRequest(@JsonProperty("username") String username, @JsonProperty("targetCell") Cell targetCell) {
        super(username, Type.CLIENT_REQUEST);
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
