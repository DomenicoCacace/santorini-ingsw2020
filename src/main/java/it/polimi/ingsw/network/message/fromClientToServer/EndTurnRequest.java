package it.polimi.ingsw.network.message.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

public class EndTurnRequest extends MessageFromClientToServer {

    @JsonCreator
    public EndTurnRequest(@JsonProperty("username") String username) {
        super(username, Type.CLIENT_REQUEST);
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.endTurn(this);
    }
}