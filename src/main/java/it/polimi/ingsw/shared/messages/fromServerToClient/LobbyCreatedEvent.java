package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;
import java.util.Map;

public class LobbyCreatedEvent extends MessageFromServerToClient {

    private final Map<String, List<String>> lobbies;

    @JsonCreator
    public LobbyCreatedEvent(@JsonProperty("username") String username, @JsonProperty("type") Type type, @JsonProperty("lobbies") Map<String, List<String>> lobbies) {
        super(username, type);
        this.lobbies = lobbies;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.createLobby(this);
    }

    public Map<String, List<String>> getLobbies() {
        return lobbies;
    }

}
