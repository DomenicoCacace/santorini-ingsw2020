package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.ReservedUsernames;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class PlayerRemovedEvent extends MessageFromServerToClient {

    private final String payload;
    private final List<Cell> gameboard;

    @JsonCreator
    public PlayerRemovedEvent(@JsonProperty("payload") String payload, @JsonProperty("gameboard") List<Cell> gameboard) {
        super(ReservedUsernames.BROADCAST.toString(), Type.PLAYER_REMOVED);
        this.payload = payload;
        this.gameboard = gameboard;
    }

    public String getPayload() {
        return payload;
    }

    public List<Cell> getGameboard() {
        return gameboard;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onPlayerRemoved(this);
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
