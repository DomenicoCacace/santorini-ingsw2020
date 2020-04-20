package it.polimi.ingsw.network.message.request.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.message.Message;


public class AddWorkerRequest extends Message {
    public final Cell targetCell;

    @JsonCreator
    public AddWorkerRequest(@JsonProperty("username") String username, @JsonProperty("targetCell") Cell targetCell) {
        super(username, Content.ADD_WORKER);
        this.targetCell = targetCell;
    }
}
