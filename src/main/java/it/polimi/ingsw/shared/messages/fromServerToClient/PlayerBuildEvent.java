package it.polimi.ingsw.shared.messages.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.shared.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.Cell;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;

public class PlayerBuildEvent extends MessageFromServerToClient {

    private final List<Cell> payload;

    @JsonCreator
    public PlayerBuildEvent(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("payload") List<Cell> payload) {
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
        visitor.onPlayerBuild(this);
    }

}
