package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.network.message.request.MessageRequest;

public class BuildableCellsRequest extends MessageRequest {

    public BuildableCellsRequest(String username) {
        super(username, Content.BUILDABLE_CELLS);
    }
}
