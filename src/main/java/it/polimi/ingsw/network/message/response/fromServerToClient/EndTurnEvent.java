package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

public class EndTurnEvent extends MessageFromServerToClient {
    private final String payload;

    @JsonCreator
    public EndTurnEvent(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("payload") String payload) {
        super(username, type);
        this.payload = payload;
    }


    public String getPayload() {
        return payload;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onTurnEnd(this);
    }

}
