package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromServerToClient;
import it.polimi.ingsw.network.message.Type;

public class ChooseToReloadMatchRequest extends MessageFromServerToClient {

    @JsonCreator
    public ChooseToReloadMatchRequest(@JsonProperty("username") String username) {
        super(username, Type.SERVER_REQUEST);
    }

    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.chooseToReloadMatch(this);
    }

}
