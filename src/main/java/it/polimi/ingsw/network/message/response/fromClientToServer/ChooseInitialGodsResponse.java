package it.polimi.ingsw.network.message.response.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class ChooseInitialGodsResponse extends MessageFromClientToServer {

    private final List<GodData> payload;

    @JsonCreator
    public ChooseInitialGodsResponse(@JsonProperty("username") String username, @JsonProperty("payload") List<GodData> payload) {
        super(username, Type.NOTIFY);
        this.payload = payload;
    }

    public List<GodData> getPayload() {
        return payload;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        visitor.chooseInitialGods(this);
    }

}
