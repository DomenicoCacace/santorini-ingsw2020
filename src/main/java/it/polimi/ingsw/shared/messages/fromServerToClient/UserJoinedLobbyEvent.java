package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;
import java.util.Map;

public class UserJoinedLobbyEvent extends MessageFromServerToClient {
    private final Map<String, List<String>> availableLobbies;
    private final int lobbySize;
    private final String joinedUser;

    @JsonCreator
    public UserJoinedLobbyEvent(@JsonProperty("username") String username, @JsonProperty("type") Type type,
                                @JsonProperty("availableLobbies") Map<String, List<String>> availableLobbies, @JsonProperty("lobbySize") int lobbySize,
                                @JsonProperty("joinedUser") String joinedUser) {
        super(username, type);
        this.availableLobbies = availableLobbies;
        this.lobbySize = lobbySize;
        this.joinedUser = joinedUser;
    }

    public String getJoinedUser() {
        return joinedUser;
    }

    @JsonGetter
    public Map<String, List<String>> getAvailableLobbies() {
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
