package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class PlayerRemovedResponse extends Message {

    public final List<Cell> payload;
    public final String outcome;

    public PlayerRemovedResponse(String outcome, List<Cell> payload) {
        super("broadcast", Content.PLAYER_REMOVED);
        this.outcome = outcome;
        this.payload = payload;
    }
}
