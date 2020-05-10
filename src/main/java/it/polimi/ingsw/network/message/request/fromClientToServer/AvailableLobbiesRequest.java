package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

public class AvailableLobbiesRequest extends MessageFromClientToServer {

    @JsonCreator
    public AvailableLobbiesRequest(@JsonProperty("username") String username,@JsonProperty("type") Type type) {
        super(username, type);
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
            visitor.lobbyRefresh(this);
    }
}
