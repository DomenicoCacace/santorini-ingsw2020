package it.polimi.ingsw.network.message.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

public class ChooseStartingPlayerResponse extends MessageFromClientToServer {

    private final String payload;

    @JsonCreator
    public ChooseStartingPlayerResponse(@JsonProperty("username") String username, @JsonProperty("payload") String payload) {
        super(username, Type.NOTIFY);
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.chooseStartingPlayer(this);
    }
}
