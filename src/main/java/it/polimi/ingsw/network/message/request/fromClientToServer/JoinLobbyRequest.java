package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

public class JoinLobbyRequest extends MessageFromClientToServer {
    private final String lobbyName;

    @JsonCreator
    public JoinLobbyRequest(@JsonProperty("username") String username, @JsonProperty("lobby name") String lobbyName) {
        super(username, Type.CLIENT_REQUEST);
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.joinLobby(this);
    }

}
