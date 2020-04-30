package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;

public class WalkableCellsRequest extends MessageFromClientToServer {

    @JsonCreator
    public WalkableCellsRequest(@JsonProperty("username") String username) {
        super(username, Content.WALKABLE_CELLS);
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.walkableCells(this);
    }
}
