package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class WalkableCellsResponse extends MessageFromServerToClient {
    private final List<Cell> payload;

    @JsonCreator
    public WalkableCellsResponse(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("payload") List<Cell> payload) {
        super(username, type);
        if (type.equals(Type.OK))
            this.payload = payload;
        else
            this.payload = null;
    }

    public List<Cell> getPayload() {
        return payload;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onWalkableCellsReceived(this);
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
