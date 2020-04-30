package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;

public class EndTurnRequest extends MessageFromClientToServer {

    @JsonCreator
    public EndTurnRequest(@JsonProperty("username") String username) {
        super(username, Content.END_TURN);
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.endTurn(this);
    }
}
