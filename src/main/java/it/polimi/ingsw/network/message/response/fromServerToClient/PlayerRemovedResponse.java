package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

public class PlayerRemovedResponse extends MessageFromServerToClient {

    private final String payload;
    private final String outcome;

    @JsonCreator
    public PlayerRemovedResponse(@JsonProperty("outcome") String outcome, @JsonProperty("payload") String payload) {
        super("broadcast", Content.PLAYER_REMOVED);
        this.outcome = outcome;
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onPlayerRemoved(this);
    }
}
