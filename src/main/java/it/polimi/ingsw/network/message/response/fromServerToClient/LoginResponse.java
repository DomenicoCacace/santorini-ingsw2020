package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;
import java.util.Map;

public class LoginResponse extends MessageFromServerToClient {
    private final Map<String, List<String>> lobbies;

    @JsonCreator
    public LoginResponse(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("lobbies") Map<String, List<String>> lobbies) {
        super(username, type);
        this.lobbies = lobbies;
    }

    public Map<String, List<String>> getLobbies() {
        return lobbies;
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onLogin(this);
    }

}
