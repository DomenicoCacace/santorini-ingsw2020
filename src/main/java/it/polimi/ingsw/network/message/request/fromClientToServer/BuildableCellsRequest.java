package it.polimi.ingsw.network.message.request.fromClientToServer;

import it.polimi.ingsw.network.message.Message;

public class BuildableCellsRequest extends Message {

    public BuildableCellsRequest(String username) {
        super(username, Content.BUILDABLE_CELLS);
    }
}
