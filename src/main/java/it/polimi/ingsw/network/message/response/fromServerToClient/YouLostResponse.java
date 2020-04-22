package it.polimi.ingsw.network.message.response.fromServerToClient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class YouLostResponse extends Message {
    @JsonCreator
    public YouLostResponse(@JsonProperty("username")String username) {
        super(username, Content.PLAYER_LOST);
    }
}
