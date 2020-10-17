package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

public class CreateLobbyRequest extends MessageFromClientToServer {
    private final String lobbyName;
    private final int lobbySize;

    @JsonCreator
    public CreateLobbyRequest(@JsonProperty("username") String username, @JsonProperty("lobbyName") String lobbyName, @JsonProperty("lobbySize") int lobbySize) {
        super(username, Type.CLIENT_REQUEST);
        this.lobbyName = lobbyName;
        this.lobbySize = lobbySize;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getLobbySize() {
        return lobbySize;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.createLobby(this);
    }
}
