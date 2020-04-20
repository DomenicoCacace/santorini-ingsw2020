package it.polimi.ingsw.network.message.request.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class ChooseNumberOfPlayersRequest extends Message {

    public ChooseNumberOfPlayersRequest(@JsonProperty("username") String username) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
    }
}
