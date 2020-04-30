package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.view.ViewInterface;

public class BuildableCellsRequest extends MessageFromClientToServer {

    @JsonCreator
    public BuildableCellsRequest(@JsonProperty("username") String username) {
        super(username, Content.BUILDABLE_CELLS);
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.buildableCells(this);
    }
}
