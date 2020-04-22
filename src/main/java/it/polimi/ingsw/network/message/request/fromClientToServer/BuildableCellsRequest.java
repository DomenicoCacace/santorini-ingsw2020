package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class BuildableCellsRequest extends Message {

    @JsonCreator
    public BuildableCellsRequest(@JsonProperty("username") String username) {
        super(username, Content.BUILDABLE_CELLS);
    }
}
