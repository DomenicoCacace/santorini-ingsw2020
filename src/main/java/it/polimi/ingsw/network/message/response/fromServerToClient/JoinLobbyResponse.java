package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

public class JoinLobbyResponse extends MessageFromServerToClient {

    @JsonCreator
    public JoinLobbyResponse(@JsonProperty("username") String username, @JsonProperty("type") Type type) {
        super(username, type);
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.joinLobby(this);
    }
}
