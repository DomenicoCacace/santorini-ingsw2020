package it.polimi.ingsw.network.message.response.fromClientToServer;

import it.polimi.ingsw.network.message.Message;

public class ChooseNumberOfPlayerResponse extends Message {

    public int numberOfPlayers;
    public final String outcome;

    public ChooseNumberOfPlayerResponse(String outcome, String username, int payload) {
        super(username, Content.CHOOSE_PLAYER_NUMBER);
        this.outcome = outcome;
        this.numberOfPlayers = payload;
    }
}
