package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class LoginResponse extends MessageFromServerToClient {

    private final List<String> lobbies;

    @JsonCreator
    public LoginResponse(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("lobbies") List<String> lobbies) {
        super(username, type);
        this.lobbies = lobbies;
    }

    public List<String> getLobbies() {
        return lobbies;
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onLogin(this);
    }

}
