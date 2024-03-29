package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;

public class ChooseStartingPlayerRequest extends MessageFromServerToClient {

    private final List<String> payload;

    @JsonCreator
    public ChooseStartingPlayerRequest(@JsonProperty("username") String username, @JsonProperty("payload") List<String> payload) {
        super(username, Type.SERVER_REQUEST);
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
