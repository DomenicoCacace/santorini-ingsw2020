package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

import java.util.List;

public class PlayerMoveResponse extends MessageFromServerToClient {

    private final List<Cell> payload;
    private final String outcome;

    @JsonCreator
    public PlayerMoveResponse(@JsonProperty("outcome") String outcome, @JsonProperty("username") String username, @JsonProperty("payload") List<Cell> payload) {
        super(username, Content.PLAYER_MOVE);
        this.outcome = outcome;
        if (outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }

    public List<Cell> getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onPlayerMove(this);
    }
}

