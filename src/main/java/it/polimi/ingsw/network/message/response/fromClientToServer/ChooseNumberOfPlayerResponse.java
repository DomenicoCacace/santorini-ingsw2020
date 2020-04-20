package it.polimi.ingsw.network.message.response.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.network.message.Message;

public class ChooseNumberOfPlayerResponse extends Message {

    public int numberOfPlayers;
    public final String outcome;

    public ChooseNumberOfPlayerResponse(@JsonProperty("outcome") String outcome,@JsonProperty("username") String username,@JsonProperty("payload") int payload) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
        this.outcome = outcome;
        this.numberOfPlayers = payload;
    }
}
