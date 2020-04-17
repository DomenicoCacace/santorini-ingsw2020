package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.response.MessageResponse;

import java.util.List;

public class BuildableCellsResponse extends MessageResponse {
    List<Cell> payload;

    public BuildableCellsResponse(String outcome, String username, List<Cell> payload) {
        super(outcome, username, Content.BUILDABLE_CELLS);
        if(outcome.equals("OK"))
            this.payload = payload;
        else
            this.payload = null;
    }
}
