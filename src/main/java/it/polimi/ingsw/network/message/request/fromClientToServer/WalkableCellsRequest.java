package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class WalkableCellsRequest extends Message {

    @JsonCreator
    public WalkableCellsRequest(@JsonProperty("username") String username) {
        super(username, Content.WALKABLE_CELLS);
    }
}
