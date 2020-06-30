package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.server.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.dataClasses.Worker;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

public class PlayerMoveRequest extends MessageFromClientToServer {

    private final Cell targetCell;
    private final Worker targetWorker;

    @JsonCreator
    public PlayerMoveRequest(@JsonProperty("username") String username, @JsonProperty("targetCell") Cell targetCell,
                             @JsonProperty("targetWorker") Worker targetWorker) {
        super(username, Type.CLIENT_REQUEST);
        this.targetCell = targetCell;
        this.targetWorker = targetWorker;
    }

    public Cell getTargetCell() {
        return targetCell;
    }

    public Worker getTargetWorker() {
        return targetWorker;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.managePlayerMove(this);
    }
}
