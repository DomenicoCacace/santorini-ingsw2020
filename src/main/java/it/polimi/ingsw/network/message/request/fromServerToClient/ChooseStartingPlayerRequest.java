package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

import java.util.List;

public class ChooseStartingPlayerRequest extends MessageFromServerToClient {

    private final List<String> payload;

    @JsonCreator
    public ChooseStartingPlayerRequest(@JsonProperty("username") String username, @JsonProperty("payload") List<String> payload) {
        super(username, Content.STARTING_PLAYER);
        this.payload = payload;
    }

    public List<String> getPayload() {
        return payload;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.chooseStartingPlayer(this);
    }
}
