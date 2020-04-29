package it.polimi.ingsw.network.message.response.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class ChooseStartingPlayerResponse extends Message {

    private final String payload;

    @JsonCreator
    public ChooseStartingPlayerResponse(@JsonProperty("username") String username, @JsonProperty("payload") String payload) {
        super(username, Content.STARTING_PLAYER);
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
