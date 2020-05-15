package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.dataClass.GodData;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

import java.util.List;

public class ChooseInitialGodsRequest extends MessageFromServerToClient {

    private final List<GodData> gods;

    @JsonCreator
    public ChooseInitialGodsRequest(@JsonProperty("username") String username, @JsonProperty("gods") List<GodData> gods) {
        super(username, Type.SERVER_REQUEST);
        this.gods = gods;
    }

    public List<GodData> getGods() {
        return gods;
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.chooseInitialGods(this);
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
