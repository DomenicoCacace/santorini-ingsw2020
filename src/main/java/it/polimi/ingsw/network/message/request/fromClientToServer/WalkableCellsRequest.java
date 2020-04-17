package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.network.message.request.MessageRequest;

public class WalkableCellsRequest extends MessageRequest {

    public WalkableCellsRequest(String username) {
        super(username, Content.WALKABLE_CELLS);
    }
}
