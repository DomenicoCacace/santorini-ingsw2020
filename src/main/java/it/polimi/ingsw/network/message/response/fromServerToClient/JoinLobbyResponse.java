package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class JoinLobbyResponse extends MessageFromServerToClient {
    private final List<String> availableLobbies;
    private final int lobbySize;

    @JsonCreator
    public JoinLobbyResponse(@JsonProperty("username") String username, @JsonProperty("type") Type type,
                             @JsonProperty("availableLobbies") List<String> availableLobbies, @JsonProperty("lobbySize") int lobbySize) {
        super(username, type);
        this.availableLobbies = availableLobbies;
        this.lobbySize = lobbySize;
    }

    @JsonGetter
    public List<String> getAvailableLobbies() {
        return availableLobbies;
    }

    @JsonGetter
    public int getLobbySize() {
        return lobbySize;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.joinLobby(this);
    }
}
