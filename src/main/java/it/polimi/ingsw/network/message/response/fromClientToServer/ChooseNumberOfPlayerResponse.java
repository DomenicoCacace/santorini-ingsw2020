package it.polimi.ingsw.network.message.response.fromClientToServer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.controller.ServerMessageManagerVisitor;
import it.polimi.ingsw.network.message.MessageFromClientToServer;
import it.polimi.ingsw.network.message.Type;

public class ChooseNumberOfPlayerResponse extends MessageFromClientToServer {

    private final int numberOfPlayers;

    @JsonCreator
    public ChooseNumberOfPlayerResponse(@JsonProperty("username") String username, @JsonProperty("payload") int payload) {
        super(username, Type.NOTIFY);
        this.numberOfPlayers = payload;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    @Override
    public void callVisitor(ServerMessageManagerVisitor visitor) {
        //Message managed directly in Lobby, we don't need this method
    }
}
