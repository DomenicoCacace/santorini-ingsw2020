package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.server.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

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
