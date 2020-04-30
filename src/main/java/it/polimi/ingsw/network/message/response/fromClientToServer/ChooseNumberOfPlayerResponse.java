package it.polimi.ingsw.network.message.response.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.MessageFromClientToServer;

public class ChooseNumberOfPlayerResponse extends MessageFromClientToServer {

    private final int numberOfPlayers;

    @JsonCreator
    public ChooseNumberOfPlayerResponse(@JsonProperty("username") String username, @JsonProperty("payload") int payload) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
        this.numberOfPlayers = payload;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        //Message managed directly in Server, we don't need this method
    }
}
