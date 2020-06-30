package it.polimi.ingsw.shared.messages.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.server.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.GodData;
import it.polimi.ingsw.shared.messages.MessageFromClientToServer;
import it.polimi.ingsw.shared.messages.Type;

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
