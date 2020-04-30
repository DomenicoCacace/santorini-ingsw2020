package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

import java.util.List;

public class ChooseWorkerPositionRequest extends MessageFromServerToClient {

    private final List<Cell> payload;

    @JsonCreator
    public ChooseWorkerPositionRequest(@JsonProperty("username") String username, @JsonProperty("payload") List<Cell> payload) {
        super(username, Content.WORKER_POSITION);
        this.payload = payload;
    }

    public List<Cell> getPayload() {
        return this.payload;
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.chooseYourWorkerPosition(this);
    }
}
