package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;

public class WalkableCellsRequest extends MessageFromClientToServer {

    @JsonCreator
    public WalkableCellsRequest(@JsonProperty("username") String username) {
        super(username, Type.CLIENT_REQUEST);
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.walkableCells(this);
    }
}
