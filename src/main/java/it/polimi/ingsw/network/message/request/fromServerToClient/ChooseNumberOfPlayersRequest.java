package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Type;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

public class ChooseNumberOfPlayersRequest extends MessageFromServerToClient {

    public ChooseNumberOfPlayersRequest(@JsonProperty("username") String username) {
        super(username, Type.SERVER_REQUEST);
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.choosePlayerNumber(this);
    }
}
