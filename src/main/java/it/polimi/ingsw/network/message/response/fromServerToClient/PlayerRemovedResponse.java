package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

import java.util.List;

public class PlayerRemovedResponse extends MessageFromServerToClient {

    private final String payload;
    private final String outcome;
    private final List<Cell> gameboard;

    @JsonCreator
    public PlayerRemovedResponse(@JsonProperty("outcome") String outcome, @JsonProperty("payload") String payload,
                                 @JsonProperty("gameboard") List<Cell> gameboard) {
        super("broadcast", Content.PLAYER_REMOVED);
        this.outcome = outcome;
        this.payload = payload;
        this.gameboard = gameboard;
    }

    public String getPayload() {
        return payload;
    }

    public String getOutcome() {
        return outcome;
    }

    public List<Cell> getGameboard() {
        return gameboard;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onPlayerRemoved(this);
    }
}
