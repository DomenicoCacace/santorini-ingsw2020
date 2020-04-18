package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;

import java.util.List;

public class WalkableCellsResponse extends Message {
    private final String outcome;
    List<Cell> payload;

    public WalkableCellsResponse(String outcome, String username, List<Cell> payload) {
        super(username, Content.WALKABLE_CELLS);
        this.outcome = outcome;
        if(outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }
}
