package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;

public class ChooseWorkerPositionRequest extends MessageFromServerToClient {

    private final List<Cell> payload;

    @JsonCreator
    public ChooseWorkerPositionRequest(@JsonProperty("username") String username, @JsonProperty("payload") List<Cell> payload) {
        super(username, Type.SERVER_REQUEST);
        this.payload = payload;
    }

    @SuppressWarnings("unused")
    public List<Cell> getPayload() {
        return this.payload;
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.chooseYourWorkerPosition(this);
    }

}
