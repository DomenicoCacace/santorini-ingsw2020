package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

public class EndTurnResponse extends Message {
    public final String outcome;
    public final String payload;

    public EndTurnResponse(String outcome, String username, String payload) {
        super(username, Content.END_TURN);
        this.payload = payload;
        this.outcome = outcome;
    }
}
