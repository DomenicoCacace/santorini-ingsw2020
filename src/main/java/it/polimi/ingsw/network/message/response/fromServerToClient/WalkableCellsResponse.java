package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.util.List;

public class WalkableCellsResponse extends MessageResponse {
    List<Cell> payload;

    public WalkableCellsResponse(String outcome, String username, List<Cell> payload) {
        super(outcome, username, Content.WALKABLE_CELLS);
        if(outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }
}
