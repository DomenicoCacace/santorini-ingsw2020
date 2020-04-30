package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.client.ClientMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromServerToClient;

public class ChooseNumberOfPlayersRequest extends MessageFromServerToClient {

    public ChooseNumberOfPlayersRequest(@JsonProperty("username") String username) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
    }


    @Override
    public void callVisitor(ClientMessageManagerVisitor visitor) {
        visitor.choosePlayerNumber(this);
    }
}
