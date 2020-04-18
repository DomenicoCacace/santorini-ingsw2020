package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.network.message.Message;

public class WalkableCellsRequest extends Message {

    public WalkableCellsRequest(String username) {
        super(username, Content.WALKABLE_CELLS);
    }
}
