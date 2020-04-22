package it.polimi.ingsw.network.message.response.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class ChooseNumberOfPlayerResponse extends Message {

    private final int numberOfPlayers;

    @JsonCreator
    public ChooseNumberOfPlayerResponse(@JsonProperty("username") String username,@JsonProperty("payload") int payload) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
        this.numberOfPlayers = payload;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
