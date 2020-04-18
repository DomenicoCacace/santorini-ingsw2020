package it.polimi.ingsw.network.message.response.fromServerToClient;

import it.polimi.ingsw.network.message.Message;

public class WinnerDeclaredResponse extends Message {

    public final String payload;
    public final String outcome;

    public WinnerDeclaredResponse(String outcome, String payload) {
        super("broadcast", Content.WINNER_DECLARED);
        this.outcome = outcome;
        this.payload = payload;
    }

}
