package it.polimi.ingsw.shared.messages.fromServerToClient;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.client.network.ClientMessageManagerVisitor;
import it.polimi.ingsw.shared.dataClasses.GodData;
import it.polimi.ingsw.shared.messages.MessageFromServerToClient;
import it.polimi.ingsw.shared.messages.Type;

import java.util.List;

public class ChosenGodsEvent extends MessageFromServerToClient {

    private final List<GodData> payload;

    @JsonCreator
    public ChosenGodsEvent(@JsonProperty("type") Type type, @JsonProperty("username") String username, @JsonProperty("payload") List<GodData> payload) {
        super(username, type);

        if (type.equals(Type.OK))
            this.payload = payload;
        else
            this.payload = null;
    }

    public List<GodData> getPayload() {
        return payload;
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.onGodChosen(this);
    }

}

