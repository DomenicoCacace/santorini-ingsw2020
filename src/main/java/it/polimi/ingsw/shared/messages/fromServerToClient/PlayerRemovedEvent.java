package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.ReservedUsernames;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;

public class PlayerRemovedEvent extends MessageFromServerToClient {

    private final String payload;
    private final List<Cell> gameBoard;

    @JsonCreator
    public PlayerRemovedEvent(@JsonProperty("payload") String payload, @JsonProperty("gameBoard") List<Cell> gameBoard) {
        super(ReservedUsernames.BROADCAST.toString(), Type.PLAYER_REMOVED);
        this.payload = payload;
        this.gameBoard = gameBoard;
    }

    public String getPayload() {
        return payload;
    }

    public List<Cell> getGameBoard() {
        return gameBoard;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onPlayerRemoved(this);
    }

}
